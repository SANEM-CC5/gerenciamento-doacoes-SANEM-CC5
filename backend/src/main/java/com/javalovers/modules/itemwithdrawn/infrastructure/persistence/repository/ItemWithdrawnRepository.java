package com.javalovers.modules.itemwithdrawn.infrastructure.persistence.repository;

import com.javalovers.modules.itemwithdrawn.domain.entity.ItemWithdrawn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemWithdrawnRepository extends JpaRepository<ItemWithdrawn,Long> {
    List<ItemWithdrawn> findByWithdrawal_WithdrawalId(Long withdrawalId);
}
