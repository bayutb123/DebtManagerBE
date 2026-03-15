package com.example.debtmanager.infrastructure.persistence

import com.example.debtmanager.domain.model.*
import com.example.debtmanager.infrastructure.entity.*

fun UserEntity.toDomain() = User(id = id, email = email, name = name, pictureUrl = pictureUrl, provider = provider, createdAt = createdAt)
fun User.toEntity() = UserEntity(email = email, name = name, pictureUrl = pictureUrl, provider = provider).also { it.id = id; it.createdAt = createdAt; it.updatedAt = createdAt }

fun ContactEntity.toDomain() = Contact(id = id, userId = userId, name = name, phone = phone, notes = notes, createdAt = createdAt)
fun Contact.toEntity() = ContactEntity(userId = userId, name = name, phone = phone, notes = notes).also { it.id = id; it.createdAt = createdAt; it.updatedAt = createdAt }

fun DebtEntity.toDomain() = Debt(id = id, userId = userId, contactId = contactId, title = title, amount = amount, paidAmount = paidAmount, dueDate = dueDate, status = status, notes = notes, createdAt = createdAt)
fun Debt.toEntity() = DebtEntity(userId = userId, contactId = contactId, title = title, amount = amount, paidAmount = paidAmount, dueDate = dueDate, status = status, notes = notes).also { it.id = id; it.createdAt = createdAt; it.updatedAt = createdAt }

fun ReceivableEntity.toDomain() = Receivable(id = id, userId = userId, contactId = contactId, title = title, amount = amount, paidAmount = paidAmount, dueDate = dueDate, status = status, notes = notes, createdAt = createdAt)
fun Receivable.toEntity() = ReceivableEntity(userId = userId, contactId = contactId, title = title, amount = amount, paidAmount = paidAmount, dueDate = dueDate, status = status, notes = notes).also { it.id = id; it.createdAt = createdAt; it.updatedAt = createdAt }

fun TransactionEntity.toDomain() = Transaction(id = id, referenceId = referenceId, type = type, amount = amount, date = date, note = note, createdAt = createdAt)
fun Transaction.toEntity() = TransactionEntity(referenceId = referenceId, type = type, amount = amount, date = date, note = note).also { it.id = id; it.createdAt = createdAt; it.updatedAt = createdAt }