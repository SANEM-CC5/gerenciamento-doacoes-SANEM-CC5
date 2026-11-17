package com.sanem.repository;

import com.sanem.model.entity.WithdrawalLimitReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

public interface WithdrawalLimitResetRepository extends JpaRepository<WithdrawalLimitReset, Long> {

    @Query("""
        SELECT MAX(wlr.resetDate)
        FROM WithdrawalLimitReset wlr
        WHERE wlr.beneficiary.beneficiaryId = :beneficiaryId
    """)
    Optional<Date> findLastResetDateByBeneficiaryId(@Param("beneficiaryId") Long beneficiaryId);
}