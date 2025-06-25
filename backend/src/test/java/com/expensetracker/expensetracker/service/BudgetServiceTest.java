package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.entity.*;
import com.expensetracker.expensetracker.repository.BudgetCategoryRepository;
import com.expensetracker.expensetracker.repository.BudgetRepository;
import com.expensetracker.expensetracker.repository.CategoryRepository;
import com.expensetracker.expensetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private BudgetCategoryRepository budgetCategoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BudgetService budgetService;

    private User testUser;
    private Category testCategory;
    private Budget testBudget;
    private BudgetCategory testBudgetCategory;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password123")
                .build();

        testCategory = Category.builder()
                .id(1L)
                .name("Food")
                .description("Food and dining expenses")
                .build();

        testBudget = Budget.builder()
                .id(1L)
                .user(testUser)
                .yearMonth(YearMonth.of(2025, 6))
                .totalBudget(new BigDecimal("1000.00"))
                .actualSpending(new BigDecimal("500.00"))
                .isActive(true)
                .notes("Test budget")
                .build();

        testBudgetCategory = BudgetCategory.builder()
                .id(1L)
                .budget(testBudget)
                .category(testCategory)
                .budgetAmount(new BigDecimal("300.00"))
                .budgetPercentage(30.0)
                .actualAmount(new BigDecimal("150.00"))
                .actualPercentage(15.0)
                .build();
    }

    // ========== POSITIVE SCENARIOS ==========

    @Test
    void createBudget_Success() {
        // Given
        YearMonth yearMonth = YearMonth.of(2025, 6);
        BigDecimal totalBudget = new BigDecimal("1000.00");
        String notes = "Test budget";

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(budgetRepository.existsByUserIdAndYearMonth(1L, yearMonth)).thenReturn(false);
        when(budgetRepository.save(any(Budget.class))).thenReturn(testBudget);

        // When
        Budget result = budgetService.createBudget(1L, yearMonth, totalBudget, notes);

        // Then
        assertNotNull(result);
        assertEquals(testBudget.getId(), result.getId());
        verify(budgetRepository).save(any(Budget.class));
    }

    @Test
    void getBudgetById_Success() {
        // Given
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget));

        // When
        Optional<Budget> result = budgetService.getBudgetById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testBudget.getId(), result.get().getId());
    }

    @Test
    void addBudgetCategory_Success() {
        // Given
        BigDecimal budgetAmount = new BigDecimal("300.00");
        Double budgetPercentage = 30.0;

        when(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(budgetCategoryRepository.existsByBudgetIdAndCategoryId(1L, 1L)).thenReturn(false);
        when(budgetCategoryRepository.save(any(BudgetCategory.class))).thenReturn(testBudgetCategory);

        // When
        BudgetCategory result = budgetService.addBudgetCategory(1L, 1L, budgetAmount, budgetPercentage);

        // Then
        assertNotNull(result);
        assertEquals(testBudgetCategory.getId(), result.getId());
        verify(budgetCategoryRepository).save(any(BudgetCategory.class));
    }

    // ========== INPUT VALIDATION ERROR SCENARIOS ==========

    @Test
    void createBudget_NullUserId_ThrowsException() {
        // Given
        YearMonth yearMonth = YearMonth.of(2025, 6);
        BigDecimal totalBudget = new BigDecimal("1000.00");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.createBudget(null, yearMonth, totalBudget, "notes"));
        assertEquals("User ID cannot be null", exception.getMessage());
    }

    @Test
    void createBudget_ZeroUserId_ThrowsException() {
        // Given
        YearMonth yearMonth = YearMonth.of(2025, 6);
        BigDecimal totalBudget = new BigDecimal("1000.00");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.createBudget(0L, yearMonth, totalBudget, "notes"));
        assertEquals("User ID must be positive", exception.getMessage());
    }

    @Test
    void createBudget_NegativeUserId_ThrowsException() {
        // Given
        YearMonth yearMonth = YearMonth.of(2025, 6);
        BigDecimal totalBudget = new BigDecimal("1000.00");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.createBudget(-1L, yearMonth, totalBudget, "notes"));
        assertEquals("User ID must be positive", exception.getMessage());
    }

    @Test
    void createBudget_NullTotalBudget_ThrowsException() {
        // Given
        YearMonth yearMonth = YearMonth.of(2025, 6);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.createBudget(1L, yearMonth, null, "notes"));
        assertEquals("Budget amount cannot be null", exception.getMessage());
    }

    @Test
    void createBudget_ZeroTotalBudget_ThrowsException() {
        // Given
        YearMonth yearMonth = YearMonth.of(2025, 6);
        BigDecimal totalBudget = BigDecimal.ZERO;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.createBudget(1L, yearMonth, totalBudget, "notes"));
        assertEquals("Budget amount must be at least 0.01", exception.getMessage());
    }

    @Test
    void createBudget_NegativeTotalBudget_ThrowsException() {
        // Given
        YearMonth yearMonth = YearMonth.of(2025, 6);
        BigDecimal totalBudget = new BigDecimal("-100.00");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.createBudget(1L, yearMonth, totalBudget, "notes"));
        assertEquals("Budget amount must be at least 0.01", exception.getMessage());
    }

    @Test
    void createBudget_TooLargeTotalBudget_ThrowsException() {
        // Given
        YearMonth yearMonth = YearMonth.of(2025, 6);
        BigDecimal totalBudget = new BigDecimal("1000000.00");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.createBudget(1L, yearMonth, totalBudget, "notes"));
        assertEquals("Budget amount cannot exceed 999999.99", exception.getMessage());
    }

    @Test
    void createBudget_NullYearMonth_ThrowsException() {
        // Given
        BigDecimal totalBudget = new BigDecimal("1000.00");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.createBudget(1L, null, totalBudget, "notes"));
        assertEquals("YearMonth cannot be null", exception.getMessage());
    }

    @Test
    void createBudget_TooOldYearMonth_ThrowsException() {
        // Given
        YearMonth yearMonth = YearMonth.now().minusMonths(13);
        BigDecimal totalBudget = new BigDecimal("1000.00");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.createBudget(1L, yearMonth, totalBudget, "notes"));
        assertEquals("Budget can only be created for periods within one year", exception.getMessage());
    }

    @Test
    void createBudget_TooFutureYearMonth_ThrowsException() {
        // Given
        YearMonth yearMonth = YearMonth.now().plusMonths(13);
        BigDecimal totalBudget = new BigDecimal("1000.00");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.createBudget(1L, yearMonth, totalBudget, "notes"));
        assertEquals("Budget can only be created for periods within one year", exception.getMessage());
    }

    @Test
    void createBudget_TooLongNotes_ThrowsException() {
        // Given
        YearMonth yearMonth = YearMonth.of(2025, 6);
        BigDecimal totalBudget = new BigDecimal("1000.00");
        String longNotes = "a".repeat(1001);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.createBudget(1L, yearMonth, totalBudget, longNotes));
        assertEquals("Notes cannot exceed 1000 characters", exception.getMessage());
    }

    @Test
    void getBudgetById_NullBudgetId_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.getBudgetById(null));
        assertEquals("Budget ID cannot be null", exception.getMessage());
    }

    @Test
    void getBudgetById_ZeroBudgetId_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.getBudgetById(0L));
        assertEquals("Budget ID must be positive", exception.getMessage());
    }

    @Test
    void addBudgetCategory_NullBudgetId_ThrowsException() {
        // Given
        BigDecimal budgetAmount = new BigDecimal("300.00");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.addBudgetCategory(null, 1L, budgetAmount, 30.0));
        assertEquals("Budget ID cannot be null", exception.getMessage());
    }

    @Test
    void addBudgetCategory_NullCategoryId_ThrowsException() {
        // Given
        BigDecimal budgetAmount = new BigDecimal("300.00");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.addBudgetCategory(1L, null, budgetAmount, 30.0));
        assertEquals("Category ID cannot be null", exception.getMessage());
    }

    @Test
    void addBudgetCategory_NullBudgetAmount_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.addBudgetCategory(1L, 1L, null, 30.0));
        assertEquals("Budget amount cannot be null", exception.getMessage());
    }

    @Test
    void addBudgetCategory_ZeroBudgetAmount_ThrowsException() {
        // Given
        BigDecimal budgetAmount = BigDecimal.ZERO;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.addBudgetCategory(1L, 1L, budgetAmount, 30.0));
        assertEquals("Budget amount must be at least 0.01", exception.getMessage());
    }

    @Test
    void addBudgetCategory_NegativeBudgetPercentage_ThrowsException() {
        // Given
        BigDecimal budgetAmount = new BigDecimal("300.00");
        Double budgetPercentage = -10.0;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.addBudgetCategory(1L, 1L, budgetAmount, budgetPercentage));
        assertEquals("Budget percentage must be between 0.0 and 100.0", exception.getMessage());
    }

    @Test
    void addBudgetCategory_TooHighBudgetPercentage_ThrowsException() {
        // Given
        BigDecimal budgetAmount = new BigDecimal("300.00");
        Double budgetPercentage = 150.0;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.addBudgetCategory(1L, 1L, budgetAmount, budgetPercentage));
        assertEquals("Budget percentage must be between 0.0 and 100.0", exception.getMessage());
    }

    @Test
    void updateBudgetSpending_NullBudgetId_ThrowsException() {
        // Given
        BigDecimal actualSpending = new BigDecimal("500.00");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.updateBudgetActualSpending(null, actualSpending));
        assertEquals("Budget ID cannot be null", exception.getMessage());
    }

    @Test
    void updateBudgetSpending_NullSpending_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.updateBudgetActualSpending(1L, null));
        assertEquals("Spending amount cannot be null", exception.getMessage());
    }

    @Test
    void updateBudgetSpending_NegativeSpending_ThrowsException() {
        // Given
        BigDecimal actualSpending = new BigDecimal("-100.00");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.updateBudgetActualSpending(1L, actualSpending));
        assertEquals("Spending amount cannot be negative", exception.getMessage());
    }

    @Test
    void updateBudgetSpending_TooLargeSpending_ThrowsException() {
        // Given
        BigDecimal actualSpending = new BigDecimal("1000000.00");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.updateBudgetActualSpending(1L, actualSpending));
        assertEquals("Spending amount cannot exceed 999999.99", exception.getMessage());
    }

    // ========== BUSINESS LOGIC ERROR SCENARIOS ==========

    @Test
    void addBudgetCategory_CategoryAmountExceedsTotalBudget_ThrowsException() {
        // Given
        BigDecimal budgetAmount = new BigDecimal("1500.00"); // Exceeds total budget of 1000
        Double budgetPercentage = 30.0;

        when(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(budgetCategoryRepository.existsByBudgetIdAndCategoryId(1L, 1L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.addBudgetCategory(1L, 1L, budgetAmount, budgetPercentage));
        assertEquals("Category budget amount cannot exceed total budget amount", exception.getMessage());
    }

    @Test
    void addBudgetCategory_CategoryAlreadyExists_ThrowsException() {
        // Given
        BigDecimal budgetAmount = new BigDecimal("300.00");
        Double budgetPercentage = 30.0;

        when(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(budgetCategoryRepository.existsByBudgetIdAndCategoryId(1L, 1L)).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> budgetService.addBudgetCategory(1L, 1L, budgetAmount, budgetPercentage));
        assertEquals("Category 1 already exists in budget 1", exception.getMessage());
    }

    // ========== CALCULATION TESTS ==========

    @Test
    void calculateBudgetPercentage_ValidInputs_ReturnsCorrectPercentage() {
        // Given
        BigDecimal categoryAmount = new BigDecimal("300.00");
        BigDecimal totalBudget = new BigDecimal("1000.00");

        // When
        double result = budgetService.calculateBudgetPercentage(categoryAmount, totalBudget);

        // Then
        assertEquals(30.0, result, 0.01);
    }

    @Test
    void calculateBudgetPercentage_NullCategoryAmount_ReturnsZero() {
        // Given
        BigDecimal totalBudget = new BigDecimal("1000.00");

        // When
        double result = budgetService.calculateBudgetPercentage(null, totalBudget);

        // Then
        assertEquals(0.0, result, 0.01);
    }

    @Test
    void calculateBudgetPercentage_NullTotalBudget_ReturnsZero() {
        // Given
        BigDecimal categoryAmount = new BigDecimal("300.00");

        // When
        double result = budgetService.calculateBudgetPercentage(categoryAmount, null);

        // Then
        assertEquals(0.0, result, 0.01);
    }

    @Test
    void calculateBudgetPercentage_ZeroTotalBudget_ReturnsZero() {
        // Given
        BigDecimal categoryAmount = new BigDecimal("300.00");
        BigDecimal totalBudget = BigDecimal.ZERO;

        // When
        double result = budgetService.calculateBudgetPercentage(categoryAmount, totalBudget);

        // Then
        assertEquals(0.0, result, 0.01);
    }
} 