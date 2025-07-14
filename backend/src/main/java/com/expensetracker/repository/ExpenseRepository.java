package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ExpenseRepository {
    private final Map<Long, Expense> expenses = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Expense save(Expense expense) {
        if (expense.getId() == null) {
            expense.setId(idGenerator.getAndIncrement());
        } else {
            expense.setUpdatedAt(LocalDateTime.now());
        }
        expenses.put(expense.getId(), expense);
        return expense;
    }

    public Optional<Expense> findById(Long id) {
        return Optional.ofNullable(expenses.get(id));
    }

    public List<Expense> findAll() {
        return new ArrayList<>(expenses.values());
    }

    public void deleteById(Long id) {
        expenses.remove(id);
    }

    public List<Expense> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return expenses.values().stream()
                .filter(expense -> expense.getDate().isAfter(startDate.minusSeconds(1)) && 
                                 expense.getDate().isBefore(endDate.plusSeconds(1)))
                .collect(Collectors.toList());
    }

    public List<Expense> findByCategory(String category) {
        return expenses.values().stream()
                .filter(expense -> expense.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public List<Expense> findRecentExpenses(int limit) {
        return expenses.values().stream()
                .sorted((e1, e2) -> e2.getDate().compareTo(e1.getDate()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Expense> findByMonth(int year, int month) {
    return expenses.values().stream()
            .filter(expense -> expense.getDate().getYear() == year && 
                             expense.getDate().getMonthValue() == month)
            .sorted(Comparator.comparing(Expense::getDate))
            .collect(Collectors.toList());
}

public List<Expense> findByYear(int year) {
    return expenses.values().stream()
            .filter(expense -> expense.getDate().getYear() == year)
            .sorted(Comparator.comparing(Expense::getDate))
            .collect(Collectors.toList());
}

}
