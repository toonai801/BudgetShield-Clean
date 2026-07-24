package com.toonai.budgetshield.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.model.BudgetCategory
import com.toonai.budgetshield.data.repository.BudgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class SettingsUiState(
    val budgets: List<BudgetCategory> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val currentMonthKey: String
        get() = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))

    fun loadBudgets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Ensure default categories exist
            budgetRepository.initializeDefaultCategoriesForMonth(currentMonthKey)

            budgetRepository.getBudgetsForMonth(currentMonthKey)
                .collect { budgets ->
                    _uiState.update { state ->
                        state.copy(
                            budgets = budgets,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun updateBudgetAmount(categoryId: Long, amountCents: Long) {
        viewModelScope.launch {
            try {
                val category = _uiState.value.budgets.find { it.id == categoryId }
                category?.let {
                    val updated = it.copy(
                        plannedAmountCents = amountCents,
                        updatedAt = System.currentTimeMillis()
                    )
                    budgetRepository.updateBudgetCategory(updated)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to update budget: ${e.message}")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
