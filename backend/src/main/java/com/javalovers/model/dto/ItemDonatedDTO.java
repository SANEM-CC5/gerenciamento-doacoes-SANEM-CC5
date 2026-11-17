package com.javalovers.model.dto;

import com.javalovers.model.dto.ItemDTO;

public record ItemDonatedDTO(
        Long itemDonatedId,
        ItemDTO item,
        Integer quantity
) {
}
