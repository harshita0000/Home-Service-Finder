package org.example.apcproject3.service;

// Temporarily disabled - MongoDB dependency removed from pom.xml
/*
import org.example.apcproject3.document.ServiceAnalytics;
import org.example.apcproject3.document.ServiceLog;
import org.example.apcproject3.repository.ServiceAnalyticsRepository;
import org.example.apcproject3.repository.ServiceLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MongoAnalyticsService {

    @Autowired
    private ServiceAnalyticsRepository analyticsRepository;

    @Autowired
    private ServiceLogRepository logRepository;

    // Service Analytics Operations
    public ServiceAnalytics createOrUpdateProviderAnalytics(Long providerId, String providerName, String category) {
        Optional<ServiceAnalytics> existing = analyticsRepository.findByProviderId(providerId);

        ServiceAnalytics analytics;
        if (existing.isPresent()) {
            analytics = existing.get();
            analytics.setUpdatedAt(LocalDateTime.now());
        } else {
            analytics = new ServiceAnalytics(providerId, providerName, category);
        }

        return analyticsRepository.save(analytics);
    }

    public void updateProviderBookingStats(Long providerId, String providerName, String category,
                                         double bookingAmount, double rating) {
        Optional<ServiceAnalytics> analyticsOpt = analyticsRepository.findByProviderId(providerId);

        ServiceAnalytics analytics = analyticsOpt.orElse(new ServiceAnalytics(providerId, providerName, category));

        // Update statistics
        analytics.setTotalBookings(analytics.getTotalBookings() + 1);
        analytics.setTotalRevenue(analytics.getTotalRevenue() + bookingAmount);

        // Calculate new average rating
        double currentAvg = analytics.getAverageRating();
        int totalBookings = analytics.getTotalBookings();
        double newAvg = ((currentAvg * (totalBookings - 1)) + rating) / totalBookings;
        analytics.setAverageRating(newAvg);

        analytics.setUpdatedAt(LocalDateTime.now());
        analyticsRepository.save(analytics);
    }

    public List<ServiceAnalytics> getTopProvidersByRevenue() {
        return analyticsRepository.findTop10ByOrderByTotalRevenueDesc();
    }

    public List<ServiceAnalytics> getTopProvidersByRating() {
        return analyticsRepository.findTop10ByOrderByAverageRatingDesc();
    }

    public List<ServiceAnalytics> getProvidersByCategory(String category) {
        return analyticsRepository.findByCategory(category);
    }

    public List<ServiceAnalytics> getHighPerformingProviders(double minRating, int minBookings) {
        return analyticsRepository.findByAverageRatingGreaterThan(minRating);
    }

    // Service Logging Operations
    public ServiceLog logServiceAction(Long bookingId, Long providerId, Long customerId,
                                     String action, String description) {
        ServiceLog log = new ServiceLog(bookingId, providerId, customerId, action, description);
        return logRepository.save(log);
    }

    public ServiceLog logServiceActionWithMetadata(Long bookingId, Long providerId, Long customerId,
                                                 String action, String description,
                                                 Map<String, Object> metadata,
                                                 String ipAddress, String userAgent) {
        ServiceLog log = new ServiceLog(bookingId, providerId, customerId, action, description);
        log.setMetadata(metadata);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        return logRepository.save(log);
    }

    public List<ServiceLog> getServiceLogsByProvider(Long providerId) {
        return logRepository.findByProviderId(providerId);
    }

    public List<ServiceLog> getServiceLogsByBooking(Long bookingId) {
        return logRepository.findByBookingId(bookingId);
    }

    public List<ServiceLog> getServiceLogsByAction(String action) {
        return logRepository.findByAction(action);
    }

    public List<ServiceLog> getRecentServiceLogs() {
        return logRepository.findTop20ByOrderByTimestampDesc();
    }

    public List<ServiceLog> getServiceLogsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return logRepository.findByTimestampBetween(start, end);
    }

    // Analytics aggregation methods
    public Map<String, Object> getProviderAnalyticsSummary(Long providerId) {
        Optional<ServiceAnalytics> analyticsOpt = analyticsRepository.findByProviderId(providerId);
        Map<String, Object> summary = new HashMap<>();

        if (analyticsOpt.isPresent()) {
            ServiceAnalytics analytics = analyticsOpt.get();
            summary.put("totalBookings", analytics.getTotalBookings());
            summary.put("totalRevenue", analytics.getTotalRevenue());
            summary.put("averageRating", analytics.getAverageRating());
            summary.put("monthlyStats", analytics.getMonthlyStats());
            summary.put("popularServices", analytics.getPopularServices());
        } else {
            summary.put("totalBookings", 0);
            summary.put("totalRevenue", 0.0);
            summary.put("averageRating", 0.0);
            summary.put("message", "No analytics data available");
        }

        return summary;
    }

    public Map<String, Object> getCategoryAnalyticsSummary(String category) {
        List<ServiceAnalytics> categoryAnalytics = analyticsRepository.findByCategory(category);

        Map<String, Object> summary = new HashMap<>();
        int totalProviders = categoryAnalytics.size();
        int totalBookings = categoryAnalytics.stream().mapToInt(ServiceAnalytics::getTotalBookings).sum();
        double totalRevenue = categoryAnalytics.stream().mapToDouble(ServiceAnalytics::getTotalRevenue).sum();
        double avgRating = categoryAnalytics.stream().mapToDouble(ServiceAnalytics::getAverageRating).average().orElse(0.0);

        summary.put("category", category);
        summary.put("totalProviders", totalProviders);
        summary.put("totalBookings", totalBookings);
        summary.put("totalRevenue", totalRevenue);
        summary.put("averageRating", avgRating);

        return summary;
    }
}
*/
