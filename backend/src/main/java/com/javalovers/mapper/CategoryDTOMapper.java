package com.javalovers.mapper;

import com.javalovers.model.dto.CategoryDTO;
import com.javalovers.model.entity.Category;
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
