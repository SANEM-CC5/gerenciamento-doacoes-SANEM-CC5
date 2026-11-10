package com.javalovers.modules.profile.application.dto.response;

public record ProfileDTO(
        Long profileId,
        String description,
        String name
) {
}
