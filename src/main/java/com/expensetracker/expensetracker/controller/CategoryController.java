package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.entity.Category;
import com.expensetracker.expensetracker.entity.UserCategory;
import com.expensetracker.expensetracker.entity.User;
import com.expensetracker.expensetracker.repository.CategoryRepository;
import com.expensetracker.expensetracker.repository.UserCategoryRepository;
import com.expensetracker.expensetracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for category management
 * Provides CRUD operations for expense categories
 */
@RestController
@RequestMapping("/api/categories")
@Validated
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserCategoryRepository userCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all default categories
     * @return List of default categories
     */
    @GetMapping("/default")
    public ResponseEntity<List<Category>> getDefaultCategories() {
        try {
            logger.info("Fetching default categories");
            List<Category> categories = categoryRepository.findByIsDefaultTrue();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Error fetching default categories: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all categories (default + user-specific)
     * @return List of all categories
     */
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        try {
            logger.info("Fetching all categories");
            List<Category> categories = categoryRepository.findByIsActiveTrue();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Error fetching all categories: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get a specific category by ID
     * @param categoryId Category ID
     * @return Category details
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategory(@PathVariable @Positive Long categoryId) {
        try {
            logger.info("Fetching category with ID: {}", categoryId);
            Optional<Category> category = categoryRepository.findById(categoryId);
            return category.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching category {}: {}", categoryId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Create a new default category (admin only)
     * @param request Category creation request
     * @return Created category
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        try {
            logger.info("Creating new category: {}", request.getName());
            
            Category category = Category.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .icon(request.getIcon())
                    .color(request.getColor())
                    .isDefault(false)
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            Category savedCategory = categoryRepository.save(category);
            logger.info("Successfully created category with ID: {}", savedCategory.getId());
            return ResponseEntity.ok(savedCategory);
        } catch (Exception e) {
            logger.error("Error creating category: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update an existing category
     * @param categoryId Category ID
     * @param request Update request
     * @return Updated category
     */
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> updateCategory(
            @PathVariable @Positive Long categoryId,
            @Valid @RequestBody UpdateCategoryRequest request) {
        try {
            logger.info("Updating category with ID: {}", categoryId);
            
            Optional<Category> existingCategory = categoryRepository.findById(categoryId);
            if (existingCategory.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Category category = existingCategory.get();
            
            // Update fields if provided
            if (request.getName() != null) {
                category.setName(request.getName());
            }
            if (request.getDescription() != null) {
                category.setDescription(request.getDescription());
            }
            if (request.getIcon() != null) {
                category.setIcon(request.getIcon());
            }
            if (request.getColor() != null) {
                category.setColor(request.getColor());
            }
            if (request.getIsActive() != null) {
                category.setIsActive(request.getIsActive());
            }

            Category updatedCategory = categoryRepository.save(category);
            logger.info("Successfully updated category with ID: {}", categoryId);
            return ResponseEntity.ok(updatedCategory);
        } catch (Exception e) {
            logger.error("Error updating category {}: {}", categoryId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Delete a category (admin only)
     * @param categoryId Category ID
     * @return Success response
     */
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable @Positive Long categoryId) {
        try {
            logger.info("Deleting category with ID: {}", categoryId);
            
            Optional<Category> category = categoryRepository.findById(categoryId);
            if (category.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Soft delete by setting isActive to false
            Category cat = category.get();
            cat.setIsActive(false);
            categoryRepository.save(cat);
            
            logger.info("Successfully deleted category with ID: {}", categoryId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting category {}: {}", categoryId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // User-specific category endpoints

    /**
     * Get user-specific categories
     * @param userId User ID
     * @return List of user categories
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<UserCategory>> getUserCategories(@PathVariable @Positive Long userId) {
        try {
            logger.info("Fetching categories for user: {}", userId);
            List<UserCategory> userCategories = userCategoryRepository.findByUserIdAndIsActiveTrue(userId);
            return ResponseEntity.ok(userCategories);
        } catch (Exception e) {
            logger.error("Error fetching categories for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Create a user-specific category
     * @param userId User ID
     * @param request User category creation request
     * @return Created user category
     */
    @PostMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<UserCategory> createUserCategory(
            @PathVariable @Positive Long userId,
            @Valid @RequestBody CreateUserCategoryRequest request) {
        try {
            logger.info("Creating new user category for user {}: {}", userId, request.getName());
            
            // Validate user exists
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

            UserCategory userCategory = UserCategory.builder()
                    .user(user)
                    .name(request.getName())
                    .description(request.getDescription())
                    .icon(request.getIcon())
                    .color(request.getColor())
                    .parentCategory(request.getParentCategoryId() != null ? 
                            categoryRepository.findById(request.getParentCategoryId()).orElse(null) : null)
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            UserCategory savedUserCategory = userCategoryRepository.save(userCategory);
            logger.info("Successfully created user category with ID: {}", savedUserCategory.getId());
            return ResponseEntity.ok(savedUserCategory);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for user category creation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating user category: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update a user-specific category
     * @param userId User ID
     * @param categoryId User category ID
     * @param request Update request
     * @return Updated user category
     */
    @PutMapping("/user/{userId}/{categoryId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<UserCategory> updateUserCategory(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long categoryId,
            @Valid @RequestBody UpdateUserCategoryRequest request) {
        try {
            logger.info("Updating user category {} for user {}", categoryId, userId);
            
            Optional<UserCategory> existingUserCategory = userCategoryRepository.findById(categoryId);
            if (existingUserCategory.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            UserCategory userCategory = existingUserCategory.get();
            
            // Verify ownership
            if (!userCategory.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Update fields if provided
            if (request.getName() != null) {
                userCategory.setName(request.getName());
            }
            if (request.getDescription() != null) {
                userCategory.setDescription(request.getDescription());
            }
            if (request.getIcon() != null) {
                userCategory.setIcon(request.getIcon());
            }
            if (request.getColor() != null) {
                userCategory.setColor(request.getColor());
            }
            if (request.getIsActive() != null) {
                userCategory.setIsActive(request.getIsActive());
            }

            UserCategory updatedUserCategory = userCategoryRepository.save(userCategory);
            logger.info("Successfully updated user category with ID: {}", categoryId);
            return ResponseEntity.ok(updatedUserCategory);
        } catch (Exception e) {
            logger.error("Error updating user category {}: {}", categoryId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Delete a user-specific category
     * @param userId User ID
     * @param categoryId User category ID
     * @return Success response
     */
    @DeleteMapping("/user/{userId}/{categoryId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Void> deleteUserCategory(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long categoryId) {
        try {
            logger.info("Deleting user category {} for user {}", categoryId, userId);
            
            Optional<UserCategory> userCategory = userCategoryRepository.findById(categoryId);
            if (userCategory.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Verify ownership
            if (!userCategory.get().getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Soft delete by setting isActive to false
            UserCategory cat = userCategory.get();
            cat.setIsActive(false);
            userCategoryRepository.save(cat);
            
            logger.info("Successfully deleted user category with ID: {}", categoryId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting user category {}: {}", categoryId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Request DTOs
    public static class CreateCategoryRequest {
        @NotBlank
        private String name;
        private String description;
        private String icon;
        private String color;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }

    public static class UpdateCategoryRequest {
        private String name;
        private String description;
        private String icon;
        private String color;
        private Boolean isActive;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }

        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }

    public static class CreateUserCategoryRequest {
        @NotBlank
        private String name;
        private String description;
        private String icon;
        private String color;
        private Long parentCategoryId;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }

        public Long getParentCategoryId() { return parentCategoryId; }
        public void setParentCategoryId(Long parentCategoryId) { this.parentCategoryId = parentCategoryId; }
    }

    public static class UpdateUserCategoryRequest {
        private String name;
        private String description;
        private String icon;
        private String color;
        private Boolean isActive;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }

        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }
} 