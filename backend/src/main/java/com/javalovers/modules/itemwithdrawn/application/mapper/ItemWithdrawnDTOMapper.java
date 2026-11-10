package com.javalovers.modules.itemwithdrawn.application.mapper;

import com.javalovers.modules.item.application.mapper.ItemDTOMapper;
import com.javalovers.modules.itemwithdrawn.application.dto.response.ItemWithdrawnDTO;
import com.javalovers.modules.itemwithdrawn.domain.entity.ItemWithdrawn;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ItemWithdrawnDTOMapper {

    private final ItemDTOMapper itemDTOMapper;

    public ItemWithdrawnDTO convert(ItemWithdrawn itemWithdrawn) {
        if (itemWithdrawn == null) { return null; }

        return new ItemWithdrawnDTO(
                itemWithdrawn.getItemWithdrawnId(),
                itemDTOMapper.convert(itemWithdrawn.getItem()),
                itemWithdrawn.getQuantity()
        );
    }
}
