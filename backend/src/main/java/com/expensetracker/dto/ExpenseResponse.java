package com.expensetracker.dto;

import com.expensetracker.model.Expense;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExpenseResponse {
    private Long id;
    private String description;
    private BigDecimal amount;
    private String category;
    private LocalDateTime date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ExpenseResponse() {}

    public ExpenseResponse(Expense expense) {
        this.id = expense.getId();
        this.description = expense.getDescription();
        this.amount = expense.getAmount();
        this.category = expense.getCategory();
        this.date = expense.getDate();
        this.createdAt = expense.getCreatedAt();
        this.updatedAt = expense.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
