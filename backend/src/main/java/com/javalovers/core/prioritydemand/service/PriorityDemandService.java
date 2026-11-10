package com.javalovers.core.prioritydemand.service;

import com.javalovers.common.exception.EntityNotFoundException;
import com.javalovers.core.appuser.domain.entity.AppUser;
import com.javalovers.core.item.domain.entity.Item;
import com.javalovers.core.item.service.ItemService;
import com.javalovers.core.prioritydemand.domain.dto.request.PriorityDemandFilterDTO;
import com.javalovers.core.prioritydemand.domain.dto.request.PriorityDemandFormDTO;
import com.javalovers.core.prioritydemand.domain.dto.response.PriorityDemandDTO;
import com.javalovers.core.prioritydemand.domain.entity.PriorityDemand;
import com.javalovers.core.prioritydemand.mapper.PriorityDemandCreateMapper;
import com.javalovers.core.prioritydemand.mapper.PriorityDemandDTOMapper;
import com.javalovers.core.prioritydemand.mapper.PriorityDemandUpdateMapper;
import com.javalovers.core.prioritydemand.repository.PriorityDemandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/** RF20 – Cadastro, consulta, atualização e inativação de demandas prioritárias. */
@Service
@RequiredArgsConstructor
public class PriorityDemandService {

    private final PriorityDemandRepository priorityDemandRepository;
    private final PriorityDemandCreateMapper createMapper;
    private final PriorityDemandUpdateMapper updateMapper;
    private final PriorityDemandDTOMapper dtoMapper;
    private final ItemService itemService;

    /** Ativas primeiro; depois prioridade (alta > baixa); prazo mais próximo; mais recentes. */
    private static final Comparator<PriorityDemand> DEFAULT_ORDER = Comparator
            .comparing((PriorityDemand d) -> d.isActive() ? 0 : 1)
            .thenComparing(d -> -d.getPriority().getWeight())
            .thenComparing(PriorityDemand::getDeadline, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(PriorityDemand::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));

    @Transactional
    public PriorityDemand create(PriorityDemandFormDTO formDTO, AppUser createdBy) {
        Item item = getValidItem(formDTO.itemId());
        if (formDTO.deadline() != null && formDTO.deadline().isBefore(LocalDate.now())) {
            throw new IllegalStateException("O prazo da demanda não pode estar no passado.");
        }
        PriorityDemand demand = createMapper.convert(formDTO, item, createdBy);
        return priorityDemandRepository.save(demand);
    }

    @Transactional
    public PriorityDemand update(Long id, PriorityDemandFormDTO formDTO) {
        PriorityDemand demand = getOrThrowException(id);
        Item item = demand.getItem() != null && demand.getItem().getItemId().equals(formDTO.itemId())
                ? demand.getItem()              // mantém o item atual mesmo que tenha sido excluído depois
                : getValidItem(formDTO.itemId());
        updateMapper.update(demand, formDTO, item);
        return priorityDemandRepository.save(demand);
    }

    @Transactional
    public PriorityDemand inactivate(Long id) {
        PriorityDemand demand = getOrThrowException(id);
        if (!demand.isActive()) {
            throw new IllegalStateException("A demanda já está inativa.");
        }
        demand.inactivate();
        return priorityDemandRepository.save(demand);
    }

    @Transactional
    public PriorityDemand reactivate(Long id) {
        PriorityDemand demand = getOrThrowException(id);
        if (demand.isActive()) {
            throw new IllegalStateException("A demanda já está ativa.");
        }
        if (demand.getItem() == null || demand.getItem().isDeleted()) {
            throw new IllegalStateException("Não é possível reativar: o item da demanda foi excluído do estoque.");
        }
        demand.reactivate();
        return priorityDemandRepository.save(demand);
    }

    public PriorityDemand getOrNull(Long id) {
        return priorityDemandRepository.findById(id).orElse(null);
    }

    public PriorityDemand getOrThrowException(Long id) {
        return priorityDemandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("demanda prioritária", id));
    }

    @Transactional(readOnly = true)
    public List<PriorityDemandDTO> list(PriorityDemandFilterDTO filter) {
        List<PriorityDemand> demands = priorityDemandRepository.findAll(generateSpecification(filter));
        LocalDate today = LocalDate.now();
        return demands.stream()
                .sorted(DEFAULT_ORDER)
                .map(d -> dtoMapper.convert(d, calculateReceived(d), today))
                .toList();
    }

    @Transactional(readOnly = true)
    public PriorityDemandDTO generateDTO(PriorityDemand demand) {
        return dtoMapper.convert(demand, calculateReceived(demand), LocalDate.now());
    }

    /**
     * Progresso "recebido X de Y": soma das doações do item registradas desde
     * a criação da demanda até o fim do dia do prazo (se houver prazo).
     */
    public long calculateReceived(PriorityDemand demand) {
        if (demand.getItem() == null || demand.getCreatedAt() == null) return 0L;

        Long itemId = demand.getItem().getItemId();
        Date from = toDate(demand.getCreatedAt());
        Number total = demand.getDeadline() == null
                ? priorityDemandRepository.sumDonatedSince(itemId, from)
                : priorityDemandRepository.sumDonatedBetween(itemId, from,
                        toDate(demand.getDeadline().plusDays(1).atStartOfDay()));
        return total != null ? total.longValue() : 0L;
    }

    private Item getValidItem(Long itemId) {
        Item item = itemService.getOrThrowException(itemId);
        if (item.isDeleted()) {
            throw new IllegalStateException("O item selecionado foi excluído do estoque.");
        }
        return item;
    }

    private static Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Specification<PriorityDemand> generateSpecification(PriorityDemandFilterDTO filter) {
        Specification<PriorityDemand> statusSpec = (root, query, cb) ->
                filter.status() == null ? cb.conjunction() : cb.equal(root.get("status"), filter.status());

        Specification<PriorityDemand> prioritySpec = (root, query, cb) ->
                filter.priority() == null ? cb.conjunction() : cb.equal(root.get("priority"), filter.priority());

        Specification<PriorityDemand> itemSpec = (root, query, cb) ->
                filter.itemId() == null ? cb.conjunction() : cb.equal(root.get("item").get("itemId"), filter.itemId());

        Specification<PriorityDemand> searchSpec = (root, query, cb) -> {
            if (filter.searchTerm() == null || filter.searchTerm().isBlank()) return cb.conjunction();
            String pattern = "%" + filter.searchTerm().trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get("item").get("description")), pattern);
        };

        return Specification.where(statusSpec).and(prioritySpec).and(itemSpec).and(searchSpec);
    }
}
