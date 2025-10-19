package org.example.apcproject3.controller;

import org.example.apcproject3.entity.ServiceCategory;
import org.example.apcproject3.service.ServiceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ServiceController {

    @Autowired
    private ServiceCategoryService serviceCategoryService;

    // Public endpoint - no authentication required
    @GetMapping("/categories")
    public ResponseEntity<List<ServiceCategory>> getAllActiveCategories() {
        List<ServiceCategory> categories = serviceCategoryService.findActiveCategoriesOrdered();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        Optional<ServiceCategory> category = serviceCategoryService.findById(id);

        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Service category not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // Admin only endpoints
    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(@Valid @RequestBody ServiceCategory category) {
        try {
            ServiceCategory savedCategory = serviceCategoryService.createCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create category: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody ServiceCategory category) {
        try {
            category.setId(id);
            ServiceCategory updatedCategory = serviceCategoryService.updateCategory(category);
            return ResponseEntity.ok(updatedCategory);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update category: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            serviceCategoryService.deleteCategory(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Category deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete category: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/admin/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ServiceCategory>> getAllCategories() {
        List<ServiceCategory> categories = serviceCategoryService.findAllCategories();
        return ResponseEntity.ok(categories);
    }
}
