package com.toonai.budgetshield.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.toonai.budgetshield.data.repository.BillRepository
import com.toonai.budgetshield.data.repository.BudgetRepository
import com.toonai.budgetshield.data.repository.IncomeRepository
import com.toonai.budgetshield.data.repository.SavingsGoalRepository
import com.toonai.budgetshield.data.repository.TransactionRepository
import com.toonai.budgetshield.data.repository.UserSettingsRepository
import com.toonai.budgetshield.data.repository.XpRepository

/**
 * CompositionLocal for accessing repositories in Compose UI.
 * Must be provided at the app root level.
 */
val LocalBillRepository = staticCompositionLocalOf<BillRepository> {
    error("BillRepository not provided. Wrap your app with Repository providers.")
}

val LocalIncomeRepository = staticCompositionLocalOf<IncomeRepository> {
    error("IncomeRepository not provided. Wrap your app with Repository providers.")
}

val LocalTransactionRepository = staticCompositionLocalOf<TransactionRepository> {
    error("TransactionRepository not provided. Wrap your app with Repository providers.")
}

val LocalXpRepository = staticCompositionLocalOf<XpRepository> {
    error("XpRepository not provided. Wrap your app with Repository providers.")
}

val LocalSavingsGoalRepository = staticCompositionLocalOf<SavingsGoalRepository> {
    error("SavingsGoalRepository not provided. Wrap your app with Repository providers.")
}

val LocalBudgetRepository = staticCompositionLocalOf<BudgetRepository> {
    error("BudgetRepository not provided. Wrap your app with Repository providers.")
}

val LocalUserSettingsRepository = staticCompositionLocalOf<UserSettingsRepository> {
    error("UserSettingsRepository not provided. Wrap your app with Repository providers.")
}
