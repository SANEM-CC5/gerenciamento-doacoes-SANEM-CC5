package com.javalovers.core.beneficiary.service;

import com.javalovers.common.crypto.DataEncryptionService;
import com.javalovers.common.crypto.FieldEncryptor;
import com.javalovers.common.exception.EntityNotFoundException;
import com.javalovers.common.specification.SearchCriteria;
import com.javalovers.common.specification.SpecificationHelper;
import com.javalovers.core.beneficiary.domain.dto.request.BeneficiaryFilterDTO;
import com.javalovers.core.beneficiary.domain.dto.request.BeneficiaryFormDTO;
import com.javalovers.core.beneficiary.domain.dto.response.BeneficiaryDTO;
import com.javalovers.core.beneficiary.domain.entity.Beneficiary;
import com.javalovers.core.beneficiary.mapper.BeneficiaryCreateMapper;
import com.javalovers.core.beneficiary.mapper.BeneficiaryDTOMapper;
import com.javalovers.core.beneficiary.mapper.BeneficiaryUpdateMapper;
import com.javalovers.core.beneficiary.repository.BeneficiaryRepository;
import com.javalovers.core.beneficiary.specification.BeneficiarySpecification;
import com.javalovers.core.beneficiarystatus.BeneficiaryStatus;
import com.javalovers.core.appuser.domain.entity.AppUser;
import com.javalovers.core.appuser.service.AppUserService;
import com.javalovers.core.card.entity.Card;
import com.javalovers.core.card.repository.CardRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final BeneficiaryCreateMapper beneficiaryCreateMapper;
    private final BeneficiaryDTOMapper beneficiaryDTOMapper;
    private final BeneficiaryUpdateMapper beneficiaryUpdateMapper;
    private final CardRepository cardRepository;
    private final EntityManager entityManager;
    private final AppUserService appUserService;
    private final DataEncryptionService dataEncryptionService;

    /** Documento completo (CPF tem 11 dígitos): busca exata pelo índice cego, direto no banco. */
    private static final int FULL_DOCUMENT_LENGTH = 11;

    public Beneficiary generateBeneficiary(BeneficiaryFormDTO beneficiaryFormDTO) {
        return beneficiaryCreateMapper.convert(beneficiaryFormDTO);
    }

    public void save (Beneficiary beneficiary) {
        ensureDocumentIsUnique(beneficiary);
        beneficiaryRepository.save(beneficiary);
    }

    /**
     * RNF12: com o CPF criptografado (IV aleatório), a restrição UNIQUE na coluna cpf deixou de
     * funcionar. A unicidade passa a ser garantida pelo índice cego (cpf_hash), com mensagem clara.
     */
    private void ensureDocumentIsUnique(Beneficiary beneficiary) {
        String index = dataEncryptionService.documentIndex(beneficiary.getCpf());
        if (index == null) return;
        beneficiaryRepository.findFirstByCpfHash(index)
                .filter(other -> !other.getBeneficiaryId().equals(beneficiary.getBeneficiaryId()))
                .ifPresent(other -> {
                    throw new IllegalStateException("Já existe um beneficiário cadastrado com este CPF/CRNM.");
                });
    }

    @Transactional(readOnly = true)
    public BeneficiaryDTO generateBeneficiaryDTO(Beneficiary beneficiary) {
        if (beneficiary != null && beneficiary.getApproverId() != null) {
            Hibernate.initialize(beneficiary.getApproverId());
            if (beneficiary.getApproverId().getProfile() != null) {
                Hibernate.initialize(beneficiary.getApproverId().getProfile());
            }
        }
        return beneficiaryDTOMapper.convert(beneficiary);
    }


    public void updateBeneficiary(Beneficiary beneficiary, BeneficiaryFormDTO beneficiaryFormDTO) {
        beneficiaryUpdateMapper.update(beneficiary, beneficiaryFormDTO);
    }

    @Transactional
    public void delete(Beneficiary beneficiary) {
        Long beneficiaryId = beneficiary.getBeneficiaryId();
        
        // Primeiro, buscar os IDs dos withdrawals associados ao beneficiário (não deletados)
        Query selectWithdrawalIdsQuery = entityManager.createNativeQuery(
            "SELECT withdrawal_id FROM withdrawal WHERE beneficiary_id = ? AND deleted_at IS NULL"
        );
        selectWithdrawalIdsQuery.setParameter(1, beneficiaryId);
        @SuppressWarnings("unchecked")
        List<Object> withdrawalIds = selectWithdrawalIdsQuery.getResultList();
        
        // Soft delete dos registros em item_withdrawn que referenciam esses withdrawals
        if (!withdrawalIds.isEmpty()) {
            for (Object withdrawalIdObj : withdrawalIds) {
                Long withdrawalId = ((Number) withdrawalIdObj).longValue();
                Query deleteItemWithdrawnQuery = entityManager.createNativeQuery(
                    "UPDATE item_withdrawn SET deleted_at = NOW() WHERE withdrawal_id = ? AND deleted_at IS NULL"
                );
                deleteItemWithdrawnQuery.setParameter(1, withdrawalId);
                deleteItemWithdrawnQuery.executeUpdate();
            }
        }
        
        // Soft delete dos withdrawals associados ao beneficiário
        Query deleteWithdrawalQuery = entityManager.createNativeQuery(
            "UPDATE withdrawal SET deleted_at = NOW() WHERE beneficiary_id = ? AND deleted_at IS NULL"
        );
        deleteWithdrawalQuery.setParameter(1, beneficiaryId);
        deleteWithdrawalQuery.executeUpdate();
        
        // Soft delete do card associado ao beneficiário (se existir)
        Optional<Card> cardOptional = cardRepository.findByBeneficiaryId(beneficiaryId);
        
        if (cardOptional.isPresent() && !cardOptional.get().isDeleted()) {
            Card card = cardOptional.get();
            card.softDelete();
            cardRepository.save(card);
        }
        
        // Soft delete do beneficiário
        beneficiary.softDelete();
        beneficiaryRepository.save(beneficiary);
    }

    @Transactional(readOnly = true)
    public List<Beneficiary> list(BeneficiaryFilterDTO beneficiaryFilterDTO) {
        Specification<Beneficiary> beneficiarySpecification = generateSpecification(beneficiaryFilterDTO);
        return applyEncryptedFieldFilters(beneficiaryRepository.findAll(beneficiarySpecification), beneficiaryFilterDTO);
    }

    @Transactional(readOnly = true)
    public List<BeneficiaryDTO> listAsDTO(BeneficiaryFilterDTO beneficiaryFilterDTO) {
        Specification<Beneficiary> beneficiarySpecification = generateSpecification(beneficiaryFilterDTO);
        List<Beneficiary> beneficiaries = applyEncryptedFieldFilters(
                beneficiaryRepository.findAll(beneficiarySpecification), beneficiaryFilterDTO);
        // Force eager loading of approverId and its profile to avoid LazyInitializationException
        beneficiaries.forEach(b -> {
            if (b.getApproverId() != null) {
                Hibernate.initialize(b.getApproverId());
                if (b.getApproverId().getProfile() != null) {
                    Hibernate.initialize(b.getApproverId().getProfile());
                }
            }
        });
        return beneficiaries.stream().map(beneficiaryDTOMapper::convert).toList();
    }

    @Transactional(readOnly = true)
    public Page<Beneficiary> list(Pageable pageable, BeneficiaryFilterDTO beneficiaryFilterDTO) {
        Specification<Beneficiary> beneficiarySpecification = generateSpecification(beneficiaryFilterDTO);
        if (!hasInMemoryFilters(beneficiaryFilterDTO)) {
            return beneficiaryRepository.findAll(beneficiarySpecification, pageable);
        }
        // Filtro parcial em campo criptografado: filtra em memória e pagina o resultado
        List<Beneficiary> filtered = applyEncryptedFieldFilters(
                beneficiaryRepository.findAll(beneficiarySpecification, pageable.getSort()), beneficiaryFilterDTO);
        if (pageable.isUnpaged()) {
            return new PageImpl<>(filtered, pageable, filtered.size());
        }
        int from = (int) Math.min(pageable.getOffset(), filtered.size());
        int to = Math.min(from + pageable.getPageSize(), filtered.size());
        return new PageImpl<>(filtered.subList(from, to), pageable, filtered.size());
    }

    // ---------------------------------------------------------------------------------
    // RNF12: CPF, telefone e dados socioeconômicos estão criptografados no banco, então
    // LIKE no SQL não funciona mais. CPF completo usa o índice cego (exato, no banco);
    // buscas parciais são aplicadas em memória, sobre os valores já descriptografados.
    // ---------------------------------------------------------------------------------

    private boolean isFullDocumentSearch(String document) {
        String normalized = FieldEncryptor.normalizeDocument(document);
        return normalized != null && normalized.length() >= FULL_DOCUMENT_LENGTH;
    }

    private boolean hasInMemoryFilters(BeneficiaryFilterDTO filter) {
        return (hasText(filter.cpf()) && !isFullDocumentSearch(filter.cpf()))
                || hasText(filter.phone())
                || hasText(filter.socioeconomicData());
    }

    private List<Beneficiary> applyEncryptedFieldFilters(List<Beneficiary> beneficiaries, BeneficiaryFilterDTO filter) {
        if (!hasInMemoryFilters(filter)) return beneficiaries;

        String cpf = hasText(filter.cpf()) && !isFullDocumentSearch(filter.cpf())
                ? FieldEncryptor.normalizeDocument(filter.cpf()) : null;
        String phone = hasText(filter.phone()) ? filter.phone().replaceAll("\\D", "") : null;
        String socio = hasText(filter.socioeconomicData()) ? filter.socioeconomicData().toLowerCase(Locale.ROOT) : null;

        return beneficiaries.stream()
                .filter(b -> cpf == null || contains(FieldEncryptor.normalizeDocument(b.getCpf()), cpf))
                .filter(b -> phone == null || phone.isEmpty()
                        || contains(b.getPhone() == null ? null : b.getPhone().replaceAll("\\D", ""), phone))
                .filter(b -> socio == null
                        || contains(b.getSocioeconomicData() == null ? null : b.getSocioeconomicData().toLowerCase(Locale.ROOT), socio))
                .toList();
    }

    private static boolean contains(String value, String term) {
        return value != null && value.contains(term);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private Specification<Beneficiary> generateSpecification(BeneficiaryFilterDTO beneficiaryFilterDTO) {
        SearchCriteria<String> fullNameCriteria = SpecificationHelper.generateInnerLikeCriteria("fullName", beneficiaryFilterDTO.fullName());
        Specification<Beneficiary> fullNameSpecification = new BeneficiarySpecification(fullNameCriteria);

        // RNF12: CPF completo -> igualdade no índice cego. Parcial, telefone e dados
        // socioeconômicos são filtrados em memória (applyEncryptedFieldFilters).
        Specification<Beneficiary> cpfSpecification = (root, query, criteriaBuilder) -> {
            if (!isFullDocumentSearch(beneficiaryFilterDTO.cpf())) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("cpfHash"),
                    dataEncryptionService.documentIndex(beneficiaryFilterDTO.cpf()));
        };
        
        Specification<Beneficiary> statusSpecification = (root, query, criteriaBuilder) -> {
            if (beneficiaryFilterDTO.beneficiaryStatus() == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("beneficiaryStatus"), beneficiaryFilterDTO.beneficiaryStatus());
        };

        Specification<Beneficiary> notDeletedSpecification = (root, query, criteriaBuilder) -> 
            criteriaBuilder.isNull(root.get("deletedAt"));

        return Specification.where(fullNameSpecification)
                .and(cpfSpecification)
                .and(statusSpecification) 
                .and(notDeletedSpecification);
    }

    @Transactional(readOnly = true)
    public Page<BeneficiaryDTO> generateBeneficiaryDTOPage(Page<Beneficiary> beneficiaryPage) {
        return beneficiaryPage.map(this::generateBeneficiaryDTO);
    }

    public List<BeneficiaryDTO> generateBeneficiaryDTOList(List<Beneficiary> beneficiaryList) {
        return beneficiaryList.stream().map(beneficiaryDTOMapper::convert).toList();
    }

    @Transactional(readOnly = true)
    public Beneficiary getOrNull(Long id) {
        Beneficiary beneficiary = beneficiaryRepository.findById(id).orElse(null);
        if (beneficiary != null && beneficiary.getApproverId() != null) {
            Hibernate.initialize(beneficiary.getApproverId());
            if (beneficiary.getApproverId().getProfile() != null) {
                Hibernate.initialize(beneficiary.getApproverId().getProfile());
            }
        }
        return beneficiary;
    }
    
    public Beneficiary getOrThrowException(Long id) {
        return beneficiaryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("beneficiary", id)
        );
    }

    @Transactional
    public void approveBeneficiary(Long beneficiaryId, Long approverUserId) {
        Beneficiary beneficiary = getOrThrowException(beneficiaryId);
        AppUser approver = appUserService.getOrThrowException(approverUserId);
        
        beneficiary.setBeneficiaryStatus(BeneficiaryStatus.APPROVED);
        beneficiary.setApproverId(approver);
        
        beneficiaryRepository.save(beneficiary);
    }

    @Transactional
    public void rejectBeneficiary(Long beneficiaryId, Long approverUserId) {
        Beneficiary beneficiary = getOrThrowException(beneficiaryId);
        AppUser approver = appUserService.getOrThrowException(approverUserId);

        beneficiary.setBeneficiaryStatus(BeneficiaryStatus.REJECTED);
        beneficiary.setApproverId(approver);

        beneficiaryRepository.save(beneficiary);
    }

    @Transactional
    public void inactivateBeneficiary(Long beneficiaryId, Long approverUserId) {
        Beneficiary beneficiary = getOrThrowException(beneficiaryId);
        AppUser approver = appUserService.getOrThrowException(approverUserId);

        beneficiary.setBeneficiaryStatus(BeneficiaryStatus.INACTIVE);
        beneficiary.setApproverId(approver);

        beneficiaryRepository.save(beneficiary);
    }

    @Transactional
    public void reactivateBeneficiary(Long beneficiaryId, Long approverUserId) {
        Beneficiary beneficiary = getOrThrowException(beneficiaryId);
        AppUser approver = appUserService.getOrThrowException(approverUserId);

        beneficiary.setBeneficiaryStatus(BeneficiaryStatus.APPROVED);
        beneficiary.setApproverId(approver);

        beneficiaryRepository.save(beneficiary);
    }
}
