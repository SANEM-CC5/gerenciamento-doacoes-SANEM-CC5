package com.javalovers.core.prioritydemand.mapper;

import com.javalovers.core.item.domain.entity.Item;
import com.javalovers.core.prioritydemand.domain.dto.request.PriorityDemandFormDTO;
import com.javalovers.core.prioritydemand.domain.entity.PriorityDemand;
import org.springframework.stereotype.Service;

@Service
public class PriorityDemandUpdateMapper {

    public void update(PriorityDemand demand, PriorityDemandFormDTO formDTO, Item item) {
        demand.setItem(item);
        demand.setQuantityNeeded(formDTO.quantityNeeded());
        demand.setPriority(formDTO.priority());
        demand.setJustification(PriorityDemandCreateMapper.normalize(formDTO.justification()));
        demand.setDeadline(formDTO.deadline());
    }
}
