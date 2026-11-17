package com.javalovers.mapper;

import com.javalovers.mapper.ItemDTOMapper;
import com.javalovers.model.dto.ItemDonatedDTO;
import com.javalovers.model.entity.ItemDonated;
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
