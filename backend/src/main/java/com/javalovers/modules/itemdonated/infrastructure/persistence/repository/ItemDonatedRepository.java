package com.javalovers.modules.itemdonated.infrastructure.persistence.repository;

import com.javalovers.modules.itemdonated.domain.entity.ItemDonated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemDonatedRepository extends JpaRepository<ItemDonated, Long> {
}
