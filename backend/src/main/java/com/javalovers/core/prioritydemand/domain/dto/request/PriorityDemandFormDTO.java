package com.javalovers.core.prioritydemand.domain.dto.request;

import com.javalovers.core.prioritydemand.domain.enums.DemandPriority;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PriorityDemandFormDTO(
        @NotNull(message = "O item é obrigatório")
        Long itemId,

        @NotNull(message = "A quantidade necessária é obrigatória")
        @Positive(message = "A quantidade necessária deve ser maior que zero")
        @Max(value = 1000000, message = "Quantidade necessária muito alta")
        Long quantityNeeded,

        @NotNull(message = "A prioridade é obrigatória")
        DemandPriority priority,

        @Size(max = 500, message = "A justificativa deve ter no máximo 500 caracteres")
        String justification,

        /** Opcional. Formato yyyy-MM-dd. */
        LocalDate deadline
) {
}
