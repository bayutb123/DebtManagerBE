package com.example.debtmanager.application.service

import com.example.debtmanager.common.utils.SecurityUtils
import com.example.debtmanager.domain.repository.DebtRepository
import com.example.debtmanager.domain.repository.ReceivableRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class DashboardService(
    private val debtRepository: DebtRepository,
    private val receivableRepository: ReceivableRepository,
    private val transactionService: TransactionService
) {
    data class Summary(
        val totalDebt: BigDecimal,
        val totalReceivable: BigDecimal,
        val netBalance: BigDecimal,
        val recentTransactions: List<com.example.debtmanager.domain.model.Transaction>
    )

    fun summary(): Summary {
        val userId = SecurityUtils.currentUserId()
        val debts = debtRepository.findAllByUserId(userId)
        val receivables = receivableRepository.findAllByUserId(userId)
        val totalDebt = debts.fold(BigDecimal.ZERO) { acc, d -> acc + d.amount - d.paidAmount }
        val totalReceivable = receivables.fold(BigDecimal.ZERO) { acc, r -> acc + r.amount - r.paidAmount }
        val netBalance = totalReceivable.subtract(totalDebt)
        val recent = transactionService.recent()
        return Summary(totalDebt, totalReceivable, netBalance, recent)
    }
}