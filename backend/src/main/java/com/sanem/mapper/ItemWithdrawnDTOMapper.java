package com.sanem.mapper;

import com.sanem.mapper.ItemDTOMapper;
import com.sanem.model.dto.ItemWithdrawnDTO;
import com.sanem.model.entity.ItemWithdrawn;
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
