package com.javalovers.repository;

import com.javalovers.model.entity.StockAlert;
import com.javalovers.model.enums.StockAlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockAlertRepository extends JpaRepository<StockAlert, Long> {
    Optional<StockAlert> findFirstByItemItemIdAndStatus(Long itemId, StockAlertStatus status);
}
