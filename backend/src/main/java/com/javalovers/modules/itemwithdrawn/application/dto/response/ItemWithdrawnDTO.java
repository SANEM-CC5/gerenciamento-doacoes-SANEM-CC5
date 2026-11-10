package com.javalovers.modules.itemwithdrawn.application.dto.response;

import com.javalovers.modules.item.application.dto.response.ItemDTO;

public record ItemWithdrawnDTO(
         Long itemWithdrawnId,
         ItemDTO itemDTO,
         Integer quantity
) {
}
