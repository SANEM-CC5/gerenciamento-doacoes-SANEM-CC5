package com.javalovers.core.item.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.PositiveOrZero;

public record ItemFormDTO(
        @JsonProperty("description")
        String description,
        
        @JsonProperty("stockQuantity")
        Long stockQuantity,
        
        @JsonProperty("tagCode")
        String tagCode,

<<<<<<< HEAD
        @JsonProperty("minimumStock")
        @PositiveOrZero(message = "O estoque mínimo não pode ser negativo.")
        Long minimumStock
=======
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
>>>>>>> origin/main
) {
}
