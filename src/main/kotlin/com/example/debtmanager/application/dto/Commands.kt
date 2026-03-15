package com.example.debtmanager.application.dto

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class GoogleAuthCommand(val idToken: String)

data class ContactCommand(
    val name: String,
    val phone: String,
    val notes: String
)

data class DebtCommand(
    val contactId: UUID,
    val title: String,
    val amount: BigDecimal,
    val dueDate: LocalDate,
    val notes: String,
)

data class ReceivableCommand(
    val contactId: UUID,
    val title: String,
    val amount: BigDecimal,
    val dueDate: LocalDate,
    val notes: String,
)

data class PaymentCommand(
    val amount: BigDecimal,
    val date: LocalDate,
    val note: String?
)