package com.javalovers.modules.donation.infrastructure.persistence.repository;

import com.javalovers.modules.donation.domain.entity.Donation;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long>, JpaSpecificationExecutor<Donation> {
    @Query("SELECT d FROM Donation d WHERE d.deletedAt IS NULL")
    List<Donation> findAll();
    
    @Query("SELECT d FROM Donation d WHERE d.donationId = :id AND d.deletedAt IS NULL")
    Optional<Donation> findById(@Param("id") Long id);
}
