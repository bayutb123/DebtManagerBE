package com.example.debtmanager.domain.repository

import com.example.debtmanager.domain.model.*
import java.time.LocalDate
import java.util.UUID

interface UserRepository {
    fun findByEmail(email: String): User?
    fun findById(id: UUID): User?
    fun save(user: User): User
}

interface ContactRepository {
    fun findAllByUserId(userId: UUID): List<Contact>
    fun findByIdAndUserId(id: UUID, userId: UUID): Contact?
    fun save(contact: Contact): Contact
    fun delete(id: UUID)
}

interface DebtRepository {
    fun findAllByUserId(userId: UUID): List<Debt>
    fun findByIdAndUserId(id: UUID, userId: UUID): Debt?
    fun save(debt: Debt): Debt
    fun delete(id: UUID)
    fun updatePayment(id: UUID, userId: UUID, amount: java.math.BigDecimal, date: LocalDate, note: String?): Debt
}

interface ReceivableRepository {
    fun findAllByUserId(userId: UUID): List<Receivable>
    fun findByIdAndUserId(id: UUID, userId: UUID): Receivable?
    fun save(receivable: Receivable): Receivable
    fun delete(id: UUID)
    fun updatePayment(id: UUID, userId: UUID, amount: java.math.BigDecimal, date: LocalDate, note: String?): Receivable
}

interface TransactionRepository {
    fun findAllByUserId(userId: UUID): List<Transaction>
    fun findByIdAndUserId(id: UUID, userId: UUID): Transaction?
    fun save(transaction: Transaction): Transaction
    fun findRecentByUserId(userId: UUID, limit: Int = 5): List<Transaction>
}