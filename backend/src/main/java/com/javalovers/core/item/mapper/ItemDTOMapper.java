package com.javalovers.core.item.mapper;

import com.javalovers.core.item.domain.dto.response.ItemDTO;
import com.javalovers.core.item.domain.entity.Item;
import org.springframework.stereotype.Service;

@Service
public class ItemDTOMapper {

    public ItemDTO convert(Item item){
        if(item == null) return null;
        return new ItemDTO(
                item.getItemId(),
                item.getDescription(),
                item.getStockQuantity(),
                item.getTagCode(),
<<<<<<< HEAD
                item.getMinimumStock(),
                item.isLowStock()
=======
                item.getSize(),
                item.getGender(),
                item.getType(),
                item.getConditionStatus(),
                item.getLocation()
>>>>>>> origin/main
        );
    }
}
