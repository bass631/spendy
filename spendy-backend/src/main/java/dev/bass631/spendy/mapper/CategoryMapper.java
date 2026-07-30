package dev.bass631.spendy.mapper;

import dev.bass631.spendy.dto.response.CategoryResponse;
import dev.bass631.spendy.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "usageCount", ignore = true)
    CategoryResponse toResponse(Category category);

    @Mapping(target = "usageCount", ignore = true)
    List<CategoryResponse> toResponseList(List<Category> categories);

    default CategoryResponse toResponseWithUsage(Category category, long usageCount) {
        return new CategoryResponse(category.getId(), category.getName(), usageCount);
    }
}
