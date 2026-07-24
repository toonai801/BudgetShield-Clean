package com.toonai.budgetshield.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.model.Transaction
import com.toonai.budgetshield.data.repository.TransactionRepository
import com.toonai.budgetshield.util.DateParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionHistoryUiState(
    val transactions: List<Transaction> = emptyList(),
    val totalIncome: Long = 0L,
    val totalExpenses: Long = 0L,
    val netAmount: Long = 0L,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionHistoryUiState())
    val uiState: StateFlow<TransactionHistoryUiState> = _uiState.asStateFlow()

    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            transactionRepository.getTransactionsForCurrentMonth()
                .collect { transactions ->
                    val income = transactions.filter { it.isIncome }.sumOf { it.amountCents }
                    val expenses = transactions.filter { it.isExpense }.sumOf { kotlin.math.abs(it.amountCents) }
                    val net = transactions.sumOf { it.amountCents }

                    _uiState.update { state ->
                        state.copy(
                            transactions = transactions.sortedByDescending { it.transactionDate },
                            totalIncome = income,
                            totalExpenses = expenses,
                            netAmount = net,
                            isLoading = false
                        )
                    }
                }
        }
    }
}
