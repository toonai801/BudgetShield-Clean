package com.toonai.budgetshield

import com.toonai.budgetshield.data.repository.BillRepository
import com.toonai.budgetshield.data.repository.BudgetRepository
import com.toonai.budgetshield.data.repository.IncomeRepository
import com.toonai.budgetshield.data.repository.SavingsGoalRepository
import com.toonai.budgetshield.data.repository.TransactionRepository
import com.toonai.budgetshield.data.repository.UserSettingsRepository
import com.toonai.budgetshield.data.repository.XpRepository

/**
 * Data class to hold all repositories for the app.
 * This provides a clean way to pass repositories through the composition.
 */
data class AppRepositories(
    val billRepository: BillRepository,
    val incomeRepository: IncomeRepository,
    val transactionRepository: TransactionRepository,
    val xpRepository: XpRepository,
    val savingsGoalRepository: SavingsGoalRepository,
    val budgetRepository: BudgetRepository,
    val userSettingsRepository: UserSettingsRepository
)
