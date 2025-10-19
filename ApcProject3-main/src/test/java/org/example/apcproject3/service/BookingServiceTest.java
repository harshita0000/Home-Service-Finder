package org.example.apcproject3.service;

import org.example.apcproject3.entity.*;
import org.example.apcproject3.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AvailabilitySlotService availabilitySlotService;

    @Mock
    private ServiceProviderService serviceProviderService;

    @InjectMocks
    private BookingService bookingService;

    private Booking testBooking;
    private User customer;
    private ServiceProvider provider;

    @BeforeEach
    void setUp() {
        customer = new User();
        customer.setId(1L);
        customer.setUsername("customer");
        customer.setRole(UserRole.CUSTOMER);

        User providerUser = new User();
        providerUser.setId(2L);
        providerUser.setUsername("provider");
        providerUser.setRole(UserRole.SERVICE_PROVIDER);

        provider = new ServiceProvider();
        provider.setId(1L);
        provider.setUser(providerUser);
        provider.setHourlyRate(new BigDecimal("50.00"));

        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setCustomer(customer);
        testBooking.setProvider(provider);
        testBooking.setStartTime(LocalDateTime.now().plusHours(1));
        testBooking.setEndTime(LocalDateTime.now().plusHours(3));
        testBooking.setDescription("Test booking");
        testBooking.setStatus(BookingStatus.PENDING);
    }

    @Test
    void createBooking_Success() {
        // Given
        when(bookingRepository.findProviderBookingsInTimeRange(any(), any(), any()))
            .thenReturn(Arrays.asList());
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(availabilitySlotService.findAvailableSlotsByProviderAndDate(any(), any()))
            .thenReturn(Arrays.asList());

        // When
        Booking result = bookingService.createBooking(testBooking);

        // Then
        assertNotNull(result);
        assertEquals(BookingStatus.PENDING, result.getStatus());
        assertEquals(new BigDecimal("100.00"), result.getTotalAmount());
        verify(bookingRepository).save(testBooking);
    }

    @Test
    void createBooking_ConflictingBookings_ThrowsException() {
        // Given
        List<Booking> conflictingBookings = Arrays.asList(testBooking);
        when(bookingRepository.findProviderBookingsInTimeRange(any(), any(), any()))
            .thenReturn(conflictingBookings);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> bookingService.createBooking(testBooking));
        assertEquals("Provider is not available during the requested time slot",
            exception.getMessage());

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void confirmBooking_Success() {
        // Given
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // When
        Booking result = bookingService.confirmBooking(1L);

        // Then
        assertNotNull(result);
        assertEquals(BookingStatus.CONFIRMED, result.getStatus());
        verify(bookingRepository).save(testBooking);
    }

    @Test
    void confirmBooking_NotPending_ThrowsException() {
        // Given
        testBooking.setStatus(BookingStatus.CONFIRMED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> bookingService.confirmBooking(1L));
        assertEquals("Only pending bookings can be confirmed", exception.getMessage());

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void cancelBooking_Success() {
        // Given
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(availabilitySlotService.findByProvider(any())).thenReturn(Arrays.asList());

        // When
        Booking result = bookingService.cancelBooking(1L, "Customer request");

        // Then
        assertNotNull(result);
        assertEquals(BookingStatus.CANCELLED, result.getStatus());
        assertTrue(result.getNotes().contains("Customer request"));
        verify(bookingRepository).save(testBooking);
    }

    @Test
    void findByCustomer_Success() {
        // Given
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findByCustomer(any(User.class))).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.findByCustomer(customer);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBooking.getId(), result.get(0).getId());
        verify(bookingRepository).findByCustomer(customer);
    }
}
