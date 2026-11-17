package com.sanem.mapper;

import com.sanem.model.dto.WithdrawalLimitConfigDTO;
import com.sanem.model.entity.WithdrawalLimitConfig;
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

