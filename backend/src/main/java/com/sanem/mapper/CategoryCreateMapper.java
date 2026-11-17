package com.sanem.mapper;

import com.sanem.model.dto.CategoryFormDTO;
import com.sanem.model.entity.Category;
import org.springframework.stereotype.Service;

@Service
public class CategoryCreateMapper {

    public Category convert(CategoryFormDTO categoryFormDTO) {
        Category category = new Category();
        category.setName(categoryFormDTO.name());

        return category;
    }
}
