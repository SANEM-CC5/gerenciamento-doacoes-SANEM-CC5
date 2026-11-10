package com.javalovers.core.prioritydemand.repository;

import com.javalovers.core.prioritydemand.domain.entity.PriorityDemand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface PriorityDemandRepository extends JpaRepository<PriorityDemand, Long>, JpaSpecificationExecutor<PriorityDemand> {

    /** Total doado do item a partir de uma data (doações e itens não excluídos). */
    @Query("SELECT COALESCE(SUM(idn.quantity), 0) FROM ItemDonated idn JOIN idn.donation d " +
           "WHERE idn.item.itemId = :itemId AND idn.deletedAt IS NULL AND d.deletedAt IS NULL " +
           "AND d.donationDate >= :fromDate")
    Number sumDonatedSince(@Param("itemId") Long itemId, @Param("fromDate") Date fromDate);

    /** Total doado do item dentro de uma janela [fromDate, toDate). */
    @Query("SELECT COALESCE(SUM(idn.quantity), 0) FROM ItemDonated idn JOIN idn.donation d " +
           "WHERE idn.item.itemId = :itemId AND idn.deletedAt IS NULL AND d.deletedAt IS NULL " +
           "AND d.donationDate >= :fromDate AND d.donationDate < :toDate")
    Number sumDonatedBetween(@Param("itemId") Long itemId,
                             @Param("fromDate") Date fromDate,
                             @Param("toDate") Date toDate);
}
