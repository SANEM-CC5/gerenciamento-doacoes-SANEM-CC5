package com.javalovers.modules.withdrawal.application.mapper;

import com.javalovers.modules.appuser.application.mapper.AppUserDTOMapper;
import com.javalovers.modules.beneficiary.application.mapper.BeneficiaryDTOMapper;
import com.javalovers.modules.itemwithdrawn.domain.entity.ItemWithdrawn;
import com.javalovers.modules.itemwithdrawn.application.mapper.ItemWithdrawnDTOMapper;
import com.javalovers.modules.itemwithdrawn.infrastructure.persistence.repository.ItemWithdrawnRepository;
import com.javalovers.modules.withdrawal.application.dto.response.WithdrawalDTO;
import com.javalovers.modules.withdrawal.domain.entity.Withdrawal;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WithdrawalDTOMapper {

    private final BeneficiaryDTOMapper beneficiaryDTOMapper;
    private final AppUserDTOMapper appUserDTOMapper;
    private final ItemWithdrawnDTOMapper itemWithdrawnDTOMapper;
    private final ItemWithdrawnRepository itemWithdrawnRepository;

    public WithdrawalDTO convert(Withdrawal withdrawal) {
        if (withdrawal == null) return null;
        
        // Buscar itens retirados
        List<ItemWithdrawn> itemsWithdrawn = withdrawal.getWithdrawalId() != null 
            ? itemWithdrawnRepository.findByWithdrawal_WithdrawalId(withdrawal.getWithdrawalId())
            : List.of();
        
        return new WithdrawalDTO(
                withdrawal.getWithdrawalId(),
                withdrawal.getWithdrawalDate(),
                beneficiaryDTOMapper.convert(withdrawal.getBeneficiary()),
                appUserDTOMapper.convert(withdrawal.getAttendantUser()),
                itemsWithdrawn.stream()
                    .map(itemWithdrawnDTOMapper::convert)
                    .collect(Collectors.toList())
        );
    }

}
