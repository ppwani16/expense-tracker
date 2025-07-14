package com.expensetracker.controller;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.dto.ExpenseSummary;
import com.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse response = expenseService.createExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(@PathVariable Long id, 
                                                       @Valid @RequestBody ExpenseRequest request) {
        try {
            ExpenseResponse response = expenseService.updateExpense(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        try {
            expenseService.deleteExpense(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpenseById(@PathVariable Long id) {
        try {
            ExpenseResponse response = expenseService.getExpenseById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses() {
        List<ExpenseResponse> expenses = expenseService.getAllExpenses();
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByDateRange(
            @RequestParam String startDate, 
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<ExpenseResponse> expenses = expenseService.getExpensesByDateRange(start, end);
            return ResponseEntity.ok(expenses);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/month/{year}/{month}")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByMonth(
            @PathVariable int year, 
            @PathVariable int month) {
        try {
            List<ExpenseResponse> expenses = expenseService.getExpensesByMonth(year, month);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ExpenseResponse>> getRecentExpenses(
            @RequestParam(defaultValue = "50") int limit) {
        try {
            List<ExpenseResponse> expenses = expenseService.getRecentExpenses(limit);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<ExpenseResponse>> getSortedExpenses(
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {
        try {
            List<ExpenseResponse> expenses = expenseService.getExpensesSortedBy(sortBy, ascending);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<ExpenseSummary> getExpenseSummary() {
        try {
            ExpenseSummary summary = expenseService.getExpenseSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/by-category")
    public ResponseEntity<Map<String, BigDecimal>> getExpensesByCategory() {
        try {
            Map<String, BigDecimal> expensesByCategory = expenseService.getExpensesByCategory();
            return ResponseEntity.ok(expensesByCategory);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/trend/{year}")
    public ResponseEntity<Map<String, BigDecimal>> getMonthlyTrend(@PathVariable int year) {
        try {
            Map<String, BigDecimal> trend = expenseService.getMonthlyTrend(year);
            return ResponseEntity.ok(trend);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
