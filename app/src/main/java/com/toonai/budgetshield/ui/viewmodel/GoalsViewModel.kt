package com.toonai.budgetshield.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.model.SavingsGoal
import com.toonai.budgetshield.data.model.ShieldLevels
import com.toonai.budgetshield.data.repository.SavingsGoalRepository
import com.toonai.budgetshield.data.repository.XpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state for Goals screen
 */
data class GoalsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val activeGoals: List<SavingsGoal> = emptyList(),
    val completedGoals: List<SavingsGoal> = emptyList(),
    val totalSavings: Long = 0L,
    val totalTarget: Long = 0L,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalXp: Int = 0,
    val currentLevel: ShieldLevels.Level = ShieldLevels.LEVELS[0],
    val xpToNextLevel: Int = 0,
    val levelProgressPercent: Int = 0
)

/**
 * ViewModel for Goals screen.
 * Manages savings goals, streaks, and shield progression preview.
 */
class GoalsViewModel(
    private val savingsGoalRepository: SavingsGoalRepository,
    private val xpRepository: XpRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    init {
        loadGoalsData()
    }

    /**
     * Load all goals-related data.
     */
    private fun loadGoalsData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Collect active goals
                launch {
                    savingsGoalRepository.activeGoals.collect { goals ->
                        _uiState.value = _uiState.value.copy(
                            activeGoals = goals
                        )
                    }
                }

                // Collect completed goals
                launch {
                    savingsGoalRepository.completedGoals.collect { goals ->
                        _uiState.value = _uiState.value.copy(
                            completedGoals = goals
                        )
                    }
                }

                // Collect total savings
                launch {
                    savingsGoalRepository.totalSavings.collect { savings ->
                        _uiState.value = _uiState.value.copy(
                            totalSavings = savings
                        )
                    }
                }

                // Collect total target
                launch {
                    savingsGoalRepository.totalSavingsTarget.collect { target ->
                        _uiState.value = _uiState.value.copy(
                            totalTarget = target
                        )
                    }
                }

                // Collect user streak
                launch {
                    savingsGoalRepository.userStreak.collect { streak ->
                        _uiState.value = _uiState.value.copy(
                            currentStreak = streak?.currentStreak ?: 0,
                            bestStreak = streak?.bestStreak ?: 0
                        )
                    }
                }

                // Collect XP and level data
                launch {
                    xpRepository.totalXp.collect { xp ->
                        val level = ShieldLevels.getLevelForXp(xp)
                        val nextLevel = ShieldLevels.getNextLevel(xp)
                        val xpNeeded = ShieldLevels.xpToNextLevel(xp)
                        val progress = if (nextLevel != null) {
                            val xpInLevel = xp - level.xpRequired
                            val xpNeededForLevel = nextLevel.xpRequired - level.xpRequired
                            ((xpInLevel * 100) / xpNeededForLevel).coerceIn(0, 100)
                        } else 100

                        _uiState.value = _uiState.value.copy(
                            totalXp = xp,
                            currentLevel = level,
                            xpToNextLevel = xpNeeded,
                            levelProgressPercent = progress,
                            isLoading = false
                        )
                    }
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load goals: ${e.message}"
                )
            }
        }
    }

    /**
     * Create a new savings goal.
     */
    fun createGoal(
        name: String,
        targetAmountCents: Long,
        icon: String = "🎯",
        deadlineDate: String? = null
    ) {
        viewModelScope.launch {
            try {
                if (name.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Please enter a goal name"
                    )
                    return@launch
                }
                if (targetAmountCents <= 0) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Please enter a valid target amount"
                    )
                    return@launch
                }

                savingsGoalRepository.createGoal(
                    name = name.trim(),
                    targetAmountCents = targetAmountCents,
                    icon = icon,
                    deadlineDate = deadlineDate
                )

                // Refresh data
                loadGoalsData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to create goal: ${e.message}"
                )
            }
        }
    }

    /**
     * Delete a goal.
     */
    fun deleteGoal(goalId: Long) {
        viewModelScope.launch {
            try {
                savingsGoalRepository.deleteGoal(goalId)
                loadGoalsData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to delete goal: ${e.message}"
                )
            }
        }
    }

    /**
     * Initialize emergency fund if it doesn't exist.
     */
    fun initializeEmergencyFund() {
        viewModelScope.launch {
            try {
                savingsGoalRepository.initializeEmergencyFund()
            } catch (e: Exception) {
                // Silently fail - emergency fund is optional
            }
        }
    }

    /**
     * Refresh all goals data.
     */
    fun refresh() {
        loadGoalsData()
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
        private val savingsGoalRepository: SavingsGoalRepository,
        private val xpRepository: XpRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GoalsViewModel::class.java)) {
                return GoalsViewModel(savingsGoalRepository, xpRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
