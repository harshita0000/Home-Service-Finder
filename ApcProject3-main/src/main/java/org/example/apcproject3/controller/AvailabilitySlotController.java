package org.example.apcproject3.controller;

import org.example.apcproject3.entity.AvailabilitySlot;
import org.example.apcproject3.entity.ServiceProvider;
import org.example.apcproject3.entity.User;
import org.example.apcproject3.service.AvailabilitySlotService;
import org.example.apcproject3.service.ServiceProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/api/availability")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AvailabilitySlotController {

    @Autowired
    private AvailabilitySlotService availabilitySlotService;

    @Autowired
    private ServiceProviderService serviceProviderService;

    // Public endpoints
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<?> getProviderAvailability(@PathVariable Long providerId) {
        try {
            Optional<ServiceProvider> providerOpt = serviceProviderService.findById(providerId);
            if (!providerOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Service provider not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            List<AvailabilitySlot> slots = availabilitySlotService.findAvailableSlotsByProvider(providerOpt.get());
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch availability: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/provider/{providerId}/date")
    public ResponseEntity<?> getProviderAvailabilityByDate(
            @PathVariable Long providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        try {
            List<AvailabilitySlot> slots = availabilitySlotService.findAvailableSlotsByProviderAndDate(providerId, date);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch availability: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Provider endpoints
    @PostMapping
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<?> createAvailabilitySlot(@Valid @RequestBody AvailabilitySlot slot, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Optional<ServiceProvider> providerOpt = serviceProviderService.findByUser(currentUser);

            if (!providerOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Service provider profile not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            slot.setProvider(providerOpt.get());
            AvailabilitySlot savedSlot = availabilitySlotService.createSlot(slot);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSlot);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create availability slot: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<?> createMultipleSlots(@Valid @RequestBody List<AvailabilitySlot> slots, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Optional<ServiceProvider> providerOpt = serviceProviderService.findByUser(currentUser);

            if (!providerOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Service provider profile not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            ServiceProvider provider = providerOpt.get();
            slots.forEach(slot -> slot.setProvider(provider));

            List<AvailabilitySlot> savedSlots = availabilitySlotService.createMultipleSlots(slots);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSlots);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create availability slots: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<?> getMyAvailabilitySlots(Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Optional<ServiceProvider> providerOpt = serviceProviderService.findByUser(currentUser);

            if (!providerOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Service provider profile not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            List<AvailabilitySlot> slots = availabilitySlotService.findByProvider(providerOpt.get());
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch availability slots: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<?> toggleSlotAvailability(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Optional<AvailabilitySlot> slotOpt = availabilitySlotService.findById(id);

            if (!slotOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Availability slot not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            AvailabilitySlot slot = slotOpt.get();
            if (!slot.getProvider().getUser().getId().equals(currentUser.getId())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Access denied");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            AvailabilitySlot updatedSlot;
            if (slot.isAvailable()) {
                updatedSlot = availabilitySlotService.markSlotAsUnavailable(id);
            } else {
                updatedSlot = availabilitySlotService.markSlotAsAvailable(id);
            }

            return ResponseEntity.ok(updatedSlot);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to toggle slot availability: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<?> deleteAvailabilitySlot(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Optional<AvailabilitySlot> slotOpt = availabilitySlotService.findById(id);

            if (!slotOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Availability slot not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            AvailabilitySlot slot = slotOpt.get();
            if (!slot.getProvider().getUser().getId().equals(currentUser.getId())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Access denied");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            availabilitySlotService.deleteSlot(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Availability slot deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete availability slot: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
