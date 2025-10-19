package org.example.apcproject3.service;

// Temporarily disabled - depends on JdbcReportingService which requires MySQL
/*
import org.example.apcproject3.document.ServiceAnalytics;
import org.example.apcproject3.document.ServiceLog;
import org.example.apcproject3.entity.Booking;
import org.example.apcproject3.entity.ServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class MultiDatabaseService implements CommandLineRunner {

    @Autowired
    private MongoAnalyticsService mongoAnalyticsService;

    @Autowired
    private JdbcReportingService jdbcReportingService;

    @Override
    public void run(String... args) throws Exception {
        // Initialize JDBC tables and sample data
        try {
            jdbcReportingService.initializeDatabase();
            System.out.println("✅ JDBC MySQL database initialized with sample data");
        } catch (Exception e) {
            System.err.println("❌ JDBC initialization error: " + e.getMessage());
        }

        // Initialize MongoDB with sample analytics data
        try {
            initializeMongoData();
            System.out.println("✅ MongoDB initialized with sample analytics data");
        } catch (Exception e) {
            System.err.println("❌ MongoDB initialization error: " + e.getMessage());
        }
    }

    private void initializeMongoData() {
        // Create sample analytics for different providers
        mongoAnalyticsService.createOrUpdateProviderAnalytics(1L, "John Smith", "Plumbing");
        mongoAnalyticsService.createOrUpdateProviderAnalytics(2L, "Mike Johnson", "Plumbing");
        mongoAnalyticsService.createOrUpdateProviderAnalytics(3L, "Alex Miller", "Electrical");
        mongoAnalyticsService.createOrUpdateProviderAnalytics(4L, "Emma Taylor", "Electrical");
        mongoAnalyticsService.createOrUpdateProviderAnalytics(5L, "Anna Thompson", "Cleaning");

        // Simulate some booking activities
        simulateBookingActivities();
    }

    private void simulateBookingActivities() {
        // Simulate various service activities for realistic data
        mongoAnalyticsService.updateProviderBookingStats(1L, "John Smith", "Plumbing", 125.0, 4.8);
        mongoAnalyticsService.updateProviderBookingStats(1L, "John Smith", "Plumbing", 200.0, 4.7);
        mongoAnalyticsService.updateProviderBookingStats(1L, "John Smith", "Plumbing", 175.0, 4.9);

        mongoAnalyticsService.updateProviderBookingStats(2L, "Mike Johnson", "Plumbing", 275.0, 4.9);
        mongoAnalyticsService.updateProviderBookingStats(2L, "Mike Johnson", "Plumbing", 220.0, 4.8);

        mongoAnalyticsService.updateProviderBookingStats(3L, "Alex Miller", "Electrical", 300.0, 4.8);
        mongoAnalyticsService.updateProviderBookingStats(3L, "Alex Miller", "Electrical", 360.0, 4.9);

        mongoAnalyticsService.updateProviderBookingStats(4L, "Emma Taylor", "Electrical", 330.0, 4.9);
        mongoAnalyticsService.updateProviderBookingStats(5L, "Anna Thompson", "Cleaning", 120.0, 4.7);

        // Log some service actions
        logSampleServiceActions();
    }

    private void logSampleServiceActions() {
        // Log various service actions for the past few days
        LocalDateTime now = LocalDateTime.now();

        Map<String, Object> metadata1 = new HashMap<>();
        metadata1.put("service_type", "emergency_repair");
        metadata1.put("urgency", "high");

        mongoAnalyticsService.logServiceActionWithMetadata(
            1L, 1L, 101L, "BOOKING_CREATED",
            "Emergency plumbing service requested",
            metadata1, "192.168.1.100", "Mozilla/5.0 Chrome"
        );

        mongoAnalyticsService.logServiceActionWithMetadata(
            1L, 1L, 101L, "SERVICE_STARTED",
            "Plumber arrived and started work",
            new HashMap<>(), "192.168.1.100", "Mozilla/5.0 Chrome"
        );

        mongoAnalyticsService.logServiceActionWithMetadata(
            1L, 1L, 101L, "SERVICE_COMPLETED",
            "Plumbing work completed successfully",
            new HashMap<>(), "192.168.1.100", "Mozilla/5.0 Chrome"
        );

        Map<String, Object> metadata2 = new HashMap<>();
        metadata2.put("service_type", "installation");
        metadata2.put("equipment", "smart_thermostat");

        mongoAnalyticsService.logServiceActionWithMetadata(
            2L, 3L, 102L, "BOOKING_CREATED",
            "Smart thermostat installation requested",
            metadata2, "192.168.1.105", "Safari"
        );
    }

    // Method to sync data between JPA, MongoDB, and JDBC when bookings are created/updated
    public void syncBookingAcrossAllDatabases(Booking booking, String action) {
        try {
            // 1. Update MongoDB analytics
            if (booking.getProvider() != null && booking.getCustomer() != null) {
                String providerName = booking.getProvider().getUser().getFirstName() + " " +
                                    booking.getProvider().getUser().getLastName();
                String category = booking.getProvider().getCategory().getName();

                // Log action in MongoDB
                mongoAnalyticsService.logServiceAction(
                    booking.getId(),
                    booking.getProvider().getId(),
                    booking.getCustomer().getId(),
                    action,
                    "Booking " + action.toLowerCase() + " for " + category + " service"
                );

                // Update analytics if booking is completed
                if ("BOOKING_COMPLETED".equals(action) && booking.getTotalAmount() != null) {
                    mongoAnalyticsService.updateProviderBookingStats(
                        booking.getProvider().getId(),
                        providerName,
                        category,
                        booking.getTotalAmount().doubleValue(),
                        4.5 // Default rating, would come from actual review
                    );
                }
            }

            // 2. Update JDBC performance metrics
            if ("BOOKING_COMPLETED".equals(action) && booking.getProvider() != null) {
                ServiceProvider provider = booking.getProvider();
                String providerName = provider.getUser().getFirstName() + " " +
                                    provider.getUser().getLastName();

                // Calculate completion time (simplified)
                double completionTime = 2.0; // Would calculate actual time
                double satisfactionScore = 4.5; // Would come from reviews

                jdbcReportingService.updateProviderPerformance(
                    provider.getId(),
                    providerName,
                    booking.getTotalAmount() != null ? booking.getTotalAmount().doubleValue() : 0.0,
                    1, // Increment completed jobs
                    completionTime,
                    satisfactionScore
                );
            }

            System.out.println("✅ Booking data synced across all databases: " + action);
        } catch (Exception e) {
            System.err.println("❌ Error syncing booking data: " + e.getMessage());
        }
    }

    // Comprehensive analytics method combining all data sources
    public Map<String, Object> getComprehensiveAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        try {
            // MongoDB analytics
            analytics.put("mongoTopProvidersByRevenue", mongoAnalyticsService.getTopProvidersByRevenue());
            analytics.put("mongoTopProvidersByRating", mongoAnalyticsService.getTopProvidersByRating());
            analytics.put("mongoRecentLogs", mongoAnalyticsService.getRecentServiceLogs());

            // JDBC analytics
            analytics.put("jdbcTopPerformers", jdbcReportingService.getTopPerformers(10));
            analytics.put("jdbcRevenueSummary", jdbcReportingService.getCategoryRevenueSummary());
            analytics.put("jdbcProviderComparison", jdbcReportingService.getProviderComparison());

            analytics.put("status", "success");
            analytics.put("dataSources", "JPA (H2) + MongoDB + JDBC (MySQL)");
            analytics.put("lastUpdated", LocalDateTime.now());

        } catch (Exception e) {
            analytics.put("status", "error");
            analytics.put("error", e.getMessage());
        }

        return analytics;
    }
}
*/
