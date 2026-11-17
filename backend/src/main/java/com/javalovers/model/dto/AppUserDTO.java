package com.javalovers.model.dto;

import com.javalovers.model.dto.ProfileDTO;
import com.javalovers.model.enums.Status;

public record AppUserDTO(
                Long userId,
                String name,
                String login,
                String email,
                Status status,
                ProfileDTO profile) {
}
