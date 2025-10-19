package org.example.apcproject3.repository;

// Temporarily disabled - MongoDB dependency removed from pom.xml
/*
import org.example.apcproject3.document.ServiceAnalytics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServiceAnalyticsRepository extends MongoRepository<ServiceAnalytics, String> {
    Optional<ServiceAnalytics> findByProviderId(Long providerId);

    List<ServiceAnalytics> findByCategory(String category);

    List<ServiceAnalytics> findByAverageRatingGreaterThan(Double rating);

    @Query("{'totalBookings': {$gte: ?0}}")
    List<ServiceAnalytics> findByTotalBookingsGreaterThanEqual(Integer bookings);

    @Query("{'category': ?0, 'totalRevenue': {$gte: ?1}}")
    List<ServiceAnalytics> findByCategoryAndMinRevenue(String category, Double minRevenue);

    List<ServiceAnalytics> findTop10ByOrderByTotalRevenueDesc();

    List<ServiceAnalytics> findTop10ByOrderByAverageRatingDesc();
}
*/
