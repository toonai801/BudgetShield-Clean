package com.toonai.budgetshield.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// Architecture Foundation - Navigation Routes (Task 3)
// All 14 product destinations with type-safe Navigation 3
// Routes implement NavKey interface for Navigation 3 compatibility

@Serializable
object SetupQuest : NavKey

@Serializable
object Home : NavKey

@Serializable
object Treasure : NavKey

@Serializable
object Bills : NavKey

@Serializable
object Stats : NavKey

@Serializable
object Goals : NavKey

@Serializable
object Settings : NavKey

@Serializable
object IncomeEntry : NavKey

@Serializable
object BillEntry : NavKey

@Serializable
object BillPayment : NavKey

@Serializable
data class BillPaymentWithId(val billId: Long) : NavKey

@Serializable
object SavingsEntry : NavKey

@Serializable
data class TransactionDetails(val transactionId: Long? = null) : NavKey

@Serializable
object BillProtected : NavKey

@Serializable
object ShieldProgression : NavKey

@Serializable
object BudgetMenu : NavKey

@Serializable
object LogSpending : NavKey

@Serializable
object Budgets : NavKey
