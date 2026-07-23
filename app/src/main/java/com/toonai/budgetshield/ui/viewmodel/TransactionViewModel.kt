package com.toonai.budgetshield.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.model.Transaction
import com.toonai.budgetshield.data.repository.TransactionRepository
import com.toonai.budgetshield.util.DateParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state for Transaction Details screen
 */
data class TransactionUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val transactions: List<Transaction> = emptyList(),
    val selectedTransaction: Transaction? = null,
    val totalIncome: Long = 0L,
    val totalExpenses: Long = 0L,
    val netAmount: Long = 0L
)

/**
 * ViewModel for Transaction Details screen.
 * Manages transaction history, details, and monthly summaries.
 */
class TransactionViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    init {
        loadRecentTransactions()
    }

    /**
     * Load recent transactions.
     */
    private fun loadRecentTransactions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val transactions = transactionRepository.getRecentTransactions(20)
                val monthKey = DateParser.currentMonthKey()

                // Calculate totals
                var income = 0L
                var expenses = 0L
                transactions.forEach { t ->
                    when {
                        t.isIncome -> income += t.amountCents
                        t.amountCents < 0 -> expenses += kotlin.math.abs(t.amountCents)
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    transactions = transactions,
                    totalIncome = income,
                    totalExpenses = expenses,
                    netAmount = income - expenses
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load transactions: ${e.message}"
                )
            }
        }
    }

    /**
     * Load a specific transaction by ID.
     */
    fun loadTransaction(transactionId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val transaction = transactionRepository.getTransactionById(transactionId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedTransaction = transaction
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load transaction: ${e.message}"
                )
            }
        }
    }

    /**
     * Delete a transaction.
     */
    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch {
            try {
                transactionRepository.deleteTransaction(transactionId)
                loadRecentTransactions() // Refresh the list
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to delete transaction: ${e.message}"
                )
            }
        }
    }

    /**
     * Refresh transactions list.
     */
    fun refresh() {
        loadRecentTransactions()
    }

    /**
     * Clear any error message.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Clear selected transaction.
     */
    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedTransaction = null)
    }

    /**
     * Factory for creating ViewModel with repository dependency.
     */
    class Factory(
        private val transactionRepository: TransactionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
                return TransactionViewModel(transactionRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
