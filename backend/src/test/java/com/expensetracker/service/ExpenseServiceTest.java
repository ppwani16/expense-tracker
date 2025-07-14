package com.expensetracker.service;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.dto.ExpenseSummary;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private ExpenseRequest expenseRequest;
    private Expense expense;

    @BeforeEach
    void setUp() {
        expenseRequest = new ExpenseRequest("Test expense", new BigDecimal("100.00"), 
                                          "Food", LocalDateTime.now());
        expense = new Expense("Test expense", new BigDecimal("100.00"), 
                            "Food", LocalDateTime.now());
        expense.setId(1L);
    }

    @Test
    @DisplayName("Should create expense successfully")
    void testCreateExpense() {
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        
        ExpenseResponse response = expenseService.createExpense(expenseRequest);
        
        assertNotNull(response);
        assertEquals("Test expense", response.getDescription());
        assertEquals(new BigDecimal("100.00"), response.getAmount());
        assertEquals("Food", response.getCategory());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    @DisplayName("Should update expense successfully")
    void testUpdateExpense() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        
        ExpenseResponse response = expenseService.updateExpense(1L, expenseRequest);
        
        assertNotNull(response);
        assertEquals("Test expense", response.getDescription());
        verify(expenseRepository, times(1)).findById(1L);
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent expense")
    void testUpdateExpenseNotFound() {
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> {
            expenseService.updateExpense(1L, expenseRequest);
        });
    }

    @Test
    @DisplayName("Should delete expense successfully")
    void testDeleteExpense() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        
        expenseService.deleteExpense(1L);
        
        verify(expenseRepository, times(1)).findById(1L);
        verify(expenseRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent expense")
    void testDeleteExpenseNotFound() {
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> {
            expenseService.deleteExpense(1L);
        });
    }

    @Test
    @DisplayName("Should get expense by ID")
    void testGetExpenseById() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        
        ExpenseResponse response = expenseService.getExpenseById(1L);
        
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test expense", response.getDescription());
        verify(expenseRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should get all expenses")
    void testGetAllExpenses() {
        List<Expense> expenses = Arrays.asList(expense);
        when(expenseRepository.findAll()).thenReturn(expenses);
        
        List<ExpenseResponse> responses = expenseService.getAllExpenses();
        
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test expense", responses.get(0).getDescription());
        verify(expenseRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get expense summary")
    void testGetExpenseSummary() {
        List<Expense> expenses = Arrays.asList(expense);
        when(expenseRepository.findAll()).thenReturn(expenses);
        when(expenseRepository.findByMonth(anyInt(), anyInt())).thenReturn(expenses);
        when(expenseRepository.findByYear(anyInt())).thenReturn(expenses);
        
        ExpenseSummary summary = expenseService.getExpenseSummary();
        
        assertNotNull(summary);
        assertEquals(new BigDecimal("100.00"), summary.getTotalExpenses());
        assertEquals(new BigDecimal("100.00"), summary.getMonthlyExpenses());
        assertEquals(new BigDecimal("100.00"), summary.getYearlyExpenses());
        assertNotNull(summary.getExpensesByCategory());
        verify(expenseRepository, times(1)).findAll();
    }
}
