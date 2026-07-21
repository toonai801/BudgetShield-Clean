package com.toonai.budgetshield.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Income schedule entity representing recurring income.
 * Used for Safe Now calculations.
 */
@Entity(
    tableName = "income_schedules",
    indices = [
        Index(value = ["isActive"])
    ]
)
data class IncomeSchedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /** Display name (e.g., "Bi-weekly Paycheck") */
    val name: String = "",

    /** Amount in cents */
    val amountCents: Long = 0,

    /** Next payday as ISO-8601 string (YYYY-MM-DD) */
    val nextPayday: String = "",

    /** Alias for nextPayday (for backward compatibility) */
    val nextPaydayDate: String = nextPayday,

    /** Frequency of income (weekly, biweekly, twice_monthly, monthly, one_time) */
    val frequency: String,

    /** Whether this income is confirmed and should be used in calculations */
    val isConfirmed: Boolean = true,

    /** Whether this income schedule is active */
    val isActive: Boolean = true,

    /** When this record was created */
    val createdAt: Long = System.currentTimeMillis(),

    /** When this record was last updated */
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Frequency options for income schedules.
 */
object IncomeFrequency {
    const val WEEKLY = "weekly"
    const val BIWEEKLY = "biweekly"
    const val SEMIMONTHLY = "semimonthly"
    const val MONTHLY = "monthly"
    const val ONE_TIME = "one_time"
    const val TWICE_MONTHLY = "twice_monthly"
}
