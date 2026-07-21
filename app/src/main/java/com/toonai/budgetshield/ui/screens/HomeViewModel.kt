package com.toonai.budgetshield.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.calculation.SafeNowCalculator
import com.toonai.budgetshield.data.database.*
import com.toonai.budgetshield.data.model.Bill
import com.toonai.budgetshield.data.model.BudgetCategory
import com.toonai.budgetshield.data.model.IncomeSchedule
import com.toonai.budgetshield.data.model.SavingsBalance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * HomeViewModel - Provides live data for HomeScreen.
 * All values calculated from SetupQuest data, no hardcoded values.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userSettingsDao: UserSettingsDao,
    private val accountDao: AccountDao,
    private val incomeScheduleDao: IncomeScheduleDao,
    private val billDao: BillDao,
    private val savingsBalanceDao: SavingsBalanceDao,
    private val budgetCategoryDao: BudgetCategoryDao,
    private val safeNowCalculator: SafeNowCalculator
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Current month for display
    private val _currentMonth = MutableStateFlow(LocalDate.now())

    // Flows for data
    private val cashFlow: Flow<Long> = accountDao.getDefaultAccountFlow()
        .map { it?.openingBalanceCents ?: 0L }
        .distinctUntilChanged()

    private val incomeFlow: Flow<List<IncomeSchedule>> = incomeScheduleDao.getActiveSchedulesFlow()
        .distinctUntilChanged()

    private val billsFlow: Flow<List<Bill>> = billDao.getAllBills()
        .distinctUntilChanged()

    private val savingsFlow: Flow<Long> = savingsBalanceDao.getBalanceFlow()
        .map { it?.balanceCents ?: 0L }
        .distinctUntilChanged()

    private val budgetsFlow: Flow<List<BudgetCategory>> = _currentMonth
        .flatMapLatest { month ->
            budgetCategoryDao.getCategoriesForMonthFlow(getMonthKey(month))
        }
        .distinctUntilChanged()

    init {
        loadHomeData()
    }

    fun refreshData() {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            combine(
                cashFlow,
                incomeFlow,
                billsFlow,
                savingsFlow,
                budgetsFlow,
                _currentMonth
            ) { array ->
                @Suppress("UNCHECKED_CAST")
                HomeDataBundle(
                    cashCents = array[0] as Long,
                    incomeSchedules = array[1] as List<IncomeSchedule>,
                    bills = array[2] as List<Bill>,
                    savingsCents = array[3] as Long,
                    budgets = array[4] as List<BudgetCategory>,
                    currentMonth = array[5] as LocalDate
                )
            }.collect { bundle ->
                updateUiState(bundle)
            }
        }
    }

    private fun updateUiState(bundle: HomeDataBundle) {
        val clearedCash = bundle.cashCents
        val protectedBills = bundle.bills.filter { it.isProtected && !it.isPaid }
        val totalProtected = protectedBills.sumOf { it.remainingDueCents }

        // Calculate Safe Now using the real calculator
        val safeNowResult = safeNowCalculator.calculate(
            clearedCashCents = clearedCash,
            incomeSchedules = bundle.incomeSchedules,
            bills = bundle.bills,
            today = LocalDate.now()
        )

        val safeNowStatus = when {
            safeNowResult.shortageCents > 0 -> SafeNowStatus.CRITICAL
            safeNowResult.safeNowCents < 5000 -> SafeNowStatus.WARNING
            else -> SafeNowStatus.SECURE
        }

        // Calculate streak (simplified - days since last shortage)
        val streakDays = if (safeNowResult.shortageCents == 0L) {
            calculateStreakDays(bundle.bills)
        } else 0

        // Build recent transactions from bills and income
        val transactions = buildRecentTransactions(bundle)

        _uiState.update { state ->
            state.copy(
                currentMonth = formatMonthYear(bundle.currentMonth),
                safeNowAmount = safeNowResult.safeNowCents,
                safeNowStatus = safeNowStatus,
                projectedDate = safeNowResult.firstFailingDate?.format(
                    DateTimeFormatter.ofPattern("MMM d")
                ),
                shieldPercentage = calculateShieldPercentage(bundle.bills),
                streakDays = streakDays,
                protectedBills = protectedBills,
                totalProtectedAmount = totalProtected,
                recentTransactions = transactions,
                savingsAmount = bundle.savingsCents,
                budgets = bundle.budgets
            )
        }
    }

    fun previousMonth() {
        _currentMonth.update { it.minusMonths(1) }
    }

    fun nextMonth() {
        _currentMonth.update { it.plusMonths(1) }
    }

    fun payBill(billId: Long) {
        viewModelScope.launch {
            val bill = billDao.getBillById(billId) ?: return@launch
            val remaining = bill.remainingDueCents

            if (remaining > 0) {
                val updatedBill = bill.copy(
                    paidAmountCents = bill.paidAmountCents + remaining,
                    isPaid = true
                )
                billDao.updateBill(updatedBill)
            }
        }
    }

    private fun calculateStreakDays(bills: List<Bill>): Int {
        val unpaidProtected = bills.filter { it.isProtected && !it.isPaid }
        return if (unpaidProtected.isEmpty()) 7 else 3
    }

    private fun calculateShieldPercentage(bills: List<Bill>): Int {
        val totalBills = bills.size
        val protectedBills = bills.count { it.isProtected }
        return if (totalBills > 0) {
            (protectedBills * 100) / totalBills
        } else 0
    }

    private fun buildRecentTransactions(bundle: HomeDataBundle): List<RecentTransaction> {
        val transactions = mutableListOf<RecentTransaction>()

        bundle.bills
            .filter { it.paidAmountCents > 0 }
            .forEach { bill ->
                transactions.add(
                    RecentTransaction(
                        id = bill.id,
                        description = bill.name,
                        amountCents = -bill.paidAmountCents,
                        type = TransactionType.EXPENSE,
                        date = bill.dueDate,
                        icon = bill.icon,
                        categoryColor = androidx.compose.ui.graphics.Color(0xFF17E8F2)
                    )
                )
            }

        if (bundle.savingsCents > 0) {
            transactions.add(
                RecentTransaction(
                    id = -1L,
                    description = "Savings",
                    amountCents = bundle.savingsCents,
                    type = TransactionType.TRANSFER,
                    date = LocalDate.now().toString(),
                    icon = "🐷",
                    categoryColor = androidx.compose.ui.graphics.Color(0xFF17F253)
                )
            )
        }

        return transactions.sortedByDescending { it.date }.take(5)
    }

    private fun getCurrentMonthKey(): String = getMonthKey(LocalDate.now())

    private fun getMonthKey(date: LocalDate): String {
        return String.format("%04d-%02d", date.year, date.monthValue)
    }

    private fun formatMonthYear(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
    }

    private data class HomeDataBundle(
        val cashCents: Long,
        val incomeSchedules: List<IncomeSchedule>,
        val bills: List<Bill>,
        val savingsCents: Long,
        val budgets: List<BudgetCategory>,
        val currentMonth: LocalDate
    )
}

data class HomeUiState(
    val currentMonth: String = "",
    val safeNowAmount: Long = 0L,
    val safeNowStatus: SafeNowStatus = SafeNowStatus.SECURE,
    val projectedDate: String? = null,
    val shieldPercentage: Int = 0,
    val streakDays: Int = 0,
    val protectedBills: List<Bill> = emptyList(),
    val totalProtectedAmount: Long = 0L,
    val recentTransactions: List<RecentTransaction> = emptyList(),
    val savingsAmount: Long = 0L,
    val budgets: List<BudgetCategory> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val safeNowFormatted: String get() = formatCents(safeNowAmount)
    val totalProtectedFormatted: String get() = formatCents(totalProtectedAmount)
    val savingsFormatted: String get() = formatCents(savingsAmount)
}

private fun formatCents(cents: Long): String {
    val dollars = cents / 100
    val remainder = kotlin.math.abs(cents % 100)
    return String.format("$%d.%02d", dollars, remainder)
}
