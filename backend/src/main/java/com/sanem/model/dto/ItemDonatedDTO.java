package com.sanem.model.dto;

import com.sanem.model.dto.ItemDTO;

public record ItemDonatedDTO(
        Long itemDonatedId,
        ItemDTO item,
        Integer quantity
) {
}
