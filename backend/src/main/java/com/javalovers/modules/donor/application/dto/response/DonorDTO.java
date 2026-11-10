package com.javalovers.modules.donor.application.dto.response;

public record DonorDTO(
        Long donorId,
        String name,
        String cpfCnpj,
        String contact,
        String email
) {
}
