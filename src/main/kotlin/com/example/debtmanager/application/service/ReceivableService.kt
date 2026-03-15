package com.example.debtmanager.application.service

import com.example.debtmanager.application.dto.PaymentCommand
import com.example.debtmanager.application.dto.ReceivableCommand
import com.example.debtmanager.common.exception.NotFoundException
import com.example.debtmanager.common.exception.ValidationException
import com.example.debtmanager.common.utils.SecurityUtils
import com.example.debtmanager.domain.model.Receivable
import com.example.debtmanager.domain.model.ReceivableStatus
import com.example.debtmanager.domain.repository.ReceivableRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Service
class ReceivableService(private val receivableRepository: ReceivableRepository) {
    fun list(): List<Receivable> = receivableRepository.findAllByUserId(SecurityUtils.currentUserId())

    fun get(id: UUID): Receivable = receivableRepository.findByIdAndUserId(id, SecurityUtils.currentUserId())
        ?: throw NotFoundException("Receivable not found")

    fun create(command: ReceivableCommand): Receivable {
        if (command.amount <= BigDecimal.ZERO) throw ValidationException("Amount must be greater than zero")
        val receivable = Receivable(
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
        return receivableRepository.save(receivable)
    }

    fun update(id: UUID, command: ReceivableCommand): Receivable {
        val existing = get(id)
        val updated = existing.copy(
            contactId = command.contactId,
            title = command.title,
            amount = command.amount,
            dueDate = command.dueDate,
            notes = command.notes,
            status = calculateStatus(command.amount, existing.paidAmount, command.dueDate)
        )
        return receivableRepository.save(updated)
    }

    fun delete(id: UUID) {
        get(id)
        receivableRepository.delete(id)
    }

    fun addPayment(id: UUID, payment: PaymentCommand): Receivable {
        if (payment.amount <= BigDecimal.ZERO) throw ValidationException("Payment amount must be greater than zero")
        val userId = SecurityUtils.currentUserId()
        receivableRepository.findByIdAndUserId(id, userId) ?: throw NotFoundException("Receivable not found")
        return receivableRepository.updatePayment(id, userId, payment.amount, payment.date, payment.note)
    }

    private fun calculateStatus(amount: BigDecimal, paid: BigDecimal, dueDate: LocalDate): ReceivableStatus = when {
        paid >= amount -> ReceivableStatus.PAID
        paid > BigDecimal.ZERO && paid < amount -> ReceivableStatus.PARTIAL
        dueDate.isBefore(LocalDate.now()) -> ReceivableStatus.OVERDUE
        else -> ReceivableStatus.ACTIVE
    }
}