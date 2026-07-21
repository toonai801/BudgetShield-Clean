package com.toonai.budgetshield.data.repository

import com.toonai.budgetshield.data.database.UserSettingsDao
import com.toonai.budgetshield.data.model.UserSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository for UserSettings operations.
 * Single source of truth for user settings data.
 */
class UserSettingsRepository(private val userSettingsDao: UserSettingsDao) {

    /** User settings as a reactive stream */
    val userSettings: Flow<UserSettings?> = userSettingsDao.getSettings()

    /** Get user settings synchronously - non-blocking suspend function */
    suspend fun getSettings(): UserSettings? {
        return userSettingsDao.getSettingsSync()
    }

    /** Get user settings as Flow */
    fun getSettingsFlow(): Flow<UserSettings?> {
        return userSettingsDao.getSettings()
    }

    /** Save or update user settings */
    suspend fun saveSettings(settings: UserSettings) {
        userSettingsDao.insertSettings(settings)
    }

    /** Update first-run completion status */
    suspend fun completeFirstRun(chapter: Int = 7) {
        userSettingsDao.setFirstRunComplete(true)
        userSettingsDao.setSetupChapter(chapter)
    }

    /** Update cash on hand */
    suspend fun updateCashOnHand(cents: Long) {
        userSettingsDao.updateCashOnHand(cents)
    }

    /** Update savings balance */
    suspend fun updateSavingsBalance(cents: Long) {
        userSettingsDao.updateSavingsBalance(cents)
    }

    /** Update selected month */
    suspend fun updateSelectedMonth(monthKey: String) {
        userSettingsDao.updateSelectedMonth(monthKey)
    }

    /** Update setup chapter progress */
    suspend fun updateSetupChapter(chapter: Int) {
        userSettingsDao.setSetupChapter(chapter)
    }

    /** Check if setup is complete */
    suspend fun isSetupComplete(): Boolean {
        val settings = userSettingsDao.getSettingsSync()
        return settings?.isFirstRunComplete ?: false
    }

    /** Get current setup chapter */
    suspend fun getSetupChapter(): Int {
        return userSettingsDao.getSettingsSync()?.setupChapter ?: 0
    }

    /** Get cash on hand in cents */
    suspend fun getCashOnHandCents(): Long {
        return userSettingsDao.getSettingsSync()?.cashOnHandCents ?: 0L
    }

    /** Get savings balance in cents */
    suspend fun getSavingsBalanceCents(): Long {
        return userSettingsDao.getSettingsSync()?.savingsBalanceCents ?: 0L
    }

    /**
     * Initialize default settings if none exist.
     * Call this on app startup.
     */
    suspend fun initializeDefaultSettings() {
        if (userSettingsDao.getSettingsSync() == null) {
            val defaultSettings = UserSettings(
                id = 1L,
                isFirstRunComplete = false,
                cashOnHandCents = 0L,
                savingsBalanceCents = 0L,
                setupChapter = 0
            )
            userSettingsDao.insertSettings(defaultSettings)
        }
    }
}
