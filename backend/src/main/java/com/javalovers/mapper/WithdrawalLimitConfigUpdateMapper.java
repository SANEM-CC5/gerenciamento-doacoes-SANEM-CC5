package com.javalovers.mapper;

import com.javalovers.model.dto.WithdrawalLimitConfigFormDTO;
import com.javalovers.model.entity.WithdrawalLimitConfig;
import org.springframework.stereotype.Service;

@Service
public class WithdrawalLimitConfigUpdateMapper {

    public void update(WithdrawalLimitConfig config, WithdrawalLimitConfigFormDTO formDTO) {
        config.setMonthlyItemLimit(formDTO.monthlyItemLimit());
        config.setIsActive(formDTO.isActive());
    }
}

