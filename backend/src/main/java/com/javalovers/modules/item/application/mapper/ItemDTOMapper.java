package com.javalovers.modules.item.application.mapper;

import com.javalovers.modules.item.application.dto.response.ItemDTO;
import com.javalovers.modules.item.domain.entity.Item;
import org.springframework.stereotype.Service;

@Service
public class ItemDTOMapper {

    public ItemDTO convert(Item item){
        if(item == null) return null;
        return new ItemDTO(
                item.getItemId(),
                item.getDescription(),
                item.getStockQuantity(),
                item.getTagCode()
        );
    }
}
