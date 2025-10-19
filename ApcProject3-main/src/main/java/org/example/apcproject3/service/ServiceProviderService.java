package org.example.apcproject3.service;

import org.example.apcproject3.entity.ServiceCategory;
import org.example.apcproject3.entity.ServiceProvider;
import org.example.apcproject3.entity.User;
import org.example.apcproject3.entity.UserRole;
import org.example.apcproject3.repository.ServiceProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class ServiceProviderService {

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private UserService userService;

    public ServiceProvider createProvider(ServiceProvider provider) {
        // Validate that the user has SERVICE_PROVIDER role
        if (provider.getUser().getRole() != UserRole.SERVICE_PROVIDER) {
            throw new RuntimeException("User must have SERVICE_PROVIDER role to become a service provider");
        }

        // Check if provider already exists for this user
        Optional<ServiceProvider> existingProvider = serviceProviderRepository.findByUser(provider.getUser());
        if (existingProvider.isPresent()) {
            throw new RuntimeException("Service provider profile already exists for this user");
        }

        return serviceProviderRepository.save(provider);
    }

    public Optional<ServiceProvider> findById(Long id) {
        return serviceProviderRepository.findById(id);
    }

    public Optional<ServiceProvider> findByUser(User user) {
        return serviceProviderRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public List<ServiceProvider> findByCategory(ServiceCategory category) {
        return serviceProviderRepository.findByCategory(category);
    }

    @Transactional(readOnly = true)
    public List<ServiceProvider> findAvailableProviders() {
        return serviceProviderRepository.findByAvailableTrue();
    }

    @Transactional(readOnly = true)
    public List<ServiceProvider> findVerifiedProviders() {
        return serviceProviderRepository.findByVerifiedTrue();
    }

    @Transactional(readOnly = true)
    public List<ServiceProvider> findAvailableProvidersByCategory(ServiceCategory category) {
        return serviceProviderRepository.findAvailableProvidersByCategory(category);
    }

    @Transactional(readOnly = true)
    public List<ServiceProvider> findTopRatedProviders(BigDecimal minRating) {
        return serviceProviderRepository.findByRatingGreaterThanEqualOrderByRatingDesc(minRating);
    }

    @Transactional(readOnly = true)
    public List<ServiceProvider> findProvidersByPriceRange(BigDecimal minRate, BigDecimal maxRate) {
        return serviceProviderRepository.findByHourlyRateBetween(minRate, maxRate);
    }

    @Transactional(readOnly = true)
    public List<ServiceProvider> findTopRatedProvidersByCategory(Long categoryId) {
        return serviceProviderRepository.findTopRatedProvidersByCategory(categoryId);
    }

    public ServiceProvider updateProvider(ServiceProvider provider) {
        ServiceProvider existingProvider = serviceProviderRepository.findById(provider.getId())
                .orElseThrow(() -> new RuntimeException("Service provider not found"));

        existingProvider.setBio(provider.getBio());
        existingProvider.setExperienceYears(provider.getExperienceYears());
        existingProvider.setHourlyRate(provider.getHourlyRate());
        existingProvider.setAvailable(provider.isAvailable());
        existingProvider.setCategory(provider.getCategory());

        return serviceProviderRepository.save(existingProvider);
    }

    public ServiceProvider updateRating(Long providerId, BigDecimal newRating, Integer reviewCount) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Service provider not found"));

        // Calculate new average rating
        BigDecimal totalRating = provider.getRating().multiply(BigDecimal.valueOf(provider.getTotalReviews()));
        totalRating = totalRating.add(newRating);
        int newTotalReviews = provider.getTotalReviews() + 1;

        BigDecimal averageRating = totalRating.divide(BigDecimal.valueOf(newTotalReviews), 2, BigDecimal.ROUND_HALF_UP);

        provider.setRating(averageRating);
        provider.setTotalReviews(newTotalReviews);

        return serviceProviderRepository.save(provider);
    }

    public ServiceProvider verifyProvider(Long providerId) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Service provider not found"));

        provider.setVerified(true);
        return serviceProviderRepository.save(provider);
    }

    public void deleteProvider(Long id) {
        serviceProviderRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public CompletableFuture<List<ServiceProvider>> findProvidersAsync() {
        return CompletableFuture.supplyAsync(() -> serviceProviderRepository.findAll());
    }
}
