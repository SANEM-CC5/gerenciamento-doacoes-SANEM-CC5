package com.sanem.controller;

import com.sanem.util.HttpUtils;
import com.sanem.model.entity.AppUser;
import com.sanem.service.AuthService;
import com.sanem.model.dto.PriorityDemandFilterDTO;
import com.sanem.model.dto.PriorityDemandFormDTO;
import com.sanem.model.dto.PriorityDemandDTO;
import com.sanem.model.entity.PriorityDemand;
import com.sanem.service.PriorityDemandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * RF20 – Demandas prioritárias. Acesso restrito a usuários autenticados (RNF02).
 * Uma futura visualização pública pode expor um endpoint somente leitura
 * reaproveitando PriorityDemandService.list(...).
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/priority-demand")
public class PriorityDemandController {

    private final PriorityDemandService priorityDemandService;
    private final AuthService authService;

    @GetMapping("/all")
    public ResponseEntity<List<PriorityDemandDTO>> list(PriorityDemandFilterDTO filterDTO) {
        return ResponseEntity.ok(priorityDemandService.list(filterDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PriorityDemandDTO> get(@PathVariable Long id) {
        PriorityDemand demand = priorityDemandService.getOrNull(id);
        if (demand == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(priorityDemandService.generateDTO(demand));
    }

    @PostMapping
    public ResponseEntity<PriorityDemandDTO> create(@RequestBody @Valid PriorityDemandFormDTO formDTO,
                                                    UriComponentsBuilder uriComponentsBuilder) {
        AppUser currentUser = authService.getCurrentUser();
        PriorityDemand demand = priorityDemandService.create(formDTO, currentUser);
        URI uri = HttpUtils.createURI(uriComponentsBuilder, "priority-demand", demand.getPriorityDemandId());
        return ResponseEntity.created(uri).body(priorityDemandService.generateDTO(demand));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PriorityDemandDTO> update(@PathVariable Long id,
                                                    @RequestBody @Valid PriorityDemandFormDTO formDTO) {
        PriorityDemand demand = priorityDemandService.update(id, formDTO);
        return ResponseEntity.ok(priorityDemandService.generateDTO(demand));
    }

    @PatchMapping("/{id}/inactivate")
    public ResponseEntity<PriorityDemandDTO> inactivate(@PathVariable Long id) {
        PriorityDemand demand = priorityDemandService.inactivate(id);
        return ResponseEntity.ok(priorityDemandService.generateDTO(demand));
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<PriorityDemandDTO> reactivate(@PathVariable Long id) {
        PriorityDemand demand = priorityDemandService.reactivate(id);
        return ResponseEntity.ok(priorityDemandService.generateDTO(demand));
    }
}
