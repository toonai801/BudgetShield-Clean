package com.toonai.budgetshield.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.database.SetupDraftDao
import com.toonai.budgetshield.data.model.Bill
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
import java.util.concurrent.Executors
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

    // Dedicated executor for persistence - survives coroutine cancellation
    private val persistenceExecutor = Executors.newSingleThreadExecutor()

    fun loadDraft() {
        android.util.Log.d("SetupQuest", "loadDraft: Starting")
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                // Submit blocking call to executor
                val settings = persistenceExecutor.submit<UserSettings?> {
                    userSettingsRepository.getSettingsBlocking()
                }.get()

                android.util.Log.d("SetupQuest", "loadDraft: userSettings=$settings")
                if (settings?.isFirstRunComplete == true) {
                    android.util.Log.d("SetupQuest", "loadDraft: Setup already complete")
                    _uiState.value = _uiState.value.copy(isComplete = true, isLoading = false)
                    return@launch
                }

                // Load from setup draft for resume capability
                val draft = persistenceExecutor.submit<SetupDraft?> {
                    setupDraftDao.getDraftBlocking()
                }.get()

                android.util.Log.d("SetupQuest", "loadDraft: draft=$draft")

                if (draft != null) {
                    restoreFromDraft(draft)
                } else {
                    _uiState.value = _uiState.value.copy(currentChapter = 1, isLoading = false)
                }
            } catch (e: Exception) {
                android.util.Log.e("SetupQuest", "loadDraft: Failed to load", e)
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to load: ${e.message}")
            }
        }
    }

    private suspend fun restoreFromDraft(draft: SetupDraft) {
        android.util.Log.d("SetupQuest", "loadDraft: Restoring from draft, chapter=${draft.currentChapter}")

        // Load bills from repository using blocking call
        val persistedBills = persistenceExecutor.submit<List<Bill>> {
            billRepository.getAllBillsBlocking()
        }.get()

        val draftBills = if (persistedBills.isNotEmpty()) {
            persistedBills.map { bill ->
                DraftBill(
                    id = bill.id,
                    name = bill.name,
                    amountInput = MoneyParser.formatCents(bill.amountCents).replace("$", ""),
                    amountCents = bill.amountCents,
                    dueDateInput = bill.dueDate,
                    isProtected = bill.isProtected
                )
            }
        } else emptyList()

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
            bills = draftBills,
            isLoading = false
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
            onFailure = { error ->
                _uiState.value = _uiState.value.copy(cashOnHandError = error.message)
            }
        )
    }

    // Chapter 2: Income
    fun updateIncomeName(name: String) {
        _uiState.value = _uiState.value.copy(incomeName = name)
        _uiState.value = _uiState.value.copy(paydayErrors = emptyMap())
        saveDraft()
    }

    fun updateIncomeAmount(input: String) {
        // Always update the input field so user sees what they type
        _uiState.value = _uiState.value.copy(incomeAmountInput = input)
        
        MoneyParser.parseToCents(input).fold(
            onSuccess = { cents ->
                _uiState.value = _uiState.value.copy(
                    incomeAmountCents = cents,
                    paydayErrors = emptyMap()
                )
                saveDraft()
            },
            onFailure = { /* Don't block typing, validate on Next */ }
        )
    }

    fun updatePaydayDate(date: String) {
        _uiState.value = _uiState.value.copy(paydayDate = date, paydayErrors = emptyMap())
        saveDraft()
    }

    fun updateFrequency(frequency: String) {
        _uiState.value = _uiState.value.copy(frequency = frequency)
        saveDraft()
    }

    fun toggleIncomeConfirmation() {
        val newState = !_uiState.value.isIncomeConfirmed
        _uiState.value = _uiState.value.copy(
            isIncomeConfirmed = newState,
            paydayErrors = emptyMap()
        )
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
        saveDraft()
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
        android.util.Log.d("SetupQuest", "goToNextChapter called, currentChapter=${_uiState.value.currentChapter}")

        if (validateCurrentChapter()) {
            val nextChapter = _uiState.value.currentChapter + 1
            android.util.Log.d("SetupQuest", "Validation passed, advancing to chapter $nextChapter")
            if (nextChapter <= 6) {
                _uiState.value = _uiState.value.copy(currentChapter = nextChapter)
                saveDraft()
            }
        } else {
            android.util.Log.d("SetupQuest", "Validation failed, staying on chapter ${_uiState.value.currentChapter}")
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
                if (state.incomeAmountCents <= 0) errors["incomeAmount"] = "Required: got ${state.incomeAmountCents}"
                if (state.paydayDate.isBlank()) errors["paydayDate"] = "Required"
                if (!state.isIncomeConfirmed) errors["confirmation"] = "Must confirm income"
                android.util.Log.d("SetupQuest", "Chapter 2 validation: incomeName='${state.incomeName}', incomeAmountCents=${state.incomeAmountCents}, paydayDate='${state.paydayDate}', isIncomeConfirmed=${state.isIncomeConfirmed}, errors=$errors")
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
    fun completeSetup(onSuccess: () -> Unit) {
        android.util.Log.d("SetupQuest", "completeSetup called")
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                // Submit all persistence work to executor
                val completed = persistenceExecutor.submit<Boolean> {
                    try {
                        // 1. Save income schedule
                        if (_uiState.value.incomeName.isNotBlank() && _uiState.value.incomeAmountCents > 0) {
                            incomeRepository.saveScheduleBlocking(
                                IncomeSchedule(
                                    id = 0L,
                                    name = _uiState.value.incomeName,
                                    amountCents = _uiState.value.incomeAmountCents,
                                    nextPaydayDate = _uiState.value.paydayDate,
                                    frequency = _uiState.value.frequency,
                                    isConfirmed = _uiState.value.isIncomeConfirmed
                                )
                            )
                            android.util.Log.d("SetupQuest", "completeSetup: Income schedule saved")
                        }

                        // 2. Save budgets for current month
                        val monthKey = DateParser.currentMonthKey()
                        budgetRepository.saveBudgetBlocking("Food", monthKey, _uiState.value.foodBudgetCents)
                        budgetRepository.saveBudgetBlocking("Wants", monthKey, _uiState.value.wantsBudgetCents)
                        android.util.Log.d("SetupQuest", "completeSetup: Budgets saved")

                        // 3. Save bills from setup
                        _uiState.value.bills.forEach { draftBill ->
                            if (draftBill.name.isNotBlank() && draftBill.amountCents > 0) {
                                billRepository.createBillBlocking(
                                    name = draftBill.name,
                                    icon = "📝",
                                    amountCents = draftBill.amountCents,
                                    dueDate = draftBill.dueDateInput,
                                    isProtected = draftBill.isProtected
                                )
                            }
                        }
                        android.util.Log.d("SetupQuest", "completeSetup: Bills saved")

                        // 4. Mark setup complete in UserSettings
                        val settings = UserSettings(
                            id = 1,
                            isFirstRunComplete = true,
                            cashOnHandCents = _uiState.value.cashOnHandCents,
                            savingsBalanceCents = _uiState.value.savingsCents,
                            setupChapter = 7,
                            selectedMonth = monthKey
                        )
                        userSettingsRepository.saveSettingsBlocking(settings)
                        android.util.Log.d("SetupQuest", "completeSetup: Settings saved")

                        // 5. Clear draft
                        setupDraftDao.clearDraftBlocking()
                        android.util.Log.d("SetupQuest", "completeSetup: Draft cleared")

                        true
                    } catch (e: Exception) {
                        android.util.Log.e("SetupQuest", "completeSetup: Failed in executor", e)
                        false
                    }
                }.get()

                _uiState.value = _uiState.value.copy(isLoading = false)
                if (completed) {
                    android.util.Log.d("SetupQuest", "completeSetup: Success, calling onSuccess")
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(error = "Activation failed")
                }
            } catch (e: Exception) {
                android.util.Log.e("SetupQuest", "completeSetup: Failed", e)
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Activation failed: ${e.message}")
            }
        }
    }

    private fun saveDraft() {
        // Submit to executor - fire and forget for drafts
        persistenceExecutor.submit {
            try {
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
                setupDraftDao.saveDraftBlocking(draft)
            } catch (e: Exception) {
                android.util.Log.e("SetupQuest", "saveDraft: Failed", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        persistenceExecutor.shutdown()
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
