package com.expensetracker.expensetracker.repository;

import com.expensetracker.expensetracker.entity.Budget;
import com.expensetracker.expensetracker.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class BudgetRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BudgetRepository budgetRepository;

    @Test
    void testSaveAndFindBudget() {
        // Create test user
        User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password123")
                .build();
        user = entityManager.persistAndFlush(user);

        // Create test budget
        YearMonth thisMonth = YearMonth.now();
        Budget budget = Budget.builder()
                .user(user)
                .yearMonth(thisMonth)
                .totalBudget(new BigDecimal("2000.00"))
                .isActive(true)
                .build();

        Budget savedBudget = budgetRepository.save(budget);
        assertNotNull(savedBudget.getId());

        // Find by ID
        Optional<Budget> foundBudget = budgetRepository.findById(savedBudget.getId());
        assertTrue(foundBudget.isPresent());
        assertEquals(savedBudget.getId(), foundBudget.get().getId());
    }

    @Test
    void testFindByUserIdAndYearMonth() {
        // Create test user
        User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password123")
                .build();
        user = entityManager.persistAndFlush(user);

        // Create test budget
        YearMonth thisMonth = YearMonth.now();
        Budget budget = Budget.builder()
                .user(user)
                .yearMonth(thisMonth)
                .totalBudget(new BigDecimal("2000.00"))
                .isActive(true)
                .build();
        budget = entityManager.persistAndFlush(budget);

        // Find by user ID and year month
        Optional<Budget> foundBudget = budgetRepository.findByUserIdAndYearMonth(user.getId(), thisMonth);
        assertTrue(foundBudget.isPresent());
        assertEquals(budget.getId(), foundBudget.get().getId());
    }

    @Test
    void testFindByUserIdOrderByYearMonthDesc() {
        // Create test user
        User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password123")
                .build();
        user = entityManager.persistAndFlush(user);

        // Create multiple budgets
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        YearMonth thisMonth = YearMonth.now();
        Budget budget1 = Budget.builder()
                .user(user)
                .yearMonth(lastMonth)
                .totalBudget(new BigDecimal("2000.00"))
                .isActive(true)
                .build();
        entityManager.persistAndFlush(budget1);

        Budget budget2 = Budget.builder()
                .user(user)
                .yearMonth(thisMonth)
                .totalBudget(new BigDecimal("2500.00"))
                .isActive(true)
                .build();
        entityManager.persistAndFlush(budget2);

        // Find all budgets for user
        List<Budget> budgets = budgetRepository.findByUserIdOrderByYearMonthDesc(user.getId());
        assertEquals(2, budgets.size());
        assertEquals(thisMonth, budgets.get(0).getYearMonth()); // Most recent first
        assertEquals(lastMonth, budgets.get(1).getYearMonth());
    }

    @Test
    void testFindActiveBudgets() {
        // Create test user
        User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password123")
                .build();
        user = entityManager.persistAndFlush(user);

        // Create active and inactive budgets
        YearMonth thisMonth = YearMonth.now();
        YearMonth nextMonth = YearMonth.now().plusMonths(1);
        Budget activeBudget = Budget.builder()
                .user(user)
                .yearMonth(thisMonth)
                .totalBudget(new BigDecimal("2000.00"))
                .isActive(true)
                .build();
        entityManager.persistAndFlush(activeBudget);

        Budget inactiveBudget = Budget.builder()
                .user(user)
                .yearMonth(nextMonth)
                .totalBudget(new BigDecimal("2500.00"))
                .isActive(false)
                .build();
        entityManager.persistAndFlush(inactiveBudget);

        // Find active budgets only
        List<Budget> activeBudgets = budgetRepository.findByUserIdAndIsActiveTrueOrderByYearMonthDesc(user.getId());
        assertEquals(1, activeBudgets.size());
        assertTrue(activeBudgets.get(0).getIsActive());
    }

    @Test
    void testExistsByUserIdAndYearMonth() {
        // Create test user
        User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password123")
                .build();
        user = entityManager.persistAndFlush(user);

        // Create test budget
        YearMonth thisMonth = YearMonth.now();
        Budget budget = Budget.builder()
                .user(user)
                .yearMonth(thisMonth)
                .totalBudget(new BigDecimal("2000.00"))
                .isActive(true)
                .build();
        entityManager.persistAndFlush(budget);

        // Check if budget exists
        assertTrue(budgetRepository.existsByUserIdAndYearMonth(user.getId(), thisMonth));
        assertFalse(budgetRepository.existsByUserIdAndYearMonth(user.getId(), thisMonth.plusMonths(1)));
    }

    @Test
    void testFindOverBudgetBudgets() {
        // Create test user
        User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password123")
                .build();
        user = entityManager.persistAndFlush(user);

        // Create over budget
        YearMonth thisMonth = YearMonth.now();
        YearMonth nextMonth = YearMonth.now().plusMonths(1);
        Budget overBudget = Budget.builder()
                .user(user)
                .yearMonth(thisMonth)
                .totalBudget(new BigDecimal("2000.00"))
                .actualSpending(new BigDecimal("2500.00"))
                .isActive(true)
                .build();
        entityManager.persistAndFlush(overBudget);

        // Create under budget
        Budget underBudget = Budget.builder()
                .user(user)
                .yearMonth(nextMonth)
                .totalBudget(new BigDecimal("2000.00"))
                .actualSpending(new BigDecimal("1500.00"))
                .isActive(true)
                .build();
        entityManager.persistAndFlush(underBudget);

        // Find over budget budgets
        List<Budget> overBudgets = budgetRepository.findOverBudgetBudgets(user.getId());
        assertEquals(1, overBudgets.size());
        assertEquals(overBudget.getId(), overBudgets.get(0).getId());
    }

    @Test
    void testCountActiveBudgets() {
        // Create test user
        User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password123")
                .build();
        user = entityManager.persistAndFlush(user);

        // Create multiple budgets
        YearMonth thisMonth = YearMonth.now();
        YearMonth nextMonth = YearMonth.now().plusMonths(1);
        YearMonth nextNextMonth = YearMonth.now().plusMonths(2);
        Budget budget1 = Budget.builder()
                .user(user)
                .yearMonth(thisMonth)
                .totalBudget(new BigDecimal("2000.00"))
                .isActive(true)
                .build();
        entityManager.persistAndFlush(budget1);

        Budget budget2 = Budget.builder()
                .user(user)
                .yearMonth(nextMonth)
                .totalBudget(new BigDecimal("2500.00"))
                .isActive(true)
                .build();
        entityManager.persistAndFlush(budget2);

        Budget inactiveBudget = Budget.builder()
                .user(user)
                .yearMonth(nextNextMonth)
                .totalBudget(new BigDecimal("3000.00"))
                .isActive(false)
                .build();
        entityManager.persistAndFlush(inactiveBudget);

        // Count active budgets
        long activeCount = budgetRepository.countByUserIdAndIsActiveTrue(user.getId());
        assertEquals(2, activeCount);
    }
} 