package com.javalovers.modules.withdrawallimit.application.mapper;

import com.javalovers.modules.withdrawallimit.application.dto.response.WithdrawalLimitConfigDTO;
import com.javalovers.modules.withdrawallimit.domain.entity.WithdrawalLimitConfig;
import org.springframework.stereotype.Service;

@Service
public class WithdrawalLimitConfigDTOMapper {

    public WithdrawalLimitConfigDTO convert(WithdrawalLimitConfig config) {
        if (config == null) return null;
        return new WithdrawalLimitConfigDTO(
                config.getConfigId(),
                config.getMonthlyItemLimit(),
                config.getIsActive(),
                config.getCreatedAt(),
                config.getUpdatedAt()
        );
    }
}

