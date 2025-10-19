package org.example.apcproject3.service;

import org.example.apcproject3.entity.ServiceCategory;
import org.example.apcproject3.repository.ServiceCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ServiceCategoryService {

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    public ServiceCategory createCategory(ServiceCategory category) {
        if (serviceCategoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("Service category with name '" + category.getName() + "' already exists!");
        }

        return serviceCategoryRepository.save(category);
    }

    public Optional<ServiceCategory> findById(Long id) {
        return serviceCategoryRepository.findById(id);
    }

    public Optional<ServiceCategory> findByName(String name) {
        return serviceCategoryRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public List<ServiceCategory> findAllCategories() {
        return serviceCategoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ServiceCategory> findActiveCategories() {
        return serviceCategoryRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<ServiceCategory> findActiveCategoriesOrdered() {
        return serviceCategoryRepository.findAllActiveCategoriesOrdered();
    }

    public ServiceCategory updateCategory(ServiceCategory category) {
        ServiceCategory existingCategory = serviceCategoryRepository.findById(category.getId())
                .orElseThrow(() -> new RuntimeException("Service category not found"));

        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setIcon(category.getIcon());
        existingCategory.setActive(category.isActive());

        return serviceCategoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        ServiceCategory category = serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service category not found"));

        // Soft delete - mark as inactive instead of deleting
        category.setActive(false);
        serviceCategoryRepository.save(category);
    }

    public boolean existsByName(String name) {
        return serviceCategoryRepository.existsByName(name);
    }
}
