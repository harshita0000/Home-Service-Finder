package org.example.apcproject3.service;

import org.example.apcproject3.entity.AvailabilitySlot;
import org.example.apcproject3.entity.ServiceProvider;
import org.example.apcproject3.repository.AvailabilitySlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AvailabilitySlotService {

    @Autowired
    private AvailabilitySlotRepository availabilitySlotRepository;

    public AvailabilitySlot createSlot(AvailabilitySlot slot) {
        // Validate that end time is after start time
        if (slot.getEndTime().isBefore(slot.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        // Check for overlapping slots for the same provider
        List<AvailabilitySlot> overlappingSlots = availabilitySlotRepository
                .findAvailableSlotsByProviderAndTimeRange(
                    slot.getProvider(),
                    slot.getStartTime(),
                    slot.getEndTime()
                );

        if (!overlappingSlots.isEmpty()) {
            throw new RuntimeException("Time slot overlaps with existing availability");
        }

        return availabilitySlotRepository.save(slot);
    }

    public Optional<AvailabilitySlot> findById(Long id) {
        return availabilitySlotRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<AvailabilitySlot> findByProvider(ServiceProvider provider) {
        return availabilitySlotRepository.findByProvider(provider);
    }

    @Transactional(readOnly = true)
    public List<AvailabilitySlot> findAvailableSlotsByProvider(ServiceProvider provider) {
        return availabilitySlotRepository.findByProviderAndAvailableTrue(provider);
    }

    @Transactional(readOnly = true)
    public List<AvailabilitySlot> findAvailableSlotsByProviderAndDate(Long providerId, LocalDateTime date) {
        return availabilitySlotRepository.findAvailableSlotsByProviderIdAndDate(providerId, date);
    }

    @Transactional(readOnly = true)
    public List<AvailabilitySlot> findAvailableSlotsBetweenTimes(LocalDateTime startTime, LocalDateTime endTime) {
        return availabilitySlotRepository.findAvailableSlotsBetweenTimes(startTime, endTime);
    }

    public AvailabilitySlot markSlotAsUnavailable(Long slotId) {
        AvailabilitySlot slot = availabilitySlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Availability slot not found"));

        slot.setAvailable(false);
        return availabilitySlotRepository.save(slot);
    }

    public AvailabilitySlot markSlotAsAvailable(Long slotId) {
        AvailabilitySlot slot = availabilitySlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Availability slot not found"));

        slot.setAvailable(true);
        return availabilitySlotRepository.save(slot);
    }

    public void deleteSlot(Long id) {
        availabilitySlotRepository.deleteById(id);
    }

    // Batch create slots for a provider
    public List<AvailabilitySlot> createMultipleSlots(List<AvailabilitySlot> slots) {
        // Validate all slots before saving
        for (AvailabilitySlot slot : slots) {
            if (slot.getEndTime().isBefore(slot.getStartTime())) {
                throw new RuntimeException("End time must be after start time for all slots");
            }
        }

        return availabilitySlotRepository.saveAll(slots);
    }
}
