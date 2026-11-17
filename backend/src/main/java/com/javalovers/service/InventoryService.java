package com.javalovers.service;

import com.javalovers.model.entity.InventoryTransaction;
import com.javalovers.model.enums.TransactionType;
import com.javalovers.repository.InventoryTransactionRepository;
import com.javalovers.model.entity.Item;
import com.javalovers.repository.ItemRepository;
import com.javalovers.service.StockAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryTransactionRepository transactionRepository;
    private final ItemRepository itemRepository;
    private final StockAlertService stockAlertService;

    @Transactional
    public void processTransaction(Item item, Long quantity, TransactionType type, Long referenceId) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("A quantidade da transação deve ser maior que zero.");
        }

        if (isOutflow(type) && item.getStockQuantity() < quantity) {
            throw new IllegalStateException(
                String.format("Estoque insuficiente para o item '%s'. Solicitado: %d, Disponível: %d", 
                item.getDescription(), quantity, item.getStockQuantity())
            );
        }

        InventoryTransaction transaction = new InventoryTransaction(item, quantity, type, referenceId);
        transactionRepository.save(transaction);

        Long newStock = calculateNewStock(item.getStockQuantity(), quantity, type);
        item.setStockQuantity(newStock);
        itemRepository.save(item);

        // RF19: verifica estoque mínimo após toda movimentação (doação, retirada, ajuste, estorno)
        stockAlertService.evaluate(item);
    }

    private boolean isOutflow(TransactionType type) {
        return type == TransactionType.WITHDRAWAL_OUT || 
               type == TransactionType.DONATION_REVERSAL || 
               type == TransactionType.MANUAL_ADJUSTMENT_OUT;
    }

    private Long calculateNewStock(Long currentStock, Long quantity, TransactionType type) {
        if (currentStock == null) currentStock = 0L;
        
        if (isOutflow(type)) {
            return currentStock - quantity;
        }
        return currentStock + quantity; 
    }

    public boolean hasTransactions(Long itemId) {
        return transactionRepository.existsByItemItemId(itemId);
    }
}
