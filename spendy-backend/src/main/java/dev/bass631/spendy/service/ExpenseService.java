package dev.bass631.spendy.service;

import dev.bass631.spendy.dto.request.AddExpenseRequest;
import dev.bass631.spendy.dto.request.UpdateExpenseRequest;
import dev.bass631.spendy.dto.response.ExpenseResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ExpenseService {
    ExpenseResponse addExpense(AddExpenseRequest request, String createdBy);
    Page<ExpenseResponse> getExpenses(UUID categoryId, int page, int limit);
    Page<ExpenseResponse> getExpenses(UUID categoryId, int page, int limit, String period, LocalDate from, LocalDate to);
    ExpenseResponse updateExpense(UUID id, UpdateExpenseRequest request);
    void deleteExpense(UUID id);
    @Deprecated
    List<ExpenseResponse> getExpenses(UUID categoryId);
}
