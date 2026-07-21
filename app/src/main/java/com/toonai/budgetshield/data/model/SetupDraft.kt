package com.toonai.budgetshield.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Setup Quest draft progress for process death/resume.
 * Single-row table with ID = 1.
 */
@Entity(tableName = "setup_draft")
data class SetupDraft(
    @PrimaryKey
    val id: Long = 1,
    val currentChapter: Int = 1, // 1-6
    val cashOnHandCents: Long? = null,
    val incomeName: String? = null,
    val incomeAmountCents: Long? = null,
    val incomeFrequency: String? = null, // weekly, biweekly, twice_monthly, monthly, one_time
    val nextPaydayDate: String? = null, // YYYY-MM-DD
    val savingsCents: Long? = null,
    val foodBudgetCents: Long? = null,
    val wantsBudgetCents: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
