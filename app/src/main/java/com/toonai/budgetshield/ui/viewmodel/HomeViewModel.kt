package com.toonai.budgetshield.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.calculator.SafeNowCalculator
import com.toonai.budgetshield.data.model.Bill
import com.toonai.budgetshield.data.model.IncomeSchedule
import com.toonai.budgetshield.data.model.Transaction
import com.toonai.budgetshield.data.model.UserSettings
import com.toonai.budgetshield.data.repository.BillRepository
import com.toonai.budgetshield.data.repository.IncomeRepository
import com.toonai.budgetshield.data.repository.SavingsGoalRepository
import com.toonai.budgetshield.data.repository.TransactionRepository
import com.toonai.budgetshield.data.repository.UserSettingsRepository
import com.toonai.budgetshield.ui.screens.TransactionType
import com.toonai.budgetshield.ui.screens.TransactionUiModel
import com.toonai.budgetshield.util.DateParser
import com.toonai.budgetshield.util.MoneyParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
    private val billRepository: BillRepository,
    private val incomeRepository: IncomeRepository,
    private val transactionRepository: TransactionRepository,
    private val savingsGoalRepository: SavingsGoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            _uiState.value = loadHomeDataState()
        }
    }

    internal suspend fun loadHomeDataState(): HomeUiState {
        return try {
            val userSettings = userSettingsRepository.getSettings()
                ?: UserSettings(id = 1L, cashOnHandCents = 0L, savingsBalanceCents = 0L)

            val bills = billRepository.allBills.first()
            val income = incomeRepository.getAllActiveSchedules().first()
            val selectedMonth = userSettings.selectedMonth.let {
                if (it.isNotEmpty()) YearMonth.parse(it) else YearMonth.now()
            }

            val safeNowResult = SafeNowCalculator.calculate(
                userSettings = userSettings,
                bills = bills.filter { it.isProtected },
                incomeSchedules = income.filter { it.isConfirmed },
                selectedMonth = selectedMonth.toString(),
                today = LocalDate.now().toString()
            )

            val totalShielded = bills
                .filter { it.isProtected && it.isPaid }
                .sumOf { it.paidAmountCents }

            val shieldPower = calculateShieldPower(bills)
            val recentTransactions = transactionRepository.getRecentTransactions(limit = 5)
            val currentStreak = savingsGoalRepository.getCurrentStreak()

            HomeUiState(
                isLoading = false,
                safeNowCents = safeNowResult.safeNowCents,
                hasShortage = safeNowResult.hasShortage,
                shortageCents = safeNowResult.shortageCents,
                cashOnHandCents = userSettings.cashOnHandCents,
                savingsCents = userSettings.savingsBalanceCents,
                selectedMonth = selectedMonth,
                currentStreak = currentStreak,
                shieldPower = shieldPower,
                totalShieldedCents = totalShielded,
                protectedBillsCount = bills.count { it.isProtected && !it.isPaid },
                recentTransactions = buildRecentTransactions(recentTransactions),
                hasUnreadRewards = false
            )
        } catch (e: Exception) {
            HomeUiState(
                isLoading = false,
                error = e.message ?: "Safe Now could not verify the saved financial data"
            )
        }
    }

    fun goToPreviousMonth() {
        val current = _uiState.value.selectedMonth
        val previous = current.minusMonths(1)
        updateSelectedMonth(previous)
    }

    fun goToNextMonth() {
        val current = _uiState.value.selectedMonth
        val next = current.plusMonths(1)
        updateSelectedMonth(next)
    }

    private fun updateSelectedMonth(month: YearMonth) {
        viewModelScope.launch {
            userSettingsRepository.updateSelectedMonth(month.toString())
            _uiState.value = _uiState.value.copy(selectedMonth = month)
            loadHomeData()
        }
    }

    /**
     * Public method to set a specific month (for date picker)
     */
    fun setSelectedMonth(month: YearMonth) {
        updateSelectedMonth(month)
    }

    private fun calculateShieldPower(bills: List<Bill>): Int {
        val protectedBills = bills.filter { it.isProtected }
        if (protectedBills.isEmpty()) return 100

        val paidBills = protectedBills.filter { it.isPaid }
        return (paidBills.size * 100 / protectedBills.size)
    }

    private fun buildRecentTransactions(transactions: List<Transaction>): List<TransactionUiModel> {
        return transactions
            .map { transaction ->
                TransactionUiModel(
                    id = transaction.id,
                    name = transaction.title,
                    amountDisplay = transaction.toDisplayAmount(),
                    date = transaction.transactionDate,
                    type = transaction.toUiType(),
                    icon = transaction.icon
                )
            }
    }

    private fun Transaction.toUiType(): TransactionType {
        return when (type) {
            Transaction.TYPE_INCOME -> TransactionType.INCOME
            Transaction.TYPE_BILL_PAYMENT -> TransactionType.BILL_PAYMENT
            Transaction.TYPE_SAVINGS -> TransactionType.SAVINGS
            Transaction.TYPE_SPENDING -> TransactionType.SPENDING
            else -> if (amountCents >= 0) TransactionType.INCOME else TransactionType.SPENDING
        }
    }

    private fun Transaction.toDisplayAmount(): String {
        val sign = if (amountCents < 0) "-" else "+"
        return "$sign${MoneyParser.formatCents(kotlin.math.abs(amountCents))}"
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val safeNowCents: Long = 0,
    val hasShortage: Boolean = false,
    val shortageCents: Long = 0,
    val cashOnHandCents: Long = 0,
    val savingsCents: Long = 0,
    val selectedMonth: YearMonth = YearMonth.now(),
    val currentStreak: Int = 0,
    val shieldPower: Int = 0,
    val totalShieldedCents: Long = 0,
    val protectedBillsCount: Int = 0,
    val recentTransactions: List<TransactionUiModel> = emptyList(),
    val hasUnreadRewards: Boolean = false
) {
    val safeNowFormatted: String
        get() = MoneyParser.formatCents(safeNowCents)

    val shortageFormatted: String
        get() = MoneyParser.formatCents(shortageCents)
}
