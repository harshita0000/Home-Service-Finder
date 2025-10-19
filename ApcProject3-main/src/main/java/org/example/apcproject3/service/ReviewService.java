package org.example.apcproject3.service;

import org.example.apcproject3.entity.Booking;
import org.example.apcproject3.entity.Review;
import org.example.apcproject3.entity.ServiceProvider;
import org.example.apcproject3.entity.User;
import org.example.apcproject3.repository.ReviewRepository;
import org.example.apcproject3.repository.BookingRepository;
import org.example.apcproject3.repository.ServiceProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    public Review createReview(Review review) {
        // Validate that booking exists and is completed
        Optional<Booking> bookingOpt = bookingRepository.findById(review.getBooking().getId());
        if (bookingOpt.isEmpty()) {
            throw new RuntimeException("Booking not found");
        }

        Booking booking = bookingOpt.get();
        if (!booking.getStatus().name().equals("COMPLETED")) {
            throw new RuntimeException("Can only review completed bookings");
        }

        // Check if review already exists for this booking
        if (reviewRepository.existsByBookingId(booking.getId())) {
            throw new RuntimeException("Review already exists for this booking");
        }

        // Verify customer owns the booking
        if (!booking.getCustomer().getId().equals(review.getCustomer().getId())) {
            throw new RuntimeException("You can only review your own bookings");
        }

        // Set provider from booking
        review.setProvider(booking.getProvider());
        review.setBooking(booking);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        // Update provider's rating and review count
        updateProviderRating(booking.getProvider());

        return savedReview;
    }

    public List<Review> getReviewsByProvider(Long providerId) {
        Optional<ServiceProvider> provider = serviceProviderRepository.findById(providerId);
        if (provider.isEmpty()) {
            throw new RuntimeException("Service provider not found");
        }
        return reviewRepository.findByProviderOrderByCreatedAtDesc(provider.get());
    }

    public List<Review> getReviewsByCustomer(User customer) {
        return reviewRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }

    public Optional<Review> getReviewByBooking(Long bookingId) {
        return reviewRepository.findByBookingId(bookingId);
    }

    public boolean hasUserReviewedBooking(Long bookingId) {
        return reviewRepository.existsByBookingId(bookingId);
    }

    public Double getAverageRatingForProvider(Long providerId) {
        Optional<ServiceProvider> provider = serviceProviderRepository.findById(providerId);
        if (provider.isEmpty()) {
            return 0.0;
        }
        Double avgRating = reviewRepository.getAverageRatingByProvider(provider.get());
        return avgRating != null ? avgRating : 0.0;
    }

    public long getReviewCountForProvider(Long providerId) {
        Optional<ServiceProvider> provider = serviceProviderRepository.findById(providerId);
        if (provider.isEmpty()) {
            return 0;
        }
        return reviewRepository.countByProvider(provider.get());
    }

    public List<Review> getRecentReviews() {
        return reviewRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public List<Review> getReviewsByRating(Integer rating) {
        return reviewRepository.findByRatingOrderByCreatedAtDesc(rating);
    }

    public List<Review> getHighRatedReviews(Integer minRating) {
        return reviewRepository.findByRatingGreaterThanEqualOrderByCreatedAtDesc(minRating);
    }

    public Review updateReview(Long reviewId, Review updatedReview, User currentUser) {
        Optional<Review> existingReviewOpt = reviewRepository.findById(reviewId);
        if (existingReviewOpt.isEmpty()) {
            throw new RuntimeException("Review not found");
        }

        Review existingReview = existingReviewOpt.get();

        // Verify user owns the review
        if (!existingReview.getCustomer().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only update your own reviews");
        }

        // Update fields
        existingReview.setRating(updatedReview.getRating());
        existingReview.setReviewText(updatedReview.getReviewText());
        existingReview.setUpdatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(existingReview);

        // Update provider's rating
        updateProviderRating(existingReview.getProvider());

        return savedReview;
    }

    public void deleteReview(Long reviewId, User currentUser) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isEmpty()) {
            throw new RuntimeException("Review not found");
        }

        Review review = reviewOpt.get();

        // Verify user owns the review
        if (!review.getCustomer().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own reviews");
        }

        ServiceProvider provider = review.getProvider();
        reviewRepository.delete(review);

        // Update provider's rating after deletion
        updateProviderRating(provider);
    }

    private void updateProviderRating(ServiceProvider provider) {
        try {
            Double avgRating = reviewRepository.getAverageRatingByProvider(provider);
            long reviewCount = reviewRepository.countByProvider(provider);

            provider.setRating(avgRating != null ? BigDecimal.valueOf(avgRating.doubleValue()) : BigDecimal.ZERO);
            provider.setTotalReviews((int) reviewCount);

            serviceProviderRepository.save(provider);
        } catch (Exception e) {
            // Log error but don't fail the review operation
            System.err.println("Error updating provider rating: " + e.getMessage());
        }
    }
}
