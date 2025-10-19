package org.example.apcproject3.repository;

import org.example.apcproject3.entity.AvailabilitySlot;
import org.example.apcproject3.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {

    List<AvailabilitySlot> findByProvider(ServiceProvider provider);

    List<AvailabilitySlot> findByProviderAndAvailableTrue(ServiceProvider provider);

    @Query("SELECT as FROM AvailabilitySlot as WHERE as.provider = :provider AND as.startTime >= :startTime AND as.endTime <= :endTime AND as.available = true")
    List<AvailabilitySlot> findAvailableSlotsByProviderAndTimeRange(
        @Param("provider") ServiceProvider provider,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT as FROM AvailabilitySlot as WHERE as.provider.id = :providerId AND as.startTime >= :date AND as.available = true ORDER BY as.startTime")
    List<AvailabilitySlot> findAvailableSlotsByProviderIdAndDate(
        @Param("providerId") Long providerId,
        @Param("date") LocalDateTime date
    );

    @Query("SELECT as FROM AvailabilitySlot as WHERE as.startTime BETWEEN :startTime AND :endTime AND as.available = true")
    List<AvailabilitySlot> findAvailableSlotsBetweenTimes(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
}
