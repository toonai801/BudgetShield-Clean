package com.toonai.budgetshield.navigation

import kotlinx.serialization.Serializable

// Architecture Foundation - Navigation Routes (Task 3)
// All 13 product destinations with type-safe Navigation 3

@Serializable
object SetupQuest

@Serializable
object Home

@Serializable
object Treasure

@Serializable
object Stats

@Serializable
object Goals

@Serializable
object Settings

@Serializable
object IncomeEntry

@Serializable
object BillEntry

@Serializable
object BillPayment

@Serializable
object SavingsEntry

@Serializable
data class TransactionDetails(val transactionId: Long? = null)

@Serializable
object BillProtected

@Serializable
object ShieldProgression