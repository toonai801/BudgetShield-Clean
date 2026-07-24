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

data class BudgetsUiState(
    val budgets: List<BudgetCategory> = emptyList(),
    val totalBudgeted: Long = 0L,
    val totalSpent: Long = 0L,
    val totalRemaining: Long = 0L,
    val currentMonthDisplay: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class BudgetsViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetsUiState())
    val uiState: StateFlow<BudgetsUiState> = _uiState.asStateFlow()

    private val currentMonthKey: String
        get() = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))

    init {
        val monthDisplay = YearMonth.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        _uiState.update { it.copy(currentMonthDisplay = monthDisplay) }
    }

    fun loadBudgets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Ensure default categories exist
            budgetRepository.initializeDefaultCategoriesForMonth(currentMonthKey)

            budgetRepository.getBudgetsForMonth(currentMonthKey)
                .collect { budgets ->
                    val totalBudgeted = budgets.sumOf { it.plannedAmountCents }
                    val totalSpent = budgets.sumOf { it.spentAmountCents }
                    val totalRemaining = totalBudgeted - totalSpent

                    _uiState.update { state ->
                        state.copy(
                            budgets = budgets.sortedByDescending { it.plannedAmountCents },
                            totalBudgeted = totalBudgeted,
                            totalSpent = totalSpent,
                            totalRemaining = totalRemaining,
                            isLoading = false
                        )
                    }
                }
        }
    }
}
