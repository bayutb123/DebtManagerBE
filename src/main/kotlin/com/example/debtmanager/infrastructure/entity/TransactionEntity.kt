package com.example.debtmanager.infrastructure.entity

import com.example.debtmanager.domain.model.TransactionType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "transactions")
class TransactionEntity(
    @Column(name = "reference_id", nullable = false)
    var referenceId: UUID,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: TransactionType,
    @Column(nullable = false)
    var amount: BigDecimal,
    @Column(nullable = false)
    var date: LocalDate,
    @Column(nullable = false)
    var note: String
) : BaseEntity()