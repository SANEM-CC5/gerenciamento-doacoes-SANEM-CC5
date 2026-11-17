package com.sanem.repository;

import com.sanem.model.entity.ItemDonated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemDonatedRepository extends JpaRepository<ItemDonated, Long> {
}
