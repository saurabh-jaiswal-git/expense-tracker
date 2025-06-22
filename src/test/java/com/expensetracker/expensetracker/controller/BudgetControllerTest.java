package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.dto.*;
import com.expensetracker.expensetracker.entity.*;
import com.expensetracker.expensetracker.service.BudgetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
class BudgetControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private BudgetService budgetService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private User testUser;
    private Category testCategory;
    private Budget testBudget;
    private BudgetCategory testBudgetCategory;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

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
    void createBudget_Success() throws Exception {
        // Given
        BudgetRequest request = new BudgetRequest();
        request.setYearMonth(YearMonth.of(2025, 6));
        request.setTotalBudget(new BigDecimal("1000.00"));
        request.setNotes("Test budget");

        when(budgetService.createBudget(anyLong(), any(YearMonth.class), any(BigDecimal.class), anyString()))
                .thenReturn(testBudget);

        // When & Then
        mockMvc.perform(post("/api/budgets")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalBudget").value(1000.0))
                .andExpect(jsonPath("$.notes").value("Test budget"));
    }

    @Test
    void getBudget_Success() throws Exception {
        // Given
        when(budgetService.getBudgetById(1L)).thenReturn(Optional.of(testBudget));

        // When & Then
        mockMvc.perform(get("/api/budgets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalBudget").value(1000.0));
    }

    @Test
    void getBudget_NotFound() throws Exception {
        // Given
        when(budgetService.getBudgetById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/budgets/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addBudgetCategory_Success() throws Exception {
        // Given
        BudgetCategoryRequest request = new BudgetCategoryRequest();
        request.setCategoryId(1L);
        request.setBudgetAmount(new BigDecimal("300.00"));
        request.setBudgetPercentage(30.0);

        when(budgetService.addBudgetCategory(anyLong(), anyLong(), any(BigDecimal.class), anyDouble()))
                .thenReturn(testBudgetCategory);

        // When & Then
        mockMvc.perform(post("/api/budgets/1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.budgetAmount").value(300.0));
    }

    // ========== INPUT VALIDATION ERROR SCENARIOS ==========

    @Test
    void createBudget_NullUserId_ThrowsException() throws Exception {
        // Given
        BudgetRequest request = new BudgetRequest();
        request.setYearMonth(YearMonth.of(2025, 6));
        request.setTotalBudget(new BigDecimal("1000.00"));

        when(budgetService.createBudget(anyLong(), any(YearMonth.class), any(BigDecimal.class), anyString()))
                .thenThrow(new IllegalArgumentException("User ID cannot be null"));

        // When & Then
        mockMvc.perform(post("/api/budgets")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBudget_NullTotalBudget_ThrowsException() throws Exception {
        // Given
        BudgetRequest request = new BudgetRequest();
        request.setYearMonth(YearMonth.of(2025, 6));
        // totalBudget is null

        // When & Then
        mockMvc.perform(post("/api/budgets")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBudget_ZeroTotalBudget_ThrowsException() throws Exception {
        // Given
        BudgetRequest request = new BudgetRequest();
        request.setYearMonth(YearMonth.of(2025, 6));
        request.setTotalBudget(BigDecimal.ZERO);

        // When & Then
        mockMvc.perform(post("/api/budgets")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBudget_NegativeTotalBudget_ThrowsException() throws Exception {
        // Given
        BudgetRequest request = new BudgetRequest();
        request.setYearMonth(YearMonth.of(2025, 6));
        request.setTotalBudget(new BigDecimal("-100.00"));

        // When & Then
        mockMvc.perform(post("/api/budgets")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBudget_NullYearMonth_ThrowsException() throws Exception {
        // Given
        BudgetRequest request = new BudgetRequest();
        request.setTotalBudget(new BigDecimal("1000.00"));
        // yearMonth is null

        // When & Then
        mockMvc.perform(post("/api/budgets")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBudgetCategory_NullCategoryId_ThrowsException() throws Exception {
        // Given
        BudgetCategoryRequest request = new BudgetCategoryRequest();
        request.setBudgetAmount(new BigDecimal("300.00"));
        // categoryId is null

        // When & Then
        mockMvc.perform(post("/api/budgets/1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBudgetCategory_NullBudgetAmount_ThrowsException() throws Exception {
        // Given
        BudgetCategoryRequest request = new BudgetCategoryRequest();
        request.setCategoryId(1L);
        // budgetAmount is null

        // When & Then
        mockMvc.perform(post("/api/budgets/1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBudgetCategory_ZeroBudgetAmount_ThrowsException() throws Exception {
        // Given
        BudgetCategoryRequest request = new BudgetCategoryRequest();
        request.setCategoryId(1L);
        request.setBudgetAmount(BigDecimal.ZERO);

        // When & Then
        mockMvc.perform(post("/api/budgets/1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBudgetCategory_NegativeBudgetAmount_ThrowsException() throws Exception {
        // Given
        BudgetCategoryRequest request = new BudgetCategoryRequest();
        request.setCategoryId(1L);
        request.setBudgetAmount(new BigDecimal("-100.00"));

        // When & Then
        mockMvc.perform(post("/api/budgets/1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ========== BUSINESS LOGIC ERROR SCENARIOS ==========

    @Test
    void createBudget_UserNotFound_ThrowsException() throws Exception {
        // Given
        BudgetRequest request = new BudgetRequest();
        request.setYearMonth(YearMonth.of(2025, 6));
        request.setTotalBudget(new BigDecimal("1000.00"));

        when(budgetService.createBudget(anyLong(), any(YearMonth.class), any(BigDecimal.class), anyString()))
                .thenThrow(new IllegalArgumentException("User not found with id: 1"));

        // When & Then
        mockMvc.perform(post("/api/budgets")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBudget_DuplicateBudget_ThrowsException() throws Exception {
        // Given
        BudgetRequest request = new BudgetRequest();
        request.setYearMonth(YearMonth.of(2025, 6));
        request.setTotalBudget(new BigDecimal("1000.00"));

        when(budgetService.createBudget(anyLong(), any(YearMonth.class), any(BigDecimal.class), anyString()))
                .thenThrow(new IllegalArgumentException("Budget already exists for user 1 and month 2025-06"));

        // When & Then
        mockMvc.perform(post("/api/budgets")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBudgetCategory_BudgetNotFound_ThrowsException() throws Exception {
        // Given
        BudgetCategoryRequest request = new BudgetCategoryRequest();
        request.setCategoryId(1L);
        request.setBudgetAmount(new BigDecimal("300.00"));

        when(budgetService.addBudgetCategory(anyLong(), anyLong(), any(BigDecimal.class), anyDouble()))
                .thenThrow(new IllegalArgumentException("Budget not found with id: 1"));

        // When & Then
        mockMvc.perform(post("/api/budgets/1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBudgetCategory_CategoryNotFound_ThrowsException() throws Exception {
        // Given
        BudgetCategoryRequest request = new BudgetCategoryRequest();
        request.setCategoryId(1L);
        request.setBudgetAmount(new BigDecimal("300.00"));

        when(budgetService.addBudgetCategory(anyLong(), anyLong(), any(BigDecimal.class), anyDouble()))
                .thenThrow(new IllegalArgumentException("Category not found with id: 1"));

        // When & Then
        mockMvc.perform(post("/api/budgets/1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBudgetCategory_CategoryAlreadyExists_ThrowsException() throws Exception {
        // Given
        BudgetCategoryRequest request = new BudgetCategoryRequest();
        request.setCategoryId(1L);
        request.setBudgetAmount(new BigDecimal("300.00"));

        when(budgetService.addBudgetCategory(anyLong(), anyLong(), any(BigDecimal.class), anyDouble()))
                .thenThrow(new IllegalArgumentException("Category 1 already exists in budget 1"));

        // When & Then
        mockMvc.perform(post("/api/budgets/1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBudgetCategory_CategoryAmountExceedsTotalBudget_ThrowsException() throws Exception {
        // Given
        BudgetCategoryRequest request = new BudgetCategoryRequest();
        request.setCategoryId(1L);
        request.setBudgetAmount(new BigDecimal("1500.00")); // Exceeds total budget

        when(budgetService.addBudgetCategory(anyLong(), anyLong(), any(BigDecimal.class), anyDouble()))
                .thenThrow(new IllegalArgumentException("Category budget amount cannot exceed total budget amount"));

        // When & Then
        mockMvc.perform(post("/api/budgets/1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ========== UPDATE SCENARIOS ==========

    @Test
    void updateBudget_Success() throws Exception {
        // Given
        BudgetUpdateRequest request = new BudgetUpdateRequest();
        request.setTotalBudget(new BigDecimal("1500.00"));
        request.setNotes("Updated budget");

        Budget updatedBudget = Budget.builder()
                .id(1L)
                .user(testUser)
                .yearMonth(YearMonth.of(2025, 6))
                .totalBudget(new BigDecimal("1500.00"))
                .actualSpending(new BigDecimal("500.00"))
                .isActive(true)
                .notes("Updated budget")
                .build();

        when(budgetService.updateBudget(anyLong(), any(BigDecimal.class), anyString()))
                .thenReturn(updatedBudget);

        // When & Then
        mockMvc.perform(put("/api/budgets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBudget").value(1500.0))
                .andExpect(jsonPath("$.notes").value("Updated budget"));
    }

    @Test
    void updateBudget_NotFound_ThrowsException() throws Exception {
        // Given
        BudgetUpdateRequest request = new BudgetUpdateRequest();
        request.setTotalBudget(new BigDecimal("1500.00"));

        when(budgetService.updateBudget(anyLong(), any(BigDecimal.class), anyString()))
                .thenThrow(new IllegalArgumentException("Budget not found with id: 1"));

        // When & Then
        mockMvc.perform(put("/api/budgets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBudgetSpending_Success() throws Exception {
        // Given
        BigDecimal actualSpending = new BigDecimal("800.00");

        Budget updatedBudget = Budget.builder()
                .id(1L)
                .user(testUser)
                .yearMonth(YearMonth.of(2025, 6))
                .totalBudget(new BigDecimal("1000.00"))
                .actualSpending(actualSpending)
                .isActive(true)
                .notes("Test budget")
                .build();

        when(budgetService.updateBudgetActualSpending(anyLong(), any(BigDecimal.class)))
                .thenReturn(updatedBudget);

        // When & Then
        mockMvc.perform(put("/api/budgets/1/spending")
                        .param("actualSpending", "800.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actualSpending").value(800.0));
    }

    @Test
    void updateBudgetSpending_NegativeAmount_ThrowsException() throws Exception {
        // Given
        when(budgetService.updateBudgetActualSpending(anyLong(), any(BigDecimal.class)))
                .thenThrow(new IllegalArgumentException("Spending amount cannot be negative"));

        // When & Then
        mockMvc.perform(put("/api/budgets/1/spending")
                        .param("actualSpending", "-100.00"))
                .andExpect(status().isBadRequest());
    }

    // ========== LIST SCENARIOS ==========

    @Test
    void getAllBudgetsByUser_Success() throws Exception {
        // Given
        List<Budget> budgets = Arrays.asList(testBudget);
        when(budgetService.getAllBudgetsByUser(1L)).thenReturn(budgets);

        // When & Then
        mockMvc.perform(get("/api/budgets/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].totalBudget").value(1000.0));
    }

    @Test
    void getBudgetCategories_Success() throws Exception {
        // Given
        List<BudgetCategory> categories = Arrays.asList(testBudgetCategory);
        when(budgetService.getBudgetCategories(1L)).thenReturn(categories);

        // When & Then
        mockMvc.perform(get("/api/budgets/1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].budgetAmount").value(300.0));
    }

    @Test
    void getBudgetStatus_Success() throws Exception {
        // Given
        when(budgetService.getBudgetStatus(1L)).thenReturn(BudgetStatus.UNDER_BUDGET);

        // When & Then
        mockMvc.perform(get("/api/budgets/1/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("\"UNDER_BUDGET\""));
    }

    @Test
    void getOverBudgetBudgets_Success() throws Exception {
        // Given
        List<Budget> overBudgets = Arrays.asList(testBudget);
        when(budgetService.getOverBudgetBudgets(1L)).thenReturn(overBudgets);

        // When & Then
        mockMvc.perform(get("/api/budgets/user/1/over-budget"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getActiveBudgets_Success() throws Exception {
        // Given
        List<Budget> activeBudgets = Arrays.asList(testBudget);
        when(budgetService.getActiveBudgets(1L)).thenReturn(activeBudgets);

        // When & Then
        mockMvc.perform(get("/api/budgets/user/1/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void countActiveBudgets_Success() throws Exception {
        // Given
        when(budgetService.countActiveBudgets(1L)).thenReturn(3L);

        // When & Then
        mockMvc.perform(get("/api/budgets/user/1/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    // ========== DELETE SCENARIOS ==========

    @Test
    void deleteBudget_Success() throws Exception {
        // Given
        doNothing().when(budgetService).deleteBudget(1L);

        // When & Then
        mockMvc.perform(delete("/api/budgets/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBudget_NotFound_ThrowsException() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("Budget not found with id: 1"))
                .when(budgetService).deleteBudget(1L);

        // When & Then
        mockMvc.perform(delete("/api/budgets/1"))
                .andExpect(status().isNotFound());
    }
} 