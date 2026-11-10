package com.javalovers.modules.itemdonated.application.dto.response;

import com.javalovers.modules.item.application.dto.response.ItemDTO;

public record ItemDonatedDTO(
        Long itemDonatedId,
        ItemDTO item,
        Integer quantity
) {
}
