package org.example.apcproject3.controller;

import org.example.apcproject3.entity.Booking;
import org.example.apcproject3.entity.BookingStatus;
import org.example.apcproject3.entity.ServiceProvider;
import org.example.apcproject3.entity.User;
import org.example.apcproject3.service.BookingService;
import org.example.apcproject3.service.ServiceProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ServiceProviderService serviceProviderService;

    // Customer endpoints
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createBooking(@Valid @RequestBody Booking booking, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            booking.setCustomer(currentUser);

            Booking savedBooking = bookingService.createBooking(booking);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create booking: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getMyBookings(Authentication authentication,
                                         @RequestParam(required = false) String status) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            List<Booking> bookings;

            if (status != null && !status.isEmpty()) {
                // Filter by status
                String[] statuses = status.split(",");
                bookings = bookingService.getBookingsByCustomerAndStatus(currentUser, statuses);
            } else {
                bookings = bookingService.getBookingsByCustomer(currentUser);
            }

            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch bookings: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<?> getBookingById(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Optional<Booking> booking = bookingService.getBookingById(id);

            if (booking.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Booking not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            // Verify user has access to this booking
            Booking bookingEntity = booking.get();
            boolean hasAccess = bookingEntity.getCustomer().getId().equals(currentUser.getId()) ||
                              (currentUser.getRole().name().equals("SERVICE_PROVIDER") &&
                               bookingEntity.getProvider().getUser().getId().equals(currentUser.getId()));

            if (!hasAccess) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Access denied");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            return ResponseEntity.ok(bookingEntity);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch booking: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Provider endpoints
    @GetMapping("/provider/my")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<?> getMyProviderBookings(Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            List<Booking> bookings = bookingService.getBookingsByProvider(currentUser);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch provider bookings: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/provider/recent")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<?> getRecentProviderBookings(Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            List<Booking> bookings = bookingService.getRecentBookingsByProvider(currentUser, 5);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch recent bookings: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Long id,
                                               @RequestParam BookingStatus status,
                                               Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Booking updatedBooking = bookingService.updateBookingStatus(id, status, currentUser);
            return ResponseEntity.ok(updatedBooking);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update booking status: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Booking cancelledBooking = bookingService.cancelBooking(id, currentUser);
            return ResponseEntity.ok(cancelledBooking);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to cancel booking: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Admin endpoints
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.findBookingsAsync().join();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Booking>> getBookingsByStatus(@PathVariable String status) {
        try {
            BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
            List<Booking> bookings = bookingService.findByStatus(bookingStatus);
            return ResponseEntity.ok(bookings);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
