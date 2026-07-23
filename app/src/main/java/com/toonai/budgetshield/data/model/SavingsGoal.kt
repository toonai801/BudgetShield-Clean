package com.toonai.budgetshield.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Savings goal entity for tracking financial targets.
 */
@Entity(tableName = "savings_goals")
@Serializable
data class SavingsGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /** Goal name */
    val name: String,

    /** Icon emoji */
    val icon: String = "🎯",

    /** Target amount in cents */
    val targetAmountCents: Long,

    /** Current saved amount in cents */
    val currentAmountCents: Long = 0L,

    /** Optional deadline date (YYYY-MM-DD) */
    val deadlineDate: String? = null,

    /** Whether goal is completed */
    val isCompleted: Boolean = false,

    /** When goal was completed */
    val completedAt: Long? = null,

    /** Priority level (1 = highest) */
    val priority: Int = 1,

    /** Whether this is an emergency fund (special handling) */
    val isEmergencyFund: Boolean = false,

    /** Timestamp when created */
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Progress percentage (0-100)
     */
    val progressPercent: Int
        get() = if (targetAmountCents > 0) {
            ((currentAmountCents * 100) / targetAmountCents).toInt().coerceIn(0, 100)
        } else 0

    /**
     * Remaining amount needed in cents
     */
    val remainingCents: Long
        get() = maxOf(0L, targetAmountCents - currentAmountCents)

    /**
     * Formatted current amount
     */
    val formattedCurrent: String
        get() = formatCents(currentAmountCents)

    /**
     * Formatted target amount
     */
    val formattedTarget: String
        get() = formatCents(targetAmountCents)

    /**
     * Whether goal is on track (over 50% complete)
     */
    val isOnTrack: Boolean
        get() = progressPercent >= 50

    companion object {
        fun formatCents(cents: Long): String {
            val dollars = cents / 100
            val remainder = cents % 100
            return String.format("$%d.%02d", dollars, kotlin.math.abs(remainder))
        }
    }
}

/**
 * User streak tracking for gamification
 */
@Entity(tableName = "user_streaks")
@Serializable
data class UserStreak(
    @PrimaryKey
    val id: Long = 1L,

    /** Current consecutive days streak */
    val currentStreak: Int = 0,

    /** Best streak ever achieved */
    val bestStreak: Int = 0,

    /** Last date user had activity (YYYY-MM-DD) */
    val lastActivityDate: String? = null,

    /** Whether streak is active today */
    val isActiveToday: Boolean = false,

    /** Count of days with any activity */
    val totalActiveDays: Int = 0,

    /** Timestamp when updated */
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Check if streak is at risk (no activity today and last activity was yesterday)
     */
    val isAtRisk: Boolean
        get() = !isActiveToday && currentStreak > 0
}
