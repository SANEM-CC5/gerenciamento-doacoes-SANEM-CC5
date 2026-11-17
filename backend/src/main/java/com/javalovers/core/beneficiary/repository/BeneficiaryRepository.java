package com.javalovers.core.beneficiary.repository;

import com.javalovers.core.beneficiary.domain.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long>, JpaSpecificationExecutor<Beneficiary> {
    @Query("SELECT b FROM Beneficiary b WHERE b.beneficiaryId = :id AND b.deletedAt IS NULL")
    java.util.Optional<Beneficiary> findById(@org.springframework.data.repository.query.Param("id") Long id);

    /** RNF12: busca pelo índice cego do documento (inclui excluídos, como a antiga restrição UNIQUE). */
    java.util.Optional<Beneficiary> findFirstByCpfHash(String cpfHash);
}
