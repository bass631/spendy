package dev.bass631.spendy.service;

import dev.bass631.spendy.dto.request.AddExpenseRequest;
import dev.bass631.spendy.dto.request.UpdateExpenseRequest;
import dev.bass631.spendy.dto.response.ExpenseResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ExpenseService {
    ExpenseResponse addExpense(AddExpenseRequest request, String createdBy);
    Page<ExpenseResponse> getExpenses(UUID categoryId, int page, int limit);
    ExpenseResponse updateExpense(UUID id, UpdateExpenseRequest request);
    void deleteExpense(UUID id);
    @Deprecated
    List<ExpenseResponse> getExpenses(UUID categoryId);
}
