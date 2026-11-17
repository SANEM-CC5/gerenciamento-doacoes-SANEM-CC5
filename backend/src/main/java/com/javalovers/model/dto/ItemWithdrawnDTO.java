package com.javalovers.model.dto;

import com.javalovers.model.dto.ItemDTO;

public record ItemWithdrawnDTO(
         Long itemWithdrawnId,
         ItemDTO itemDTO,
         Integer quantity
) {
}
