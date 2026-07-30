package dev.bass631.spendy.service;

import dev.bass631.spendy.dto.request.CreateCategoryRequest;
import dev.bass631.spendy.dto.request.UpdateCategoryRequest;
import dev.bass631.spendy.dto.response.CategoryResponse;
import dev.bass631.spendy.exception.ConflictException;
import dev.bass631.spendy.exception.ResourceNotFoundException;
import dev.bass631.spendy.mapper.CategoryMapper;
import dev.bass631.spendy.model.Category;
import dev.bass631.spendy.repository.CategoryRepository;
import dev.bass631.spendy.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesSortedByFrequency() {
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        List<Category> categories = categoryRepository.findAllSortedByFrequencySince(weekAgo);
        return categories.stream()
                .map(c -> categoryMapper.toResponseWithUsage(c, 0))
                .toList();
    }

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new ConflictException("Категория '" + request.name() + "' уже существует");
        }
        Category category = Category.builder()
                .name(request.name())
                .createdAt(LocalDateTime.now())
                .build();
        category = categoryRepository.save(category);
        return categoryMapper.toResponseWithUsage(category, 0);
    }

    @Override
    public CategoryResponse renameCategory(UUID id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if (!category.getName().equals(request.name()) && categoryRepository.existsByName(request.name())) {
            throw new ConflictException("Категория '" + request.name() + "' уже существует");
        }
        category.setName(request.name());
        category = categoryRepository.save(category);
        return categoryMapper.toResponseWithUsage(category, 0);
    }

    @Override
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        expenseRepository.deleteByCategoryId(category.getId());
        categoryRepository.delete(category);
    }
}
