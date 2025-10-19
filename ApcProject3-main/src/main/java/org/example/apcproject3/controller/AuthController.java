package org.example.apcproject3.controller;

import org.example.apcproject3.dto.JwtAuthenticationResponse;
import org.example.apcproject3.dto.LoginRequest;
import org.example.apcproject3.dto.SignUpRequest;
import org.example.apcproject3.entity.User;
import org.example.apcproject3.entity.UserRole;
import org.example.apcproject3.security.JwtTokenProvider;
import org.example.apcproject3.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsernameOrEmail(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);
            User user = (User) authentication.getPrincipal();

            return ResponseEntity.ok(new JwtAuthenticationResponse(
                jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
            ));

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid username/email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            // Check if username is already taken
            if (userService.existsByUsername(signUpRequest.getUsername())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Username is already taken!");
                return ResponseEntity.badRequest().body(error);
            }

            // Check if email is already in use
            if (userService.existsByEmail(signUpRequest.getEmail())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Email Address already in use!");
                return ResponseEntity.badRequest().body(error);
            }

            // Create new user
            User user = new User();
            user.setUsername(signUpRequest.getUsername());
            user.setEmail(signUpRequest.getEmail());
            user.setPassword(signUpRequest.getPassword());
            user.setFirstName(signUpRequest.getFirstName());
            user.setLastName(signUpRequest.getLastName());
            user.setPhoneNumber(signUpRequest.getPhoneNumber());
            user.setAddress(signUpRequest.getAddress());

            // Set role
            try {
                user.setRole(UserRole.valueOf(signUpRequest.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                user.setRole(UserRole.CUSTOMER); // Default role
            }

            User savedUser = userService.createUser(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", savedUser.getId());
            response.put("username", savedUser.getUsername());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('CUSTOMER') or hasRole('SERVICE_PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            if (userDetails instanceof User) {
                User user = (User) userDetails;
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("username", user.getUsername());
                userInfo.put("email", user.getEmail());
                userInfo.put("firstName", user.getFirstName());
                userInfo.put("lastName", user.getLastName());
                userInfo.put("role", user.getRole().name());
                userInfo.put("phoneNumber", user.getPhoneNumber());
                userInfo.put("address", user.getAddress());
                userInfo.put("enabled", user.isEnabled());

                return ResponseEntity.ok(userInfo);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('USER') or hasRole('CUSTOMER') or hasRole('SERVICE_PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordData,
                                          Authentication authentication) {
        try {
            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");

            if (currentPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().body("Current password and new password are required");
            }

            User user = (User) authentication.getPrincipal();
            boolean success = userService.changePassword(user.getId(), currentPassword, newPassword);

            if (success) {
                return ResponseEntity.ok("Password changed successfully");
            } else {
                return ResponseEntity.badRequest().body("Invalid current password");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Password change failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            SecurityContextHolder.clearContext();

            Map<String, String> response = new HashMap<>();
            response.put("message", "Logged out successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Logout failed: " + e.getMessage());
        }
    }

    // Admin endpoints for user management
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.findAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch users: " + e.getMessage());
        }
    }

    @PutMapping("/admin/users/{userId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long userId) {
        try {
            User user = userService.toggleUserStatus(userId);
            return ResponseEntity.ok("User status updated: " + (user.isEnabled() ? "enabled" : "disabled"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update user status: " + e.getMessage());
        }
    }

    @PutMapping("/admin/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRole(@PathVariable Long userId,
                                          @RequestBody Map<String, String> roleData) {
        try {
            String newRole = roleData.get("role");
            if (newRole == null) {
                return ResponseEntity.badRequest().body("Role is required");
            }

            User user = userService.updateUserRole(userId, UserRole.valueOf(newRole.toUpperCase()));
            return ResponseEntity.ok("User role updated to: " + user.getRole().name());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role: " + roleData.get("role"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update user role: " + e.getMessage());
        }
    }
}
