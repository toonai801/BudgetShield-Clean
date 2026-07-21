package com.toonai.budgetshield.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Income recurrence rules.
 * Frequency: weekly, biweekly, twice_monthly, monthly, one_time
 */
@Entity(tableName = "income_schedules")
data class IncomeSchedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amountCents: Long,
    val frequency: String, // weekly, biweekly, twice_monthly, monthly, one_time
    val nextPaydayDate: String, // YYYY-MM-DD
    val isConfirmed: Boolean = true,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
