package com.toonai.budgetshield.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.toonai.budgetshield.data.model.UserSettings
import kotlinx.coroutines.flow.Flow

/**
 * DAO for UserSettings entity.
 * Manages first-run completion, cash on hand, savings, and user preferences.
 */
@Dao
interface UserSettingsDao {

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<UserSettings?>

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): UserSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: UserSettings)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: UserSettings)

    @Update
    suspend fun updateSettings(settings: UserSettings)

    @Update
    suspend fun update(settings: UserSettings)

    @Query("UPDATE user_settings SET isFirstRunComplete = :complete WHERE id = 1")
    suspend fun setFirstRunComplete(complete: Boolean)

    @Query("UPDATE user_settings SET setupChapter = :chapter WHERE id = 1")
    suspend fun setSetupChapter(chapter: Int)

    @Query("UPDATE user_settings SET cashOnHandCents = :cents WHERE id = 1")
    suspend fun updateCashOnHand(cents: Long)

    @Query("UPDATE user_settings SET savingsBalanceCents = :cents WHERE id = 1")
    suspend fun updateSavingsBalance(cents: Long)

    @Query("UPDATE user_settings SET selectedMonth = :month WHERE id = 1")
    suspend fun updateSelectedMonth(month: String)
}
