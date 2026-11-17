package com.javalovers.model.dto;

public record DonorFilterDTO(
        String name,
        String cpfCnpj,
        String contact,
        String email
) {
}
