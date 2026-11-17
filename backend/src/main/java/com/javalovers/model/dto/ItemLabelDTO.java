package com.javalovers.model.dto;

public record ItemLabelDTO(
        Long itemId,
        String description,
        String tagCode,
        String qrCodeBase64
) {
}

