package com.toonai.budgetshield.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.toonai.budgetshield.data.model.IncomeFrequency
import com.toonai.budgetshield.ui.viewmodel.SetupQuestUiState
import com.toonai.budgetshield.ui.viewmodel.SetupQuestViewModel
import com.toonai.budgetshield.util.DateParser
import com.toonai.budgetshield.util.MoneyParser
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
@Composable
fun SetupQuestScreen(
    onComplete: () -> Unit,
    viewModel: SetupQuestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.currentChapter, uiState.isComplete) {
        android.util.Log.d("SetupQuest", "Chapter: ${uiState.currentChapter}, isComplete: ${uiState.isComplete}")
    }

    LaunchedEffect(Unit) {
        viewModel.loadDraft()
    }

    val isComplete = uiState.isComplete
    LaunchedEffect(isComplete) {
        if (isComplete) {
            onComplete()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("setup_quest_root"),
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
                Box(modifier = Modifier.fillMaxSize()) {
                    SetupQuestContent(
                        uiState = uiState,
                        onPrevious = { viewModel.goToPreviousChapter() },
                        onNext = { viewModel.goToNextChapter() },
                        onCompleteSetup = { viewModel.completeSetup(onComplete) },
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
                        onUpdateBillIcon = { id, icon -> viewModel.updateBillIcon(id, icon) },
                        onToggleBillProtection = { id -> viewModel.toggleBillProtection(id) },
                        onRemoveBill = { viewModel.removeBill(it) },
                        onUpdateFoodBudget = { viewModel.updateFoodBudget(it) },
                        onUpdateWantsBudget = { viewModel.updateWantsBudget(it) }
                    )
                }
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
    onUpdateBillIcon: (Long, String) -> Unit,
    onToggleBillProtection: (Long) -> Unit,
    onRemoveBill: (Long) -> Unit,
    onUpdateFoodBudget: (String) -> Unit,
    onUpdateWantsBudget: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
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
                    onUpdateCashOnHand = onUpdateCashOnHand
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
                    onUpdateBillIcon = onUpdateBillIcon,
                    onToggleBillProtection = onToggleBillProtection,
                    onRemoveBill = onRemoveBill
                )
                4 -> ChapterSavings(
                    savingsInput = uiState.savingsInput,
                    savingsError = uiState.savingsError,
                    onUpdateSavings = onUpdateSavings
                )
                5 -> ChapterMonthlyBudgets(
                    foodBudgetInput = uiState.foodBudgetInput,
                    foodBudgetError = uiState.foodBudgetError,
                    wantsBudgetInput = uiState.wantsBudgetInput,
                    wantsBudgetError = uiState.wantsBudgetError,
                    onUpdateFoodBudget = onUpdateFoodBudget,
                    onUpdateWantsBudget = onUpdateWantsBudget
                )
                6 -> ChapterShieldReview(
                    uiState = uiState,
                    onCompleteSetup = onCompleteSetup
                )
            }
        }

        SetupNavigationFooter(
            currentChapter = uiState.currentChapter,
            totalChapters = 6,
            canProceed = when (uiState.currentChapter) {
                1 -> uiState.cashOnHandError == null && uiState.cashOnHandInput.isNotBlank()
                2 -> {
                    val canProceed = uiState.paydayErrors.isEmpty() && uiState.isIncomeConfirmed && 
                       uiState.incomeName.isNotBlank() && uiState.incomeAmountInput.isNotBlank() && 
                       uiState.paydayDate.isNotBlank()
                    android.util.Log.d("SetupQuest", "Chapter 2 canProceed: errorsEmpty=${uiState.paydayErrors.isEmpty()}, isConfirmed=${uiState.isIncomeConfirmed}, nameBlank=${uiState.incomeName.isBlank()}, amountBlank=${uiState.incomeAmountInput.isBlank()}, dateBlank=${uiState.paydayDate.isBlank()}, incomeAmountCents=${uiState.incomeAmountCents}")
                    canProceed
                }
                3 -> uiState.billErrors.isEmpty()
                4 -> uiState.savingsError == null
                5 -> uiState.foodBudgetError == null && uiState.wantsBudgetError == null &&
                       uiState.foodBudgetInput.isNotBlank() && uiState.wantsBudgetInput.isNotBlank()
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
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.testTag("setup_quest_title")
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
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                modifier = Modifier.testTag("chapter_indicator")
            )
        }
    }
}

@Composable
private fun ChapterCash(
    cashOnHandInput: String,
    cashOnHandError: String?,
    onUpdateCashOnHand: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Chapter 1: Cash on Hand",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.testTag("chapter1_title")
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter your cleared checking balance. Zero is allowed.",
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
            modifier = Modifier
                .fillMaxWidth()
                .testTag("chapter1_cash_input")
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Parse existing date if available
    // Using Locale.US for consistent date formatting
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US) }
    val selectedDateMillis = remember(paydayDate) {
        try {
            if (paydayDate.isNotBlank()) {
                LocalDate.parse(paydayDate, dateFormatter)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            } else {
                LocalDate.now()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            }
        } catch (e: Exception) {
            LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }
    }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis
    )
    
    // Date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = java.time.Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            val formattedDate = date.format(dateFormatter)
                            onUpdatePaydayDate(formattedDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Select")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Chapter 2: Payday",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.testTag("chapter2_title")
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
            modifier = Modifier
                .fillMaxWidth()
                .testTag("chapter2_name_input")
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = incomeAmountInput,
            onValueChange = { input ->
                // Strip non-numeric characters except decimal point
                val cleaned = input.replace(Regex("[^0-9.]"), "")
                // Format as currency as user types
                onUpdateIncomeAmount(cleaned)
            },
            label = { Text("Amount") },
            prefix = { Text("$") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = paydayErrors.containsKey("incomeAmount"),
            supportingText = paydayErrors["incomeAmount"]?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("chapter2_amount_input")
        )
        // Show formatted currency below input
        if (incomeAmountInput.isNotBlank()) {
            MoneyParser.parseToCents(incomeAmountInput).fold(
                onSuccess = { cents ->
                    Text(
                        text = "= ${MoneyParser.formatCents(cents)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                },
                onFailure = {}
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = paydayDate,
            onValueChange = { input ->
                // Allow direct text input for MM/DD/YYYY format
                // Filter to only allow digits and slashes
                val cleaned = input.replace(Regex("[^0-9/]"), "")
                onUpdatePaydayDate(cleaned)
            },
            label = { Text("Next Payday") },
            placeholder = { Text("MM/DD/YYYY") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = paydayErrors.containsKey("paydayDate"),
            supportingText = paydayErrors["paydayDate"]?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("chapter2_date_input"),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Text("📅")
                }
            }
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUpdateFrequency(value) },
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
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleIncomeConfirmation() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isIncomeConfirmed,
                onCheckedChange = { onToggleIncomeConfirmation() },
                modifier = Modifier.testTag("chapter2_confirmation_checkbox")
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
    onUpdateBillIcon: (Long, String) -> Unit,
    onToggleBillProtection: (Long) -> Unit,
    onRemoveBill: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Chapter 3: Bills",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.testTag("chapter3_title")
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
                onUpdateIcon = { onUpdateBillIcon(bill.id, it) },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BillCard(
    bill: DraftBill,
    errors: Map<String, String>,
    onUpdateName: (String) -> Unit,
    onUpdateAmount: (String) -> Unit,
    onUpdateDueDate: (String) -> Unit,
    onUpdateIcon: (String) -> Unit,
    onToggleProtection: () -> Unit,
    onRemove: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showIconPicker by remember { mutableStateOf(false) }
    
    // Bill category options with icons
    val billCategories = listOf(
        "🏠" to "Housing",
        "⚡" to "Utilities", 
        "🍔" to "Food",
        "🚗" to "Transport",
        "📱" to "Phone",
        "📺" to "Streaming",
        "💊" to "Health",
        "📄" to "Other"
    )
    
    // Parse existing date if available
    // Using Locale.US for consistent date formatting
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MM/dd", Locale.US) }
    val selectedDateMillis = remember(bill.dueDateInput) {
        try {
            if (bill.dueDateInput.isNotBlank()) {
                // Try to parse MM/DD format and convert to current year
                val parts = bill.dueDateInput.split("/")
                if (parts.size >= 2) {
                    val month = parts[0].toInt()
                    val day = parts[1].toInt()
                    LocalDate.of(LocalDate.now().year, month, day)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                } else {
                    LocalDate.now()
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                }
            } else {
                LocalDate.now()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            }
        } catch (e: Exception) {
            LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }
    }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis
    )
    
    // Date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = java.time.Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            // Format as MM/DD for bills
                            val formattedDate = "${date.monthValue}/${date.dayOfMonth}"
                            onUpdateDueDate(formattedDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Select")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Icon Picker Dialog
    if (showIconPicker) {
        AlertDialog(
            onDismissRequest = { showIconPicker = false },
            title = { Text("Select Category") },
            text = {
                Column {
                    billCategories.chunked(4).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            row.forEach { (icon, label) ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clickable {
                                            onUpdateIcon(icon)
                                            showIconPicker = false
                                        }
                                        .padding(8.dp)
                                ) {
                                    Text(text = icon, fontSize = 24.sp)
                                    Text(text = label, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showIconPicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }

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
            // Icon selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Category:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Card(
                    onClick = { showIconPicker = true },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(text = bill.icon, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = billCategories.find { it.first == bill.icon }?.second ?: "Other",
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "▼", fontSize = 10.sp)
                    }
                }
            }
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errors.containsKey("dueDate"),
                    supportingText = errors["dueDate"]?.let { { Text(it) } },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Text(text = "📅", fontSize = 12.sp)
                        }
                    }
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
    savingsInput: String,
    savingsError: String?,
    onUpdateSavings: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Chapter 4: Savings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.testTag("chapter4_title")
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter your current savings balance.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = savingsInput,
            onValueChange = onUpdateSavings,
            label = { Text("Savings Balance") },
            prefix = { Text("$") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = savingsError != null,
            supportingText = savingsError?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("chapter4_savings_input")
        )
    }
}

@Composable
private fun ChapterMonthlyBudgets(
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
            text = "Chapter 5: Monthly Budgets",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.testTag("chapter5_title")
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
            modifier = Modifier
                .fillMaxWidth()
                .testTag("chapter5_food_input")
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
            modifier = Modifier
                .fillMaxWidth()
                .testTag("chapter5_wants_input")
        )
    }
}

@Composable
private fun ChapterShieldReview(
    uiState: SetupQuestUiState,
    onCompleteSetup: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Chapter 6: Shield Review",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.testTag("chapter6_title")
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Review your setup before activation:",
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
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onCompleteSetup,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Activate My Shield")
        }
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
                    onClick = {
                        android.util.Log.d("SetupQuest", "Next button clicked, canProceed=$canProceed")
                        onNext()
                    },
                    enabled = canProceed,
                    modifier = Modifier.testTag("setup_next_button")
                ) {
                    Text("Next")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("→")
                }
            } else {
                Button(
                    onClick = onNext,
                    enabled = canProceed,
                    modifier = Modifier.testTag("setup_finish_button")
                ) {
                    Text("Finish")
                }
            }
        }
    }
}

data class DraftBill(
    val id: Long = System.currentTimeMillis(),
    val name: String = "",
    val amountInput: String = "",
    val amountCents: Long = 0,
    val dueDateInput: String = "",
    val isProtected: Boolean = false,
    val icon: String = "📄"
)
