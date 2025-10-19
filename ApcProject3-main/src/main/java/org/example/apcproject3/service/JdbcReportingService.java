package org.example.apcproject3.service;

// Temporarily disabled - depends on MySQL ReportingJdbcRepository
/*
import org.example.apcproject3.repository.ReportingJdbcRepository;
import org.example.apcproject3.repository.ReportingJdbcRepository.ProviderPerformanceDto;
import org.example.apcproject3.repository.ReportingJdbcRepository.BookingMetricsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class JdbcReportingService {

    @Autowired
    private ReportingJdbcRepository jdbcRepository;

    public void initializeDatabase() {
        jdbcRepository.initializeTables();
        insertSampleData();
    }

    // Provider Performance Management
    public void updateProviderPerformance(Long providerId, String providerName,
                                        double totalEarnings, int completedJobs,
                                        double avgCompletionTime, double satisfactionScore) {
        jdbcRepository.insertProviderPerformance(providerId, providerName, totalEarnings,
                                                completedJobs, avgCompletionTime, satisfactionScore);
    }

    public List<ProviderPerformanceDto> getTopPerformers(int limit) {
        return jdbcRepository.getTopPerformingProviders(limit);
    }

    public List<ProviderPerformanceDto> getHighEarningProviders(double minEarnings) {
        return jdbcRepository.getProvidersByEarnings(minEarnings);
    }

    // Booking Metrics Management
    public void updateDailyMetrics(String date, String category, int bookings,
                                 double revenue, double avgValue, double cancellationRate) {
        jdbcRepository.insertBookingMetrics(date, category, bookings, revenue, avgValue, cancellationRate);
    }

    public List<BookingMetricsDto> getMetricsForDateRange(String startDate, String endDate) {
        return jdbcRepository.getDailyBookingMetrics(startDate, endDate);
    }

    public List<Map<String, Object>> getCategoryRevenueSummary() {
        return jdbcRepository.getCategoryRevenueSummary();
    }

    public List<Map<String, Object>> getProviderComparison() {
        return jdbcRepository.getProviderComparisonAnalytics();
    }

    // Sample data insertion for demonstration
    private void insertSampleData() {
        try {
            // Insert sample provider performance data
            updateProviderPerformance(1L, "John Smith", 2500.0, 25, 2.5, 4.8);
            updateProviderPerformance(2L, "Mike Johnson", 3200.0, 32, 2.1, 4.9);
            updateProviderPerformance(3L, "Sarah Williams", 1800.0, 22, 2.8, 4.7);
            updateProviderPerformance(4L, "David Brown", 2100.0, 28, 2.3, 4.6);
            updateProviderPerformance(5L, "Lisa Davis", 2800.0, 30, 2.0, 4.8);

            // Insert sample booking metrics
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (int i = 0; i < 30; i++) {
                LocalDate date = today.minusDays(i);
                String dateStr = date.format(formatter);

                // Sample data for different categories
                updateDailyMetrics(dateStr, "Plumbing", (int)(Math.random() * 10) + 5,
                                 (Math.random() * 1000) + 500, (Math.random() * 100) + 50,
                                 Math.random() * 0.1);

                updateDailyMetrics(dateStr, "Electrical", (int)(Math.random() * 8) + 3,
                                 (Math.random() * 1200) + 600, (Math.random() * 120) + 60,
                                 Math.random() * 0.08);

                updateDailyMetrics(dateStr, "Cleaning", (int)(Math.random() * 15) + 8,
                                 (Math.random() * 800) + 300, (Math.random() * 80) + 30,
                                 Math.random() * 0.05);
            }

            System.out.println("JDBC sample data inserted successfully");
        } catch (Exception e) {
            System.err.println("Error inserting sample JDBC data: " + e.getMessage());
        }
    }
}
*/
