package com.javalovers.repository;

import com.javalovers.model.entity.ItemWithdrawn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemWithdrawnRepository extends JpaRepository<ItemWithdrawn,Long> {
    List<ItemWithdrawn> findByWithdrawal_WithdrawalId(Long withdrawalId);
}
