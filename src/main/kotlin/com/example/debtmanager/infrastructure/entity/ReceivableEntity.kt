package com.example.debtmanager.infrastructure.entity

import com.example.debtmanager.domain.model.ReceivableStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "receivables")
class ReceivableEntity(
    @Column(name = "user_id", nullable = false)
    var userId: UUID,
    @Column(name = "contact_id", nullable = false)
    var contactId: UUID,
    @Column(nullable = false)
    var title: String,
    @Column(nullable = false)
    var amount: BigDecimal,
    @Column(name = "paid_amount", nullable = false)
    var paidAmount: BigDecimal,
    @Column(name = "due_date", nullable = false)
    var dueDate: LocalDate,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ReceivableStatus,
    @Column(nullable = false)
    var notes: String
) : BaseEntity()