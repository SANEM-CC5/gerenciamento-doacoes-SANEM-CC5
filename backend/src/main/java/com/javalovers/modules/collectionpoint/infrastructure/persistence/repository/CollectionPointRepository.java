package com.javalovers.modules.collectionpoint.infrastructure.persistence.repository;

import com.javalovers.modules.collectionpoint.domain.entity.CollectionPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionPointRepository
        extends JpaRepository<CollectionPoint, Long>, JpaSpecificationExecutor<CollectionPoint> {

    @Query("SELECT c FROM CollectionPoint c WHERE c.collectionPointId = :id AND c.deletedAt IS NULL")
    Optional<CollectionPoint> findById(@Param("id") Long id);
}
