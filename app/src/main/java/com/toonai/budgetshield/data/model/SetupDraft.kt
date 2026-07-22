package com.toonai.budgetshield.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Setup draft for process-death resume during Setup Quest.
 * Stores incomplete setup progress so user can resume after app restart.
 */
@Entity(tableName = "setup_drafts")
data class SetupDraft(
    @PrimaryKey
    val id: Long = 1L,

    /** Current chapter (1-6) */
    val currentChapter: Int = 1,

    // Chapter 1: Cash on Hand
    val cashOnHandCents: Long = 0,

    // Chapter 2: Income
    val incomeName: String = "",
    val incomeAmountCents: Long = 0,
    val nextPaydayDate: String = "",
    val frequency: String = "",
    val isIncomeConfirmed: Boolean = false,

    // Chapter 4: Savings (stored separately from cash)
    val savingsBalanceCents: Long = 0,

    // Chapter 5: Budgets
    val foodBudgetCents: Long = 0,
    val wantsBudgetCents: Long = 0,

    /** Last update timestamp */
    val updatedAt: Long = System.currentTimeMillis()
)
