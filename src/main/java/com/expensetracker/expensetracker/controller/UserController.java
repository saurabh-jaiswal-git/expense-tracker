package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.entity.User;
import com.expensetracker.expensetracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

/**
 * REST Controller for user management
 * Provides CRUD operations for user accounts
 */
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get all users (admin only)
     * @return List of all users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            logger.info("Fetching all users");
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error fetching all users: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get a specific user by ID
     * @param userId User ID
     * @return User details
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<User> getUser(@PathVariable @Positive Long userId) {
        try {
            logger.info("Fetching user with ID: {}", userId);
            Optional<User> user = userRepository.findById(userId);
            return user.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get user by email
     * @param email User email
     * @return User details
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserByEmail(@PathVariable @Email String email) {
        try {
            logger.info("Fetching user with email: {}", email);
            Optional<User> user = userRepository.findByEmail(email);
            return user.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching user by email {}: {}", email, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Create a new user (admin only)
     * @param request User creation request
     * @return Created user
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            logger.info("Creating new user: {}", request.getEmail());
            
            // Check if email already exists
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                logger.warn("User with email {} already exists", request.getEmail());
                return ResponseEntity.badRequest().build();
            }

            User user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .phone(request.getPhone())
                    .dateOfBirth(request.getDateOfBirth())
                    .monthlyIncome(request.getMonthlyIncome())
                    .currency(request.getCurrency())
                    .timezone(request.getTimezone())
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            User savedUser = userRepository.save(user);
            logger.info("Successfully created user with ID: {}", savedUser.getId());
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update user profile
     * @param userId User ID
     * @param request Update request
     * @return Updated user
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<User> updateUser(
            @PathVariable @Positive Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        try {
            logger.info("Updating user with ID: {}", userId);
            
            Optional<User> existingUser = userRepository.findById(userId);
            if (existingUser.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = existingUser.get();
            
            // Update fields if provided
            if (request.getFirstName() != null) {
                user.setFirstName(request.getFirstName());
            }
            if (request.getLastName() != null) {
                user.setLastName(request.getLastName());
            }
            if (request.getPhone() != null) {
                user.setPhone(request.getPhone());
            }
            if (request.getDateOfBirth() != null) {
                user.setDateOfBirth(request.getDateOfBirth());
            }
            if (request.getMonthlyIncome() != null) {
                user.setMonthlyIncome(request.getMonthlyIncome());
            }
            if (request.getCurrency() != null) {
                user.setCurrency(request.getCurrency());
            }
            if (request.getTimezone() != null) {
                user.setTimezone(request.getTimezone());
            }
            if (request.getIsActive() != null) {
                user.setIsActive(request.getIsActive());
            }

            user.setUpdatedAt(LocalDateTime.now());
            User updatedUser = userRepository.save(user);
            logger.info("Successfully updated user with ID: {}", userId);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Error updating user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Change user password
     * @param userId User ID
     * @param request Password change request
     * @return Success response
     */
    @PutMapping("/{userId}/password")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Void> changePassword(
            @PathVariable @Positive Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            logger.info("Changing password for user with ID: {}", userId);
            
            Optional<User> existingUser = userRepository.findById(userId);
            if (existingUser.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = existingUser.get();
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            
            logger.info("Successfully changed password for user with ID: {}", userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error changing password for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Delete a user (admin only)
     * @param userId User ID
     * @return Success response
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable @Positive Long userId) {
        try {
            logger.info("Deleting user with ID: {}", userId);
            
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Soft delete by setting isActive to false
            User u = user.get();
            u.setIsActive(false);
            u.setUpdatedAt(LocalDateTime.now());
            userRepository.save(u);
            
            logger.info("Successfully deleted user with ID: {}", userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get user profile summary
     * @param userId User ID
     * @return User profile summary
     */
    @GetMapping("/{userId}/profile")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable @Positive Long userId) {
        try {
            logger.info("Fetching profile for user: {}", userId);
            
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User u = user.get();
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", u.getId());
            profile.put("email", u.getEmail());
            profile.put("fullName", u.getFullName());
            profile.put("firstName", u.getFirstName());
            profile.put("lastName", u.getLastName());
            profile.put("phone", u.getPhone());
            profile.put("dateOfBirth", u.getDateOfBirth());
            profile.put("monthlyIncome", u.getMonthlyIncome());
            profile.put("currency", u.getCurrency());
            profile.put("timezone", u.getTimezone());
            profile.put("isActive", u.isActive());
            profile.put("createdAt", u.getCreatedAt());
            profile.put("updatedAt", u.getUpdatedAt());

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            logger.error("Error fetching profile for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Check if email exists
     * @param email Email to check
     * @return Email availability status
     */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@PathVariable @Email String email) {
        try {
            logger.info("Checking if email exists: {}", email);
            boolean exists = userRepository.findByEmail(email).isPresent();
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (Exception e) {
            logger.error("Error checking email {}: {}", email, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Request DTOs
    public static class CreateUserRequest {
        @NotNull
        @Email
        private String email;

        @NotNull
        @NotBlank
        private String password;

        @NotNull
        @NotBlank
        private String firstName;

        private String lastName;
        private String phone;
        private LocalDate dateOfBirth;
        private BigDecimal monthlyIncome;
        private String currency = "INR";
        private String timezone = "Asia/Kolkata";

        // Getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

        public BigDecimal getMonthlyIncome() { return monthlyIncome; }
        public void setMonthlyIncome(BigDecimal monthlyIncome) { this.monthlyIncome = monthlyIncome; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }
    }

    public static class UpdateUserRequest {
        private String firstName;
        private String lastName;
        private String phone;
        private LocalDate dateOfBirth;
        private BigDecimal monthlyIncome;
        private String currency;
        private String timezone;
        private Boolean isActive;

        // Getters and setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

        public BigDecimal getMonthlyIncome() { return monthlyIncome; }
        public void setMonthlyIncome(BigDecimal monthlyIncome) { this.monthlyIncome = monthlyIncome; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }

        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }

    public static class ChangePasswordRequest {
        @NotNull
        @NotBlank
        private String newPassword;

        // Getters and setters
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
} 