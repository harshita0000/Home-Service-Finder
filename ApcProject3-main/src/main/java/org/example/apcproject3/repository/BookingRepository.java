package org.example.apcproject3.repository;

import org.example.apcproject3.entity.Booking;
import org.example.apcproject3.entity.BookingStatus;
import org.example.apcproject3.entity.ServiceProvider;
import org.example.apcproject3.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomer(User customer);

    List<Booking> findByProvider(ServiceProvider provider);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByCustomerAndStatus(User customer, BookingStatus status);

    List<Booking> findByProviderAndStatus(ServiceProvider provider, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.customer = :customer ORDER BY b.createdAt DESC")
    List<Booking> findByCustomerOrderByCreatedAtDesc(@Param("customer") User customer);

    @Query("SELECT b FROM Booking b WHERE b.provider = :provider ORDER BY b.startTime ASC")
    List<Booking> findByProviderOrderByStartTimeAsc(@Param("provider") ServiceProvider provider);

    @Query("SELECT b FROM Booking b WHERE b.startTime >= :startTime AND b.endTime <= :endTime")
    List<Booking> findBookingsBetweenTimes(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT b FROM Booking b WHERE b.provider = :provider AND b.startTime >= :startTime AND b.endTime <= :endTime AND b.status != 'CANCELLED'")
    List<Booking> findProviderBookingsInTimeRange(
        @Param("provider") ServiceProvider provider,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.provider = :provider AND b.status = 'COMPLETED'")
    Long countCompletedBookingsByProvider(@Param("provider") ServiceProvider provider);

    // Add missing methods for BookingService
    List<Booking> findByCustomerAndStatusIn(User customer, List<BookingStatus> statuses);

    @Query("SELECT b FROM Booking b WHERE b.provider = :provider ORDER BY b.createdAt DESC")
    List<Booking> findTop5ByProviderOrderByCreatedAtDesc(@Param("provider") ServiceProvider provider);
}
