package com.javalovers.modules.itemdonated.application.mapper;

import com.javalovers.modules.item.application.mapper.ItemDTOMapper;
import com.javalovers.modules.itemdonated.application.dto.response.ItemDonatedDTO;
import com.javalovers.modules.itemdonated.domain.entity.ItemDonated;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ItemDonatedDTOMapper {

    private final ItemDTOMapper itemDTOMapper;

    public ItemDonatedDTO convert(ItemDonated itemDonated) {
        if (itemDonated == null) {
            return null;
        }

        return new ItemDonatedDTO(
                itemDonated.getItemDonatedId(),
                itemDTOMapper.convert(itemDonated.getItem()),
                itemDonated.getQuantity()
        );
    }
}
