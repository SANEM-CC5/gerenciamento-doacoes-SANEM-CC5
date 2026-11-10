package com.javalovers.core.prioritydemand;

import com.javalovers.core.item.domain.entity.Item;
import com.javalovers.core.item.service.ItemService;
import com.javalovers.core.prioritydemand.domain.dto.request.PriorityDemandFormDTO;
import com.javalovers.core.prioritydemand.domain.dto.response.PriorityDemandDTO;
import com.javalovers.core.prioritydemand.domain.entity.PriorityDemand;
import com.javalovers.core.prioritydemand.domain.enums.DemandPriority;
import com.javalovers.core.prioritydemand.domain.enums.DemandStatus;
import com.javalovers.core.prioritydemand.mapper.PriorityDemandCreateMapper;
import com.javalovers.core.prioritydemand.mapper.PriorityDemandDTOMapper;
import com.javalovers.core.prioritydemand.mapper.PriorityDemandUpdateMapper;
import com.javalovers.core.prioritydemand.repository.PriorityDemandRepository;
import com.javalovers.core.prioritydemand.service.PriorityDemandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/** RF20 – progresso "recebido X de Y" e regras de cadastro/inativação. */
@ExtendWith(MockitoExtension.class)
class PriorityDemandTest {

    @Mock
    private PriorityDemandRepository repository;

    @Mock
    private ItemService itemService;

    private PriorityDemandService service;
    private final PriorityDemandDTOMapper dtoMapper = new PriorityDemandDTOMapper();

    @BeforeEach
    void setUp() {
        service = new PriorityDemandService(repository, new PriorityDemandCreateMapper(),
                new PriorityDemandUpdateMapper(), dtoMapper, itemService);
    }

    private Item item() {
        Item item = new Item();
        item.setItemId(8L);
        item.setDescription("Fralda Descartável Tam M");
        item.setStockQuantity(45L);
        return item;
    }

    private PriorityDemand demand(long needed, LocalDate deadline) {
        PriorityDemand d = new PriorityDemand();
        d.setPriorityDemandId(1L);
        d.setItem(item());
        d.setQuantityNeeded(needed);
        d.setPriority(DemandPriority.HIGH);
        d.setDeadline(deadline);
        d.setStatus(DemandStatus.ACTIVE);
        d.setCreatedAt(LocalDateTime.of(2026, 6, 1, 0, 0));
        return d;
    }

    // ---------- progresso (mapper) ----------

    @Test
    void calculaProgressoParcial() {
        PriorityDemandDTO dto = dtoMapper.convert(demand(100, null), 40, LocalDate.of(2026, 11, 2));
        assertEquals(40L, dto.quantityReceived());
        assertEquals(60L, dto.quantityRemaining());
        assertEquals(40, dto.progressPercent());
        assertFalse(dto.fulfilled());
    }

    @Test
    void demandaAtendidaQuandoRecebidoAtingeANecessidade() {
        PriorityDemandDTO dto = dtoMapper.convert(demand(100, null), 130, LocalDate.of(2026, 11, 2));
        assertTrue(dto.fulfilled());
        assertEquals(100, dto.progressPercent()); // limitado a 100%
        assertEquals(0L, dto.quantityRemaining());
    }

    @Test
    void marcaComoVencidaQuandoPrazoPassouSemAtender() {
        PriorityDemandDTO dto = dtoMapper.convert(demand(100, LocalDate.of(2026, 10, 1)), 10, LocalDate.of(2026, 11, 2));
        assertTrue(dto.overdue());
    }

    @Test
    void naoMarcaComoVencidaSeJaFoiAtendida() {
        PriorityDemandDTO dto = dtoMapper.convert(demand(100, LocalDate.of(2026, 10, 1)), 100, LocalDate.of(2026, 11, 2));
        assertFalse(dto.overdue());
    }

    // ---------- progresso (service) ----------

    @Test
    void semPrazoSomaDoacoesDesdeACriacao() {
        when(repository.sumDonatedSince(eq(8L), any(Date.class))).thenReturn(35L);
        assertEquals(35L, service.calculateReceived(demand(100, null)));
        verify(repository, never()).sumDonatedBetween(any(), any(), any());
    }

    @Test
    void comPrazoSomaDoacoesAteOFimDoDiaDoPrazo() {
        when(repository.sumDonatedBetween(eq(8L), any(Date.class), any(Date.class))).thenReturn(12L);
        assertEquals(12L, service.calculateReceived(demand(100, LocalDate.of(2026, 12, 20))));
        verify(repository, never()).sumDonatedSince(any(), any());
    }

    // ---------- regras de cadastro ----------

    @Test
    void naoPermitePrazoNoPassadoAoCadastrar() {
        when(itemService.getOrThrowException(8L)).thenReturn(item());
        PriorityDemandFormDTO form = new PriorityDemandFormDTO(8L, 10L, DemandPriority.HIGH, null,
                LocalDate.now().minusDays(1));
        assertThrows(IllegalStateException.class, () -> service.create(form, null));
        verify(repository, never()).save(any(PriorityDemand.class));
    }

    @Test
    void naoPermiteDemandaParaItemExcluido() {
        Item deleted = item();
        deleted.setDeletedAt(LocalDateTime.now());
        when(itemService.getOrThrowException(8L)).thenReturn(deleted);
        PriorityDemandFormDTO form = new PriorityDemandFormDTO(8L, 10L, DemandPriority.LOW, null, null);
        assertThrows(IllegalStateException.class, () -> service.create(form, null));
    }

    @Test
    void inativarMudaStatusSemExcluir() {
        PriorityDemand d = demand(100, null);
        when(repository.findById(1L)).thenReturn(Optional.of(d));
        when(repository.save(any(PriorityDemand.class))).thenAnswer(inv -> inv.getArgument(0));

        service.inactivate(1L);

        assertEquals(DemandStatus.INACTIVE, d.getStatus());
        assertNotNull(d.getInactivatedAt());
        verify(repository, never()).delete(any(PriorityDemand.class));
    }

    @Test
    void reativarDemandaInativa() {
        PriorityDemand d = demand(100, null);
        d.inactivate();
        when(repository.findById(1L)).thenReturn(Optional.of(d));
        when(repository.save(any(PriorityDemand.class))).thenAnswer(inv -> inv.getArgument(0));

        service.reactivate(1L);

        assertEquals(DemandStatus.ACTIVE, d.getStatus());
        assertNull(d.getInactivatedAt());
    }
}
