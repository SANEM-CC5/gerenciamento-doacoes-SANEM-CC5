package com.javalovers.core.item.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ItemFormDTO(
        @JsonProperty("description")
        String description,
        
        @JsonProperty("stockQuantity")
        Long stockQuantity,
        
        @JsonProperty("tagCode")
        String tagCode,

        @JsonProperty("size")
        String size,

        @JsonProperty("gender")
        String gender,

        @JsonProperty("type")
        String type,

        @JsonProperty("conditionStatus")
        String conditionStatus,

        @JsonProperty("location")
        String location
) {
}
