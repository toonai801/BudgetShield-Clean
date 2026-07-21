package com.toonai.budgetshield.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.model.IncomeSchedule
import com.toonai.budgetshield.data.model.UserSettings
import com.toonai.budgetshield.data.repository.BudgetRepository
import com.toonai.budgetshield.data.repository.IncomeRepository
import com.toonai.budgetshield.data.repository.UserSettingsRepository
import com.toonai.budgetshield.ui.screens.DraftBill
import com.toonai.budgetshield.util.DateParser
import com.toonai.budgetshield.util.MoneyParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupQuestViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
    private val incomeRepository: IncomeRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupQuestUiState())
    val uiState: StateFlow<SetupQuestUiState> = _uiState.asStateFlow()

    private var userSettings: UserSettings? = null

    fun loadDraft() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                userSettings = userSettingsRepository.getSettings()
                val settings = userSettings
                if (settings != null && settings.setupChapter in 1..6) {
                    restoreFromDraft(settings)
                } else {
                    _uiState.value = _uiState.value.copy(currentChapter = 1, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to load: ${e.message}")
            }
        }
    }

    private suspend fun restoreFromDraft(settings: UserSettings) {
        _uiState.value = _uiState.value.copy(
            currentChapter = settings.setupChapter,
            cashOnHandCents = settings.cashOnHandCents,
            cashOnHandInput = MoneyParser.formatCents(settings.cashOnHandCents).replace("$", ""),
            savingsCents = settings.savingsBalanceCents,
            savingsInput = MoneyParser.formatCents(settings.savingsBalanceCents).replace("$", ""),
            isLoading = false
        )

        val income = incomeRepository.getActiveSchedule().first()
        if (income != null) {
            _uiState.value = _uiState.value.copy(
                incomeName = income.name,
                incomeAmountCents = income.amountCents,
                incomeAmountInput = MoneyParser.formatCents(income.amountCents).replace("$", ""),
                paydayDate = income.nextPaydayDate,
                frequency = income.frequency,
                isIncomeConfirmed = income.isConfirmed
            )
        }

        val monthKey = DateParser.currentMonthKey()
        val foodBudget = budgetRepository.getBudgetForCategory("Food", monthKey).first()
        val wantsBudget = budgetRepository.getBudgetForCategory("Wants", monthKey).first()

        _uiState.value = _uiState.value.copy(
            foodBudgetCents = foodBudget?.plannedAmountCents ?: 0,
            foodBudgetInput = MoneyParser.formatCents(foodBudget?.plannedAmountCents ?: 0).replace("$", ""),
            wantsBudgetCents = wantsBudget?.plannedAmountCents ?: 0,
            wantsBudgetInput = MoneyParser.formatCents(wantsBudget?.plannedAmountCents ?: 0).replace("$", "")
        )
    }

    // Chapter 1: Cash on Hand
    fun updateCashOnHand(input: String) {
        _uiState.value = _uiState.value.copy(cashOnHandInput = input, cashOnHandError = null)
        MoneyParser.parseToCents(input).fold(
            onSuccess = { cents ->
                if (cents < 0) {
                    _uiState.value = _uiState.value.copy(cashOnHandError = "Cannot be negative")
                } else {
                    _uiState.value = _uiState.value.copy(cashOnHandCents = cents)
                    saveDraft()
                }
            },
            onFailure = {}
        )
    }

    // Chapter 2: Payday
    fun updateIncomeName(name: String) {
        _uiState.value = _uiState.value.copy(incomeName = name)
        saveDraft()
    }

    fun updateIncomeAmount(input: String) {
        _uiState.value = _uiState.value.copy(incomeAmountInput = input, paydayErrors = _uiState.value.paydayErrors - "incomeAmount")
        MoneyParser.parseToCents(input).fold(
            onSuccess = { cents ->
                if (cents < 0) {
                    _uiState.value = _uiState.value.copy(paydayErrors = _uiState.value.paydayErrors + ("incomeAmount" to "Cannot be negative"))
                } else {
                    _uiState.value = _uiState.value.copy(incomeAmountCents = cents)
                    saveDraft()
                }
            },
            onFailure = {}
        )
    }

    fun updatePaydayDate(date: String) {
        _uiState.value = _uiState.value.copy(paydayDate = date, paydayErrors = _uiState.value.paydayErrors - "paydayDate")
        saveDraft()
    }

    fun updateFrequency(frequency: String) {
        _uiState.value = _uiState.value.copy(frequency = frequency)
        saveDraft()
    }

    fun toggleIncomeConfirmation() {
        val newState = !_uiState.value.isIncomeConfirmed
        _uiState.value = _uiState.value.copy(isIncomeConfirmed = newState)
        saveDraft()
    }

    // Chapter 3: Bills
    fun addBill(draftBill: DraftBill) {
        val currentBills = _uiState.value.bills.toMutableList()
        currentBills.add(draftBill)
        _uiState.value = _uiState.value.copy(bills = currentBills)
    }

    fun updateBillName(billId: Long, name: String) {
        val updatedBills = _uiState.value.bills.map { bill ->
            if (bill.id == billId) bill.copy(name = name) else bill
        }
        _uiState.value = _uiState.value.copy(bills = updatedBills)
    }

    fun updateBillAmount(billId: Long, amount: String) {
        val updatedBills = _uiState.value.bills.map { bill ->
            if (bill.id == billId) bill.copy(amountInput = amount) else bill
        }
        _uiState.value = _uiState.value.copy(bills = updatedBills)

        MoneyParser.parseToCents(amount).fold(
            onSuccess = { cents ->
                val billsWithCents = _uiState.value.bills.map { bill ->
                    if (bill.id == billId) bill.copy(amountCents = cents) else bill
                }
                _uiState.value = _uiState.value.copy(bills = billsWithCents)
            },
            onFailure = {}
        )
    }

    fun updateBillDueDate(billId: Long, dueDate: String) {
        val updatedBills = _uiState.value.bills.map { bill ->
            if (bill.id == billId) bill.copy(dueDateInput = dueDate) else bill
        }
        _uiState.value = _uiState.value.copy(bills = updatedBills)
    }

    fun toggleBillProtection(billId: Long) {
        val updatedBills = _uiState.value.bills.map { bill ->
            if (bill.id == billId) bill.copy(isProtected = !bill.isProtected) else bill
        }
        _uiState.value = _uiState.value.copy(bills = updatedBills)
    }

    fun removeBill(billId: Long) {
        val updatedBills = _uiState.value.bills.filter { it.id != billId }
        _uiState.value = _uiState.value.copy(bills = updatedBills)
    }

    // Chapter 4: Savings
    fun updateSavings(input: String) {
        _uiState.value = _uiState.value.copy(savingsInput = input, savingsError = null)
        MoneyParser.parseToCents(input).fold(
            onSuccess = { cents ->
                if (cents < 0) {
                    _uiState.value = _uiState.value.copy(savingsError = "Cannot be negative")
                } else {
                    _uiState.value = _uiState.value.copy(savingsCents = cents)
                    saveDraft()
                }
            },
            onFailure = {}
        )
    }

    // Chapter 5: Budgets
    fun updateFoodBudget(input: String) {
        _uiState.value = _uiState.value.copy(foodBudgetInput = input, foodBudgetError = null)
        MoneyParser.parseToCents(input).fold(
            onSuccess = { cents ->
                if (cents < 0) {
                    _uiState.value = _uiState.value.copy(foodBudgetError = "Cannot be negative")
                } else {
                    _uiState.value = _uiState.value.copy(foodBudgetCents = cents)
                    saveDraft()
                }
            },
            onFailure = {}
        )
    }

    fun updateWantsBudget(input: String) {
        _uiState.value = _uiState.value.copy(wantsBudgetInput = input, wantsBudgetError = null)
        MoneyParser.parseToCents(input).fold(
            onSuccess = { cents ->
                if (cents < 0) {
                    _uiState.value = _uiState.value.copy(wantsBudgetError = "Cannot be negative")
                } else {
                    _uiState.value = _uiState.value.copy(wantsBudgetCents = cents)
                    saveDraft()
                }
            },
            onFailure = {}
        )
    }

    // Navigation
    fun goToNextChapter() {
        if (validateCurrentChapter()) {
            val nextChapter = _uiState.value.currentChapter + 1
            if (nextChapter <= 6) {
                _uiState.value = _uiState.value.copy(currentChapter = nextChapter)
                saveDraft()
            }
        }
    }

    fun goToPreviousChapter() {
        val prevChapter = _uiState.value.currentChapter - 1
        if (prevChapter >= 1) {
            _uiState.value = _uiState.value.copy(currentChapter = prevChapter)
        }
    }

    private fun validateCurrentChapter(): Boolean {
        val state = _uiState.value
        return when (state.currentChapter) {
            1 -> state.cashOnHandCents >= 0
            2 -> {
                val errors = mutableMapOf<String, String>()
                if (state.incomeName.isBlank()) errors["incomeName"] = "Required"
                if (state.incomeAmountCents <= 0) errors["incomeAmount"] = "Required"
                if (state.paydayDate.isBlank()) errors["paydayDate"] = "Required"
                _uiState.value = state.copy(paydayErrors = errors)
                errors.isEmpty() && state.isIncomeConfirmed
            }
            else -> true
        }
    }

    // Complete setup
    fun completeSetup() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val settings = UserSettings(
                    id = 1,
                    isFirstRunComplete = true,
                    cashOnHandCents = _uiState.value.cashOnHandCents,
                    savingsBalanceCents = _uiState.value.savingsCents,
                    setupChapter = 7,
                    selectedMonth = DateParser.currentMonthKey()
                )
                userSettingsRepository.saveSettings(settings)
                _uiState.value = _uiState.value.copy(isLoading = false, isComplete = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed: ${e.message}")
            }
        }
    }

    private fun saveDraft() {
        viewModelScope.launch {
            val state = _uiState.value
            val settings = UserSettings(
                id = 1,
                isFirstRunComplete = false,
                cashOnHandCents = state.cashOnHandCents,
                savingsBalanceCents = state.savingsCents,
                setupChapter = state.currentChapter,
                selectedMonth = DateParser.currentMonthKey()
            )
            userSettingsRepository.saveSettings(settings)

            if (state.incomeName.isNotBlank() && state.incomeAmountCents > 0) {
                val schedule = IncomeSchedule(
                    id = 0L,
                    name = state.incomeName,
                    amountCents = state.incomeAmountCents,
                    nextPaydayDate = state.paydayDate,
                    frequency = state.frequency,
                    isConfirmed = state.isIncomeConfirmed
                )
                incomeRepository.saveSchedule(schedule)
            }

            val monthKey = DateParser.currentMonthKey()
            budgetRepository.saveBudget("Food", monthKey, state.foodBudgetCents)
            budgetRepository.saveBudget("Wants", monthKey, state.wantsBudgetCents)
        }
    }
}

data class SetupQuestUiState(
    val currentChapter: Int = 1,
    val isLoading: Boolean = false,
    val isComplete: Boolean = false,
    val error: String? = null,

    // Chapter 1
    val cashOnHandInput: String = "",
    val cashOnHandCents: Long = 0,
    val cashOnHandError: String? = null,

    // Chapter 2
    val incomeName: String = "",
    val incomeAmountInput: String = "",
    val incomeAmountCents: Long = 0,
    val paydayDate: String = "",
    val frequency: String = "BIWEEKLY",
    val isIncomeConfirmed: Boolean = false,
    val paydayErrors: Map<String, String> = emptyMap(),

    // Chapter 3
    val bills: List<DraftBill> = emptyList(),
    val billErrors: Map<Long, Map<String, String>> = emptyMap(),

    // Chapter 4
    val savingsInput: String = "",
    val savingsCents: Long = 0,
    val savingsError: String? = null,

    // Chapter 5
    val foodBudgetInput: String = "",
    val foodBudgetCents: Long = 0,
    val wantsBudgetInput: String = "",
    val wantsBudgetCents: Long = 0,
    val foodBudgetError: String? = null,
    val wantsBudgetError: String? = null
)

enum class SetupChapter(val number: Int, val title: String) {
    CASH_ON_HAND(1, "Cash on Hand"),
    PAYDAY(2, "Payday Schedule"),
    BILLS(3, "Monthly Bills"),
    SAVINGS(4, "Savings Balance"),
    BUDGETS(5, "Budget Categories"),
    REVIEW(6, "Shield Review")
}
