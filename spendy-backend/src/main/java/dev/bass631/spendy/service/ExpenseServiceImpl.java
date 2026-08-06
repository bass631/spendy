package dev.bass631.spendy.service;

import dev.bass631.spendy.dto.request.AddExpenseRequest;
import dev.bass631.spendy.dto.request.UpdateExpenseRequest;
import dev.bass631.spendy.dto.response.ExpenseResponse;
import dev.bass631.spendy.exception.ResourceNotFoundException;
import dev.bass631.spendy.mapper.ExpenseMapper;
import dev.bass631.spendy.model.Category;
import dev.bass631.spendy.model.Expense;
import dev.bass631.spendy.repository.CategoryRepository;
import dev.bass631.spendy.repository.ExpenseRepository;
import dev.bass631.spendy.util.PeriodResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseMapper expenseMapper;

    @Override
    public ExpenseResponse addExpense(AddExpenseRequest request, String createdBy) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Expense expense = Expense.builder()
                .category(category)
                .amount(request.amount())
                .description(request.description())
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .build();
        expense = expenseRepository.save(expense);
        return expenseMapper.toResponse(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getExpenses(UUID categoryId, int page, int limit) {
        return getExpenses(categoryId, page, limit, null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getExpenses(UUID categoryId, int page, int limit, String period, LocalDate from, LocalDate to) {
        PageRequest pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (period == null || period.isBlank()) {
            return expenseRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId, pageable)
                    .map(expenseMapper::toResponse);
        }
        LocalDateTime periodFrom = PeriodResolver.resolveFrom(period, from);
        LocalDateTime periodTo = PeriodResolver.resolveTo(period, to);
        return expenseRepository.findByCategoryIdAndCreatedAtBetweenOrderByCreatedAtDesc(categoryId, periodFrom, periodTo, pageable)
                .map(expenseMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Deprecated
    public List<ExpenseResponse> getExpenses(UUID categoryId) {
        return getExpenses(categoryId, 0, 1000).getContent();
    }

    @Override
    public ExpenseResponse updateExpense(UUID id, UpdateExpenseRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        expense.setAmount(request.amount());
        expense.setDescription(request.description());
        expense = expenseRepository.save(expense);
        return expenseMapper.toResponse(expense);
    }

    @Override
    public void deleteExpense(UUID id) {
        if (!expenseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Expense not found");
        }
        expenseRepository.deleteById(id);
    }
}
