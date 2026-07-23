package com.toonai.budgetshield.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.model.UserSettings
import com.toonai.budgetshield.data.repository.BillRepository
import com.toonai.budgetshield.data.repository.IncomeRepository
import com.toonai.budgetshield.data.repository.SavingsGoalRepository
import com.toonai.budgetshield.data.repository.TransactionRepository
import com.toonai.budgetshield.data.repository.UserSettingsRepository
import com.toonai.budgetshield.data.repository.XpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * UI state for Settings screen
 */
data class SettingsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userSettings: UserSettings? = null,
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val statsSummary: AppStatsSummary = AppStatsSummary(),
    val showRestartDialog: Boolean = false
)

/**
 * App statistics summary for settings
 */
data class AppStatsSummary(
    val totalTransactions: Int = 0,
    val totalBills: Int = 0,
    val totalIncomeSources: Int = 0,
    val totalSavingsGoals: Int = 0,
    val totalXp: Int = 0
)

/**
 * ViewModel for Settings screen.
 * Manages user preferences, app data, and setup restart.
 */
class SettingsViewModel(
    private val userSettingsRepository: UserSettingsRepository,
    private val billRepository: BillRepository? = null,
    private val incomeRepository: IncomeRepository? = null,
    private val savingsGoalRepository: SavingsGoalRepository? = null,
    private val transactionRepository: TransactionRepository? = null,
    private val xpRepository: XpRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
        loadAppStats()
    }

    /**
     * Load user settings.
     */
    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val settings = userSettingsRepository.getSettings()
                _uiState.value = _uiState.value.copy(
                    userSettings = settings,
                    notificationsEnabled = settings?.notificationsEnabled ?: true,
                    darkModeEnabled = true,
                    soundEnabled = true,
                    hapticEnabled = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to load settings: ${e.message}"
                )
            }
        }
    }

    /**
     * Load app statistics from repositories.
     */
    private fun loadAppStats() {
        viewModelScope.launch {
            try {
                val totalTransactions = transactionRepository?.getTransactionCount() ?: 0
                val totalBills = billRepository?.hasBills()?.let { if (it) 1 else 0 } ?: 0 // TODO: get actual count from DAO
                val totalIncomeSources = incomeRepository?.getAllActiveSchedules()?.first()?.size ?: 0
                val totalXp = xpRepository?.totalXp?.first() ?: 0

                _uiState.value = _uiState.value.copy(
                    statsSummary = AppStatsSummary(
                        totalTransactions = totalTransactions,
                        totalBills = 0, // Would need bill count query
                        totalIncomeSources = totalIncomeSources,
                        totalSavingsGoals = 0, // Would need savings goal count
                        totalXp = totalXp
                    )
                )
            } catch (e: Exception) {
                // Silently fail - stats are optional
            }
        }
    }

    /**
     * Update notification preference.
     */
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
            persistNotificationSettings()
        }
    }

    /**
     * Update dark mode preference.
     */
    fun setDarkModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(darkModeEnabled = enabled)
            // Dark mode is app-level, persisted via Theme/PreferencesManager if needed
        }
    }

    /**
     * Update sound preference.
     */
    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(soundEnabled = enabled)
            // Sound is app-level setting
        }
    }

    /**
     * Update haptic feedback preference.
     */
    fun setHapticEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(hapticEnabled = enabled)
            // Haptic is app-level setting
        }
    }

    /**
     * Persist notification settings to UserSettings.
     */
    private suspend fun persistNotificationSettings() {
        try {
            val currentSettings = userSettingsRepository.getSettings()
            if (currentSettings != null) {
                val updated = currentSettings.copy(
                    notificationsEnabled = _uiState.value.notificationsEnabled,
                    updatedAt = System.currentTimeMillis()
                )
                userSettingsRepository.saveSettings(updated)
            }
        } catch (e: Exception) {
            // Silently fail - notification settings are not critical
        }
    }

    /**
     * Reset first-run to restart setup quest.
     */
    fun restartSetup() {
        viewModelScope.launch {
            try {
                userSettingsRepository.saveSettings(
                    UserSettings(
                        id = 1,
                        isFirstRunComplete = false,
                        setupChapter = 0
                    )
                )
                _uiState.value = _uiState.value.copy(showRestartDialog = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to restart setup: ${e.message}"
                )
            }
        }
    }

    /**
     * Dismiss the restart dialog.
     */
    fun dismissRestartDialog() {
        _uiState.value = _uiState.value.copy(showRestartDialog = false)
    }

    /**
     * Clear all app data (dangerous operation).
     */
    fun clearAllData() {
        viewModelScope.launch {
            try {
                // This would clear all repositories
                // Implementation depends on DAO clear methods
                _uiState.value = _uiState.value.copy(
                    showRestartDialog = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to clear data: ${e.message}"
                )
            }
        }
    }

    /**
     * Export data (placeholder for future implementation).
     */
    fun exportData() {
        // TODO: Implement data export
    }

    /**
     * Import data (placeholder for future implementation).
     */
    fun importData() {
        // TODO: Implement data import
    }

    /**
     * Refresh settings.
     */
    fun refresh() {
        loadSettings()
        loadAppStats()
    }

    /**
     * Clear any error message.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Factory for creating ViewModel with dependencies.
     */
    class Factory(
        private val userSettingsRepository: UserSettingsRepository,
        private val billRepository: BillRepository? = null,
        private val incomeRepository: IncomeRepository? = null,
        private val savingsGoalRepository: SavingsGoalRepository? = null,
        private val transactionRepository: TransactionRepository? = null,
        private val xpRepository: XpRepository? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                return SettingsViewModel(
                    userSettingsRepository,
                    billRepository,
                    incomeRepository,
                    savingsGoalRepository,
                    transactionRepository,
                    xpRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
