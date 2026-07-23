package com.toonai.budgetshield.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.repository.IncomeRepository
import com.toonai.budgetshield.data.repository.XpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state for Income Entry screen
 */
data class IncomeEntryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false,
    val incomeId: Long? = null
)

/**
 * ViewModel for Income Entry screen.
 * Handles creating new income schedules with validation and XP rewards.
 */
class IncomeEntryViewModel(
    private val incomeRepository: IncomeRepository,
    private val xpRepository: XpRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncomeEntryUiState())
    val uiState: StateFlow<IncomeEntryUiState> = _uiState.asStateFlow()

    /**
     * Save a new income schedule.
     *
     * @param name Income name (e.g., "Salary", "Freelance")
     * @param amountCents Amount in cents
     * @param nextPayday Next payday as YYYY-MM-DD
     * @param frequency Payment frequency (weekly, biweekly, semimonthly, monthly)
     */
    fun saveIncome(
        name: String,
        amountCents: Long,
        nextPayday: String,
        frequency: String = "semimonthly"
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Validate inputs
            when {
                name.isBlank() -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Please enter an income name"
                    )
                    return@launch
                }
                amountCents <= 0 -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Please enter a valid amount"
                    )
                    return@launch
                }
                nextPayday.isBlank() -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Please select a payday"
                    )
                    return@launch
                }
            }

            try {
                // Create the income schedule
                val incomeId = incomeRepository.createIncomeSchedule(
                    name = name.trim(),
                    amountCents = amountCents,
                    nextPayday = nextPayday,
                    frequency = frequency,
                    isConfirmed = true,
                    isActive = true
                )

                // Award XP for adding income
                xpRepository.awardIncomeXp(incomeId, name.trim())

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    saveSuccess = true,
                    incomeId = incomeId
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to save income: ${e.message}"
                )
            }
        }
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
        _uiState.value = _uiState.value.copy(saveSuccess = false, incomeId = null)
    }

    /**
     * Factory for creating ViewModel with dependencies.
     */
    class Factory(
        private val incomeRepository: IncomeRepository,
        private val xpRepository: XpRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(IncomeEntryViewModel::class.java)) {
                return IncomeEntryViewModel(incomeRepository, xpRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
