package com.example.debtmanager.infrastructure.repository

import com.example.debtmanager.domain.model.*
import com.example.debtmanager.domain.repository.*
import com.example.debtmanager.infrastructure.entity.*
import com.example.debtmanager.infrastructure.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

interface JpaUserRepository : JpaRepository<UserEntity, UUID> {
    fun findByEmail(email: String): UserEntity?
}

interface JpaContactRepository : JpaRepository<ContactEntity, UUID> {
    fun findAllByUserId(userId: UUID): List<ContactEntity>
    fun findByIdAndUserId(id: UUID, userId: UUID): ContactEntity?
}

interface JpaDebtRepository : JpaRepository<DebtEntity, UUID> {
    fun findAllByUserId(userId: UUID): List<DebtEntity>
    fun findByIdAndUserId(id: UUID, userId: UUID): DebtEntity?
}

interface JpaReceivableRepository : JpaRepository<ReceivableEntity, UUID> {
    fun findAllByUserId(userId: UUID): List<ReceivableEntity>
    fun findByIdAndUserId(id: UUID, userId: UUID): ReceivableEntity?
}

interface JpaTransactionRepository : JpaRepository<TransactionEntity, UUID> {
    @Query(
        """
        select t from TransactionEntity t
        where t.referenceId in (
            select d.id from DebtEntity d where d.userId = :userId
        ) or t.referenceId in (
            select r.id from ReceivableEntity r where r.userId = :userId
        )
        order by t.date desc
        """
    )
    fun findAllByUserId(@Param("userId") userId: UUID): List<TransactionEntity>

    @Query(
        """
        select t from TransactionEntity t
        where t.id = :id and (
            t.referenceId in (select d.id from DebtEntity d where d.userId = :userId) or
            t.referenceId in (select r.id from ReceivableEntity r where r.userId = :userId)
        )
        """
    )
    fun findByIdAndUserId(@Param("id") id: UUID, @Param("userId") userId: UUID): TransactionEntity?

    @Query(
        """
        select t from TransactionEntity t
        where t.referenceId in (
            select d.id from DebtEntity d where d.userId = :userId
        ) or t.referenceId in (
            select r.id from ReceivableEntity r where r.userId = :userId
        )
        order by t.date desc
        """
    )
    fun findRecentByUserId(@Param("userId") userId: UUID): List<TransactionEntity>
}

@Repository
class UserRepositoryImpl(private val jpa: JpaUserRepository) : UserRepository {
    override fun findByEmail(email: String): User? = jpa.findByEmail(email)?.toDomain()
    override fun findById(id: UUID): User? = jpa.findById(id).orElse(null)?.toDomain()
    override fun save(user: User): User = jpa.save(user.toEntity()).toDomain()
}

@Repository
class ContactRepositoryImpl(private val jpa: JpaContactRepository) : ContactRepository {
    override fun findAllByUserId(userId: UUID): List<Contact> = jpa.findAllByUserId(userId).map { it.toDomain() }
    override fun findByIdAndUserId(id: UUID, userId: UUID): Contact? = jpa.findByIdAndUserId(id, userId)?.toDomain()
    override fun save(contact: Contact): Contact = jpa.save(contact.toEntity()).toDomain()
    override fun delete(id: UUID) { jpa.deleteById(id) }
}

@Repository
class DebtRepositoryImpl(private val jpa: JpaDebtRepository, private val transactionRepository: JpaTransactionRepository) : DebtRepository {
    override fun findAllByUserId(userId: UUID): List<Debt> = jpa.findAllByUserId(userId).map { it.toDomain() }
    override fun findByIdAndUserId(id: UUID, userId: UUID): Debt? = jpa.findByIdAndUserId(id, userId)?.toDomain()
    override fun save(debt: Debt): Debt = jpa.save(debt.toEntity()).toDomain()
    override fun delete(id: UUID) { jpa.deleteById(id) }
    override fun updatePayment(id: UUID, userId: UUID, amount: java.math.BigDecimal, date: java.time.LocalDate, note: String?): Debt {
        val entity = jpa.findByIdAndUserId(id, userId) ?: throw IllegalArgumentException("Debt not found")
        entity.paidAmount = entity.paidAmount.add(amount)
        entity.status = calculateDebtStatus(entity.amount, entity.paidAmount, entity.dueDate)
        val transaction = TransactionEntity(referenceId = entity.id, type = TransactionType.DEBT, amount = amount, date = date, note = note ?: "")
        transaction.id = UUID.randomUUID()
        transactionRepository.save(transaction)
        return jpa.save(entity).toDomain()
    }
}

@Repository
class ReceivableRepositoryImpl(private val jpa: JpaReceivableRepository, private val transactionRepository: JpaTransactionRepository) : ReceivableRepository {
    override fun findAllByUserId(userId: UUID): List<Receivable> = jpa.findAllByUserId(userId).map { it.toDomain() }
    override fun findByIdAndUserId(id: UUID, userId: UUID): Receivable? = jpa.findByIdAndUserId(id, userId)?.toDomain()
    override fun save(receivable: Receivable): Receivable = jpa.save(receivable.toEntity()).toDomain()
    override fun delete(id: UUID) { jpa.deleteById(id) }
    override fun updatePayment(id: UUID, userId: UUID, amount: java.math.BigDecimal, date: java.time.LocalDate, note: String?): Receivable {
        val entity = jpa.findByIdAndUserId(id, userId) ?: throw IllegalArgumentException("Receivable not found")
        entity.paidAmount = entity.paidAmount.add(amount)
        entity.status = calculateReceivableStatus(entity.amount, entity.paidAmount, entity.dueDate)
        val transaction = TransactionEntity(referenceId = entity.id, type = TransactionType.RECEIVABLE, amount = amount, date = date, note = note ?: "")
        transaction.id = UUID.randomUUID()
        transactionRepository.save(transaction)
        return jpa.save(entity).toDomain()
    }
}

@Repository
class TransactionRepositoryImpl(private val jpa: JpaTransactionRepository) : TransactionRepository {
    override fun findAllByUserId(userId: UUID): List<Transaction> = jpa.findAllByUserId(userId).map { it.toDomain() }
    override fun findByIdAndUserId(id: UUID, userId: UUID): Transaction? = jpa.findByIdAndUserId(id, userId)?.toDomain()
    override fun save(transaction: Transaction): Transaction = jpa.save(transaction.toEntity()).toDomain()
    override fun findRecentByUserId(userId: UUID, limit: Int): List<Transaction> =
        jpa.findRecentByUserId(userId).take(limit).map { it.toDomain() }
}

private fun calculateDebtStatus(amount: java.math.BigDecimal, paid: java.math.BigDecimal, dueDate: java.time.LocalDate): DebtStatus {
    return when {
        paid >= amount -> DebtStatus.PAID
        paid > java.math.BigDecimal.ZERO && paid < amount -> DebtStatus.PARTIAL
        dueDate.isBefore(java.time.LocalDate.now()) -> DebtStatus.OVERDUE
        else -> DebtStatus.ACTIVE
    }
}

private fun calculateReceivableStatus(amount: java.math.BigDecimal, paid: java.math.BigDecimal, dueDate: java.time.LocalDate): ReceivableStatus {
    return when {
        paid >= amount -> ReceivableStatus.PAID
        paid > java.math.BigDecimal.ZERO && paid < amount -> ReceivableStatus.PARTIAL
        dueDate.isBefore(java.time.LocalDate.now()) -> ReceivableStatus.OVERDUE
        else -> ReceivableStatus.ACTIVE
    }
}
