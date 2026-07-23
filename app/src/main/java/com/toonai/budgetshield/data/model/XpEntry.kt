package com.toonai.budgetshield.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * XP entry tracking user progression and achievements.
 * Each XP earning activity creates a new entry.
 */
@Entity(tableName = "xp_entries")
@Serializable
data class XpEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /** XP amount earned (can be negative for penalties) */
    val amount: Int,

    /** Activity type that earned XP */
    val activityType: String,

    /** Human-readable description */
    val description: String,

    /** Related entity ID (bill, transaction, etc.) */
    val relatedId: Long? = null,

    /** Date as ISO-8601 string (YYYY-MM-DD) */
    val entryDate: String,

    /** Timestamp when created */
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Activity types that can earn XP
 */
object XpActivityTypes {
    const val PROTECT_BILL = "protect_bill"
    const val PAY_BILL = "pay_bill"
    const val ADD_INCOME = "add_income"
    const val ADD_SAVINGS = "add_savings"
    const val DAILY_STREAK = "daily_streak"
    const val WEEKLY_REVIEW = "weekly_review"
    const val REACH_GOAL = "reach_goal"
    const val COMPLETE_SETUP = "complete_setup"
    const val BUDGET_ON_TRACK = "budget_on_track"

    /** Base XP values for each activity */
    fun baseXp(activityType: String): Int = when (activityType) {
        PROTECT_BILL -> 50
        PAY_BILL -> 25
        ADD_INCOME -> 30
        ADD_SAVINGS -> 25
        DAILY_STREAK -> 50
        WEEKLY_REVIEW -> 20
        REACH_GOAL -> 100
        COMPLETE_SETUP -> 200
        BUDGET_ON_TRACK -> 15
        else -> 10
    }
}

/**
 * Shield level configuration
 */
object ShieldLevels {
    data class Level(
        val level: Int,
        val name: String,
        val xpRequired: Int,
        val xpBoostPercent: Int,
        val rewards: List<String>
    )

    val LEVELS = listOf(
        Level(1, "Novice Shield", 0, 0, listOf("Basic protection", "Daily streak tracking")),
        Level(2, "Apprentice Shield", 500, 10, listOf("XP boost +10%", "Custom themes")),
        Level(3, "Guardian Shield", 1500, 25, listOf("XP boost +25%", "Priority support")),
        Level(4, "Champion Shield", 3000, 50, listOf("XP boost +50%", "Exclusive badge")),
        Level(5, "Legend Shield", 5000, 75, listOf("XP boost +75%", "Legend badge", "Early access"))
    )

    fun getLevelForXp(totalXp: Int): Level {
        return LEVELS.lastOrNull { totalXp >= it.xpRequired } ?: LEVELS.first()
    }

    fun getNextLevel(currentXp: Int): Level? {
        return LEVELS.firstOrNull { it.xpRequired > currentXp }
    }

    fun xpToNextLevel(currentXp: Int): Int {
        val next = getNextLevel(currentXp)
        return next?.xpRequired?.minus(currentXp) ?: 0
    }
}

/**
 * Achievement definition
 */
@Entity(tableName = "achievements")
@Serializable
data class Achievement(
    @PrimaryKey
    val id: String,

    /** Display name */
    val name: String,

    /** Description of how to earn */
    val description: String,

    /** Icon emoji */
    val icon: String,

    /** XP reward for completing */
    val xpReward: Int,

    /** Category for grouping */
    val category: String,

    /** Whether this achievement has been unlocked */
    val isUnlocked: Boolean = false,

    /** When it was unlocked */
    val unlockedAt: Long? = null,

    /** Progress towards completion (0-100) */
    val progress: Int = 0,

    /** Target value for completion */
    val targetValue: Int = 1
)

/**
 * Predefined achievements
 */
object AchievementsList {
    val ALL = listOf(
        Achievement("first_bill", "Bill Protector", "Protect your first bill", "🛡️", 50, "protection", targetValue = 1),
        Achievement("protect_5", "Shield Master", "Protect 5 bills at once", "🛡️", 100, "protection", targetValue = 5),
        Achievement("protect_10", "Fortress Builder", "Protect 10 bills at once", "🏰", 250, "protection", targetValue = 10),
        Achievement("pay_first", "First Payment", "Pay your first bill", "💸", 25, "payments", targetValue = 1),
        Achievement("pay_10", "Regular Payer", "Pay 10 bills", "📅", 100, "payments", targetValue = 10),
        Achievement("streak_3", "Getting Started", "3-day streak", "🔥", 30, "streak", targetValue = 3),
        Achievement("streak_7", "Week Warrior", "7-day streak", "🔥", 100, "streak", targetValue = 7),
        Achievement("streak_30", "Month Master", "30-day streak", "🔥", 500, "streak", targetValue = 30),
        Achievement("save_100", "First Savings", "Save $100 total", "💰", 50, "savings", targetValue = 10000),
        Achievement("save_1000", "Saver", "Save $1,000 total", "🏦", 200, "savings", targetValue = 100000),
        Achievement("complete_setup", "Budget Shield Initiate", "Complete setup quest", "✨", 200, "milestone", targetValue = 1),
        Achievement("shield_level_2", "Apprentice", "Reach Shield Level 2", "⭐", 100, "progression", targetValue = 1),
        Achievement("shield_level_3", "Guardian", "Reach Shield Level 3", "⭐", 250, "progression", targetValue = 1)
    )
}
