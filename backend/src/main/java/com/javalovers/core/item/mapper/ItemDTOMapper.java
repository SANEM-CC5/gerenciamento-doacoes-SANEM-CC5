package com.javalovers.core.item.mapper;

import com.javalovers.core.category.domain.dto.response.CategoryDTO;
import com.javalovers.core.item.domain.dto.response.ItemDTO;
import com.javalovers.core.item.domain.entity.Item;
import org.springframework.stereotype.Service;

@Service
public class ItemDTOMapper {

    public ItemDTO convert(Item item){
        if(item == null) return null;
        CategoryDTO categoryDTO = null;
        if (item.getCategory() != null) {
            categoryDTO = new CategoryDTO(
                    item.getCategory().getCategoryId(),
                    item.getCategory().getName()
            );
        }
        return new ItemDTO(
                item.getItemId(),
                item.getDescription(),
                item.getStockQuantity(),
                item.getTagCode(),
                categoryDTO,
                item.getSize(),
                item.getConservationState(),
                item.getGender()
        );
    }
}
