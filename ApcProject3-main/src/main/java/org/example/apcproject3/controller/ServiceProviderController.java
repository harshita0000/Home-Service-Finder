package org.example.apcproject3.controller;

import org.example.apcproject3.entity.ServiceProvider;
import org.example.apcproject3.entity.ServiceCategory;
import org.example.apcproject3.entity.User;
import org.example.apcproject3.service.ServiceProviderService;
import org.example.apcproject3.service.ServiceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/providers")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ServiceProviderController {

    @Autowired
    private ServiceProviderService serviceProviderService;

    @Autowired
    private ServiceCategoryService serviceCategoryService;

    // Public endpoints
    @GetMapping
    public ResponseEntity<List<ServiceProvider>> getAllProviders() {
        List<ServiceProvider> providers = serviceProviderService.findAvailableProviders();
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProviderById(@PathVariable Long id) {
        Optional<ServiceProvider> provider = serviceProviderService.findById(id);

        if (provider.isPresent()) {
            return ResponseEntity.ok(provider.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Service provider not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getProvidersByCategory(@PathVariable Long categoryId) {
        try {
            Optional<ServiceCategory> category = serviceCategoryService.findById(categoryId);
            if (!category.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Service category not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            List<ServiceProvider> providers = serviceProviderService.findAvailableProvidersByCategory(category.get());
            return ResponseEntity.ok(providers);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch providers: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<ServiceProvider>> getTopRatedProviders(@RequestParam(defaultValue = "4.0") BigDecimal minRating) {
        List<ServiceProvider> providers = serviceProviderService.findTopRatedProviders(minRating);
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<ServiceProvider>> getProvidersByPriceRange(
            @RequestParam BigDecimal minRate,
            @RequestParam BigDecimal maxRate) {
        List<ServiceProvider> providers = serviceProviderService.findProvidersByPriceRange(minRate, maxRate);
        return ResponseEntity.ok(providers);
    }

    // Provider-specific endpoints
    @PostMapping("/profile")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<?> createProviderProfile(@Valid @RequestBody ServiceProvider provider, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            provider.setUser(currentUser);

            ServiceProvider savedProvider = serviceProviderService.createProvider(provider);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProvider);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create provider profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/profile/my")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<?> getMyProviderProfile(Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Optional<ServiceProvider> provider = serviceProviderService.findByUser(currentUser);

            if (provider.isPresent()) {
                return ResponseEntity.ok(provider.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Provider profile not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch provider profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/profile/{id}")
    @PreAuthorize("hasRole('SERVICE_PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateProviderProfile(@PathVariable Long id, @Valid @RequestBody ServiceProvider provider, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();

            // Check if the provider belongs to the current user (unless admin)
            Optional<ServiceProvider> existingProvider = serviceProviderService.findById(id);
            if (!existingProvider.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Provider not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            if (!currentUser.getRole().name().equals("ADMIN") &&
                !existingProvider.get().getUser().getId().equals(currentUser.getId())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Access denied");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            provider.setId(id);
            ServiceProvider updatedProvider = serviceProviderService.updateProvider(provider);
            return ResponseEntity.ok(updatedProvider);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update provider profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Admin endpoints
    @PostMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> verifyProvider(@PathVariable Long id) {
        try {
            ServiceProvider provider = serviceProviderService.verifyProvider(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Provider verified successfully");
            response.put("provider", provider);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to verify provider: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProvider(@PathVariable Long id) {
        try {
            serviceProviderService.deleteProvider(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Provider deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete provider: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get provider details for booking modal
    @GetMapping("/providers/{id}/details")
    public ResponseEntity<?> getProviderDetails(@PathVariable Long id) {
        try {
            Optional<ServiceProvider> provider = serviceProviderService.findById(id);
            if (provider.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Service provider not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            ServiceProvider p = provider.get();
            Map<String, Object> details = new HashMap<>();
            details.put("id", p.getId());
            details.put("name", p.getUser().getFirstName() + " " + p.getUser().getLastName());
            details.put("initials", p.getUser().getFirstName().substring(0,1) + p.getUser().getLastName().substring(0,1));
            details.put("category", p.getCategory().getName());
            details.put("hourlyRate", p.getHourlyRate());
            details.put("rating", p.getRating());
            details.put("bio", p.getBio());
            details.put("available", p.isAvailable());
            details.put("verified", p.isVerified());

            return ResponseEntity.ok(details);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch provider details: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
