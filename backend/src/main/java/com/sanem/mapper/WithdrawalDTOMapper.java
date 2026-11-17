package com.sanem.mapper;

import com.sanem.mapper.AppUserDTOMapper;
import com.sanem.mapper.BeneficiaryDTOMapper;
import com.sanem.model.entity.ItemWithdrawn;
import com.sanem.mapper.ItemWithdrawnDTOMapper;
import com.sanem.repository.ItemWithdrawnRepository;
import com.sanem.model.dto.WithdrawalDTO;
import com.sanem.model.entity.Withdrawal;
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
