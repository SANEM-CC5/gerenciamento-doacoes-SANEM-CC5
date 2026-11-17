package com.javalovers.mapper;

import com.javalovers.model.dto.CategoryFormDTO;
import com.javalovers.model.entity.Category;
import org.springframework.stereotype.Service;

@Service
public class CategoryUpdateMapper {

    public void update(Category category, CategoryFormDTO categoryFormDTO) {
        category.setName(categoryFormDTO.name());
    }
}
