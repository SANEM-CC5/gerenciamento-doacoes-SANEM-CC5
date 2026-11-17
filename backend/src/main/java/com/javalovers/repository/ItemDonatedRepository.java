package com.javalovers.repository;

import com.javalovers.model.entity.ItemDonated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemDonatedRepository extends JpaRepository<ItemDonated, Long> {
}
