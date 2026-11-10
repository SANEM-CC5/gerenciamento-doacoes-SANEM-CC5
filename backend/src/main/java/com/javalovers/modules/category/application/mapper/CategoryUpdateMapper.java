package com.javalovers.modules.category.application.mapper;

import com.javalovers.modules.category.application.dto.request.CategoryFormDTO;
import com.javalovers.modules.category.domain.entity.Category;
import org.springframework.stereotype.Service;

@Service
public class CategoryUpdateMapper {

    public void update(Category category, CategoryFormDTO categoryFormDTO) {
        category.setName(categoryFormDTO.name());
    }
}
