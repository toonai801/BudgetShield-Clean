package com.toonai.budgetshield.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.model.SavingsGoal
import com.toonai.budgetshield.data.repository.SavingsGoalRepository
import com.toonai.budgetshield.data.repository.TransactionRepository
import com.toonai.budgetshield.data.repository.XpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state for Savings Entry screen
 */
data class SavingsEntryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false,
    val goals: List<SavingsGoal> = emptyList(),
    val currentStreak: Int = 0,
    val xpEarned: Int = 0
)

/**
 * ViewModel for Savings Entry screen.
 * Handles saving money, updating goals, streaks, and XP rewards.
 */
class SavingsEntryViewModel(
    private val savingsGoalRepository: SavingsGoalRepository,
    private val transactionRepository: TransactionRepository,
    private val xpRepository: XpRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavingsEntryUiState())
    val uiState: StateFlow<SavingsEntryUiState> = _uiState.asStateFlow()

    init {
        loadGoalsAndStreak()
    }

    /**
     * Load active savings goals and current streak.
     */
    private fun loadGoalsAndStreak() {
        viewModelScope.launch {
            try {
                savingsGoalRepository.activeGoals.collect { goals ->
                    val streak = savingsGoalRepository.getCurrentStreak()
                    _uiState.value = _uiState.value.copy(
                        goals = goals,
                        currentStreak = streak
                    )
                }
            } catch (e: Exception) {
                // Silently handle - goals are optional
            }
        }
    }

    /**
     * Save money and optionally contribute to a goal.
     *
     * @param amountCents Amount to save in cents
     * @param note Optional note/description
     * @param goalId Optional goal ID to contribute to
     */
    fun saveMoney(
        amountCents: Long,
        note: String? = null,
        goalId: Long? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, xpEarned = 0)

            // Validate input
            if (amountCents <= 0) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Please enter a valid amount to save"
                )
                return@launch
            }

            try {
                // Create savings transaction
                val title = note?.takeIf { it.isNotBlank() } ?: "Savings Deposit"
                transactionRepository.createSavingsTransaction(
                    title = title,
                    amountCents = amountCents,
                    description = note
                )

                // Contribute to goal if specified
                if (goalId != null) {
                    savingsGoalRepository.contributeToGoal(goalId, amountCents)
                }

                // Record activity for streak
                savingsGoalRepository.recordActivity()

                // Award XP for saving
                val xpEntry = xpRepository.awardSavingsXp(amountCents)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    saveSuccess = true,
                    xpEarned = xpEntry.amount
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to save: ${e.message}"
                )
            }
        }
    }

    /**
     * Refresh goals and streak data.
     */
    fun refresh() {
        loadGoalsAndStreak()
    }

    /**
     * Clear any error message.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Reset the success state (call after navigation).
     */
    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false, xpEarned = 0)
    }

    /**
     * Factory for creating ViewModel with dependencies.
     */
    class Factory(
        private val savingsGoalRepository: SavingsGoalRepository,
        private val transactionRepository: TransactionRepository,
        private val xpRepository: XpRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SavingsEntryViewModel::class.java)) {
                return SavingsEntryViewModel(
                    savingsGoalRepository,
                    transactionRepository,
                    xpRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
