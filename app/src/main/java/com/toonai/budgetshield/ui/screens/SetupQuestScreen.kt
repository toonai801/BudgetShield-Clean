package com.toonai.budgetshield.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
// Icons temporarily removed for build - will restore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toonai.budgetshield.data.model.IncomeFrequency
import com.toonai.budgetshield.ui.viewmodel.SetupQuestUiState
import com.toonai.budgetshield.ui.viewmodel.SetupQuestViewModel

@Composable
fun SetupQuestScreen(
    onComplete: () -> Unit,
    viewModel: SetupQuestViewModel = viewModel(factory = SetupQuestViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadDraft()
    }

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onComplete()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                SetupQuestContent(
                    uiState = uiState,
                    onPrevious = { viewModel.goToPreviousChapter() },
                    onNext = { viewModel.goToNextChapter() },
                    onCompleteSetup = { viewModel.completeSetup() },
                    onUpdateCashOnHand = { viewModel.updateCashOnHand(it) },
                    onUpdateSavings = { viewModel.updateSavings(it) },
                    onUpdateIncomeName = { viewModel.updateIncomeName(it) },
                    onUpdateIncomeAmount = { viewModel.updateIncomeAmount(it) },
                    onUpdatePaydayDate = { viewModel.updatePaydayDate(it) },
                    onUpdateFrequency = { viewModel.updateFrequency(it) },
                    onToggleIncomeConfirmation = { viewModel.toggleIncomeConfirmation() },
                    onAddBill = { viewModel.addBill(it) },
                    onUpdateBillName = { id, name -> viewModel.updateBillName(id, name) },
                    onUpdateBillAmount = { id, amount -> viewModel.updateBillAmount(id, amount) },
                    onUpdateBillDueDate = { id, date -> viewModel.updateBillDueDate(id, date) },
                    onToggleBillProtection = { id -> viewModel.toggleBillProtection(id) },
                    onRemoveBill = { viewModel.removeBill(it) },
                    onUpdateFoodBudget = { viewModel.updateFoodBudget(it) },
                    onUpdateWantsBudget = { viewModel.updateWantsBudget(it) }
                )
            }
        }
    }
}

@Composable
private fun SetupQuestContent(
    uiState: SetupQuestUiState,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onCompleteSetup: () -> Unit,
    onUpdateCashOnHand: (String) -> Unit,
    onUpdateSavings: (String) -> Unit,
    onUpdateIncomeName: (String) -> Unit,
    onUpdateIncomeAmount: (String) -> Unit,
    onUpdatePaydayDate: (String) -> Unit,
    onUpdateFrequency: (String) -> Unit,
    onToggleIncomeConfirmation: () -> Unit,
    onAddBill: (DraftBill) -> Unit,
    onUpdateBillName: (Long, String) -> Unit,
    onUpdateBillAmount: (Long, String) -> Unit,
    onUpdateBillDueDate: (Long, String) -> Unit,
    onToggleBillProtection: (Long) -> Unit,
    onRemoveBill: (Long) -> Unit,
    onUpdateFoodBudget: (String) -> Unit,
    onUpdateWantsBudget: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SetupProgressHeader(
            currentChapter = uiState.currentChapter,
            totalChapters = 6
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            when (uiState.currentChapter) {
                1 -> ChapterCash(
                    cashOnHandInput = uiState.cashOnHandInput,
                    cashOnHandError = uiState.cashOnHandError,
                    savingsInput = uiState.savingsInput,
                    savingsError = uiState.savingsError,
                    onUpdateCashOnHand = onUpdateCashOnHand,
                    onUpdateSavings = onUpdateSavings
                )
                2 -> ChapterPayday(
                    incomeName = uiState.incomeName,
                    incomeAmountInput = uiState.incomeAmountInput,
                    paydayDate = uiState.paydayDate,
                    frequency = uiState.frequency,
                    isIncomeConfirmed = uiState.isIncomeConfirmed,
                    paydayErrors = uiState.paydayErrors,
                    onUpdateIncomeName = onUpdateIncomeName,
                    onUpdateIncomeAmount = onUpdateIncomeAmount,
                    onUpdatePaydayDate = onUpdatePaydayDate,
                    onUpdateFrequency = onUpdateFrequency,
                    onToggleIncomeConfirmation = onToggleIncomeConfirmation
                )
                3 -> ChapterBills(
                    bills = uiState.bills,
                    billErrors = uiState.billErrors,
                    onAddBill = onAddBill,
                    onUpdateBillName = onUpdateBillName,
                    onUpdateBillAmount = onUpdateBillAmount,
                    onUpdateBillDueDate = onUpdateBillDueDate,
                    onToggleBillProtection = onToggleBillProtection,
                    onRemoveBill = onRemoveBill
                )
                4 -> ChapterSavings(
                    foodBudgetInput = uiState.foodBudgetInput,
                    foodBudgetError = uiState.foodBudgetError,
                    wantsBudgetInput = uiState.wantsBudgetInput,
                    wantsBudgetError = uiState.wantsBudgetError,
                    onUpdateFoodBudget = onUpdateFoodBudget,
                    onUpdateWantsBudget = onUpdateWantsBudget
                )
                5 -> ChapterReview(
                    uiState = uiState
                )
                6 -> ChapterComplete(
                    onCompleteSetup = onCompleteSetup
                )
            }
        }

        SetupNavigationFooter(
            currentChapter = uiState.currentChapter,
            totalChapters = 6,
            canProceed = when (uiState.currentChapter) {
                1 -> uiState.cashOnHandError == null && uiState.savingsError == null
                2 -> uiState.paydayErrors.isEmpty() && uiState.isIncomeConfirmed
                3 -> uiState.billErrors.isEmpty()
                4 -> uiState.foodBudgetError == null && uiState.wantsBudgetError == null
                5 -> true
                6 -> true
                else -> false
            },
            onPrevious = onPrevious,
            onNext = onNext
        )
    }
}

@Composable
private fun SetupProgressHeader(
    currentChapter: Int,
    totalChapters: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Setup Quest",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { currentChapter / totalChapters.toFloat() },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Chapter $currentChapter of $totalChapters",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ChapterCash(
    cashOnHandInput: String,
    cashOnHandError: String?,
    savingsInput: String,
    savingsError: String?,
    onUpdateCashOnHand: (String) -> Unit,
    onUpdateSavings: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Chapter 1: Cash on Hand",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tell us about your current money situation.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = cashOnHandInput,
            onValueChange = onUpdateCashOnHand,
            label = { Text("Cash on Hand") },
            prefix = { Text("$") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = cashOnHandError != null,
            supportingText = cashOnHandError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = savingsInput,
            onValueChange = onUpdateSavings,
            label = { Text("Savings Balance (optional)") },
            prefix = { Text("$") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = savingsError != null,
            supportingText = savingsError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ChapterPayday(
    incomeName: String,
    incomeAmountInput: String,
    paydayDate: String,
    frequency: String,
    isIncomeConfirmed: Boolean,
    paydayErrors: Map<String, String>,
    onUpdateIncomeName: (String) -> Unit,
    onUpdateIncomeAmount: (String) -> Unit,
    onUpdatePaydayDate: (String) -> Unit,
    onUpdateFrequency: (String) -> Unit,
    onToggleIncomeConfirmation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Chapter 2: Your Payday",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Set up your income schedule.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = incomeName,
            onValueChange = onUpdateIncomeName,
            label = { Text("Income Name") },
            placeholder = { Text("e.g., Bi-weekly Paycheck") },
            isError = paydayErrors.containsKey("incomeName"),
            supportingText = paydayErrors["incomeName"]?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = incomeAmountInput,
            onValueChange = onUpdateIncomeAmount,
            label = { Text("Amount") },
            prefix = { Text("$") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = paydayErrors.containsKey("incomeAmount"),
            supportingText = paydayErrors["incomeAmount"]?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = paydayDate,
            onValueChange = onUpdatePaydayDate,
            label = { Text("Next Payday (MM/DD/YYYY)") },
            isError = paydayErrors.containsKey("paydayDate"),
            supportingText = paydayErrors["paydayDate"]?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "How often?",
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        val frequencies = listOf(
            IncomeFrequency.WEEKLY to "Weekly",
            IncomeFrequency.BIWEEKLY to "Every 2 weeks",
            IncomeFrequency.SEMIMONTHLY to "Twice a month",
            IncomeFrequency.MONTHLY to "Monthly"
        )
        frequencies.forEach { (value, label) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = frequency == value,
                    onClick = { onUpdateFrequency(value) }
                )
                Text(text = label)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isIncomeConfirmed,
                onCheckedChange = { onToggleIncomeConfirmation() }
            )
            Text("This income is confirmed and ready to use")
        }
    }
}

@Composable
private fun ChapterBills(
    bills: List<DraftBill>,
    billErrors: Map<Long, Map<String, String>>,
    onAddBill: (DraftBill) -> Unit,
    onUpdateBillName: (Long, String) -> Unit,
    onUpdateBillAmount: (Long, String) -> Unit,
    onUpdateBillDueDate: (Long, String) -> Unit,
    onToggleBillProtection: (Long) -> Unit,
    onRemoveBill: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Chapter 3: Your Bills",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add your recurring bills. Protect the important ones.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onAddBill(DraftBill()) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+  Add Bill")
        }
        Spacer(modifier = Modifier.height(16.dp))
        bills.forEach { bill ->
            BillCard(
                bill = bill,
                errors = billErrors[bill.id] ?: emptyMap(),
                onUpdateName = { onUpdateBillName(bill.id, it) },
                onUpdateAmount = { onUpdateBillAmount(bill.id, it) },
                onUpdateDueDate = { onUpdateBillDueDate(bill.id, it) },
                onToggleProtection = { onToggleBillProtection(bill.id) },
                onRemove = { onRemoveBill(bill.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (bills.isEmpty()) {
            Text(
                text = "No bills added yet. Tap \"Add Bill\" to get started.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun BillCard(
    bill: DraftBill,
    errors: Map<String, String>,
    onUpdateName: (String) -> Unit,
    onUpdateAmount: (String) -> Unit,
    onUpdateDueDate: (String) -> Unit,
    onToggleProtection: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = bill.name,
                onValueChange = onUpdateName,
                label = { Text("Bill Name") },
                isError = errors.containsKey("name"),
                supportingText = errors["name"]?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = bill.amountInput,
                    onValueChange = onUpdateAmount,
                    label = { Text("Amount") },
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = errors.containsKey("amount"),
                    supportingText = errors["amount"]?.let { { Text(it) } },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = bill.dueDateInput,
                    onValueChange = onUpdateDueDate,
                    label = { Text("Due Date") },
                    placeholder = { Text("MM/DD") },
                    isError = errors.containsKey("dueDate"),
                    supportingText = errors["dueDate"]?.let { { Text(it) } },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = bill.isProtected,
                    onCheckedChange = { onToggleProtection() }
                )
                Text("Protect this bill")
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onRemove) {
                    Text("Remove", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun ChapterSavings(
    foodBudgetInput: String,
    foodBudgetError: String?,
    wantsBudgetInput: String,
    wantsBudgetError: String?,
    onUpdateFoodBudget: (String) -> Unit,
    onUpdateWantsBudget: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Chapter 4: Budget Categories",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Set aside money for essentials and fun.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = foodBudgetInput,
            onValueChange = onUpdateFoodBudget,
            label = { Text("Food Budget (per month)") },
            prefix = { Text("$") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = foodBudgetError != null,
            supportingText = foodBudgetError?.let { { Text(it) } } ?: { Text("Groceries, dining out, etc.") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = wantsBudgetInput,
            onValueChange = onUpdateWantsBudget,
            label = { Text("Wants Budget (per month)") },
            prefix = { Text("$") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = wantsBudgetError != null,
            supportingText = wantsBudgetError?.let { { Text(it) } } ?: { Text("Entertainment, hobbies, etc.") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ChapterReview(
    uiState: SetupQuestUiState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Chapter 5: Review",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Here's what you've set up:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        ReviewCard(
            title = "Cash & Savings",
            items = listOf(
                "Cash on Hand" to "$${uiState.cashOnHandInput}",
                "Savings" to "$${uiState.savingsInput}"
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        ReviewCard(
            title = "Income",
            items = listOf(
                "Name" to uiState.incomeName,
                "Amount" to "$${uiState.incomeAmountInput}",
                "Next Payday" to uiState.paydayDate,
                "Frequency" to uiState.frequency
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Bills (${uiState.bills.size})",
                    style = MaterialTheme.typography.titleMedium
                )
                uiState.bills.forEach { bill ->
                    Text(
                        text = "• ${bill.name}: $${bill.amountInput} (Due: ${bill.dueDateInput})${if (bill.isProtected) " ✓ Protected" else ""}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        ReviewCard(
            title = "Monthly Budgets",
            items = listOf(
                "Food" to "$${uiState.foodBudgetInput}",
                "Wants" to "$${uiState.wantsBudgetInput}"
            )
        )
    }
}

@Composable
private fun ReviewCard(
    title: String,
    items: List<Pair<String, String>>
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            items.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = label, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ChapterComplete(
    onCompleteSetup: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "✓",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "You're All Set!",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your budget shield is ready to protect your money.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onCompleteSetup,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Using Budget Shield")
        }
    }
}

@Composable
private fun SetupNavigationFooter(
    currentChapter: Int,
    totalChapters: Int,
    canProceed: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentChapter > 1) {
                OutlinedButton(onClick = onPrevious) {
                    Text("←")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }
            if (currentChapter < totalChapters) {
                Button(
                    onClick = onNext,
                    enabled = canProceed
                ) {
                    Text("Next")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("→")
                }
            } else {
                Button(
                    onClick = onNext,
                    enabled = canProceed
                ) {
                    Text("Finish")
                }
            }
        }
    }
}

// Draft bill data class
data class DraftBill(
    val id: Long = System.currentTimeMillis(),
    val name: String = "",
    val amountInput: String = "",
    val amountCents: Long = 0,
    val dueDateInput: String = "",
    val isProtected: Boolean = false
)
