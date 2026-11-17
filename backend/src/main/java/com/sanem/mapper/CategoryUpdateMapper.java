package com.sanem.mapper;

import com.sanem.model.dto.CategoryFormDTO;
import com.sanem.model.entity.Category;
import org.springframework.stereotype.Service;

@Service
public class CategoryUpdateMapper {

    public void update(Category category, CategoryFormDTO categoryFormDTO) {
        category.setName(categoryFormDTO.name());
    }
}
