package org.example.apcproject3.repository;

import org.example.apcproject3.entity.Review;
import org.example.apcproject3.entity.ServiceProvider;
import org.example.apcproject3.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Find reviews by provider
    List<Review> findByProviderOrderByCreatedAtDesc(ServiceProvider provider);

    // Find reviews by customer
    List<Review> findByCustomerOrderByCreatedAtDesc(User customer);

    // Find review by booking (one review per booking)
    Optional<Review> findByBookingId(Long bookingId);

    // Check if a review exists for a booking
    boolean existsByBookingId(Long bookingId);

    // Get average rating for a provider
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.provider = :provider")
    Double getAverageRatingByProvider(@Param("provider") ServiceProvider provider);

    // Count reviews for a provider
    long countByProvider(ServiceProvider provider);

    // Find recent reviews (for homepage/dashboard)
    List<Review> findTop10ByOrderByCreatedAtDesc();

    // Find reviews by rating
    List<Review> findByRatingOrderByCreatedAtDesc(Integer rating);

    // Find reviews with rating greater than or equal to specified value
    List<Review> findByRatingGreaterThanEqualOrderByCreatedAtDesc(Integer minRating);
}
