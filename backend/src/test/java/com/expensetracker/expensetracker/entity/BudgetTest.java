package com.expensetracker.expensetracker.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

class BudgetTest {

    private User testUser;
    private Category testCategory;
    private Budget testBudget;
    private BudgetCategory testBudgetCategory;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password123")
                .build();

        // Create test category
        testCategory = Category.builder()
                .id(1L)
                .name("Food & Dining")
                .description("Food and dining expenses")
                .isActive(true)
                .build();

        // Create test budget category
        testBudgetCategory = BudgetCategory.builder()
                .id(1L)
                .category(testCategory)
                .budgetAmount(new BigDecimal("500.00"))
                .budgetPercentage(25.0)
                .build();

        // Create test budget
        testBudget = Budget.builder()
                .id(1L)
                .user(testUser)
                .yearMonth(YearMonth.of(2024, 1))
                .totalBudget(new BigDecimal("2000.00"))
                .isActive(true)
                .build();
    }

    @Test
    void testBudgetCreation() {
        assertNotNull(testBudget);
        assertEquals(1L, testBudget.getId());
        assertEquals(testUser, testBudget.getUser());
        assertEquals(YearMonth.of(2024, 1), testBudget.getYearMonth());
        assertEquals(new BigDecimal("2000.00"), testBudget.getTotalBudget());
        assertTrue(testBudget.getIsActive());
    }

    @Test
    void testBudgetWithCategories() {
        List<BudgetCategory> categories = new ArrayList<>();
        categories.add(testBudgetCategory);
        testBudget.setBudgetCategories(categories);

        assertEquals(1, testBudget.getBudgetCategories().size());
        assertEquals(testBudgetCategory, testBudget.getBudgetCategories().get(0));
    }

    @Test
    void testBudgetCategoryCreation() {
        assertNotNull(testBudgetCategory);
        assertEquals(1L, testBudgetCategory.getId());
        assertEquals(testCategory, testBudgetCategory.getCategory());
        assertEquals(new BigDecimal("500.00"), testBudgetCategory.getBudgetAmount());
        assertEquals(25.0, testBudgetCategory.getBudgetPercentage());
    }

    @Test
    void testBudgetPeriodValidation() {
        // Test current month
        YearMonth currentMonth = YearMonth.now();
        assertDoesNotThrow(() -> {
            Budget currentBudget = Budget.builder()
                    .user(testUser)
                    .yearMonth(currentMonth)
                    .totalBudget(new BigDecimal("1000.00"))
                    .isActive(true)
                    .build();
        });

        // Test future month (within 1 year)
        YearMonth futureMonth = YearMonth.now().plusMonths(6);
        assertDoesNotThrow(() -> {
            Budget futureBudget = Budget.builder()
                    .user(testUser)
                    .yearMonth(futureMonth)
                    .totalBudget(new BigDecimal("1000.00"))
                    .isActive(true)
                    .build();
        });

        // Test past month (within 1 year)
        YearMonth pastMonth = YearMonth.now().minusMonths(6);
        assertDoesNotThrow(() -> {
            Budget pastBudget = Budget.builder()
                    .user(testUser)
                    .yearMonth(pastMonth)
                    .totalBudget(new BigDecimal("1000.00"))
                    .isActive(true)
                    .build();
        });
    }

    @Test
    void testBudgetStatusCalculation() {
        // Test budget with no actual spending
        testBudget.setActualSpending(new BigDecimal("0.00"));
        assertEquals(BudgetStatus.UNDER_BUDGET, testBudget.getStatus());

        // Test budget under spending (95% of budget)
        testBudget.setActualSpending(new BigDecimal("1900.00"));
        assertEquals(BudgetStatus.UNDER_BUDGET, testBudget.getStatus());

        // Test budget at limit (between 95% and 105%)
        testBudget.setActualSpending(new BigDecimal("2000.00"));
        assertEquals(BudgetStatus.AT_LIMIT, testBudget.getStatus());

        // Test budget over spending (105% of budget)
        testBudget.setActualSpending(new BigDecimal("2100.00"));
        assertEquals(BudgetStatus.OVER_BUDGET, testBudget.getStatus());
    }

    @Test
    void testBudgetPercentageCalculation() {
        testBudget.setTotalBudget(new BigDecimal("1000.00"));
        testBudgetCategory.setBudgetAmount(new BigDecimal("250.00"));
        
        assertEquals(25.0, testBudgetCategory.calculatePercentage(testBudget.getTotalBudget()));
    }

    @Test
    void testBudgetAmountCalculation() {
        testBudget.setTotalBudget(new BigDecimal("1000.00"));
        testBudgetCategory.setBudgetPercentage(30.0);
        
        assertEquals(new BigDecimal("300.00"), testBudgetCategory.calculateAmount(testBudget.getTotalBudget()));
    }

    @Test
    void testBudgetHelperMethods() {
        // Test isCurrentMonth
        Budget currentBudget = Budget.builder()
                .user(testUser)
                .yearMonth(YearMonth.now())
                .totalBudget(new BigDecimal("1000.00"))
                .build();
        assertTrue(currentBudget.isCurrentMonth());

        // Test isFutureMonth
        Budget futureBudget = Budget.builder()
                .user(testUser)
                .yearMonth(YearMonth.now().plusMonths(1))
                .totalBudget(new BigDecimal("1000.00"))
                .build();
        assertTrue(futureBudget.isFutureMonth());

        // Test isPastMonth
        Budget pastBudget = Budget.builder()
                .user(testUser)
                .yearMonth(YearMonth.now().minusMonths(1))
                .totalBudget(new BigDecimal("1000.00"))
                .build();
        assertTrue(pastBudget.isPastMonth());
    }

    @Test
    void testBudgetCategoryHelperMethods() {
        testBudgetCategory.setActualAmount(new BigDecimal("300.00"));
        
        // Test remaining amount
        assertEquals(new BigDecimal("200.00"), testBudgetCategory.getRemainingAmount());
        
        // Test spending percentage
        assertEquals(60.0, testBudgetCategory.getSpendingPercentage());
        
        // Test isUnderBudget
        assertTrue(testBudgetCategory.isUnderBudget());
        
        // Test isOverBudget
        testBudgetCategory.setActualAmount(new BigDecimal("600.00"));
        assertTrue(testBudgetCategory.isOverBudget());
    }
} 