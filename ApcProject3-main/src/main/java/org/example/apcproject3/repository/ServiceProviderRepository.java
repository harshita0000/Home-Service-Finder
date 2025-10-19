package org.example.apcproject3.repository;

import org.example.apcproject3.entity.ServiceProvider;
import org.example.apcproject3.entity.ServiceCategory;
import org.example.apcproject3.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    Optional<ServiceProvider> findByUser(User user);

    List<ServiceProvider> findByCategory(ServiceCategory category);

    List<ServiceProvider> findByAvailableTrue();

    List<ServiceProvider> findByVerifiedTrue();

    @Query("SELECT sp FROM ServiceProvider sp WHERE sp.category = :category AND sp.available = true")
    List<ServiceProvider> findAvailableProvidersByCategory(@Param("category") ServiceCategory category);

    @Query("SELECT sp FROM ServiceProvider sp WHERE sp.rating >= :minRating ORDER BY sp.rating DESC")
    List<ServiceProvider> findByRatingGreaterThanEqualOrderByRatingDesc(@Param("minRating") BigDecimal minRating);

    @Query("SELECT sp FROM ServiceProvider sp WHERE sp.hourlyRate BETWEEN :minRate AND :maxRate")
    List<ServiceProvider> findByHourlyRateBetween(@Param("minRate") BigDecimal minRate, @Param("maxRate") BigDecimal maxRate);

    @Query("SELECT sp FROM ServiceProvider sp WHERE sp.category.id = :categoryId AND sp.available = true AND sp.verified = true ORDER BY sp.rating DESC")
    List<ServiceProvider> findTopRatedProvidersByCategory(@Param("categoryId") Long categoryId);
}
