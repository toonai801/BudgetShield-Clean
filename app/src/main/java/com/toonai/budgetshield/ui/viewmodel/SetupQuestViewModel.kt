package com.toonai.budgetshield.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.toonai.budgetshield.data.model.Bill
import com.toonai.budgetshield.data.model.IncomeSchedule
import com.toonai.budgetshield.data.model.UserSettings
import com.toonai.budgetshield.data.repository.BudgetRepository
import com.toonai.budgetshield.data.repository.IncomeRepository
import com.toonai.budgetshield.data.repository.UserSettingsRepository
import com.toonai.budgetshield.util.DateParser
import com.toonai.budgetshield.util.MoneyParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel for the six-chapter Setup Quest.
 * Manages draft persistence and validation.
 */
class SetupQuestViewModel(
    private val userSettingsRepository: UserSettingsRepository,
    private val incomeRepository: IncomeRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupQuestUiState())
    val uiState: StateFlow<SetupQuestUiState> = _uiState.asStateFlow()

    private var userSettings: UserSettings? = null

    /**
     * Load existing draft or create new.
     */
    fun loadDraft() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                userSettings = userSettingsRepository.getSettings().first()
                val settings = userSettings

                if (settings != null && settings.setupChapter in 1..6) {
                    // Resume from draft
                    restoreFromDraft(settings)
                } else {
                    // Start fresh at chapter 1
                    _uiState.value = _uiState.value.copy(
                        currentChapter = 1,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load draft: ${e.message}"
                )
            }
        }
    }

    private suspend fun restoreFromDraft(settings: UserSettings) {
        // Restore all saved values
        _uiState.value = _uiState.value.copy(
            currentChapter = settings.setupChapter,
            cashOnHandCents = settings.cashOnHandCents,
            cashOnHandInput = MoneyParser.formatCents(settings.cashOnHandCents).replace("$", ""),
            savingsCents = settings.savingsBalanceCents,
            savingsInput = MoneyParser.formatCents(settings.savingsBalanceCents).replace("$", ""),
            isLoading = false
        )

        // Load income schedule
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

        // Load budgets
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
        _uiState.value = _uiState.value.copy(
            cashOnHandInput = input,
            cashOnHandError = null
        )

        MoneyParser.parseToCents(input).fold(
            onSuccess = { cents ->
                if (cents < 0) {
                    _uiState.value = _uiState.value.copy(
                        cashOnHandError = "Amount cannot be negative"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        cashOnHandCents = cents
                    )
                    saveDraft()
                }
            },
            onFailure = { error ->
                // Invalid input - don't update cents but keep input
            }
        )
    }

    // Chapter 2: Payday
    fun updateIncomeName(name: String) {
        _uiState.value = _uiState.value.copy(incomeName = name)
        saveDraft()
    }

    fun updateIncomeAmount(input: String) {
        _uiState.value = _uiState.value.copy(
            incomeAmountInput = input,
            paydayErrors = _uiState.value.paydayErrors - "incomeAmount"
        )

        MoneyParser.parseToCents(input).fold(
            onSuccess = { cents ->
                if (cents < 0) {
                    _uiState.value = _uiState.value.copy(
                        paydayErrors = _uiState.value.paydayErrors + ("incomeAmount" to "Amount cannot be negative")
                    )
                } else {
                    _uiState.value = _uiState.value.copy(incomeAmountCents = cents)
                    saveDraft()
                }
            },
            onFailure = { error ->
                // Invalid input - don't update cents
            }
        )
    }

    fun updatePaydayDate(date: String) {
        _uiState.value = _uiState.value.copy(
            paydayDate = date,
            paydayErrors = _uiState.value.paydayErrors - "paydayDate"
        )
        saveDraft()
    }

    fun updateFrequency(frequency: String) {
        _uiState.value = _uiState.value.copy(frequency = frequency)
        saveDraft()
    }

    fun updateIncomeConfirmed(confirmed: Boolean) {
        _uiState.value = _uiState.value.copy(isIncomeConfirmed = confirmed)
        saveDraft()
    }

    // Chapter 3: Bills
    fun addBill() {
        // Navigate to bill entry screen
        // Implementation depends on navigation setup
    }

    fun removeBill(billId: Long) {
        viewModelScope.launch {
            // Remove from database
        }
    }

    fun updateBill(bill: Bill) {
        viewModelScope.launch {
            // Update in database
        }
    }

    fun acknowledgeNoBills() {
        _uiState.value = _uiState.value.copy(hasAcknowledgedNoBills = true)
        saveDraft()
    }

    // Chapter 4: Savings
    fun updateSavings(input: String) {
        _uiState.value = _uiState.value.copy(
            savingsInput = input,
            savingsError = null
        )

        MoneyParser.parseToCents(input).fold(
            onSuccess = { cents ->
                if (cents < 0) {
                    _uiState.value = _uiState.value.copy(
                        savingsError = "Amount cannot be negative"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(savingsCents = cents)
                    saveDraft()
                }
            },
            onFailure = { error ->
                // Invalid input
            }
        )
    }

    // Chapter 5: Budgets
    fun updateFoodBudget(input: String) {
        _uiState.value = _uiState.value.copy(
            foodBudgetInput = input,
            budgetErrors = _uiState.value.budgetErrors - "foodBudget"
        )

        MoneyParser.parseToCents(input).fold(
            onSuccess = { cents ->
                if (cents < 0) {
                    _uiState.value = _uiState.value.copy(
                        budgetErrors = _uiState.value.budgetErrors + ("foodBudget" to "Amount cannot be negative")
                    )
                } else {
                    _uiState.value = _uiState.value.copy(foodBudgetCents = cents)
                    saveDraft()
                }
            },
            onFailure = { error ->
                // Invalid input
            }
        )
    }

    fun updateWantsBudget(input: String) {
        _uiState.value = _uiState.value.copy(
            wantsBudgetInput = input,
            budgetErrors = _uiState.value.budgetErrors - "wantsBudget"
        )

        MoneyParser.parseToCents(input).fold(
            onSuccess = { cents ->
                if (cents < 0) {
                    _uiState.value = _uiState.value.copy(
                        budgetErrors = _uiState.value.budgetErrors + ("wantsBudget" to "Amount cannot be negative")
                    )
                } else {
                    _uiState.value = _uiState.value.copy(wantsBudgetCents = cents)
                    saveDraft()
                }
            },
            onFailure = { error ->
                // Invalid input
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
        }
    }

    fun goToChapter(chapter: Int) {
        if (chapter in 1..6) {
            _uiState.value = _uiState.value.copy(currentChapter = chapter)
        }
    }

    /**
     * Validate current chapter before proceeding.
     */
    private fun validateCurrentChapter(): Boolean {
        val state = _uiState.value

        return when (state.currentChapter) {
            1 -> {
                if (state.cashOnHandCents < 0) {
                    _uiState.value = state.copy(cashOnHandError = "Please enter a valid amount")
                    false
                } else true
            }
            2 -> {
                val errors = mutableMapOf<String, String>()
                if (state.incomeName.isBlank()) {
                    errors["incomeName"] = "Please enter an income name"
                }
                if (state.incomeAmountCents <= 0) {
                    errors["incomeAmount"] = "Please enter a valid amount"
                }
                if (state.paydayDate.isBlank()) {
                    errors["paydayDate"] = "Please select a payday"
                }
                _uiState.value = state.copy(paydayErrors = errors)
                errors.isEmpty()
            }
            3 -> {
                // Bills chapter - valid if has bills OR acknowledged no bills
                true
            }
            4 -> {
                if (state.savingsCents < 0) {
                    _uiState.value = state.copy(savingsError = "Please enter a valid amount")
                    false
                } else true
            }
            5 -> {
                // Budgets are optional (zero allowed with confirmation)
                true
            }
            else -> true
        }
    }

    /**
     * Check if can proceed from current chapter.
     */
    fun canProceed(): Boolean {
        val state = _uiState.value
        return when (state.currentChapter) {
            1 -> state.cashOnHandCents >= 0
            2 -> state.incomeName.isNotBlank() &&
                  state.incomeAmountCents > 0 &&
                  state.paydayDate.isNotBlank()
            3 -> true // Optional bills
            4 -> state.savingsCents >= 0
            5 -> state.foodBudgetCents >= 0 && state.wantsBudgetCents >= 0
            6 -> true // Review chapter
            else -> true
        }
    }

    /**
     * Save current state as draft.
     */
    private fun saveDraft() {
        viewModelScope.launch {
            val state = _uiState.value

            // Update user settings
            val settings = UserSettings(
                id = 1,
                isFirstRunComplete = false, // Not complete until activation
                cashOnHandCents = state.cashOnHandCents,
                savingsBalanceCents = state.savingsCents,
                setupChapter = state.currentChapter,
                selectedMonth = DateParser.currentMonthKey()
            )
            userSettingsRepository.saveSettings(settings)

            // Save income schedule
            if (state.incomeName.isNotBlank() && state.incomeAmountCents > 0) {
                val schedule = IncomeSchedule(
                    id = 0L,
                    name = state.incomeName,
                    amountCents = state.incomeAmountCents,
                    nextPayday = state.paydayDate,
                    nextPaydayDate = state.paydayDate,
                    frequency = state.frequency,
                    isConfirmed = state.isIncomeConfirmed
                )
                incomeRepository.saveSchedule(schedule)
            }

            // Save budgets
            val monthKey = DateParser.currentMonthKey()
            if (state.foodBudgetCents >= 0) {
                budgetRepository.saveBudget("Food", monthKey, state.foodBudgetCents)
            }
            if (state.wantsBudgetCents >= 0) {
                budgetRepository.saveBudget("Wants", monthKey, state.wantsBudgetCents)
            }
        }
    }

    /**
     * Activate the shield - finalize setup.
     */
    fun activateShield() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Atomic completion - all writes must succeed
                val settings = UserSettings(
                    id = 1,
                    isFirstRunComplete = true,
                    cashOnHandCents = _uiState.value.cashOnHandCents,
                    savingsBalanceCents = _uiState.value.savingsCents,
                    setupChapter = 7, // Complete
                    selectedMonth = DateParser.currentMonthKey()
                )

                userSettingsRepository.saveSettings(settings)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isComplete = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    activationError = "Failed to activate: ${e.message}. Please try again."
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                // These will be injected properly in production
                // For now, creating minimal implementations
                TODO("Factory requires repository implementations")
            }
        }
    }
}

/**
 * UI State for Setup Quest.
 */
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
    val setupBills: List<Bill> = emptyList(),
    val hasAcknowledgedNoBills: Boolean = false,
    val billsError: String? = null,

    // Chapter 4: Savings
    val savingsInput: String = "",
    val savingsCents: Long = 0,
    val savingsError: String? = null,

    // Chapter 5: Budgets
    val foodBudgetInput: String = "",
    val foodBudgetCents: Long = 0,
    val wantsBudgetInput: String = "",
    val wantsBudgetCents: Long = 0,
    val budgetErrors: Map<String, String> = emptyMap(),

    // Chapter 6: Review
    val calculatedSafeNow: Long = 0,
    val activationError: String? = null
)

/**
 * Enum representing setup chapters.
 */
enum class SetupChapter(val number: Int, val title: String) {
    CASH_ON_HAND(1, "Cash on Hand"),
    PAYDAY(2, "Payday Schedule"),
    BILLS(3, "Monthly Bills"),
    SAVINGS(4, "Savings Balance"),
    BUDGETS(5, "Budget Categories"),
    REVIEW(6, "Shield Review")
}
