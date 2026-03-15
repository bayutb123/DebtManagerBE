package com.example.debtmanager.application.service

import com.example.debtmanager.common.exception.NotFoundException
import com.example.debtmanager.common.utils.SecurityUtils
import com.example.debtmanager.domain.model.Transaction
import com.example.debtmanager.domain.repository.TransactionRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TransactionService(private val transactionRepository: TransactionRepository) {
    fun list(): List<Transaction> = transactionRepository.findAllByUserId(SecurityUtils.currentUserId())

    fun get(id: UUID): Transaction = transactionRepository.findByIdAndUserId(id, SecurityUtils.currentUserId())
        ?: throw NotFoundException("Transaction not found")

    fun recent(limit: Int = 5): List<Transaction> = transactionRepository.findRecentByUserId(SecurityUtils.currentUserId(), limit)
}