package com.javalovers.core.stockalert.service;

import com.javalovers.core.item.domain.entity.Item;
import com.javalovers.core.notification.service.NotificationService;
import com.javalovers.core.stockalert.domain.entity.StockAlert;
import com.javalovers.core.stockalert.domain.enums.StockAlertStatus;
import com.javalovers.core.stockalert.repository.StockAlertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** RF19 – regras de disparo/resolução do alerta de estoque mínimo. */
@ExtendWith(MockitoExtension.class)
class StockAlertServiceTest {

    @Mock
    private StockAlertRepository stockAlertRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private StockAlertService stockAlertService;

    private Item item(long stock, Long minimum) {
        Item item = new Item();
        item.setItemId(1L);
        item.setDescription("Cobertor de casal");
        item.setStockQuantity(stock);
        item.setMinimumStock(minimum);
        return item;
    }

    private void noOpenAlert() {
        when(stockAlertRepository.findFirstByItemItemIdAndStatus(1L, StockAlertStatus.OPEN))
                .thenReturn(Optional.empty());
    }

    @Test
    void abreAlertaENotificaQuandoSaldoAtingeOMinimo() {
        noOpenAlert();
        when(stockAlertRepository.save(any(StockAlert.class))).thenAnswer(inv -> inv.getArgument(0));

        stockAlertService.evaluate(item(3, 3L)); // saldo == mínimo -> "atingir"

        ArgumentCaptor<StockAlert> captor = ArgumentCaptor.forClass(StockAlert.class);
        verify(notificationService).notifyLowStock(captor.capture());
        assertEquals(StockAlertStatus.OPEN, captor.getValue().getStatus());
        assertEquals(3L, captor.getValue().getStockQuantity());
        assertEquals(3L, captor.getValue().getMinimumStock());
    }

    @Test
    void abreAlertaQuandoSaldoFicaAbaixoDoMinimo() {
        noOpenAlert();
        when(stockAlertRepository.save(any(StockAlert.class))).thenAnswer(inv -> inv.getArgument(0));

        stockAlertService.evaluate(item(1, 3L));

        verify(notificationService).notifyLowStock(any(StockAlert.class));
    }

    @Test
    void naoNotificaQuandoSaldoEstaAcimaDoMinimo() {
        noOpenAlert();

        stockAlertService.evaluate(item(4, 3L));

        verifyNoInteractions(notificationService);
        verify(stockAlertRepository, never()).save(any());
    }

    @Test
    void naoNotificaNovamenteEnquantoAlertaEstiverAberto() {
        StockAlert open = new StockAlert(item(2, 3L));
        when(stockAlertRepository.findFirstByItemItemIdAndStatus(1L, StockAlertStatus.OPEN))
                .thenReturn(Optional.of(open));

        stockAlertService.evaluate(item(1, 3L));

        verifyNoInteractions(notificationService);
        verify(stockAlertRepository, never()).save(any());
    }

    @Test
    void resolveAlertaQuandoEstoqueEReposto() {
        StockAlert open = new StockAlert(item(2, 3L));
        when(stockAlertRepository.findFirstByItemItemIdAndStatus(1L, StockAlertStatus.OPEN))
                .thenReturn(Optional.of(open));

        stockAlertService.evaluate(item(10, 3L));

        assertEquals(StockAlertStatus.RESOLVED, open.getStatus());
        assertNotNull(open.getResolvedAt());
        verify(stockAlertRepository).save(open);
        verifyNoInteractions(notificationService);
    }

    @Test
    void ignoraItemSemEstoqueMinimoConfigurado() {
        noOpenAlert();

        stockAlertService.evaluate(item(0, null));

        verifyNoInteractions(notificationService);
        verify(stockAlertRepository, never()).save(any());
    }

    @Test
    void itemExcluidoNaoGeraNotificacaoEResolveAlertaAberto() {
        Item deleted = item(0, 3L);
        deleted.setDeletedAt(LocalDateTime.now());
        StockAlert open = new StockAlert(item(1, 3L));
        when(stockAlertRepository.findFirstByItemItemIdAndStatus(1L, StockAlertStatus.OPEN))
                .thenReturn(Optional.of(open));

        stockAlertService.evaluate(deleted);

        assertEquals(StockAlertStatus.RESOLVED, open.getStatus());
        verifyNoInteractions(notificationService);
    }
}
