package com.expensetracker.expensetracker.repository;

import com.expensetracker.expensetracker.entity.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {
    
    /**
     * Find all active user categories for a user
     */
    List<UserCategory> findByUserIdAndIsActiveTrue(Long userId);
    
    /**
     * Find all user categories for a user
     */
    List<UserCategory> findByUserId(Long userId);
    
    /**
     * Find user categories by name for a user (case-insensitive)
     */
    List<UserCategory> findByUserIdAndNameContainingIgnoreCase(Long userId, String name);
    
    /**
     * Find active user categories by name for a user (case-insensitive)
     */
    List<UserCategory> findByUserIdAndIsActiveTrueAndNameContainingIgnoreCase(Long userId, String name);
    
    /**
     * Check if user category exists by name for a user
     */
    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);
    
    /**
     * Find user categories by parent category
     */
    List<UserCategory> findByUserIdAndParentCategoryId(Long userId, Long parentCategoryId);
    
    /**
     * Find user categories without parent (top-level)
     */
    List<UserCategory> findByUserIdAndParentCategoryIsNull(Long userId);
    
    /**
     * Count user categories for a user
     */
    long countByUserId(Long userId);
    
    /**
     * Count active user categories for a user
     */
    long countByUserIdAndIsActiveTrue(Long userId);
    
    /**
     * Find user categories by active status for a user
     */
    List<UserCategory> findByUserIdAndIsActive(Long userId, Boolean isActive);
    
    /**
     * Find user categories with specific parent category
     */
    @Query("SELECT uc FROM UserCategory uc WHERE uc.user.id = :userId AND uc.parentCategory.id = :parentCategoryId")
    List<UserCategory> findByUserIdAndParentCategoryIdQuery(
            @Param("userId") Long userId, @Param("parentCategoryId") Long parentCategoryId);
} 