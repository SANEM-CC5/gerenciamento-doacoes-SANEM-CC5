package com.javalovers.core.stockalert.service;

import com.javalovers.core.item.domain.entity.Item;
import com.javalovers.core.notification.service.NotificationService;
import com.javalovers.core.stockalert.domain.entity.StockAlert;
import com.javalovers.core.stockalert.domain.enums.StockAlertStatus;
import com.javalovers.core.stockalert.repository.StockAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * RF19 – Verifica o saldo de um item contra o estoque mínimo configurado e,
 * quando o saldo atinge ou fica abaixo do mínimo, abre um alerta e notifica
 * os usuários autorizados.
 *
 * Regras:
 *  - saldo <= mínimo e nenhum alerta aberto  -> abre alerta + notifica;
 *  - saldo <= mínimo e alerta já aberto      -> nada (evita notificação repetida);
 *  - saldo  > mínimo (ou mínimo removido, ou item excluído) com alerta aberto -> resolve o alerta.
 *
 * Roda dentro da mesma transação da movimentação de estoque: se a operação
 * (doação, retirada, ajuste) falhar e sofrer rollback, o alerta também é desfeito.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockAlertService {

    private final StockAlertRepository stockAlertRepository;
    private final NotificationService notificationService;

    @Transactional
    public void evaluate(Item item) {
        if (item == null || item.getItemId() == null) return;

        Optional<StockAlert> openAlert =
                stockAlertRepository.findFirstByItemItemIdAndStatus(item.getItemId(), StockAlertStatus.OPEN);

        boolean shouldAlert = !item.isDeleted() && item.isLowStock();

        if (shouldAlert && openAlert.isEmpty()) {
            StockAlert alert = stockAlertRepository.save(new StockAlert(item));
            int notified = notificationService.notifyLowStock(alert);
            log.info("Estoque mínimo atingido para o item '{}' (saldo={}, mínimo={}). {} usuário(s) notificado(s).",
                    item.getDescription(), alert.getStockQuantity(), alert.getMinimumStock(), notified);
        } else if (!shouldAlert && openAlert.isPresent()) {
            StockAlert alert = openAlert.get();
            alert.resolve();
            stockAlertRepository.save(alert);
            log.info("Alerta de estoque mínimo do item '{}' resolvido (saldo atual={}).",
                    item.getDescription(), item.getStockQuantity());
        }
    }
}
