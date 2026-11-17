package com.sanem.model.dto;

import com.sanem.model.dto.ProfileDTO;
import com.sanem.model.enums.Status;

public record AppUserDTO(
                Long userId,
                String name,
                String login,
                String email,
                Status status,
                ProfileDTO profile) {
}
