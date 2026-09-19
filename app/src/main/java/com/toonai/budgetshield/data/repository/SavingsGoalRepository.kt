package com.toonai.budgetshield.data.repository

import androidx.room.withTransaction
import com.toonai.budgetshield.data.database.BudgetShieldDatabase
import com.toonai.budgetshield.data.database.SavingsGoalDao
import com.toonai.budgetshield.data.database.UserStreakDao
import com.toonai.budgetshield.data.model.SavingsGoal
import com.toonai.budgetshield.data.model.Transaction
import com.toonai.budgetshield.data.model.TransactionCategories
import com.toonai.budgetshield.data.model.UserStreak
import com.toonai.budgetshield.data.model.XpActivityTypes
import com.toonai.budgetshield.data.model.XpEntry
import com.toonai.budgetshield.util.DateParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/**
 * Repository for savings goals and streak operations.
 */
class SavingsGoalRepository(
    private val database: BudgetShieldDatabase
) {
    private val savingsGoalDao: SavingsGoalDao = database.savingsGoalDao()
    private val userStreakDao: UserStreakDao = database.userStreakDao()

    /** All savings goals as a reactive stream */
    val allGoals: Flow<List<SavingsGoal>> = savingsGoalDao.getAllGoals()

    /** Active (incomplete) goals */
    val activeGoals: Flow<List<SavingsGoal>> = savingsGoalDao.getActiveGoals()

    /** Completed goals */
    val completedGoals: Flow<List<SavingsGoal>> = savingsGoalDao.getCompletedGoals()

    /** Total current savings */
    val totalSavings: Flow<Long> = savingsGoalDao.getTotalSavings().map { it ?: 0L }

    /** Total savings target for active goals */
    val totalSavingsTarget: Flow<Long> = savingsGoalDao.getTotalSavingsTarget().map { it ?: 0L }

    /** User streak as a reactive stream */
    val userStreak: Flow<UserStreak?> = userStreakDao.getUserStreak()

    /**
     * Get a goal by ID.
     */
    suspend fun getGoalById(goalId: Long): SavingsGoal? {
        return savingsGoalDao.getGoalById(goalId)
    }

    /**
     * Create a new savings goal.
     */
    suspend fun createGoal(
        name: String,
        targetAmountCents: Long,
        icon: String = "🎯",
        deadlineDate: String? = null,
        isEmergencyFund: Boolean = false,
        priority: Int = 1
    ): Long {
        val goal = SavingsGoal(
            name = name.trim(),
            targetAmountCents = targetAmountCents,
            icon = icon,
            deadlineDate = deadlineDate,
            isEmergencyFund = isEmergencyFund,
            priority = priority
        )
        return savingsGoalDao.insertGoal(goal)
    }

    /**
     * Add money to a savings goal.
     */
    suspend fun contributeToGoal(goalId: Long, amountCents: Long) {
        savingsGoalDao.addToGoal(goalId, amountCents)

        // Check if goal is now complete
        val goal = getGoalById(goalId)
        if (goal != null && goal.currentAmountCents >= goal.targetAmountCents) {
            savingsGoalDao.markGoalComplete(goalId)
        }
    }

    data class SavingsContributionResult(
        val transactionId: Long,
        val xpEarned: Int,
        val completedGoal: Boolean
    )

    /**
     * Atomically records a savings contribution.
     *
     * The operation transfers cleared cash into savings, optionally updates a
     * savings goal, appends the immutable transaction ledger row, records the
     * savings XP entry, and updates the activity streak as one database commit.
     */
    suspend fun recordSavingsContribution(
        amountCents: Long,
        note: String? = null,
        goalId: Long? = null
    ): SavingsContributionResult? {
        if (amountCents <= 0) return null

        return database.withTransaction {
            val settingsDao = database.userSettingsDao()
            val settings = settingsDao.getSettingsSync() ?: return@withTransaction null
            if (settings.cashOnHandCents < amountCents) return@withTransaction null

            val goal = goalId?.let { savingsGoalDao.getGoalById(it) }
            if (goalId != null && goal == null) return@withTransaction null

            settingsDao.updateSettings(
                settings.copy(
                    cashOnHandCents = settings.cashOnHandCents - amountCents,
                    savingsBalanceCents = settings.savingsBalanceCents + amountCents,
                    updatedAt = System.currentTimeMillis()
                )
            )

            var completedGoal = false
            if (goal != null) {
                val newCurrent = goal.currentAmountCents + amountCents
                savingsGoalDao.updateGoal(
                    goal.copy(
                        currentAmountCents = newCurrent,
                        isCompleted = goal.isCompleted || newCurrent >= goal.targetAmountCents,
                        completedAt = if (!goal.isCompleted && newCurrent >= goal.targetAmountCents) {
                            System.currentTimeMillis()
                        } else {
                            goal.completedAt
                        }
                    )
                )
                completedGoal = !goal.isCompleted && newCurrent >= goal.targetAmountCents
            }

            val today = DateParser.today()
            val title = note?.takeIf { it.isNotBlank() } ?: "Savings Deposit"
            val xpAmount = XpActivityTypes.baseXp(XpActivityTypes.ADD_SAVINGS)
            val transactionId = database.transactionDao().insertTransaction(
                Transaction(
                    type = Transaction.TYPE_SAVINGS,
                    title = title,
                    description = note,
                    amountCents = -amountCents,
                    category = TransactionCategories.SAVINGS,
                    icon = "🏦",
                    earnsXp = true,
                    xpEarned = xpAmount,
                    transactionDate = today
                )
            )

            database.xpEntryDao().insertXpEntry(
                XpEntry(
                    amount = xpAmount,
                    activityType = XpActivityTypes.ADD_SAVINGS,
                    description = "Saved ${SavingsGoal.formatCents(amountCents)}",
                    relatedId = transactionId,
                    entryDate = today
                )
            )

            recordActivity()

            SavingsContributionResult(
                transactionId = transactionId,
                xpEarned = xpAmount,
                completedGoal = completedGoal
            )
        }
    }

    /**
     * Delete a goal.
     */
    suspend fun deleteGoal(goalId: Long) {
        savingsGoalDao.deleteGoalById(goalId)
    }

    /**
     * Initialize emergency fund if it doesn't exist.
     */
    suspend fun initializeEmergencyFund() {
        val existing = savingsGoalDao.getEmergencyFund()
        if (existing == null) {
            createGoal(
                name = "Emergency Fund",
                targetAmountCents = 500000, // $5,000
                icon = "🚨",
                isEmergencyFund = true,
                priority = 0 // Highest priority
            )
        }
    }

    /**
     * Record activity for streak tracking.
     * Call this whenever user performs a trackable action.
     */
    suspend fun recordActivity() {
        val today = java.time.LocalDate.now().toString()
        val currentStreak = userStreakDao.getUserStreakSync()

        if (currentStreak == null) {
            // First activity
            val newStreak = UserStreak(
                currentStreak = 1,
                bestStreak = 1,
                lastActivityDate = today,
                isActiveToday = true,
                totalActiveDays = 1
            )
            userStreakDao.insertOrUpdateStreak(newStreak)
        } else if (currentStreak.isActiveToday) {
            // Already active today, no change
            return
        } else {
            // Check if continuing streak
            val yesterday = LocalDate.now().minusDays(1).toString()
            val newStreak = if (currentStreak.lastActivityDate == yesterday) {
                // Continuing streak
                currentStreak.currentStreak + 1
            } else {
                // Streak broken
                1
            }

            val updatedStreak = UserStreak(
                currentStreak = newStreak,
                bestStreak = maxOf(currentStreak.bestStreak, newStreak),
                lastActivityDate = today,
                isActiveToday = true,
                totalActiveDays = currentStreak.totalActiveDays + 1
            )
            userStreakDao.insertOrUpdateStreak(updatedStreak)
        }
    }

    /**
     * Get current streak days.
     */
    suspend fun getCurrentStreak(): Int {
        return userStreakDao.getUserStreakSync()?.currentStreak ?: 0
    }

    /**
     * Check and update streak status (call daily).
     */
    suspend fun checkAndResetStreakIfNeeded() {
        val streak = userStreakDao.getUserStreakSync()
        if (streak != null) {
            val today = java.time.LocalDate.now().toString()
            val yesterday = LocalDate.now().minusDays(1).toString()

            if (!streak.isActiveToday && streak.lastActivityDate != yesterday && streak.lastActivityDate != today) {
                // Streak broken - reset
                val updated = streak.copy(
                    currentStreak = 0,
                    isActiveToday = false
                )
                userStreakDao.insertOrUpdateStreak(updated)
            } else if (streak.isActiveToday && streak.lastActivityDate != today) {
                // New day, reset isActiveToday
                val updated = streak.copy(isActiveToday = false)
                userStreakDao.insertOrUpdateStreak(updated)
            }
        }
    }

    /**
     * Get completed goal count.
     */
    suspend fun getCompletedGoalCount(): Int {
        return savingsGoalDao.getCompletedGoalCount()
    }
}

// Helper extension for Flow mapping - uses standard Kotlin Flow operations
// Removed custom map extension to avoid conflicts with kotlinx.coroutines.flow.map
