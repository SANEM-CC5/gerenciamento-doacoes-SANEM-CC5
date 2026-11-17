package com.javalovers.mapper;

import com.javalovers.model.entity.Item;
import com.javalovers.model.dto.PriorityDemandFormDTO;
import com.javalovers.model.entity.PriorityDemand;
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
