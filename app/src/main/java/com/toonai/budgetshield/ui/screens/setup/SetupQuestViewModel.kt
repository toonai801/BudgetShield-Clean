package com.toonai.budgetshield.ui.screens.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.database.*
import com.toonai.budgetshield.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * ViewModel for the 6-step Setup Quest.
 * Persists draft state for process-death resume.
 */
@HiltViewModel
class SetupQuestViewModel @Inject constructor(
    private val userSettingsDao: UserSettingsDao,
    private val setupDraftDao: SetupDraftDao,
    private val accountDao: AccountDao,
    private val incomeScheduleDao: IncomeScheduleDao,
    private val billDao: BillDao,
    private val savingsBalanceDao: SavingsBalanceDao,
    private val budgetCategoryDao: BudgetCategoryDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupQuestUiState())
    val uiState: StateFlow<SetupQuestUiState> = _uiState.asStateFlow()

    init {
        loadDraftOrStartFresh()
    }

    private fun loadDraftOrStartFresh() {
        viewModelScope.launch {
            val draft = setupDraftDao.getDraft()
            if (draft != null && draft.currentChapter in 1..6) {
                _uiState.update { state ->
                    state.copy(
                        currentChapter = draft.currentChapter,
                        cashOnHandCents = draft.cashOnHandCents,
                        incomeName = draft.incomeName ?: "",
                        incomeAmountCents = draft.incomeAmountCents,
                        incomeFrequency = draft.incomeFrequency ?: "monthly",
                        nextPaydayDate = draft.nextPaydayDate ?: LocalDate.now().toString(),
                        bills = emptyList(), // Bills are handled separately
                        savingsCents = draft.savingsCents,
                        foodBudgetCents = draft.foodBudgetCents,
                        wantsBudgetCents = draft.wantsBudgetCents
                    )
                }
                // Load bills from BillDao for setup
                val existingBills = billDao.getAllBills().first()
                    .filter { !it.isPaid } // Only unpaid bills during setup
                    .map { bill ->
                        SetupBillDraft(
                            name = bill.name,
                            icon = bill.icon,
                            amountCents = bill.remainingDueCents,
                            dueDate = bill.dueDate,
                            isProtected = bill.isProtected
                        )
                    }
                _uiState.update { it.copy(bills = existingBills) }
            }
        }
    }

    private fun saveDraft() {
        viewModelScope.launch {
            val current = _uiState.value
            val draft = SetupDraft(
                currentChapter = current.currentChapter,
                cashOnHandCents = current.cashOnHandCents,
                incomeName = current.incomeName.takeIf { it.isNotBlank() },
                incomeAmountCents = current.incomeAmountCents,
                incomeFrequency = current.incomeFrequency.takeIf { it.isNotBlank() },
                nextPaydayDate = current.nextPaydayDate.takeIf { it.isNotBlank() },
                savingsCents = current.savingsCents,
                foodBudgetCents = current.foodBudgetCents,
                wantsBudgetCents = current.wantsBudgetCents
            )
            setupDraftDao.saveDraft(draft)
        }
    }

    // Navigation
    fun goToNextChapter() {
        _uiState.update { it.copy(currentChapter = (it.currentChapter + 1).coerceAtMost(6)) }
        saveDraft()
    }

    fun goToPreviousChapter() {
        _uiState.update { it.copy(currentChapter = (it.currentChapter - 1).coerceAtLeast(1)) }
        saveDraft()
    }

    // Chapter 1: Cash
    fun updateCashOnHand(cents: Long?) {
        _uiState.update { it.copy(cashOnHandCents = cents) }
        saveDraft()
    }

    // Chapter 2: Income
    fun updateIncomeName(name: String) {
        _uiState.update { it.copy(incomeName = name) }
        saveDraft()
    }

    fun updateIncomeAmount(cents: Long?) {
        _uiState.update { it.copy(incomeAmountCents = cents) }
        saveDraft()
    }

    fun updateIncomeFrequency(frequency: String) {
        _uiState.update { it.copy(incomeFrequency = frequency) }
        saveDraft()
    }

    fun updateNextPayday(date: String) {
        _uiState.update { it.copy(nextPaydayDate = date) }
        saveDraft()
    }

    // Chapter 3: Bills
    fun addBill(bill: SetupBillDraft) {
        _uiState.update { it.copy(bills = it.bills + bill) }
        saveDraft()
    }

    fun removeBill(bill: SetupBillDraft) {
        _uiState.update { it.copy(bills = it.bills.filter { b -> b != bill }) }
        saveDraft()
    }

    // Chapter 4: Savings
    fun updateSavings(cents: Long?) {
        _uiState.update { it.copy(savingsCents = cents) }
        saveDraft()
    }

    // Chapter 5: Budgets
    fun updateFoodBudget(cents: Long?) {
        _uiState.update { it.copy(foodBudgetCents = cents) }
        saveDraft()
    }

    fun updateWantsBudget(cents: Long?) {
        _uiState.update { it.copy(wantsBudgetCents = cents) }
        saveDraft()
    }

    // Chapter 6: Activate
    fun activateSetup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isActivating = true) }

            val current = _uiState.value
            val now = System.currentTimeMillis()
            val monthKey = getCurrentMonthKey()

            try {
                // 1. Save UserSettings
                userSettingsDao.saveSettings(
                    UserSettings(
                        isFirstRunComplete = true,
                        createdAt = now,
                        updatedAt = now
                    )
                )

                // 2. Save Account (Cash on Hand)
                current.cashOnHandCents?.let { cash ->
                    accountDao.insert(
                        Account(
                            name = "Primary Account",
                            openingBalanceCents = cash,
                            createdAt = now,
                            updatedAt = now
                        )
                    )
                }

                // 3. Save Income Schedule
                if (current.incomeName.isNotBlank() && current.incomeAmountCents != null) {
                    incomeScheduleDao.insert(
                        IncomeSchedule(
                            name = current.incomeName,
                            amountCents = current.incomeAmountCents,
                            frequency = current.incomeFrequency,
                            nextPaydayDate = current.nextPaydayDate,
                            isConfirmed = true,
                            createdAt = now,
                            updatedAt = now
                        )
                    )
                }

                // 4. Save Bills (from setup draft)
                current.bills.forEach { billDraft ->
                    billDao.insertBill(
                        Bill(
                            name = billDraft.name,
                            icon = billDraft.icon,
                            amountCents = billDraft.amountCents,
                            dueDate = billDraft.dueDate,
                            isProtected = billDraft.isProtected,
                            isPaid = false,
                            createdAt = now
                        )
                    )
                }

                // 5. Save Savings
                current.savingsCents?.let { savings ->
                    savingsBalanceDao.insert(
                        SavingsBalance(
                            balanceCents = savings,
                            createdAt = now,
                            updatedAt = now
                        )
                    )
                }

                // 6. Save Budget Categories
                current.foodBudgetCents?.let { foodBudget ->
                    budgetCategoryDao.insert(
                        BudgetCategory(
                            name = "Food & Essentials",
                            monthKey = monthKey,
                            plannedAmountCents = foodBudget,
                            createdAt = now,
                            updatedAt = now
                        )
                    )
                }

                current.wantsBudgetCents?.let { wantsBudget ->
                    budgetCategoryDao.insert(
                        BudgetCategory(
                            name = "Wants & Extras",
                            monthKey = monthKey,
                            plannedAmountCents = wantsBudget,
                            createdAt = now,
                            updatedAt = now
                        )
                    )
                }

                // 7. Clear setup draft
                setupDraftDao.clearDraft()

                _uiState.update { it.copy(isComplete = true, isActivating = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isActivating = false, error = e.message) }
            }
        }
    }

    private fun getCurrentMonthKey(): String {
        val now = LocalDate.now()
        return String.format("%04d-%02d", now.year, now.monthValue)
    }
}

data class SetupQuestUiState(
    val currentChapter: Int = 1,
    val cashOnHandCents: Long? = null,
    val incomeName: String = "",
    val incomeAmountCents: Long? = null,
    val incomeFrequency: String = "monthly",
    val nextPaydayDate: String = LocalDate.now().toString(),
    val bills: List<SetupBillDraft> = emptyList(),
    val savingsCents: Long? = null,
    val foodBudgetCents: Long? = null,
    val wantsBudgetCents: Long? = null,
    val isActivating: Boolean = false,
    val isComplete: Boolean = false,
    val error: String? = null
) {
    val isChapter1Valid: Boolean get() = (cashOnHandCents ?: 0) >= 0
    val isChapter2Valid: Boolean get() = incomeName.isNotBlank() && (incomeAmountCents ?: 0) > 0
    val isChapter3Valid: Boolean get() = bills.isNotEmpty()
    val isChapter4Valid: Boolean get() = (savingsCents ?: 0) >= 0
    val isChapter5Valid: Boolean get() = (foodBudgetCents ?: 0) >= 0 && (wantsBudgetCents ?: 0) >= 0
    val isChapter6Valid: Boolean get() = isChapter1Valid && isChapter2Valid && isChapter3Valid
}

data class SetupBillDraft(
    val name: String,
    val icon: String,
    val amountCents: Long,
    val dueDate: String,
    val isProtected: Boolean = true
)
