package com.javalovers.mapper;

import com.javalovers.model.dto.CategoryFormDTO;
import com.javalovers.model.entity.Category;
import org.springframework.stereotype.Service;

@Service
public class CategoryCreateMapper {

    public Category convert(CategoryFormDTO categoryFormDTO) {
        Category category = new Category();
        category.setName(categoryFormDTO.name());

        return category;
    }
}
