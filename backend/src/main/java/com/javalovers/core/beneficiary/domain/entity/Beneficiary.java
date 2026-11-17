package com.javalovers.core.beneficiary.domain.entity;

import com.javalovers.common.crypto.DataEncryptionService;
import com.javalovers.common.crypto.EncryptedStringConverter;
import com.javalovers.common.crypto.FieldEncryptor;
import com.javalovers.common.entity.SoftDeletable;
import com.javalovers.core.beneficiarystatus.BeneficiaryStatus;
import com.javalovers.core.appuser.domain.entity.AppUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "beneficiary")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Beneficiary implements SoftDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "beneficiary_id")
    private Long beneficiaryId;

    @NotBlank
    @Column(name = "full_name")
    private String fullName;

    /**
     * RNF12 / LGPD: documento (CPF/CRNM/NIF) criptografado no banco (AES-256-GCM).
     * Na aplicação o valor circula em claro; a conversão é automática.
     */
    @NotBlank
    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "cpf")
    private String cpf;

    /**
     * Índice cego (HMAC-SHA256) do documento normalizado. Permite busca exata e
     * garante unicidade sem guardar o documento em claro. Calculado automaticamente.
     */
    @Column(name = "cpf_hash", length = 64)
    private String cpfHash;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "phone")
    private String phone;

    /** Pode conter dado sensível (ex.: saúde – LGPD art. 5º, II), por isso também é criptografado. */
    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "socioeconomic_data")
    private String socioeconomicData;

    @Convert(converter = BeneficiaryStatus.BeneficiaryStatusConverter.class)
    @Column(name = "beneficiary_status")
    private BeneficiaryStatus beneficiaryStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approver_user_id")
    private AppUser approverId;

    @Column(name = "withdrawal_limit")
    private Integer withdrawalLimit;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    @PreUpdate
    protected void refreshDocumentIndex() {
        this.cpfHash = DataEncryptionService.encryptor().blindIndex(FieldEncryptor.normalizeDocument(cpf));
    }
}
