package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.dto.GoalRequest;
import com.expensetracker.expensetracker.dto.GoalResponse;
import com.expensetracker.expensetracker.dto.GoalProgressRequest;
import com.expensetracker.expensetracker.entity.Goal;
import com.expensetracker.expensetracker.entity.GoalStatus;
import com.expensetracker.expensetracker.entity.GoalType;
import com.expensetracker.expensetracker.repository.GoalRepository;
import com.expensetracker.expensetracker.repository.UserRepository;
import com.expensetracker.expensetracker.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalService goalService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password123")
                .build();
    }

    // Tests will be added here for various scenarios

    @Test
    void testCreateAndUpdateSimpleSavingsGoal() {
        // Arrange
        GoalRequest request = new GoalRequest();
        request.setName("Save for Phone");
        request.setDescription("Buy a new phone");
        request.setTargetAmount(new BigDecimal("10000"));
        request.setGoalType(GoalType.SAVINGS);
        request.setStartDate(LocalDate.now());
        request.setTargetDate(LocalDate.now().plusMonths(5));

        Goal savedGoal = Goal.builder()
                .id(1L)
                .user(testUser)
                .name("Save for Phone")
                .description("Buy a new phone")
                .targetAmount(new BigDecimal("10000"))
                .currentAmount(BigDecimal.ZERO)
                .goalType(GoalType.SAVINGS)
                .targetDate(request.getTargetDate())
                .startDate(request.getStartDate())
                .status(GoalStatus.ACTIVE)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(goalRepository.save(any(Goal.class))).thenReturn(savedGoal);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(savedGoal));

        // Act: Create goal
        GoalResponse response = goalService.createGoal(1L, request);

        // Assert: Goal created
        assertNotNull(response);
        assertEquals("Save for Phone", response.getName());
        assertEquals(new BigDecimal("10000"), response.getTargetAmount());
        assertEquals(BigDecimal.ZERO, response.getCurrentAmount());
        assertEquals(GoalStatus.ACTIVE, response.getStatus());

        // Act: Update progress (simulate saving 2000)
        GoalProgressRequest progressRequest = new GoalProgressRequest();
        progressRequest.setAmount(new BigDecimal("2000"));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));
        GoalResponse progressResponse = goalService.updateGoalProgress(1L, progressRequest);

        // Assert: Progress updated
        assertEquals(new BigDecimal("2000"), progressResponse.getCurrentAmount());
        assertEquals(GoalStatus.ACTIVE, progressResponse.getStatus());

        // Act: Update progress to reach target
        progressRequest.setAmount(new BigDecimal("8000"));
        progressResponse = goalService.updateGoalProgress(1L, progressRequest);

        // Assert: Goal completed
        assertEquals(new BigDecimal("10000"), progressResponse.getCurrentAmount());
        assertEquals(GoalStatus.COMPLETED, progressResponse.getStatus());
    }

    @Test
    void testDebtPayoffGoalWithIrregularPayments() {
        // Arrange
        GoalRequest request = new GoalRequest();
        request.setName("Pay off Credit Card");
        request.setDescription("Clear outstanding credit card debt");
        request.setTargetAmount(new BigDecimal("20000"));
        request.setGoalType(GoalType.DEBT_PAYOFF);
        request.setStartDate(LocalDate.now());
        request.setTargetDate(LocalDate.now().plusMonths(6));

        BigDecimal[] currentAmount = {BigDecimal.ZERO};
        GoalStatus[] status = {GoalStatus.ACTIVE};

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> {
            Goal g = invocation.getArgument(0);
            currentAmount[0] = g.getCurrentAmount();
            status[0] = g.getStatus();
            return g;
        });
        when(goalRepository.findById(2L)).thenAnswer(invocation -> Optional.of(
            Goal.builder()
                .id(2L)
                .user(testUser)
                .name("Pay off Credit Card")
                .description("Clear outstanding credit card debt")
                .targetAmount(new BigDecimal("20000"))
                .currentAmount(currentAmount[0])
                .goalType(GoalType.DEBT_PAYOFF)
                .targetDate(request.getTargetDate())
                .startDate(request.getStartDate())
                .status(status[0])
                .build()
        ));

        // Act: Create goal
        GoalResponse response = goalService.createGoal(1L, request);
        assertNotNull(response);
        assertEquals("Pay off Credit Card", response.getName());
        assertEquals(GoalStatus.ACTIVE, response.getStatus());
        assertEquals(BigDecimal.ZERO, response.getCurrentAmount());

        // First payment: 2000
        GoalProgressRequest progressRequest = new GoalProgressRequest();
        progressRequest.setAmount(new BigDecimal("2000"));
        GoalResponse progressResponse = goalService.updateGoalProgress(2L, progressRequest);
        assertEquals(new BigDecimal("2000"), progressResponse.getCurrentAmount());
        assertEquals(GoalStatus.ACTIVE, progressResponse.getStatus());

        // Second payment: 5000
        progressRequest.setAmount(new BigDecimal("5000"));
        progressResponse = goalService.updateGoalProgress(2L, progressRequest);
        assertEquals(new BigDecimal("7000"), progressResponse.getCurrentAmount());
        assertEquals(GoalStatus.ACTIVE, progressResponse.getStatus());

        // Third payment: 8000
        progressRequest.setAmount(new BigDecimal("8000"));
        progressResponse = goalService.updateGoalProgress(2L, progressRequest);
        assertEquals(new BigDecimal("15000"), progressResponse.getCurrentAmount());
        assertEquals(GoalStatus.ACTIVE, progressResponse.getStatus());

        // Final payment: 6000 (should complete the goal)
        progressRequest.setAmount(new BigDecimal("6000"));
        progressResponse = goalService.updateGoalProgress(2L, progressRequest);
        assertEquals(new BigDecimal("21000"), progressResponse.getCurrentAmount());
        assertEquals(GoalStatus.COMPLETED, progressResponse.getStatus());
    }

    @Test
    void testMultipleGoalsTrackedInParallel() {
        // Arrange
        GoalRequest vacationGoalRequest = new GoalRequest();
        vacationGoalRequest.setName("Save for Vacation");
        vacationGoalRequest.setDescription("Trip to Goa");
        vacationGoalRequest.setTargetAmount(new BigDecimal("50000"));
        vacationGoalRequest.setGoalType(GoalType.SAVINGS);
        vacationGoalRequest.setStartDate(LocalDate.now());
        vacationGoalRequest.setTargetDate(LocalDate.now().plusMonths(12));

        GoalRequest laptopGoalRequest = new GoalRequest();
        laptopGoalRequest.setName("Buy Laptop");
        laptopGoalRequest.setDescription("New MacBook");
        laptopGoalRequest.setTargetAmount(new BigDecimal("80000"));
        laptopGoalRequest.setGoalType(GoalType.SAVINGS);
        laptopGoalRequest.setStartDate(LocalDate.now());
        laptopGoalRequest.setTargetDate(LocalDate.now().plusMonths(10));

        BigDecimal[] vacationAmount = {BigDecimal.ZERO};
        GoalStatus[] vacationStatus = {GoalStatus.ACTIVE};
        BigDecimal[] laptopAmount = {BigDecimal.ZERO};
        GoalStatus[] laptopStatus = {GoalStatus.ACTIVE};

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> {
            Goal g = invocation.getArgument(0);
            if (g.getName().equals("Save for Vacation")) {
                vacationAmount[0] = g.getCurrentAmount();
                vacationStatus[0] = g.getStatus();
            } else if (g.getName().equals("Buy Laptop")) {
                laptopAmount[0] = g.getCurrentAmount();
                laptopStatus[0] = g.getStatus();
            }
            return g;
        });
        when(goalRepository.findById(3L)).thenAnswer(invocation -> Optional.of(
            Goal.builder()
                .id(3L)
                .user(testUser)
                .name("Save for Vacation")
                .description("Trip to Goa")
                .targetAmount(new BigDecimal("50000"))
                .currentAmount(vacationAmount[0])
                .goalType(GoalType.SAVINGS)
                .targetDate(vacationGoalRequest.getTargetDate())
                .startDate(vacationGoalRequest.getStartDate())
                .status(vacationStatus[0])
                .build()
        ));
        when(goalRepository.findById(4L)).thenAnswer(invocation -> Optional.of(
            Goal.builder()
                .id(4L)
                .user(testUser)
                .name("Buy Laptop")
                .description("New MacBook")
                .targetAmount(new BigDecimal("80000"))
                .currentAmount(laptopAmount[0])
                .goalType(GoalType.SAVINGS)
                .targetDate(laptopGoalRequest.getTargetDate())
                .startDate(laptopGoalRequest.getStartDate())
                .status(laptopStatus[0])
                .build()
        ));

        // Act: Create both goals
        GoalResponse vacationGoalResponse = goalService.createGoal(1L, vacationGoalRequest);
        GoalResponse laptopGoalResponse = goalService.createGoal(1L, laptopGoalRequest);
        assertNotNull(vacationGoalResponse);
        assertNotNull(laptopGoalResponse);
        assertEquals("Save for Vacation", vacationGoalResponse.getName());
        assertEquals("Buy Laptop", laptopGoalResponse.getName());
        assertEquals(GoalStatus.ACTIVE, vacationGoalResponse.getStatus());
        assertEquals(GoalStatus.ACTIVE, laptopGoalResponse.getStatus());

        // Update progress for vacation goal
        GoalProgressRequest vacationProgress = new GoalProgressRequest();
        vacationProgress.setAmount(new BigDecimal("20000"));
        GoalResponse vacationProgressResponse = goalService.updateGoalProgress(3L, vacationProgress);
        assertEquals(new BigDecimal("20000"), vacationProgressResponse.getCurrentAmount());
        assertEquals(GoalStatus.ACTIVE, vacationProgressResponse.getStatus());

        // Update progress for laptop goal
        GoalProgressRequest laptopProgress = new GoalProgressRequest();
        laptopProgress.setAmount(new BigDecimal("30000"));
        GoalResponse laptopProgressResponse = goalService.updateGoalProgress(4L, laptopProgress);
        assertEquals(new BigDecimal("30000"), laptopProgressResponse.getCurrentAmount());
        assertEquals(GoalStatus.ACTIVE, laptopProgressResponse.getStatus());

        // Complete vacation goal
        vacationProgress.setAmount(new BigDecimal("30000"));
        vacationProgressResponse = goalService.updateGoalProgress(3L, vacationProgress);
        assertEquals(new BigDecimal("50000"), vacationProgressResponse.getCurrentAmount());
        assertEquals(GoalStatus.COMPLETED, vacationProgressResponse.getStatus());

        // Laptop goal should still be active
        assertEquals(GoalStatus.ACTIVE, laptopStatus[0]);
    }

    @Test
    void testGoalWithNoBudget_ProgressTrackingOnly() {
        // Arrange
        GoalRequest request = new GoalRequest();
        request.setName("Festival Savings");
        request.setDescription("Save for Diwali festival");
        request.setTargetAmount(new BigDecimal("5000"));
        request.setGoalType(GoalType.SAVINGS);
        request.setStartDate(LocalDate.now());
        request.setTargetDate(LocalDate.now().plusMonths(3));

        BigDecimal[] currentAmount = {BigDecimal.ZERO};
        GoalStatus[] status = {GoalStatus.ACTIVE};

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> {
            Goal g = invocation.getArgument(0);
            currentAmount[0] = g.getCurrentAmount();
            status[0] = g.getStatus();
            return g;
        });
        when(goalRepository.findById(5L)).thenAnswer(invocation -> Optional.of(
            Goal.builder()
                .id(5L)
                .user(testUser)
                .name("Festival Savings")
                .description("Save for Diwali festival")
                .targetAmount(new BigDecimal("5000"))
                .currentAmount(currentAmount[0])
                .goalType(GoalType.SAVINGS)
                .targetDate(request.getTargetDate())
                .startDate(request.getStartDate())
                .status(status[0])
                .build()
        ));

        // Act: Create goal
        GoalResponse response = goalService.createGoal(1L, request);
        assertNotNull(response);
        assertEquals("Festival Savings", response.getName());
        assertEquals(GoalStatus.ACTIVE, response.getStatus());
        assertEquals(BigDecimal.ZERO, response.getCurrentAmount());

        // Update progress (partial)
        GoalProgressRequest progressRequest = new GoalProgressRequest();
        progressRequest.setAmount(new BigDecimal("2000"));
        GoalResponse progressResponse = goalService.updateGoalProgress(5L, progressRequest);
        assertEquals(new BigDecimal("2000"), progressResponse.getCurrentAmount());
        assertEquals(GoalStatus.ACTIVE, progressResponse.getStatus());

        // Update progress to complete
        progressRequest.setAmount(new BigDecimal("3000"));
        progressResponse = goalService.updateGoalProgress(5L, progressRequest);
        assertEquals(new BigDecimal("5000"), progressResponse.getCurrentAmount());
        assertEquals(GoalStatus.COMPLETED, progressResponse.getStatus());
    }

    @Test
    void testGoalWithBudgetIntegration_SurplusUsedForGoal() {
        // Arrange
        GoalRequest request = new GoalRequest();
        request.setName("Save for Emergency Fund");
        request.setDescription("Build an emergency fund");
        request.setTargetAmount(new BigDecimal("12000"));
        request.setGoalType(GoalType.SAVINGS);
        request.setStartDate(LocalDate.now());
        request.setTargetDate(LocalDate.now().plusMonths(6));

        BigDecimal[] currentAmount = {BigDecimal.ZERO};
        GoalStatus[] status = {GoalStatus.ACTIVE};

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> {
            Goal g = invocation.getArgument(0);
            currentAmount[0] = g.getCurrentAmount();
            status[0] = g.getStatus();
            return g;
        });
        when(goalRepository.findById(6L)).thenAnswer(invocation -> Optional.of(
            Goal.builder()
                .id(6L)
                .user(testUser)
                .name("Save for Emergency Fund")
                .description("Build an emergency fund")
                .targetAmount(new BigDecimal("12000"))
                .currentAmount(currentAmount[0])
                .goalType(GoalType.SAVINGS)
                .targetDate(request.getTargetDate())
                .startDate(request.getStartDate())
                .status(status[0])
                .build()
        ));

        // Act: Create goal
        GoalResponse response = goalService.createGoal(1L, request);
        assertNotNull(response);
        assertEquals("Save for Emergency Fund", response.getName());
        assertEquals(GoalStatus.ACTIVE, response.getStatus());
        assertEquals(BigDecimal.ZERO, response.getCurrentAmount());

        // Simulate budget surplus for 3 months (e.g., 2000/month)
        BigDecimal monthlySurplus = new BigDecimal("2000");
        GoalProgressRequest progressRequest = new GoalProgressRequest();
        for (int i = 1; i <= 3; i++) {
            progressRequest.setAmount(monthlySurplus);
            GoalResponse progressResponse = goalService.updateGoalProgress(6L, progressRequest);
            assertEquals(monthlySurplus.multiply(BigDecimal.valueOf(i)), progressResponse.getCurrentAmount());
            assertEquals(GoalStatus.ACTIVE, progressResponse.getStatus());
        }

        // Simulate a larger surplus in the 4th month to complete the goal
        progressRequest.setAmount(new BigDecimal("6000"));
        GoalResponse finalProgress = goalService.updateGoalProgress(6L, progressRequest);
        assertEquals(new BigDecimal("12000"), finalProgress.getCurrentAmount());
        assertEquals(GoalStatus.COMPLETED, finalProgress.getStatus());
    }

    @Test
    void testMissedOrDelayedGoal_InsufficientProgress() {
        // Arrange
        GoalRequest request = new GoalRequest();
        request.setName("Save for Bike");
        request.setDescription("Buy a new bike");
        request.setTargetAmount(new BigDecimal("24000"));
        request.setGoalType(GoalType.SAVINGS);
        request.setStartDate(LocalDate.now());
        request.setTargetDate(LocalDate.now().plusMonths(12));

        BigDecimal[] currentAmount = {BigDecimal.ZERO};
        GoalStatus[] status = {GoalStatus.ACTIVE};

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> {
            Goal g = invocation.getArgument(0);
            currentAmount[0] = g.getCurrentAmount();
            status[0] = g.getStatus();
            return g;
        });
        when(goalRepository.findById(7L)).thenAnswer(invocation -> Optional.of(
            Goal.builder()
                .id(7L)
                .user(testUser)
                .name("Save for Bike")
                .description("Buy a new bike")
                .targetAmount(new BigDecimal("24000"))
                .currentAmount(currentAmount[0])
                .goalType(GoalType.SAVINGS)
                .targetDate(request.getTargetDate())
                .startDate(request.getStartDate())
                .status(status[0])
                .build()
        ));

        // Act: Create goal
        GoalResponse response = goalService.createGoal(1L, request);
        assertNotNull(response);
        assertEquals("Save for Bike", response.getName());
        assertEquals(GoalStatus.ACTIVE, response.getStatus());
        assertEquals(BigDecimal.ZERO, response.getCurrentAmount());

        // Simulate insufficient progress (e.g., only 4000 saved out of 24000)
        GoalProgressRequest progressRequest = new GoalProgressRequest();
        progressRequest.setAmount(new BigDecimal("4000"));
        GoalResponse progressResponse = goalService.updateGoalProgress(7L, progressRequest);
        assertEquals(new BigDecimal("4000"), progressResponse.getCurrentAmount());
        assertEquals(GoalStatus.ACTIVE, progressResponse.getStatus());

        // Simulate another small progress (e.g., 2000 more)
        progressRequest.setAmount(new BigDecimal("2000"));
        progressResponse = goalService.updateGoalProgress(7L, progressRequest);
        assertEquals(new BigDecimal("6000"), progressResponse.getCurrentAmount());
        assertEquals(GoalStatus.ACTIVE, progressResponse.getStatus());

        // The goal should still be active and not completed
        assertTrue(progressResponse.getCurrentAmount().compareTo(new BigDecimal("24000")) < 0);
        assertEquals(GoalStatus.ACTIVE, progressResponse.getStatus());
    }

    @Test
    void testOverCompletion_ProgressExceedsTarget() {
        // Arrange
        GoalRequest request = new GoalRequest();
        request.setName("Save for TV");
        request.setDescription("Buy a new TV");
        request.setTargetAmount(new BigDecimal("15000"));
        request.setGoalType(GoalType.SAVINGS);
        request.setStartDate(LocalDate.now());
        request.setTargetDate(LocalDate.now().plusMonths(4));

        BigDecimal[] currentAmount = {BigDecimal.ZERO};
        GoalStatus[] status = {GoalStatus.ACTIVE};

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> {
            Goal g = invocation.getArgument(0);
            currentAmount[0] = g.getCurrentAmount();
            status[0] = g.getStatus();
            return g;
        });
        when(goalRepository.findById(8L)).thenAnswer(invocation -> Optional.of(
            Goal.builder()
                .id(8L)
                .user(testUser)
                .name("Save for TV")
                .description("Buy a new TV")
                .targetAmount(new BigDecimal("15000"))
                .currentAmount(currentAmount[0])
                .goalType(GoalType.SAVINGS)
                .targetDate(request.getTargetDate())
                .startDate(request.getStartDate())
                .status(status[0])
                .build()
        ));

        // Act: Create goal
        GoalResponse response = goalService.createGoal(1L, request);
        assertNotNull(response);
        assertEquals("Save for TV", response.getName());
        assertEquals(GoalStatus.ACTIVE, response.getStatus());
        assertEquals(BigDecimal.ZERO, response.getCurrentAmount());

        // Over-complete the goal in one update (e.g., save 20000)
        GoalProgressRequest progressRequest = new GoalProgressRequest();
        progressRequest.setAmount(new BigDecimal("20000"));
        GoalResponse progressResponse = goalService.updateGoalProgress(8L, progressRequest);
        assertEquals(new BigDecimal("20000"), progressResponse.getCurrentAmount());
        assertEquals(GoalStatus.COMPLETED, progressResponse.getStatus());
    }

    @Test
    void testGoalWithNoProgress_NoRelevantTransactions() {
        // Arrange
        GoalRequest request = new GoalRequest();
        request.setName("Save for Course");
        request.setDescription("Online course fee");
        request.setTargetAmount(new BigDecimal("8000"));
        request.setGoalType(GoalType.SAVINGS);
        request.setStartDate(LocalDate.now());
        request.setTargetDate(LocalDate.now().plusMonths(2));

        Goal savedGoal = Goal.builder()
                .id(9L)
                .user(testUser)
                .name("Save for Course")
                .description("Online course fee")
                .targetAmount(new BigDecimal("8000"))
                .currentAmount(BigDecimal.ZERO)
                .goalType(GoalType.SAVINGS)
                .targetDate(request.getTargetDate())
                .startDate(request.getStartDate())
                .status(GoalStatus.ACTIVE)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(goalRepository.save(any(Goal.class))).thenReturn(savedGoal);
        when(goalRepository.findById(9L)).thenReturn(Optional.of(savedGoal));

        // Act: Create goal
        GoalResponse response = goalService.createGoal(1L, request);
        assertNotNull(response);
        assertEquals("Save for Course", response.getName());
        assertEquals(GoalStatus.ACTIVE, response.getStatus());
        assertEquals(BigDecimal.ZERO, response.getCurrentAmount());

        // No progress update is made
        // Assert: Goal remains active and current amount is zero
        assertEquals(GoalStatus.ACTIVE, response.getStatus());
        assertEquals(BigDecimal.ZERO, response.getCurrentAmount());
    }

    @Test
    void testUpdateGoal_DetailsChanged() {
        // Arrange
        GoalRequest createRequest = new GoalRequest();
        createRequest.setName("Save for Tablet");
        createRequest.setDescription("Buy a new tablet");
        createRequest.setTargetAmount(new BigDecimal("10000"));
        createRequest.setGoalType(GoalType.SAVINGS);
        createRequest.setStartDate(LocalDate.now());
        createRequest.setTargetDate(LocalDate.now().plusMonths(5));

        Goal savedGoal = Goal.builder()
                .id(10L)
                .user(testUser)
                .name("Save for Tablet")
                .description("Buy a new tablet")
                .targetAmount(new BigDecimal("10000"))
                .currentAmount(BigDecimal.ZERO)
                .goalType(GoalType.SAVINGS)
                .targetDate(createRequest.getTargetDate())
                .startDate(createRequest.getStartDate())
                .status(GoalStatus.ACTIVE)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(goalRepository.save(any(Goal.class))).thenReturn(savedGoal);
        when(goalRepository.findById(10L)).thenReturn(Optional.of(savedGoal));

        // Act: Create goal
        GoalResponse response = goalService.createGoal(1L, createRequest);
        assertNotNull(response);
        assertEquals("Save for Tablet", response.getName());
        assertEquals("Buy a new tablet", response.getDescription());
        assertEquals(new BigDecimal("10000"), response.getTargetAmount());
        assertEquals(GoalType.SAVINGS, response.getGoalType());

        // Prepare update request
        GoalRequest updateRequest = new GoalRequest();
        updateRequest.setName("Save for iPad");
        updateRequest.setDescription("Buy an Apple iPad");
        updateRequest.setTargetAmount(new BigDecimal("15000"));
        updateRequest.setGoalType(GoalType.INVESTMENT);
        updateRequest.setStartDate(LocalDate.now().plusDays(1));
        updateRequest.setTargetDate(LocalDate.now().plusMonths(6));

        // Act: Update goal
        GoalResponse updatedResponse = goalService.updateGoal(10L, updateRequest);
        assertNotNull(updatedResponse);
        assertEquals("Save for iPad", updatedResponse.getName());
        assertEquals("Buy an Apple iPad", updatedResponse.getDescription());
        assertEquals(new BigDecimal("15000"), updatedResponse.getTargetAmount());
        assertEquals(GoalType.INVESTMENT, updatedResponse.getGoalType());
        assertEquals(LocalDate.now().plusDays(1), updatedResponse.getStartDate());
        assertEquals(LocalDate.now().plusMonths(6), updatedResponse.getTargetDate());
    }

    @Test
    void testDeletedGoal_NotReturnedInListUserGoals() {
        // Arrange
        Goal activeGoal = Goal.builder()
                .id(11L)
                .user(testUser)
                .name("Active Goal")
                .status(GoalStatus.ACTIVE)
                .build();
        Goal deletedGoal = Goal.builder()
                .id(12L)
                .user(testUser)
                .name("Deleted Goal")
                .status(GoalStatus.DELETED)
                .build();
        when(goalRepository.findByUserId(1L)).thenReturn(List.of(activeGoal, deletedGoal));

        // Act
        List<GoalResponse> responses = goalService.listUserGoals(1L);

        // Assert
        assertEquals(1, responses.size());
        assertEquals("Active Goal", responses.get(0).getName());
    }

    @Test
    void testUpdateGoal_ThrowsOnDeletedGoal() {
        // Arrange
        Goal deletedGoal = Goal.builder()
                .id(13L)
                .user(testUser)
                .name("Deleted Goal")
                .status(GoalStatus.DELETED)
                .build();
        when(goalRepository.findById(13L)).thenReturn(Optional.of(deletedGoal));

        GoalRequest updateRequest = new GoalRequest();
        updateRequest.setName("Should Fail");
        updateRequest.setDescription("Should Fail");
        updateRequest.setTargetAmount(new BigDecimal("100"));
        updateRequest.setGoalType(GoalType.SAVINGS);
        updateRequest.setStartDate(LocalDate.now());
        updateRequest.setTargetDate(LocalDate.now().plusDays(1));

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                goalService.updateGoal(13L, updateRequest));
        assertEquals("Cannot update a deleted goal.", ex.getMessage());
    }

    @Test
    void testUpdateGoalProgress_ThrowsOnDeletedGoal() {
        // Arrange
        Goal deletedGoal = Goal.builder()
                .id(14L)
                .user(testUser)
                .name("Deleted Goal")
                .status(GoalStatus.DELETED)
                .build();
        when(goalRepository.findById(14L)).thenReturn(Optional.of(deletedGoal));

        GoalProgressRequest progressRequest = new GoalProgressRequest();
        progressRequest.setAmount(new BigDecimal("100"));

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                goalService.updateGoalProgress(14L, progressRequest));
        assertEquals("Cannot update progress on a deleted goal.", ex.getMessage());
    }
} 