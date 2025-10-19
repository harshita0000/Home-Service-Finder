package org.example.apcproject3.service;

import org.example.apcproject3.entity.User;
import org.example.apcproject3.entity.UserRole;
import org.example.apcproject3.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(UserRole.CUSTOMER);
    }

    @Test
    void createUser_Success() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.createUser(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userRepository).existsByUsername(testUser.getUsername());
        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(passwordEncoder).encode(testUser.getPassword());
        verify(userRepository).save(testUser);
    }

    @Test
    void createUser_UsernameExists_ThrowsException() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.createUser(testUser));
        assertEquals("Username is already taken!", exception.getMessage());

        verify(userRepository).existsByUsername(testUser.getUsername());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_EmailExists_ThrowsException() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.createUser(testUser));
        assertEquals("Email Address already in use!", exception.getMessage());

        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void findByUsername_Success() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findByUsername("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getUsername(), result.get().getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void findByRole_Success() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByRole(any(UserRole.class))).thenReturn(users);

        // When
        List<User> result = userService.findUsersByRole(UserRole.CUSTOMER);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getRole(), result.get(0).getRole());
        verify(userRepository).findByRole(UserRole.CUSTOMER);
    }

    @Test
    void changePassword_Success() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        boolean result = userService.changePassword(1L, "oldPassword", "newPassword");

        // Then
        assertTrue(result);
        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches("oldPassword", testUser.getPassword());
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(testUser);
    }

    @Test
    void changePassword_WrongOldPassword_ThrowsException() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.changePassword(1L, "wrongPassword", "newPassword"));
        assertEquals("Current password is incorrect", exception.getMessage());

        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches("wrongPassword", testUser.getPassword());
        verify(userRepository, never()).save(any());
    }
}
