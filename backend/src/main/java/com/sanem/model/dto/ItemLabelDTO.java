package com.sanem.model.dto;

public record ItemLabelDTO(
        Long itemId,
        String description,
        String tagCode,
        String qrCodeBase64
) {
}

