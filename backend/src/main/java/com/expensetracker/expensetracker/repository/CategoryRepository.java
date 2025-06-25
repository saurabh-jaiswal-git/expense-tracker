package com.expensetracker.expensetracker.repository;

import com.expensetracker.expensetracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Find all default categories
     */
    List<Category> findByIsDefaultTrue();
    
    /**
     * Find all active categories
     */
    List<Category> findByIsActiveTrue();
    
    /**
     * Find categories by name (case-insensitive)
     */
    List<Category> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find active categories by name (case-insensitive)
     */
    List<Category> findByIsActiveTrueAndNameContainingIgnoreCase(String name);
    
    /**
     * Check if category exists by name
     */
    boolean existsByNameIgnoreCase(String name);
    
    /**
     * Find categories by default status
     */
    List<Category> findByIsDefault(Boolean isDefault);
    
    /**
     * Find categories by active status
     */
    List<Category> findByIsActive(Boolean isActive);
} 