package com.javalovers.modules.category.application.mapper;

import com.javalovers.modules.category.application.dto.request.CategoryFormDTO;
import com.javalovers.modules.category.domain.entity.Category;
import org.springframework.stereotype.Service;

@Service
public class CategoryCreateMapper {

    public Category convert(CategoryFormDTO categoryFormDTO) {
        Category category = new Category();
        category.setName(categoryFormDTO.name());

        return category;
    }
}
