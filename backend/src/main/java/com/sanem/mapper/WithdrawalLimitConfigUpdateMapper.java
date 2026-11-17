package com.sanem.mapper;

import com.sanem.model.dto.WithdrawalLimitConfigFormDTO;
import com.sanem.model.entity.WithdrawalLimitConfig;
import org.springframework.stereotype.Service;

@Service
public class WithdrawalLimitConfigUpdateMapper {

    public void update(WithdrawalLimitConfig config, WithdrawalLimitConfigFormDTO formDTO) {
        config.setMonthlyItemLimit(formDTO.monthlyItemLimit());
        config.setIsActive(formDTO.isActive());
    }
}

