package com.example.debtmanager.interfaces.request

import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class GoogleAuthRequest(
    @field:NotBlank val idToken: String
)

data class ContactRequest(
    @field:NotBlank val name: String,
    @field:NotBlank val phone: String,
    val notes: String = ""
)

data class DebtRequest(
    @field:NotNull val contactId: UUID,
    @field:NotBlank val title: String,
    @field:Positive val amount: BigDecimal,
    @field:NotNull val dueDate: LocalDate,
    val notes: String = ""
)

data class ReceivableRequest(
    @field:NotNull val contactId: UUID,
    @field:NotBlank val title: String,
    @field:Positive val amount: BigDecimal,
    @field:NotNull val dueDate: LocalDate,
    val notes: String = ""
)

data class PaymentRequest(
    @field:Positive val amount: BigDecimal,
    @field:NotNull val date: LocalDate,
    val note: String? = null
)