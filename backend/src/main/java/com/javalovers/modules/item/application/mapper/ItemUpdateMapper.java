package com.javalovers.modules.item.application.mapper;

import com.javalovers.modules.item.application.dto.request.ItemFormDTO;
import com.javalovers.modules.item.domain.entity.Item;
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
    }
}
