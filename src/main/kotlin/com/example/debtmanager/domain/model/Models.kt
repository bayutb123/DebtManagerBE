package com.example.debtmanager.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

enum class Provider { GOOGLE }

enum class DebtStatus { ACTIVE, PARTIAL, PAID, OVERDUE }

enum class ReceivableStatus { ACTIVE, PARTIAL, PAID, OVERDUE }

enum class TransactionType { DEBT, RECEIVABLE }

data class User(
    val id: UUID,
    val email: String,
    val name: String,
    val pictureUrl: String,
    val provider: Provider,
    val createdAt: Instant,
)

data class Contact(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val phone: String,
    val notes: String,
    val createdAt: Instant,
)

data class Debt(
    val id: UUID,
    val userId: UUID,
    val contactId: UUID,
    val title: String,
    val amount: BigDecimal,
    val paidAmount: BigDecimal,
    val dueDate: LocalDate,
    val status: DebtStatus,
    val notes: String,
    val createdAt: Instant,
)

data class Receivable(
    val id: UUID,
    val userId: UUID,
    val contactId: UUID,
    val title: String,
    val amount: BigDecimal,
    val paidAmount: BigDecimal,
    val dueDate: LocalDate,
    val status: ReceivableStatus,
    val notes: String,
    val createdAt: Instant,
)

data class Transaction(
    val id: UUID,
    val referenceId: UUID,
    val type: TransactionType,
    val amount: BigDecimal,
    val date: LocalDate,
    val note: String,
    val createdAt: Instant,
)