package dev.bass631.spendy.service;

import dev.bass631.spendy.dto.request.CreateCategoryRequest;
import dev.bass631.spendy.dto.request.UpdateCategoryRequest;
import dev.bass631.spendy.dto.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<CategoryResponse> getCategoriesSortedByFrequency();
    CategoryResponse createCategory(CreateCategoryRequest request);
    CategoryResponse renameCategory(UUID id, UpdateCategoryRequest request);
    void deleteCategory(UUID id);
}
