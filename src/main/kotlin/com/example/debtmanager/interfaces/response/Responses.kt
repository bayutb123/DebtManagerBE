package com.example.debtmanager.interfaces.response

import com.example.debtmanager.domain.model.*
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class UserResponse(val id: UUID, val email: String, val name: String, val pictureUrl: String)

data class ContactResponse(val id: UUID, val name: String, val phone: String, val notes: String, val createdAt: Instant)

data class DebtResponse(
    val id: UUID,
    val contactId: UUID,
    val title: String,
    val amount: BigDecimal,
    val paidAmount: BigDecimal,
    val dueDate: LocalDate,
    val status: DebtStatus,
    val notes: String,
    val createdAt: Instant
)

data class ReceivableResponse(
    val id: UUID,
    val contactId: UUID,
    val title: String,
    val amount: BigDecimal,
    val paidAmount: BigDecimal,
    val dueDate: LocalDate,
    val status: ReceivableStatus,
    val notes: String,
    val createdAt: Instant
)

data class TransactionResponse(
    val id: UUID,
    val referenceId: UUID,
    val type: TransactionType,
    val amount: BigDecimal,
    val date: LocalDate,
    val note: String,
    val createdAt: Instant
)

data class AuthResponse(val token: String, val user: UserResponse)

data class DashboardSummaryResponse(
    val totalDebt: BigDecimal,
    val totalReceivable: BigDecimal,
    val netBalance: BigDecimal,
    val recentTransactions: List<TransactionResponse>
)

fun User.toResponse() = UserResponse(id, email, name, pictureUrl)
fun Contact.toResponse() = ContactResponse(id, name, phone, notes, createdAt)
fun Debt.toResponse() = DebtResponse(id, contactId, title, amount, paidAmount, dueDate, status, notes, createdAt)
fun Receivable.toResponse() = ReceivableResponse(id, contactId, title, amount, paidAmount, dueDate, status, notes, createdAt)
fun Transaction.toResponse() = TransactionResponse(id, referenceId, type, amount, date, note, createdAt)