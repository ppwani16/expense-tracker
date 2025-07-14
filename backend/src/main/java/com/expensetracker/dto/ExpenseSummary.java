package com.expensetracker.dto;

import java.math.BigDecimal;
import java.util.Map;

public class ExpenseSummary {
    private BigDecimal totalExpenses;
    private BigDecimal monthlyExpenses;
    private BigDecimal yearlyExpenses;
    private Map<String, BigDecimal> expensesByCategory;
    private String highestSpendCategory;
    private String lowestSpendCategory;
    private BigDecimal highestSpendAmount;
    private BigDecimal lowestSpendAmount;

    public ExpenseSummary() {}

    public ExpenseSummary(BigDecimal totalExpenses, BigDecimal monthlyExpenses, 
                         BigDecimal yearlyExpenses, Map<String, BigDecimal> expensesByCategory,
                         String highestSpendCategory, String lowestSpendCategory,
                         BigDecimal highestSpendAmount, BigDecimal lowestSpendAmount) {
        this.totalExpenses = totalExpenses;
        this.monthlyExpenses = monthlyExpenses;
        this.yearlyExpenses = yearlyExpenses;
        this.expensesByCategory = expensesByCategory;
        this.highestSpendCategory = highestSpendCategory;
        this.lowestSpendCategory = lowestSpendCategory;
        this.highestSpendAmount = highestSpendAmount;
        this.lowestSpendAmount = lowestSpendAmount;
    }

    // Existing getters and setters...

    public BigDecimal getHighestSpendAmount() {
        return highestSpendAmount;
    }

    public void setHighestSpendAmount(BigDecimal highestSpendAmount) {
        this.highestSpendAmount = highestSpendAmount;
    }

    public BigDecimal getLowestSpendAmount() {
        return lowestSpendAmount;
    }

    public void setLowestSpendAmount(BigDecimal lowestSpendAmount) {
        this.lowestSpendAmount = lowestSpendAmount;
    }

    // All other existing getters and setters remain the same...
    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getMonthlyExpenses() {
        return monthlyExpenses;
    }

    public void setMonthlyExpenses(BigDecimal monthlyExpenses) {
        this.monthlyExpenses = monthlyExpenses;
    }

    public BigDecimal getYearlyExpenses() {
        return yearlyExpenses;
    }

    public void setYearlyExpenses(BigDecimal yearlyExpenses) {
        this.yearlyExpenses = yearlyExpenses;
    }

    public Map<String, BigDecimal> getExpensesByCategory() {
        return expensesByCategory;
    }

    public void setExpensesByCategory(Map<String, BigDecimal> expensesByCategory) {
        this.expensesByCategory = expensesByCategory;
    }

    public String getHighestSpendCategory() {
        return highestSpendCategory;
    }

    public void setHighestSpendCategory(String highestSpendCategory) {
        this.highestSpendCategory = highestSpendCategory;
    }

    public String getLowestSpendCategory() {
        return lowestSpendCategory;
    }

    public void setLowestSpendCategory(String lowestSpendCategory) {
        this.lowestSpendCategory = lowestSpendCategory;
    }
}

