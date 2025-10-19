package org.example.apcproject3.repository;

import org.example.apcproject3.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {

    Optional<ServiceCategory> findByName(String name);

    List<ServiceCategory> findByActiveTrue();

    @Query("SELECT sc FROM ServiceCategory sc WHERE sc.active = true ORDER BY sc.name")
    List<ServiceCategory> findAllActiveCategoriesOrdered();

    Boolean existsByName(String name);
}
