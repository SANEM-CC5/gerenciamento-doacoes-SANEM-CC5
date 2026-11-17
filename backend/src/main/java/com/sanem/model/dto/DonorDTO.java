package com.sanem.model.dto;

public record DonorDTO(
        Long donorId,
        String name,
        String cpfCnpj,
        String contact,
        String email
) {
}
