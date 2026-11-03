package com.javalovers.core.stockalert.repository;

import com.javalovers.core.stockalert.domain.entity.StockAlert;
import com.javalovers.core.stockalert.domain.enums.StockAlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockAlertRepository extends JpaRepository<StockAlert, Long> {
    Optional<StockAlert> findFirstByItemItemIdAndStatus(Long itemId, StockAlertStatus status);
}
