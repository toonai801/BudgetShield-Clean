package com.toonai.budgetshield.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.model.Transaction
import com.toonai.budgetshield.data.model.TransactionCategories
import com.toonai.budgetshield.data.repository.TransactionRepository
import com.toonai.budgetshield.data.repository.XpRepository
import com.toonai.budgetshield.util.DateParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Category breakdown data
 */
data class CategoryBreakdown(
    val category: String,
    val amountCents: Long,
    val percentage: Int,
    val icon: String
)

/**
 * UI state for Stats screen
 */
data class StatsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedMonth: String = DateParser.currentMonthKey(),
    val totalIncome: Long = 0L,
    val totalExpenses: Long = 0L,
    val netAmount: Long = 0L,
    val categoryBreakdown: List<CategoryBreakdown> = emptyList(),
    val monthlyXp: Int = 0,
    val recentTransactions: List<Transaction> = emptyList()
)

/**
 * ViewModel for Stats screen.
 * Manages spending statistics, category breakdowns, and monthly summaries.
 */
class StatsViewModel(
    private val transactionRepository: TransactionRepository,
    private val xpRepository: XpRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStatsForMonth(DateParser.currentMonthKey())
    }

    /**
     * Load statistics for a specific month.
     */
    fun loadStatsForMonth(monthKey: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, selectedMonth = monthKey)

            try {
                // Get transactions for the month
                val startDate = "$monthKey-01"
                val endDate = DateParser.getLastDayOfMonth(monthKey)

                var totalIncome = 0L
                var totalExpenses = 0L
                val categoryMap = mutableMapOf<String, Long>()

                transactionRepository.getTransactionsForDateRange(startDate, endDate)
                    .collect { transactions ->
                        transactions.forEach { transaction ->
                            when {
                                transaction.isIncome -> {
                                    totalIncome += transaction.amountCents
                                }
                                transaction.amountCents < 0 -> {
                                    val absAmount = kotlin.math.abs(transaction.amountCents)
                                    totalExpenses += absAmount
                                    val cat = transaction.category ?: TransactionCategories.OTHER
                                    categoryMap[cat] = (categoryMap[cat] ?: 0L) + absAmount
                                }
                            }
                        }

                        // Calculate category breakdown
                        val breakdown = if (totalExpenses > 0) {
                            categoryMap.map { (cat, amount) ->
                                CategoryBreakdown(
                                    category = cat,
                                    amountCents = amount,
                                    percentage = ((amount * 100) / totalExpenses).toInt(),
                                    icon = getCategoryIcon(cat)
                                )
                            }.sortedByDescending { it.amountCents }
                        } else emptyList()

                        // Get monthly XP - collect from Flow
                        xpRepository.getXpForMonth(monthKey).collect { monthlyXp ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                totalIncome = totalIncome,
                                totalExpenses = totalExpenses,
                                netAmount = totalIncome - totalExpenses,
                                categoryBreakdown = breakdown,
                                monthlyXp = monthlyXp,
                                recentTransactions = transactions.take(5)
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load stats: ${e.message}"
                )
            }
        }
    }

    /**
     * Get icon for a category.
     */
    private fun getCategoryIcon(category: String): String {
        return when (category) {
            TransactionCategories.FOOD -> "🍔"
            TransactionCategories.WANTS -> "🎮"
            TransactionCategories.BILLS -> "📄"
            TransactionCategories.SAVINGS -> "🏦"
            TransactionCategories.TRANSPORT -> "🚌"
            TransactionCategories.UTILITIES -> "⚡"
            TransactionCategories.INCOME -> "💰"
            else -> "💳"
        }
    }

    /**
     * Format month key for display (e.g., "2026-07" -> "July 2026").
     */
    fun formatMonthKey(monthKey: String): String {
        return try {
            val parts = monthKey.split("-")
            if (parts.size == 2) {
                val year = parts[0]
                val month = parts[1].toInt()
                val monthName = when (month) {
                    1 -> "January"
                    2 -> "February"
                    3 -> "March"
                    4 -> "April"
                    5 -> "May"
                    6 -> "June"
                    7 -> "July"
                    8 -> "August"
                    9 -> "September"
                    10 -> "October"
                    11 -> "November"
                    12 -> "December"
                    else -> monthKey
                }
                "$monthName $year"
            } else {
                monthKey
            }
        } catch (e: Exception) {
            monthKey
        }
    }

    /**
     * Refresh stats for current month.
     */
    fun refresh() {
        loadStatsForMonth(_uiState.value.selectedMonth)
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
        private val transactionRepository: TransactionRepository,
        private val xpRepository: XpRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
                return StatsViewModel(transactionRepository, xpRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
