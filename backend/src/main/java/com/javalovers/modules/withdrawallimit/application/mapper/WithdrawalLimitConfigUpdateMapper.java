package com.javalovers.modules.withdrawallimit.application.mapper;

import com.javalovers.modules.withdrawallimit.application.dto.request.WithdrawalLimitConfigFormDTO;
import com.javalovers.modules.withdrawallimit.domain.entity.WithdrawalLimitConfig;
import org.springframework.stereotype.Service;

@Service
public class WithdrawalLimitConfigUpdateMapper {

    public void update(WithdrawalLimitConfig config, WithdrawalLimitConfigFormDTO formDTO) {
        config.setMonthlyItemLimit(formDTO.monthlyItemLimit());
        config.setIsActive(formDTO.isActive());
    }
}

