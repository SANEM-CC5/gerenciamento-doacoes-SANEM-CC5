package com.javalovers.modules.donor.application.dto.request;

public record DonorFilterDTO(
        String name,
        String cpfCnpj,
        String contact,
        String email
) {
}
