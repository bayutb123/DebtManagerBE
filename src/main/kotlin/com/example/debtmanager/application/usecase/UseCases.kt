package com.example.debtmanager.application.usecase

import com.example.debtmanager.application.dto.*
import com.example.debtmanager.application.service.*
import com.example.debtmanager.domain.model.*
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class AuthenticateUseCase(private val authService: AuthService) {
    fun execute(command: GoogleAuthCommand): AuthService.AuthResult = authService.authenticate(command)
}

@Component
class ContactUseCase(private val service: ContactService) {
    fun list(): List<Contact> = service.list()
    fun get(id: UUID): Contact = service.get(id)
    fun create(command: ContactCommand): Contact = service.create(command)
    fun update(id: UUID, command: ContactCommand): Contact = service.update(id, command)
    fun delete(id: UUID) = service.delete(id)
}

@Component
class DebtUseCase(private val service: DebtService) {
    fun list(): List<Debt> = service.list()
    fun get(id: UUID): Debt = service.get(id)
    fun create(command: DebtCommand): Debt = service.create(command)
    fun update(id: UUID, command: DebtCommand): Debt = service.update(id, command)
    fun delete(id: UUID) = service.delete(id)
    fun pay(id: UUID, command: PaymentCommand): Debt = service.addPayment(id, command)
}

@Component
class ReceivableUseCase(private val service: ReceivableService) {
    fun list(): List<Receivable> = service.list()
    fun get(id: UUID): Receivable = service.get(id)
    fun create(command: ReceivableCommand): Receivable = service.create(command)
    fun update(id: UUID, command: ReceivableCommand): Receivable = service.update(id, command)
    fun delete(id: UUID) = service.delete(id)
    fun pay(id: UUID, command: PaymentCommand): Receivable = service.addPayment(id, command)
}

@Component
class TransactionUseCase(private val service: TransactionService) {
    fun list(): List<Transaction> = service.list()
    fun get(id: UUID): Transaction = service.get(id)
}

@Component
class DashboardUseCase(private val service: DashboardService) {
    fun summary(): DashboardService.Summary = service.summary()
}