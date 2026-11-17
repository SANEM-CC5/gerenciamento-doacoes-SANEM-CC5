package com.sanem.model.dto;

import com.sanem.model.dto.ItemDTO;

public record ItemWithdrawnDTO(
         Long itemWithdrawnId,
         ItemDTO itemDTO,
         Integer quantity
) {
}
