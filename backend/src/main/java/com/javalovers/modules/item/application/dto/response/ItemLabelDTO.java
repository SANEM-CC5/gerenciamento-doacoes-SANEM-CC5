package com.javalovers.modules.item.application.dto.response;

public record ItemLabelDTO(
        Long itemId,
        String description,
        String tagCode,
        String qrCodeBase64
) {
}

