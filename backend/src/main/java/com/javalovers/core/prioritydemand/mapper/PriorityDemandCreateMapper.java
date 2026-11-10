package com.javalovers.core.prioritydemand.mapper;

import com.javalovers.core.appuser.domain.entity.AppUser;
import com.javalovers.core.item.domain.entity.Item;
import com.javalovers.core.prioritydemand.domain.dto.request.PriorityDemandFormDTO;
import com.javalovers.core.prioritydemand.domain.entity.PriorityDemand;
import com.javalovers.core.prioritydemand.domain.enums.DemandStatus;
import org.springframework.stereotype.Service;

@Service
public class PriorityDemandCreateMapper {

    public PriorityDemand convert(PriorityDemandFormDTO formDTO, Item item, AppUser createdBy) {
        PriorityDemand demand = new PriorityDemand();
        demand.setItem(item);
        demand.setQuantityNeeded(formDTO.quantityNeeded());
        demand.setPriority(formDTO.priority());
        demand.setJustification(normalize(formDTO.justification()));
        demand.setDeadline(formDTO.deadline());
        demand.setStatus(DemandStatus.ACTIVE);
        demand.setCreatedBy(createdBy);
        return demand;
    }

    static String normalize(String text) {
        if (text == null) return null;
        String trimmed = text.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
