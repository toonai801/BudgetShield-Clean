package com.toonai.budgetshield.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.model.BudgetCategory
import com.toonai.budgetshield.data.model.TransactionCategories
import com.toonai.budgetshield.data.repository.BudgetRepository
import com.toonai.budgetshield.data.repository.TransactionRepository
import com.toonai.budgetshield.util.MoneyParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class LogSpendingUiState(
    val budgets: List<BudgetCategory> = emptyList(),
    val selectedBudget: BudgetCategory? = null,
    val amountInput: String = "",
    val amountCents: Long? = null,
    val amountError: String? = null,
    val note: String = "",
    val currentMonthDisplay: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
) {
    val canSave: Boolean
        get() = selectedBudget != null && amountCents != null && amountCents > 0 && amountError == null
}

@HiltViewModel
class LogSpendingViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogSpendingUiState())
    val uiState: StateFlow<LogSpendingUiState> = _uiState.asStateFlow()

    private val currentMonthKey: String
        get() = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))

    init {
        // Set current month display
        val monthDisplay = YearMonth.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        _uiState.update { it.copy(currentMonthDisplay = monthDisplay) }
    }

    fun loadBudgetsForCurrentMonth() {
        viewModelScope.launch {
            // Ensure default categories exist
            budgetRepository.initializeDefaultCategoriesForMonth(currentMonthKey)
            
            // Load budgets
            budgetRepository.getBudgetsForMonth(currentMonthKey)
                .collect { budgets ->
                    _uiState.update { state ->
                        state.copy(budgets = budgets.filter { it.plannedAmountCents > 0 })
                    }
                }
        }
    }

    fun selectBudget(budget: BudgetCategory) {
        _uiState.update { it.copy(selectedBudget = budget) }
    }

    fun updateAmount(input: String) {
        _uiState.update { state ->
            val parsed = MoneyParser.parseToCents(input)
            val error = when {
                input.isBlank() -> null
                parsed.isFailure -> "Invalid amount"
                parsed.getOrNull() == 0L -> "Amount must be greater than 0"
                else -> null
            }
            
            state.copy(
                amountInput = input,
                amountCents = parsed.getOrNull(),
                amountError = error
            )
        }
    }

    fun updateNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun saveSpending() {
        val state = _uiState.value
        val budget = state.selectedBudget ?: return
        val amountCents = state.amountCents ?: return

        if (!state.canSave) return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                // Add spending to the budget category
                budgetRepository.addSpending(budget.id, amountCents)

                // Create a transaction record for history
                val category = when (budget.name) {
                    "Food" -> TransactionCategories.FOOD
                    "Wants" -> TransactionCategories.WANTS
                    else -> TransactionCategories.OTHER
                }
                transactionRepository.createSpendingTransaction(
                    title = state.note.ifBlank { budget.name },
                    amountCents = amountCents,
                    category = category,
                    description = state.note
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSaved = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to save: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
