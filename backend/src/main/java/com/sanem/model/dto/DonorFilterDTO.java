package com.sanem.model.dto;

public record DonorFilterDTO(
        String name,
        String cpfCnpj,
        String contact,
        String email
) {
}
