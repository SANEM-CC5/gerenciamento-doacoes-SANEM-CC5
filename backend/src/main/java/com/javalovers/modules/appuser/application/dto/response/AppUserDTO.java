package com.javalovers.modules.appuser.application.dto.response;

import com.javalovers.modules.profile.application.dto.response.ProfileDTO;
import com.javalovers.shared.domain.enums.Status;

public record AppUserDTO(
                Long userId,
                String name,
                String login,
                String email,
                Status status,
                ProfileDTO profile) {
}
