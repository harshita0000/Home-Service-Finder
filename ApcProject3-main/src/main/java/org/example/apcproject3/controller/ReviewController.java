package org.example.apcproject3.controller;

import org.example.apcproject3.entity.Review;
import org.example.apcproject3.entity.User;
import org.example.apcproject3.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Create a new review (customers only)
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createReview(@Valid @RequestBody Review review, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            review.setCustomer(currentUser);

            Review savedReview = reviewService.createReview(review);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedReview);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create review: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get reviews for a specific provider (public endpoint)
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<?> getReviewsByProvider(@PathVariable Long providerId) {
        try {
            List<Review> reviews = reviewService.getReviewsByProvider(providerId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch reviews: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get reviews by current customer
    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getMyReviews(Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            List<Review> reviews = reviewService.getReviewsByCustomer(currentUser);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch your reviews: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get review for a specific booking
    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<?> getReviewByBooking(@PathVariable Long bookingId) {
        try {
            Optional<Review> review = reviewService.getReviewByBooking(bookingId);
            if (review.isPresent()) {
                return ResponseEntity.ok(review.get());
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("message", "No review found for this booking");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch review: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Check if user has reviewed a booking
    @GetMapping("/booking/{bookingId}/exists")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> hasUserReviewedBooking(@PathVariable Long bookingId) {
        try {
            boolean hasReviewed = reviewService.hasUserReviewedBooking(bookingId);
            Map<String, Boolean> response = new HashMap<>();
            response.put("hasReviewed", hasReviewed);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to check review status: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get provider's average rating and review count
    @GetMapping("/provider/{providerId}/stats")
    public ResponseEntity<?> getProviderStats(@PathVariable Long providerId) {
        try {
            Double avgRating = reviewService.getAverageRatingForProvider(providerId);
            long reviewCount = reviewService.getReviewCountForProvider(providerId);

            Map<String, Object> stats = new HashMap<>();
            stats.put("averageRating", Math.round(avgRating * 10.0) / 10.0); // Round to 1 decimal
            stats.put("totalReviews", reviewCount);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch provider stats: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get recent reviews (for dashboard/homepage)
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentReviews() {
        try {
            List<Review> reviews = reviewService.getRecentReviews();
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch recent reviews: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get reviews by rating
    @GetMapping("/rating/{rating}")
    public ResponseEntity<?> getReviewsByRating(@PathVariable Integer rating) {
        try {
            if (rating < 1 || rating > 5) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Rating must be between 1 and 5");
                return ResponseEntity.badRequest().body(error);
            }

            List<Review> reviews = reviewService.getReviewsByRating(rating);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch reviews by rating: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get high-rated reviews (4+ stars)
    @GetMapping("/high-rated")
    public ResponseEntity<?> getHighRatedReviews(@RequestParam(defaultValue = "4") Integer minRating) {
        try {
            List<Review> reviews = reviewService.getHighRatedReviews(minRating);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch high-rated reviews: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Update a review
    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId,
                                        @Valid @RequestBody Review updatedReview,
                                        Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Review review = reviewService.updateReview(reviewId, updatedReview, currentUser);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update review: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Delete a review
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            reviewService.deleteReview(reviewId, currentUser);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Review deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete review: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
