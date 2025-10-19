package org.example.apcproject3.service;

import org.example.apcproject3.entity.*;
import org.example.apcproject3.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private AvailabilitySlotService availabilitySlotService;

    @Autowired
    private ServiceProviderService serviceProviderService;

    // Temporarily disabled - depends on MultiDatabaseService
    // @Autowired
    // private MultiDatabaseService multiDatabaseService;

    public Booking createBooking(Booking booking) {
        // Validate booking times
        if (booking.getEndTime().isBefore(booking.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        // Check if provider is available during the requested time
        List<Booking> conflictingBookings = bookingRepository.findProviderBookingsInTimeRange(
            booking.getProvider(),
            booking.getStartTime(),
            booking.getEndTime()
        );

        if (!conflictingBookings.isEmpty()) {
            throw new RuntimeException("Provider is not available during the requested time slot");
        }

        // Calculate total amount if not provided
        if (booking.getTotalAmount() == null) {
            BigDecimal duration = calculateDurationInHours(booking.getStartTime(), booking.getEndTime());
            booking.setTotalAmount(duration.multiply(booking.getProvider().getHourlyRate()));
        }

        // Mark corresponding availability slot as unavailable
        markAvailabilitySlotAsUnavailable(booking);

        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        // Sync to MongoDB and JDBC - temporarily disabled
        // multiDatabaseService.syncBookingAcrossAllDatabases(savedBooking, "BOOKING_CREATED");

        return savedBooking;
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Booking> findByCustomer(User customer) {
        return bookingRepository.findByCustomer(customer);
    }

    @Transactional(readOnly = true)
    public List<Booking> findByProvider(ServiceProvider provider) {
        return bookingRepository.findByProvider(provider);
    }

    @Transactional(readOnly = true)
    public List<Booking> findByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Booking> findCustomerBookingsOrderedByDate(User customer) {
        return bookingRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }

    @Transactional(readOnly = true)
    public List<Booking> findProviderBookingsOrderedByTime(ServiceProvider provider) {
        return bookingRepository.findByProviderOrderByStartTimeAsc(provider);
    }

    public Booking confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Only pending bookings can be confirmed");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setUpdatedAt(LocalDateTime.now());
        Booking savedBooking = bookingRepository.save(booking);

        // Sync to MongoDB and JDBC
        // multiDatabaseService.syncBookingAcrossAllDatabases(savedBooking, "BOOKING_CONFIRMED");

        return savedBooking;
    }

    public Booking startService(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Only confirmed bookings can be started");
        }

        booking.setStatus(BookingStatus.IN_PROGRESS);
        booking.setUpdatedAt(LocalDateTime.now());
        Booking savedBooking = bookingRepository.save(booking);

        // Sync to MongoDB and JDBC
        // multiDatabaseService.syncBookingAcrossAllDatabases(savedBooking, "SERVICE_STARTED");

        return savedBooking;
    }

    public Booking completeService(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.IN_PROGRESS) {
            throw new RuntimeException("Only in-progress bookings can be completed");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        booking.setUpdatedAt(LocalDateTime.now());
        Booking savedBooking = bookingRepository.save(booking);

        // Sync to MongoDB and JDBC
        // multiDatabaseService.syncBookingAcrossAllDatabases(savedBooking, "BOOKING_COMPLETED");

        return savedBooking;
    }

    public Booking cancelBooking(Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new RuntimeException("Completed bookings cannot be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setNotes(booking.getNotes() + "\nCancellation reason: " + reason);

        // Make the availability slot available again
        makeAvailabilitySlotAvailable(booking);

        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Booking booking) {
        Booking existingBooking = bookingRepository.findById(booking.getId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Only allow updates for pending bookings
        if (existingBooking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Only pending bookings can be updated");
        }

        existingBooking.setDescription(booking.getDescription());
        existingBooking.setAddress(booking.getAddress());
        existingBooking.setNotes(booking.getNotes());

        return bookingRepository.save(existingBooking);
    }

    @Transactional(readOnly = true)
    public List<Booking> findBookingsBetweenTimes(LocalDateTime startTime, LocalDateTime endTime) {
        return bookingRepository.findBookingsBetweenTimes(startTime, endTime);
    }

    @Transactional(readOnly = true)
    public Long getCompletedBookingsCount(ServiceProvider provider) {
        return bookingRepository.countCompletedBookingsByProvider(provider);
    }

    @Transactional(readOnly = true)
    public CompletableFuture<List<Booking>> findBookingsAsync() {
        return CompletableFuture.supplyAsync(() -> bookingRepository.findAll());
    }

    // Add missing methods for BookingController
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByCustomer(User customer) {
        return bookingRepository.findByCustomer(customer);
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsByCustomerAndStatus(User customer, String[] statuses) {
        List<BookingStatus> bookingStatuses = Arrays.stream(statuses)
                .map(status -> BookingStatus.valueOf(status.toUpperCase()))
                .collect(Collectors.toList());
        return bookingRepository.findByCustomerAndStatusIn(customer, bookingStatuses);
    }

    @Transactional(readOnly = true)
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsByProvider(User user) {
        ServiceProvider provider = serviceProviderService.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Service provider not found for user"));
        return bookingRepository.findByProvider(provider);
    }

    @Transactional(readOnly = true)
    public List<Booking> getRecentBookingsByProvider(User user, int limit) {
        ServiceProvider provider = serviceProviderService.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Service provider not found for user"));
        return bookingRepository.findTop5ByProviderOrderByCreatedAtDesc(provider);
    }

    public Booking updateBookingStatus(Long bookingId, BookingStatus status, User user) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Verify the user is the service provider for this booking
        if (!booking.getProvider().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only update bookings for your services");
        }

        booking.setStatus(status);
        booking.setUpdatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(Long bookingId, User currentUser) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Verify the user is the customer for this booking
        if (!booking.getCustomer().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only cancel your own bookings");
        }

        // Only allow cancellation of pending or confirmed bookings
        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Cannot cancel booking with status: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());

        // Make availability slot available again
        restoreAvailabilitySlot(booking);

        return bookingRepository.save(booking);
    }

    private void restoreAvailabilitySlot(Booking booking) {
        try {
            // Find the availability slot that corresponds to this booking and mark it as available
            List<AvailabilitySlot> slots = availabilitySlotService.findByProvider(booking.getProvider());
            for (AvailabilitySlot slot : slots) {
                if (slot.getStartTime().equals(booking.getStartTime()) &&
                    slot.getEndTime().equals(booking.getEndTime()) &&
                    !slot.isAvailable()) {
                    availabilitySlotService.markSlotAsAvailable(slot.getId());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error restoring availability slot: " + e.getMessage());
        }
    }

    private BigDecimal calculateDurationInHours(LocalDateTime startTime, LocalDateTime endTime) {
        long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
        return BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP);
    }

    private void markAvailabilitySlotAsUnavailable(Booking booking) {
        // Find and mark the corresponding availability slot as unavailable
        // This is a simplified implementation
        List<AvailabilitySlot> slots = availabilitySlotService.findAvailableSlotsByProviderAndDate(
            booking.getProvider().getId(),
            booking.getStartTime()
        );

        for (AvailabilitySlot slot : slots) {
            if (slot.getStartTime().equals(booking.getStartTime()) &&
                slot.getEndTime().equals(booking.getEndTime())) {
                availabilitySlotService.markSlotAsUnavailable(slot.getId());
                break;
            }
        }
    }

    private void makeAvailabilitySlotAvailable(Booking booking) {
        // Find and mark the corresponding availability slot as available again
        List<AvailabilitySlot> slots = availabilitySlotService.findByProvider(booking.getProvider());

        for (AvailabilitySlot slot : slots) {
            if (slot.getStartTime().equals(booking.getStartTime()) &&
                slot.getEndTime().equals(booking.getEndTime())) {
                availabilitySlotService.markSlotAsAvailable(slot.getId());
                break;
            }
        }
    }
}
