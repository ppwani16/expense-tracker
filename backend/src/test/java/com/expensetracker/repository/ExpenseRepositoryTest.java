package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseRepositoryTest {

    private ExpenseRepository expenseRepository;

    @BeforeEach
    void setUp() {
        expenseRepository = new ExpenseRepository();
    }

    @Test
    @DisplayName("Should save expense and generate ID")
    void testSaveExpense() {
        Expense expense = new Expense("Test expense", new BigDecimal("100.00"), 
                                    "Food", LocalDateTime.now());
        
        Expense savedExpense = expenseRepository.save(expense);
        
        assertNotNull(savedExpense.getId());
        assertEquals("Test expense", savedExpense.getDescription());
        assertEquals(new BigDecimal("100.00"), savedExpense.getAmount());
        assertEquals("Food", savedExpense.getCategory());
    }

    @Test
    @DisplayName("Should find expense by ID")
    void testFindById() {
        Expense expense = new Expense("Test expense", new BigDecimal("100.00"), 
                                    "Food", LocalDateTime.now());
        Expense savedExpense = expenseRepository.save(expense);
        
        Optional<Expense> foundExpense = expenseRepository.findById(savedExpense.getId());
        
        assertTrue(foundExpense.isPresent());
        assertEquals(savedExpense.getId(), foundExpense.get().getId());
    }

    @Test
    @DisplayName("Should return empty when expense not found")
    void testFindByIdNotFound() {
        Optional<Expense> foundExpense = expenseRepository.findById(999L);
        
        assertFalse(foundExpense.isPresent());
    }

    @Test
    @DisplayName("Should find all expenses")
    void testFindAll() {
        Expense expense1 = new Expense("Expense 1", new BigDecimal("100.00"), 
                                     "Food", LocalDateTime.now());
        Expense expense2 = new Expense("Expense 2", new BigDecimal("50.00"), 
                                     "Transport", LocalDateTime.now());
        
        expenseRepository.save(expense1);
        expenseRepository.save(expense2);
        
        List<Expense> expenses = expenseRepository.findAll();
        
        assertEquals(2, expenses.size());
    }

    @Test
    @DisplayName("Should delete expense by ID")
    void testDeleteById() {
        Expense expense = new Expense("Test expense", new BigDecimal("100.00"), 
                                    "Food", LocalDateTime.now());
        Expense savedExpense = expenseRepository.save(expense);
        
        expenseRepository.deleteById(savedExpense.getId());
        
        Optional<Expense> foundExpense = expenseRepository.findById(savedExpense.getId());
        assertFalse(foundExpense.isPresent());
    }

    @Test
    @DisplayName("Should find expenses by date range")
    void testFindByDateBetween() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime tomorrow = now.plusDays(1);
        
        Expense expense1 = new Expense("Yesterday", new BigDecimal("100.00"), 
                                     "Food", yesterday);
        Expense expense2 = new Expense("Today", new BigDecimal("50.00"), 
                                     "Transport", now);
        Expense expense3 = new Expense("Tomorrow", new BigDecimal("75.00"), 
                                     "Entertainment", tomorrow);
        
        expenseRepository.save(expense1);
        expenseRepository.save(expense2);
        expenseRepository.save(expense3);
        
        List<Expense> expenses = expenseRepository.findByDateBetween(yesterday, now);
        
        assertEquals(2, expenses.size());
    }

    @Test
    @DisplayName("Should find recent expenses with limit")
    void testFindRecentExpenses() {
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = 0; i < 10; i++) {
            Expense expense = new Expense("Expense " + i, new BigDecimal("100.00"), 
                                        "Food", now.minusDays(i));
            expenseRepository.save(expense);
        }
        
        List<Expense> recentExpenses = expenseRepository.findRecentExpenses(5);
        
        assertEquals(5, recentExpenses.size());
    }
}
