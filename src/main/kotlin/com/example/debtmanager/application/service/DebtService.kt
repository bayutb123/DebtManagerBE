package com.example.debtmanager.application.service

import com.example.debtmanager.application.dto.DebtCommand
import com.example.debtmanager.application.dto.PaymentCommand
import com.example.debtmanager.common.exception.NotFoundException
import com.example.debtmanager.common.exception.ValidationException
import com.example.debtmanager.common.utils.SecurityUtils
import com.example.debtmanager.domain.model.Debt
import com.example.debtmanager.domain.model.DebtStatus
import com.example.debtmanager.domain.repository.DebtRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Service
class DebtService(private val debtRepository: DebtRepository) {
    fun list(): List<Debt> = debtRepository.findAllByUserId(SecurityUtils.currentUserId())

    fun get(id: UUID): Debt = debtRepository.findByIdAndUserId(id, SecurityUtils.currentUserId())
        ?: throw NotFoundException("Debt not found")

    fun create(command: DebtCommand): Debt {
        if (command.amount <= BigDecimal.ZERO) throw ValidationException("Amount must be greater than zero")
        val debt = Debt(
            id = UUID.randomUUID(),
            userId = SecurityUtils.currentUserId(),
            contactId = command.contactId,
            title = command.title,
            amount = command.amount,
            paidAmount = BigDecimal.ZERO,
            dueDate = command.dueDate,
            status = calculateStatus(command.amount, BigDecimal.ZERO, command.dueDate),
            notes = command.notes,
            createdAt = Instant.now()
        )
        return debtRepository.save(debt)
    }

    fun update(id: UUID, command: DebtCommand): Debt {
        val existing = get(id)
        val updated = existing.copy(
            contactId = command.contactId,
            title = command.title,
            amount = command.amount,
            dueDate = command.dueDate,
            notes = command.notes,
            status = calculateStatus(command.amount, existing.paidAmount, command.dueDate)
        )
        return debtRepository.save(updated)
    }

    fun delete(id: UUID) {
        get(id)
        debtRepository.delete(id)
    }

    fun addPayment(id: UUID, payment: PaymentCommand): Debt {
        if (payment.amount <= BigDecimal.ZERO) throw ValidationException("Payment amount must be greater than zero")
        val userId = SecurityUtils.currentUserId()
        debtRepository.findByIdAndUserId(id, userId) ?: throw NotFoundException("Debt not found")
        return debtRepository.updatePayment(id, userId, payment.amount, payment.date, payment.note)
    }

    private fun calculateStatus(amount: BigDecimal, paid: BigDecimal, dueDate: LocalDate): DebtStatus = when {
        paid >= amount -> DebtStatus.PAID
        paid > BigDecimal.ZERO && paid < amount -> DebtStatus.PARTIAL
        dueDate.isBefore(LocalDate.now()) -> DebtStatus.OVERDUE
        else -> DebtStatus.ACTIVE
    }
}
