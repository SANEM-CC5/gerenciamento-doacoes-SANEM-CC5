package com.javalovers.modules.item.infrastructure.persistence.repository;

import com.javalovers.modules.item.domain.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {
    @org.springframework.data.jpa.repository.Query("SELECT i FROM Item i WHERE i.tagCode = :tagCode AND i.deletedAt IS NULL")
    java.util.Optional<Item> findByTagCode(@org.springframework.data.repository.query.Param("tagCode") String tagCode);
}
