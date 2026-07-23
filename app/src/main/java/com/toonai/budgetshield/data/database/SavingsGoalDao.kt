package com.toonai.budgetshield.data.database

import androidx.room.*
import com.toonai.budgetshield.data.model.SavingsGoal
import com.toonai.budgetshield.data.model.UserStreak
import kotlinx.coroutines.flow.Flow

/**
 * DAO for savings goal operations.
 */
@Dao
interface SavingsGoalDao {

    @Query("SELECT * FROM savings_goals ORDER BY priority ASC, createdAt DESC")
    fun getAllGoals(): Flow<List<SavingsGoal>>

    @Query("SELECT * FROM savings_goals WHERE isCompleted = 0 ORDER BY priority ASC, createdAt DESC")
    fun getActiveGoals(): Flow<List<SavingsGoal>>

    @Query("SELECT * FROM savings_goals WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedGoals(): Flow<List<SavingsGoal>>

    @Query("SELECT * FROM savings_goals WHERE id = :goalId LIMIT 1")
    suspend fun getGoalById(goalId: Long): SavingsGoal?

    @Query("SELECT * FROM savings_goals WHERE isEmergencyFund = 1 LIMIT 1")
    suspend fun getEmergencyFund(): SavingsGoal?

    @Query("SELECT SUM(currentAmountCents) FROM savings_goals")
    fun getTotalSavings(): Flow<Long?>

    @Query("SELECT SUM(targetAmountCents) FROM savings_goals WHERE isCompleted = 0")
    fun getTotalSavingsTarget(): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: SavingsGoal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBlocking(goal: SavingsGoal): Long

    @Update
    suspend fun updateGoal(goal: SavingsGoal)

    @Query("UPDATE savings_goals SET currentAmountCents = currentAmountCents + :amountCents WHERE id = :goalId")
    suspend fun addToGoal(goalId: Long, amountCents: Long)

    @Query("UPDATE savings_goals SET isCompleted = 1, completedAt = :timestamp WHERE id = :goalId")
    suspend fun markGoalComplete(goalId: Long, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteGoal(goal: SavingsGoal)

    @Query("DELETE FROM savings_goals WHERE id = :goalId")
    suspend fun deleteGoalById(goalId: Long)

    @Query("SELECT COUNT(*) FROM savings_goals WHERE isCompleted = 1")
    suspend fun getCompletedGoalCount(): Int
}

/**
 * DAO for user streak operations.
 */
@Dao
interface UserStreakDao {

    @Query("SELECT * FROM user_streaks WHERE id = 1 LIMIT 1")
    fun getUserStreak(): Flow<UserStreak?>

    @Query("SELECT * FROM user_streaks WHERE id = 1 LIMIT 1")
    suspend fun getUserStreakSync(): UserStreak?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStreak(streak: UserStreak)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBlocking(streak: UserStreak)

    @Query("UPDATE user_streaks SET currentStreak = :streak, bestStreak = :best, lastActivityDate = :date, isActiveToday = :active, totalActiveDays = :total, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateStreak(
        streak: Int,
        best: Int,
        date: String,
        active: Boolean,
        total: Int,
        timestamp: Long = System.currentTimeMillis()
    )
}
