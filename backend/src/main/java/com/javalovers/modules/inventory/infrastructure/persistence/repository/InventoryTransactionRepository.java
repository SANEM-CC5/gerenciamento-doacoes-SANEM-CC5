package com.javalovers.modules.inventory.infrastructure.persistence.repository;

import com.javalovers.modules.inventory.domain.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    boolean existsByItemItemId(Long itemId);
}
