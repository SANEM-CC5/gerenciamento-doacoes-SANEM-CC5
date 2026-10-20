package com.javalovers.core.collectionpoint.domain.dto.request;

import com.javalovers.core.collectionpoint.domain.enums.CollectionPointType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CollectionPointFormDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
        String name,

        @NotNull(message = "Tipo é obrigatório")
        CollectionPointType type,

        @NotBlank(message = "Logradouro é obrigatório")
        String street,

        String number,

        String neighborhood,

        String city,

        String state,

        String zipCode,

        @NotNull(message = "Latitude é obrigatória")
        @DecimalMin(value = "-90.0", message = "Latitude deve estar entre -90 e 90")
        @DecimalMax(value = "90.0", message = "Latitude deve estar entre -90 e 90")
        Double latitude,

        @NotNull(message = "Longitude é obrigatória")
        @DecimalMin(value = "-180.0", message = "Longitude deve estar entre -180 e 180")
        @DecimalMax(value = "180.0", message = "Longitude deve estar entre -180 e 180")
        Double longitude,

        String operatingHours,

        String phone
) {
}
