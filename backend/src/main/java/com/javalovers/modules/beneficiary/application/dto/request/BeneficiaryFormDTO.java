package com.javalovers.modules.beneficiary.application.dto.request;

import com.javalovers.shared.validation.ValidCpf;
import com.javalovers.modules.beneficiary.domain.enums.BeneficiaryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BeneficiaryFormDTO(
        @NotBlank(message = "Nome completo é obrigatório")
        @Size(min = 3, max = 255, message = "Nome completo deve ter entre 3 e 255 caracteres")
        String fullName,
        
        @ValidCpf(message = "CPF inválido")
        String cpf,
        
        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(regexp = "^[0-9]{10,11}$", message = "Telefone deve conter 10 ou 11 dígitos numéricos")
        String phone,
        
        String socioeconomicData,
        BeneficiaryStatus beneficiaryStatus,
        
        Integer withdrawalLimit
) {
}
