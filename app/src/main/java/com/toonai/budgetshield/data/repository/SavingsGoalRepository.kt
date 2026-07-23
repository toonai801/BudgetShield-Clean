package com.toonai.budgetshield.data.repository

import com.toonai.budgetshield.data.database.SavingsGoalDao
import com.toonai.budgetshield.data.database.UserStreakDao
import com.toonai.budgetshield.data.model.SavingsGoal
import com.toonai.budgetshield.data.model.UserStreak
import com.toonai.budgetshield.util.DateParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Repository for savings goals and streak operations.
 */
class SavingsGoalRepository(
    private val savingsGoalDao: SavingsGoalDao,
    private val userStreakDao: UserStreakDao
) {

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
        if (goal != null && goal.currentAmountCents + amountCents >= goal.targetAmountCents) {
            savingsGoalDao.markGoalComplete(goalId)
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
