package com.javalovers.modules.withdrawal.domain.entity;

import com.javalovers.shared.entity.SoftDeletable;
import com.javalovers.modules.appuser.domain.entity.AppUser;
import com.javalovers.modules.beneficiary.domain.entity.Beneficiary;
import com.javalovers.modules.itemwithdrawn.domain.entity.ItemWithdrawn;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "withdrawal")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Withdrawal implements SoftDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "withdrawal_id")
    private Long withdrawalId;

    @NotNull
    @Column(name = "withdrawal_date")
    private Date withdrawalDate;

    @NotNull
    @JoinColumn(name = "beneficiary_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Beneficiary beneficiary;

    @NotNull
    @JoinColumn(name = "attendant_user_id")
    @OneToOne(fetch = FetchType.LAZY)
    private AppUser attendantUser;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "withdrawal", fetch = FetchType.LAZY)
    private List<ItemWithdrawn> items;
}
