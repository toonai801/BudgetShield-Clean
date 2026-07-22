package com.toonai.budgetshield.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.database.SetupDraftDao
import com.toonai.budgetshield.data.model.IncomeSchedule
import com.toonai.budgetshield.data.model.SetupDraft
import com.toonai.budgetshield.data.model.UserSettings
import com.toonai.budgetshield.data.repository.BillRepository
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
    private val budgetRepository: BudgetRepository,
    private val billRepository: BillRepository,
    private val setupDraftDao: SetupDraftDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupQuestUiState())
    val uiState: StateFlow<SetupQuestUiState> = _uiState.asStateFlow()

    fun loadDraft() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // First check if setup is already complete
                val userSettings = userSettingsRepository.getSettings()
                if (userSettings?.isFirstRunComplete == true) {
                    _uiState.value = _uiState.value.copy(isComplete = true, isLoading = false)
                    return@launch
                }

                // Load from setup draft for resume capability
                val draft = setupDraftDao.getDraftSync()
                if (draft != null) {
                    restoreFromDraft(draft)
                } else {
                    _uiState.value = _uiState.value.copy(currentChapter = 1, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to load: ${e.message}")
            }
        }
    }

    private suspend fun restoreFromDraft(draft: SetupDraft) {
        _uiState.value = _uiState.value.copy(
            currentChapter = draft.currentChapter.coerceIn(1, 6),
            cashOnHandCents = draft.cashOnHandCents,
            cashOnHandInput = MoneyParser.formatCents(draft.cashOnHandCents).replace("$", ""),
            savingsCents = draft.savingsBalanceCents,
            savingsInput = MoneyParser.formatCents(draft.savingsBalanceCents).replace("$", ""),
            incomeName = draft.incomeName,
            incomeAmountCents = draft.incomeAmountCents,
            incomeAmountInput = MoneyParser.formatCents(draft.incomeAmountCents).replace("$", ""),
            paydayDate = draft.nextPaydayDate,
            frequency = draft.frequency.ifBlank { "BIWEEKLY" },
            isIncomeConfirmed = draft.isIncomeConfirmed,
            foodBudgetCents = draft.foodBudgetCents,
            foodBudgetInput = MoneyParser.formatCents(draft.foodBudgetCents).replace("$", ""),
            wantsBudgetCents = draft.wantsBudgetCents,
            wantsBudgetInput = MoneyParser.formatCents(draft.wantsBudgetCents).replace("$", ""),
            isLoading = false
        )

        // Load persisted bills for Chapter 3
        val persistedBills = billRepository.allBills.first()
        if (persistedBills.isNotEmpty()) {
            val draftBills = persistedBills.map { bill ->
                DraftBill(
                    id = bill.id,
                    name = bill.name,
                    amountInput = MoneyParser.formatCents(bill.amountCents).replace("$", ""),
                    amountCents = bill.amountCents,
                    dueDateInput = bill.dueDate,
                    isProtected = bill.isProtected
                )
            }
            _uiState.value = _uiState.value.copy(bills = draftBills)
        }
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
            onFailure = { error ->
                _uiState.value = _uiState.value.copy(cashOnHandError = error.message)
            }
        )
    }

    // Chapter 2: Payday
    fun updateIncomeName(name: String) {
        _uiState.value = _uiState.value.copy(incomeName = name)
        saveDraft()
    }

    fun updateIncomeAmount(input: String) {
        val currentErrors = _uiState.value.paydayErrors.toMutableMap()
        currentErrors.remove("incomeAmount")
        _uiState.value = _uiState.value.copy(incomeAmountInput = input, paydayErrors = currentErrors.toMap())
        MoneyParser.parseToCents(input).fold(
            onSuccess = { cents ->
                if (cents < 0) {
                    currentErrors["incomeAmount"] = "Cannot be negative"
                    _uiState.value = _uiState.value.copy(paydayErrors = currentErrors.toMap())
                } else {
                    _uiState.value = _uiState.value.copy(incomeAmountCents = cents)
                    saveDraft()
                }
            },
            onFailure = { error ->
                currentErrors["incomeAmount"] = error.message ?: "Invalid amount"
                _uiState.value = _uiState.value.copy(paydayErrors = currentErrors.toMap())
            }
        )
    }

    fun updatePaydayDate(date: String) {
        val currentErrors = _uiState.value.paydayErrors.toMutableMap()
        currentErrors.remove("paydayDate")
        
        // Validate date format
        val dateError = validateDate(date)
        if (dateError != null && date.isNotBlank()) {
            currentErrors["paydayDate"] = dateError
        }
        
        _uiState.value = _uiState.value.copy(paydayDate = date, paydayErrors = currentErrors.toMap())
        if (dateError == null) {
            saveDraft()
        }
    }

    private fun validateDate(date: String): String? {
        if (date.isBlank()) return null
        val parts = date.split("/")
        if (parts.size != 3) return "Use MM/DD/YYYY format"
        
        try {
            val month = parts[0].toInt()
            val day = parts[1].toInt()
            val year = parts[2].toInt()
            
            if (month !in 1..12) return "Invalid month"
            if (day !in 1..31) return "Invalid day"
            if (year !in 2000..2100) return "Invalid year"
        } catch (e: NumberFormatException) {
            return "Invalid date"
        }
        return null
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
        saveDraft()
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
                saveDraft()
            },
            onFailure = {}
        )
    }

    fun updateBillDueDate(billId: Long, dueDate: String) {
        val updatedBills = _uiState.value.bills.map { bill ->
            if (bill.id == billId) bill.copy(dueDateInput = dueDate) else bill
        }
        _uiState.value = _uiState.value.copy(bills = updatedBills)
        saveDraft()
    }

    fun toggleBillProtection(billId: Long) {
        val updatedBills = _uiState.value.bills.map { bill ->
            if (bill.id == billId) bill.copy(isProtected = !bill.isProtected) else bill
        }
        _uiState.value = _uiState.value.copy(bills = updatedBills)
        saveDraft()
    }

    fun removeBill(billId: Long) {
        val updatedBills = _uiState.value.bills.filter { it.id != billId }
        _uiState.value = _uiState.value.copy(bills = updatedBills)
        saveDraft()
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
            onFailure = { error ->
                _uiState.value = _uiState.value.copy(savingsError = error.message)
            }
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
            onFailure = { error ->
                _uiState.value = _uiState.value.copy(foodBudgetError = error.message)
            }
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
            onFailure = { error ->
                _uiState.value = _uiState.value.copy(wantsBudgetError = error.message)
            }
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
            saveDraft()
        }
    }

    private fun validateCurrentChapter(): Boolean {
        val state = _uiState.value
        return when (state.currentChapter) {
            1 -> state.cashOnHandError == null && state.cashOnHandInput.isNotBlank()
            2 -> {
                val errors = mutableMapOf<String, String>()
                if (state.incomeName.isBlank()) errors["incomeName"] = "Required"
                if (state.incomeAmountCents <= 0) errors["incomeAmount"] = "Required"
                if (state.paydayDate.isBlank()) errors["paydayDate"] = "Required"
                if (!state.isIncomeConfirmed) errors["confirmation"] = "Must confirm income"
                _uiState.value = state.copy(paydayErrors = errors)
                errors.isEmpty()
            }
            3 -> true // Bills are optional
            4 -> state.savingsError == null
            5 -> state.foodBudgetError == null && state.wantsBudgetError == null &&
                 state.foodBudgetInput.isNotBlank() && state.wantsBudgetInput.isNotBlank()
            6 -> true
            else -> true
        }
    }

    // Complete setup atomically
    fun completeSetup() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // 1. Save income schedule
                if (_uiState.value.incomeName.isNotBlank() && _uiState.value.incomeAmountCents > 0) {
                    val schedule = IncomeSchedule(
                        id = 0L,
                        name = _uiState.value.incomeName,
                        amountCents = _uiState.value.incomeAmountCents,
                        nextPaydayDate = _uiState.value.paydayDate,
                        frequency = _uiState.value.frequency,
                        isConfirmed = _uiState.value.isIncomeConfirmed
                    )
                    incomeRepository.saveSchedule(schedule)
                }

                // 2. Save budgets for current month
                val monthKey = DateParser.currentMonthKey()
                budgetRepository.saveBudget("Food", monthKey, _uiState.value.foodBudgetCents)
                budgetRepository.saveBudget("Wants", monthKey, _uiState.value.wantsBudgetCents)

                // 3. Save bills from setup
                _uiState.value.bills.forEach { draftBill ->
                    if (draftBill.name.isNotBlank() && draftBill.amountCents > 0) {
                        billRepository.createBill(
                            name = draftBill.name,
                            icon = "📝",
                            amountCents = draftBill.amountCents,
                            dueDate = draftBill.dueDateInput,
                            isProtected = draftBill.isProtected
                        )
                    }
                }

                // 4. Mark setup complete in UserSettings
                val settings = UserSettings(
                    id = 1,
                    isFirstRunComplete = true,
                    cashOnHandCents = _uiState.value.cashOnHandCents,
                    savingsBalanceCents = _uiState.value.savingsCents,
                    setupChapter = 7, // Complete
                    selectedMonth = monthKey
                )
                userSettingsRepository.saveSettings(settings)

                // 5. Clear draft
                setupDraftDao.clearDraft()

                _uiState.value = _uiState.value.copy(isLoading = false, isComplete = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, 
                    error = "Activation failed: ${e.message}"
                )
            }
        }
    }

    private fun saveDraft() {
        viewModelScope.launch {
            val state = _uiState.value
            val draft = SetupDraft(
                id = 1,
                currentChapter = state.currentChapter,
                cashOnHandCents = state.cashOnHandCents,
                incomeName = state.incomeName,
                incomeAmountCents = state.incomeAmountCents,
                nextPaydayDate = state.paydayDate,
                frequency = state.frequency,
                isIncomeConfirmed = state.isIncomeConfirmed,
                savingsBalanceCents = state.savingsCents,
                foodBudgetCents = state.foodBudgetCents,
                wantsBudgetCents = state.wantsBudgetCents,
                updatedAt = System.currentTimeMillis()
            )
            setupDraftDao.saveDraft(draft)
        }
    }
}

data class SetupQuestUiState(
    val currentChapter: Int = 1,
    val isLoading: Boolean = false,
    val isComplete: Boolean = false,
    val error: String? = null,

    // Chapter 1: Cash on Hand
    val cashOnHandInput: String = "",
    val cashOnHandCents: Long = 0,
    val cashOnHandError: String? = null,

    // Chapter 2: Payday
    val incomeName: String = "",
    val incomeAmountInput: String = "",
    val incomeAmountCents: Long = 0,
    val paydayDate: String = "",
    val frequency: String = "BIWEEKLY",
    val isIncomeConfirmed: Boolean = false,
    val paydayErrors: Map<String, String> = emptyMap(),

    // Chapter 3: Bills
    val bills: List<DraftBill> = emptyList(),
    val billErrors: Map<Long, Map<String, String>> = emptyMap(),

    // Chapter 4: Savings
    val savingsInput: String = "",
    val savingsCents: Long = 0,
    val savingsError: String? = null,

    // Chapter 5: Budgets
    val foodBudgetInput: String = "",
    val foodBudgetCents: Long = 0,
    val wantsBudgetInput: String = "",
    val wantsBudgetCents: Long = 0,
    val foodBudgetError: String? = null,
    val wantsBudgetError: String? = null
)
