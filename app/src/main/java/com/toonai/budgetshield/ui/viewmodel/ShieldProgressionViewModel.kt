package com.toonai.budgetshield.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.model.Achievement
import com.toonai.budgetshield.data.model.ShieldLevels
import com.toonai.budgetshield.data.model.XpEntry
import com.toonai.budgetshield.data.repository.XpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state for Shield Progression screen
 */
data class ShieldProgressionUiState(
    val isLoading: Boolean = false,
    val totalXp: Int = 0,
    val currentLevel: ShieldLevels.Level = ShieldLevels.LEVELS[0],
    val nextLevel: ShieldLevels.Level? = null,
    val xpToNextLevel: Int = 0,
    val levelProgressPercent: Int = 0,
    val recentXpEntries: List<XpEntry> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val unlockedAchievements: List<Achievement> = emptyList(),
    val xpHistory: List<XpEntry> = emptyList(),
    val errorMessage: String? = null
)

/**
 * ViewModel for Shield Progression screen.
 * Manages XP, level progression, and achievements display.
 */
class ShieldProgressionViewModel(
    private val xpRepository: XpRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShieldProgressionUiState())
    val uiState: StateFlow<ShieldProgressionUiState> = _uiState.asStateFlow()

    init {
        loadProgressionData()
    }

    /**
     * Load all progression data (XP, level, achievements).
     */
    private fun loadProgressionData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Collect XP data
                launch {
                    xpRepository.totalXp.collect { xp ->
                        val level = ShieldLevels.getLevelForXp(xp)
                        val nextLvl = ShieldLevels.getNextLevel(xp)
                        val xpNeeded = ShieldLevels.xpToNextLevel(xp)
                        val progress = if (nextLvl != null) {
                            val xpInLevel = xp - level.xpRequired
                            val xpNeededForLevel = nextLvl.xpRequired - level.xpRequired
                            ((xpInLevel * 100) / xpNeededForLevel).coerceIn(0, 100)
                        } else 100

                        _uiState.value = _uiState.value.copy(
                            totalXp = xp,
                            currentLevel = level,
                            nextLevel = nextLvl,
                            xpToNextLevel = xpNeeded,
                            levelProgressPercent = progress
                        )
                    }
                }

                // Collect achievements
                launch {
                    xpRepository.allAchievements.collect { achievements ->
                        val unlocked = achievements.filter { it.isUnlocked }
                        _uiState.value = _uiState.value.copy(
                            achievements = achievements,
                            unlockedAchievements = unlocked
                        )
                    }
                }

                // Load XP history
                launch {
                    xpRepository.xpHistory.collect { entries ->
                        _uiState.value = _uiState.value.copy(
                            xpHistory = entries
                        )
                    }
                }

                // Load recent XP entries
                launch {
                    xpRepository.xpHistory.collect { entries ->
                        _uiState.value = _uiState.value.copy(
                            recentXpEntries = entries.take(10),
                            isLoading = false
                        )
                    }
                }

                // Initialize achievements if needed
                xpRepository.initializeAchievementsIfNeeded()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load progression: ${e.message}"
                )
            }
        }
    }

    /**
     * Get the level reward description.
     */
    fun getLevelRewardDescription(level: ShieldLevels.Level): String {
        return when (level.level) {
            1 -> "Basic Protection"
            2 -> "+5% XP Boost"
            3 -> "+10% XP Boost"
            4 -> "+15% XP Boost"
            5 -> "+20% XP Boost"
            else -> "Maximum Protection"
        }
    }

    /**
     * Load progression data - public version for manual refresh
     */
    fun refresh() {
        loadProgressionData()
    }

    /**
     * Clear any error message.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Factory for creating ViewModel with repository dependency.
     */
    class Factory(
        private val xpRepository: XpRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ShieldProgressionViewModel::class.java)) {
                return ShieldProgressionViewModel(xpRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
