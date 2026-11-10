package com.javalovers.modules.category.application.mapper;

import com.javalovers.modules.category.application.dto.response.CategoryDTO;
import com.javalovers.modules.category.domain.entity.Category;
import org.springframework.stereotype.Service;

@Service
public class CategoryDTOMapper {

    public CategoryDTO convert(Category category) {
        if(category == null) return null;
        return new CategoryDTO(
                category.getCategoryId(),
                category.getName()
        );
    }
}
