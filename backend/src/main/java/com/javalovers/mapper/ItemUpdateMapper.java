package com.javalovers.mapper;

import com.javalovers.model.dto.ItemFormDTO;
import com.javalovers.model.entity.Item;
import org.springframework.stereotype.Service;

@Service
public class ItemUpdateMapper {

    public void update(Item item, ItemFormDTO itemFormDTO){
        item.setDescription(itemFormDTO.description());
        String tag = itemFormDTO.tagCode();
        if (tag != null && tag.trim().isEmpty()) {
            tag = null;
        }
        item.setTagCode(tag);
        item.setMinimumStock(itemFormDTO.minimumStock());
        item.setSize(itemFormDTO.size());
        item.setGender(itemFormDTO.gender());
        item.setType(itemFormDTO.type());
        item.setConditionStatus(itemFormDTO.conditionStatus());
        item.setLocation(itemFormDTO.location());
    }
}
