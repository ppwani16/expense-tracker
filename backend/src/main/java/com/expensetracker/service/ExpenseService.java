package com.expensetracker.service;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.dto.ExpenseSummary;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public ExpenseResponse createExpense(ExpenseRequest request) {
        Expense expense = new Expense(request.getDescription(), request.getAmount(), 
                                    request.getCategory(), request.getDate());
        Expense savedExpense = expenseRepository.save(expense);
        return new ExpenseResponse(savedExpense);
    }

    public ExpenseResponse updateExpense(Long id, ExpenseRequest request) {
        Optional<Expense> existingExpense = expenseRepository.findById(id);
        if (existingExpense.isEmpty()) {
            throw new RuntimeException("Expense not found with id: " + id);
        }

        Expense expense = existingExpense.get();
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDate(request.getDate());
        expense.setUpdatedAt(LocalDateTime.now());

        Expense updatedExpense = expenseRepository.save(expense);
        return new ExpenseResponse(updatedExpense);
    }

    public void deleteExpense(Long id) {
        if (!expenseRepository.findById(id).isPresent()) {
            throw new RuntimeException("Expense not found with id: " + id);
        }
        expenseRepository.deleteById(id);
    }

    public ExpenseResponse getExpenseById(Long id) {
        Optional<Expense> expense = expenseRepository.findById(id);
        if (expense.isEmpty()) {
            throw new RuntimeException("Expense not found with id: " + id);
        }
        return new ExpenseResponse(expense.get());
    }

    public List<ExpenseResponse> getAllExpenses() {
        return expenseRepository.findAll().stream()
                .map(ExpenseResponse::new)
                .collect(Collectors.toList());
    }

    public List<ExpenseResponse> getExpensesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return expenseRepository.findByDateBetween(startDate, endDate).stream()
                .map(ExpenseResponse::new)
                .collect(Collectors.toList());
    }

    public List<ExpenseResponse> getExpensesByMonth(int year, int month) {
        return expenseRepository.findByMonth(year, month).stream()
                .map(ExpenseResponse::new)
                .collect(Collectors.toList());
    }

    public List<ExpenseResponse> getRecentExpenses(int limit) {
        return expenseRepository.findRecentExpenses(limit).stream()
                .map(ExpenseResponse::new)
                .collect(Collectors.toList());
    }

    public List<ExpenseResponse> getExpensesSortedBy(String sortBy, boolean ascending) {
        List<Expense> expenses = expenseRepository.findAll();
        
        Comparator<Expense> comparator;
        switch (sortBy.toLowerCase()) {
            case "amount":
                comparator = Comparator.comparing(Expense::getAmount);
                break;
            case "category":
                comparator = Comparator.comparing(Expense::getCategory);
                break;
            case "date":
            default:
                comparator = Comparator.comparing(Expense::getDate);
                break;
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        return expenses.stream()
                .sorted(comparator)
                .map(ExpenseResponse::new)
                .collect(Collectors.toList());
    }

    public ExpenseSummary getExpenseSummary() {
    List<Expense> allExpenses = expenseRepository.findAll();
    
    BigDecimal totalExpenses = allExpenses.stream()
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    LocalDateTime now = LocalDateTime.now();
    BigDecimal monthlyExpenses = expenseRepository.findByMonth(now.getYear(), now.getMonthValue())
            .stream()
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal yearlyExpenses = expenseRepository.findByYear(now.getYear())
            .stream()
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    Map<String, BigDecimal> expensesByCategory = allExpenses.stream()
            .collect(Collectors.groupingBy(
                    Expense::getCategory,
                    Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
            ));

    // Find highest and lowest with amounts
    Map.Entry<String, BigDecimal> highestEntry = expensesByCategory.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .orElse(null);

    Map.Entry<String, BigDecimal> lowestEntry = expensesByCategory.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .orElse(null);

    String highestSpendCategory = highestEntry != null ? highestEntry.getKey() : "None";
    String lowestSpendCategory = lowestEntry != null ? lowestEntry.getKey() : "None";
    BigDecimal highestSpendAmount = highestEntry != null ? highestEntry.getValue() : BigDecimal.ZERO;
    BigDecimal lowestSpendAmount = lowestEntry != null ? lowestEntry.getValue() : BigDecimal.ZERO;

    return new ExpenseSummary(totalExpenses, monthlyExpenses, yearlyExpenses, 
                            expensesByCategory, highestSpendCategory, lowestSpendCategory,
                            highestSpendAmount, lowestSpendAmount);
}


    public Map<String, BigDecimal> getExpensesByCategory() {
        return expenseRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
                ));
    }

    public Map<String, BigDecimal> getMonthlyTrend(int year) {
    List<Expense> yearlyExpenses = expenseRepository.findByYear(year);
    
    // Create ordered map for months
    Map<String, BigDecimal> monthlyTrend = new LinkedHashMap<>();
    
    // Initialize all months with zero
    String[] months = {"January", "February", "March", "April", "May", "June",
                      "July", "August", "September", "October", "November", "December"};
    
    for (String month : months) {
        monthlyTrend.put(month, BigDecimal.ZERO);
    }
    
    // Populate with actual data
    for (Expense expense : yearlyExpenses) {
        int monthValue = expense.getDate().getMonthValue();
        String monthName = months[monthValue - 1]; // Convert to proper month name
        
        monthlyTrend.merge(monthName, expense.getAmount(), BigDecimal::add);
    }
    
    return monthlyTrend;
}


}
