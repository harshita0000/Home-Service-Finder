package org.example.apcproject3.service;

import org.example.apcproject3.entity.User;
import org.example.apcproject3.entity.UserRole;
import org.example.apcproject3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        // Check if user already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email Address already in use!");
        }

        // Encode password only if it's not already encoded
        if (!user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail));
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<User> findUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    public List<User> findActiveUsers() {
        return userRepository.findAllActiveUsers();
    }

    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User updateUserProfile(Long userId, User updatedUser) {
        Optional<User> existingUserOpt = userRepository.findById(userId);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            // Update only allowed fields (not password, username, or email)
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            existingUser.setAddress(updatedUser.getAddress());
            existingUser.setProfileImage(updatedUser.getProfileImage());
            existingUser.setUpdatedAt(LocalDateTime.now());

            return userRepository.save(existingUser);
        }
        throw new RuntimeException("User not found with id: " + userId);
    }

    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Verify current password
            if (passwordEncoder.matches(currentPassword, user.getPassword())) {
                // Update password
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public User toggleUserStatus(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEnabled(!user.isEnabled());
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found with id: " + userId);
    }

    public User updateUserRole(Long userId, UserRole newRole) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(newRole);
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found with id: " + userId);
    }

    public void deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }

    // Async method for user operations
    @Transactional(readOnly = true)
    public CompletableFuture<List<User>> findAllUsersAsync() {
        return CompletableFuture.supplyAsync(() -> userRepository.findAll());
    }

    // Method to handle OAuth2 user registration/update
    public User processOAuthUser(String email, String name, String provider, String providerId) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            // Update existing user
            User user = existingUser.get();
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        } else {
            // Create new user
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(generateUsernameFromEmail(email));

            // Parse name if available
            if (name != null && !name.isEmpty()) {
                String[] nameParts = name.split(" ", 2);
                newUser.setFirstName(nameParts[0]);
                if (nameParts.length > 1) {
                    newUser.setLastName(nameParts[1]);
                } else {
                    newUser.setLastName("");
                }
            }

            // Set OAuth2 provider info
            newUser.setProvider(org.example.apcproject3.entity.AuthProvider.valueOf(provider.toUpperCase()));
            newUser.setProviderId(providerId);
            newUser.setRole(UserRole.CUSTOMER);
            newUser.setEnabled(true);
            newUser.setPassword(""); // OAuth2 users don't have passwords

            return userRepository.save(newUser);
        }
    }

    private String generateUsernameFromEmail(String email) {
        String baseUsername = email.substring(0, email.indexOf('@'));
        String username = baseUsername;
        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }

    // User statistics methods for admin dashboard
    public long getTotalUsersCount() {
        return userRepository.count();
    }

    public long getActiveUsersCount() {
        return userRepository.findAllActiveUsers().size();
    }

    public long getUsersByRoleCount(UserRole role) {
        return userRepository.findByRole(role).size();
    }

    public List<User> getRecentlyRegisteredUsers(int limit) {
        // Use findAll and manually limit since we don't have the specific query
        List<User> allUsers = userRepository.findAll();
        allUsers.sort((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()));
        return allUsers.subList(0, Math.min(limit, allUsers.size()));
    }
}
