package com.expensetracker.controller;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseService expenseService;

    @Autowired
    private ObjectMapper objectMapper;

    private ExpenseRequest expenseRequest;
    private ExpenseResponse expenseResponse;

    @BeforeEach
    void setUp() {
        expenseRequest = new ExpenseRequest("Test expense", new BigDecimal("100.00"), 
                                          "Food", LocalDateTime.now());
        expenseResponse = new ExpenseResponse();
        expenseResponse.setId(1L);
        expenseResponse.setDescription("Test expense");
        expenseResponse.setAmount(new BigDecimal("100.00"));
        expenseResponse.setCategory("Food");
        expenseResponse.setDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create expense successfully")
    void testCreateExpense() throws Exception {
        when(expenseService.createExpense(any(ExpenseRequest.class))).thenReturn(expenseResponse);

        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test expense"))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.category").value("Food"));
    }

    @Test
    @DisplayName("Should update expense successfully")
    void testUpdateExpense() throws Exception {
        when(expenseService.updateExpense(anyLong(), any(ExpenseRequest.class))).thenReturn(expenseResponse);

        mockMvc.perform(put("/api/expenses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test expense"));
    }

    @Test
    @DisplayName("Should delete expense successfully")
    void testDeleteExpense() throws Exception {
        mockMvc.perform(delete("/api/expenses/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should get expense by ID")
    void testGetExpenseById() throws Exception {
        when(expenseService.getExpenseById(1L)).thenReturn(expenseResponse);

        mockMvc.perform(get("/api/expenses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test expense"));
    }

    @Test
    @DisplayName("Should get all expenses")
    void testGetAllExpenses() throws Exception {
        List<ExpenseResponse> expenses = Arrays.asList(expenseResponse);
        when(expenseService.getAllExpenses()).thenReturn(expenses);

        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Test expense"));
    }

    @Test
    @DisplayName("Should validate expense request")
    void testValidateExpenseRequest() throws Exception {
        ExpenseRequest invalidRequest = new ExpenseRequest();
        invalidRequest.setDescription("");
        invalidRequest.setAmount(new BigDecimal("-100.00"));

        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
