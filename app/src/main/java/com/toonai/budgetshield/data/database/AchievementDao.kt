package com.toonai.budgetshield.data.database

import androidx.room.*
import com.toonai.budgetshield.data.model.Achievement
import kotlinx.coroutines.flow.Flow

/**
 * DAO for achievement operations.
 */
@Dao
interface AchievementDao {

    @Query("SELECT * FROM achievements ORDER BY category ASC, id ASC")
    fun getAllAchievements(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE isUnlocked = 1 ORDER BY unlockedAt DESC")
    fun getUnlockedAchievements(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE isUnlocked = 0 ORDER BY progress DESC")
    fun getLockedAchievements(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE id = :achievementId LIMIT 1")
    suspend fun getAchievementById(achievementId: String): Achievement?

    @Query("SELECT * FROM achievements WHERE category = :category ORDER BY isUnlocked DESC, progress DESC")
    fun getAchievementsByCategory(category: String): Flow<List<Achievement>>

    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    fun getUnlockedCount(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM achievements")
    suspend fun getTotalAchievementCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(achievements: List<Achievement>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBlocking(achievement: Achievement)

    @Update
    suspend fun updateAchievement(achievement: Achievement)

    @Query("UPDATE achievements SET progress = :progress WHERE id = :achievementId")
    suspend fun updateProgress(achievementId: String, progress: Int)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :timestamp, progress = 100 WHERE id = :achievementId")
    suspend fun unlockAchievement(achievementId: String, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteAchievement(achievement: Achievement)

    @Query("DELETE FROM achievements WHERE id = :achievementId")
    suspend fun deleteAchievementById(achievementId: String)
}
