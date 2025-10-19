package org.example.apcproject3.repository;

import org.example.apcproject3.entity.User;
import org.example.apcproject3.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    List<User> findByRole(UserRole role);

    List<User> findByEnabled(boolean enabled);

    @Query("SELECT u FROM User u WHERE u.enabled = true")
    List<User> findAllActiveUsers();

    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:name% OR u.lastName LIKE %:name%")
    List<User> findByName(@Param("name") String name);

    // Count methods for statistics
    long countByEnabled(boolean enabled);

    long countByRole(UserRole role);

    // Recent users query
    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    List<User> findTopNByOrderByCreatedAtDesc(@Param("limit") int limit);

    // Advanced search queries
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.email LIKE %:keyword% OR u.firstName LIKE %:keyword% OR u.lastName LIKE %:keyword%")
    List<User> searchUsers(@Param("keyword") String keyword);

    // Find users by provider (OAuth2)
    @Query("SELECT u FROM User u WHERE u.provider = :provider")
    List<User> findByProvider(@Param("provider") org.example.apcproject3.entity.AuthProvider provider);

    // Find users registered in date range
    @Query("SELECT u FROM User u WHERE u.createdAt >= :startDate AND u.createdAt <= :endDate")
    List<User> findUsersRegisteredBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                         @Param("endDate") java.time.LocalDateTime endDate);

    // Find top active users by role
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.enabled = true ORDER BY u.createdAt DESC")
    List<User> findTopActiveUsersByRole(@Param("role") UserRole role, org.springframework.data.domain.Pageable pageable);
}
