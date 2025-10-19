package org.example.apcproject3.document;

// Temporarily disabled - MongoDB dependency removed from pom.xml
/*
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "service_analytics")
public class ServiceAnalytics {

    @Id
    private String id;

    @Field("provider_id")
    private Long providerId;

    @Field("provider_name")
    private String providerName;

    @Field("category")
    private String category;

    @Field("total_bookings")
    private Integer totalBookings = 0;

    @Field("total_revenue")
    private Double totalRevenue = 0.0;

    @Field("average_rating")
    private Double averageRating = 0.0;

    @Field("monthly_stats")
    private Map<String, Integer> monthlyStats;

    @Field("popular_services")
    private List<String> popularServices;

    @Field("customer_feedback")
    private List<CustomerFeedback> customerFeedback;

    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Field("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Nested class for customer feedback
    public static class CustomerFeedback {
        private Long customerId;
        private String customerName;
        private Integer rating;
        private String comment;
        private LocalDateTime feedbackDate;

        // Constructors
        public CustomerFeedback() {}

        public CustomerFeedback(Long customerId, String customerName, Integer rating, String comment) {
            this.customerId = customerId;
            this.customerName = customerName;
            this.rating = rating;
            this.comment = comment;
            this.feedbackDate = LocalDateTime.now();
        }

        // Getters and Setters
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }

        public LocalDateTime getFeedbackDate() { return feedbackDate; }
        public void setFeedbackDate(LocalDateTime feedbackDate) { this.feedbackDate = feedbackDate; }
    }

    // Constructors
    public ServiceAnalytics() {}

    public ServiceAnalytics(Long providerId, String providerName, String category) {
        this.providerId = providerId;
        this.providerName = providerName;
        this.category = category;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getTotalBookings() { return totalBookings; }
    public void setTotalBookings(Integer totalBookings) { this.totalBookings = totalBookings; }

    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Map<String, Integer> getMonthlyStats() { return monthlyStats; }
    public void setMonthlyStats(Map<String, Integer> monthlyStats) { this.monthlyStats = monthlyStats; }

    public List<String> getPopularServices() { return popularServices; }
    public void setPopularServices(List<String> popularServices) { this.popularServices = popularServices; }

    public List<CustomerFeedback> getCustomerFeedback() { return customerFeedback; }
    public void setCustomerFeedback(List<CustomerFeedback> customerFeedback) { this.customerFeedback = customerFeedback; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
*/
