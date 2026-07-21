package com.toonai.budgetshield.ui.screens.setup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDate

// Premium gamified dark theme colors
private val BackgroundDark = Color(0xFF02070D)
private val PanelBackground = Color(0xFF0A1A2E)
private val PanelBorder = Color(0xFF14364A)
private val CyanAccent = Color(0xFF17E8F2)
private val TextMuted = Color(0xFFA6B1BF)
private val TextBright = Color(0xFFFFFFFF)

/**
 * Non-bypassable 6-step Setup Quest Screen.
 * No footer shown during setup - user cannot navigate away until complete.
 * Process-death resume via SetupDraft.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupQuestScreen(
    onComplete: () -> Unit = {},
    viewModel: SetupQuestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Auto-complete when setup finished
    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onComplete()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (uiState.currentChapter) {
                            1 -> "Setup: Cash on Hand"
                            2 -> "Setup: Income Schedule"
                            3 -> "Setup: Monthly Bills"
                            4 -> "Setup: Savings Goal"
                            5 -> "Setup: Budget Categories"
                            6 -> "Setup: Review & Activate"
                            else -> "BudgetShield Setup"
                        },
                        color = TextBright
                    )
                },
                navigationIcon = {
                    if (uiState.currentChapter > 1) {
                        IconButton(onClick = { viewModel.goToPreviousChapter() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = CyanAccent
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PanelBackground,
                    titleContentColor = TextBright
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            // Progress indicator
            SetupProgressIndicator(
                currentChapter = uiState.currentChapter,
                totalChapters = 6,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Error display
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .testTag("setup_error_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF3D0A0A)
                    )
                ) {
                    Text(
                        text = error,
                        color = Color(0xFFFF6B6B),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Chapter content
            when (uiState.currentChapter) {
                1 -> Chapter1CashOnHand(
                    cashCents = uiState.cashOnHandCents,
                    onCashChanged = { viewModel.updateCashOnHand(it) },
                    isValid = uiState.isChapter1Valid,
                    onNext = { viewModel.goToNextChapter() }
                )
                2 -> Chapter2IncomeSchedule(
                    incomeName = uiState.incomeName,
                    incomeAmountCents = uiState.incomeAmountCents,
                    incomeFrequency = uiState.incomeFrequency,
                    nextPaydayDate = uiState.nextPaydayDate,
                    onIncomeNameChanged = { viewModel.updateIncomeName(it) },
                    onIncomeAmountChanged = { viewModel.updateIncomeAmount(it) },
                    onFrequencyChanged = { viewModel.updateIncomeFrequency(it) },
                    onNextPaydayChanged = { viewModel.updateNextPayday(it) },
                    isValid = uiState.isChapter2Valid,
                    onNext = { viewModel.goToNextChapter() }
                )
                3 -> Chapter3Bills(
                    bills = uiState.bills,
                    onAddBill = { viewModel.addBill(it) },
                    onRemoveBill = { viewModel.removeBill(it) },
                    isValid = uiState.isChapter3Valid,
                    onNext = { viewModel.goToNextChapter() }
                )
                4 -> Chapter4Savings(
                    savingsCents = uiState.savingsCents,
                    onSavingsChanged = { viewModel.updateSavings(it) },
                    isValid = uiState.isChapter4Valid,
                    onNext = { viewModel.goToNextChapter() }
                )
                5 -> Chapter5Budgets(
                    foodBudgetCents = uiState.foodBudgetCents,
                    wantsBudgetCents = uiState.wantsBudgetCents,
                    onFoodBudgetChanged = { viewModel.updateFoodBudget(it) },
                    onWantsBudgetChanged = { viewModel.updateWantsBudget(it) },
                    isValid = uiState.isChapter5Valid,
                    onNext = { viewModel.goToNextChapter() }
                )
                6 -> Chapter6Review(
                    cashOnHandCents = uiState.cashOnHandCents,
                    incomeName = uiState.incomeName,
                    incomeAmountCents = uiState.incomeAmountCents,
                    incomeFrequency = uiState.incomeFrequency,
                    nextPaydayDate = uiState.nextPaydayDate,
                    bills = uiState.bills,
                    savingsCents = uiState.savingsCents,
                    foodBudgetCents = uiState.foodBudgetCents,
                    wantsBudgetCents = uiState.wantsBudgetCents,
                    onActivate = { viewModel.activateSetup() },
                    isActivating = uiState.isActivating
                )
            }
        }
    }
}

@Composable
private fun SetupProgressIndicator(
    currentChapter: Int,
    totalChapters: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..totalChapters) {
            val isCompleted = i < currentChapter
            val isCurrent = i == currentChapter

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> CyanAccent
                            isCurrent -> CyanAccent.copy(alpha = 0.3f)
                            else -> PanelBackground
                        }
                    )
                    .border(
                        width = if (isCurrent) 2.dp else 0.dp,
                        color = if (isCurrent) CyanAccent else Color.Transparent,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = BackgroundDark,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(
                        text = i.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = when {
                            isCurrent -> CyanAccent
                            else -> TextMuted
                        }
                    )
                }
            }

            if (i < totalChapters) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .padding(horizontal = 4.dp)
                        .background(
                            if (i < currentChapter) {
                                CyanAccent
                            } else {
                                PanelBackground
                            }
                        )
                )
            }
        }
    }
}

// Chapter 1: Cash on Hand
@Composable
private fun Chapter1CashOnHand(
    cashCents: Long?,
    onCashChanged: (Long?) -> Unit,
    isValid: Boolean,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Let's start with your cash",
            style = MaterialTheme.typography.headlineSmall,
            color = TextBright,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "How much cash do you have available right now? Include checking accounts and physical cash.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextMuted
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PanelBackground)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                MoneyInputField(
                    value = cashCents,
                    onValueChanged = onCashChanged,
                    label = "Cash on Hand",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            enabled = isValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("chapter1_continue_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = CyanAccent,
                disabledContainerColor = PanelBackground,
                disabledContentColor = TextMuted
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Continue",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

// Chapter 2: Income Schedule
@Composable
private fun Chapter2IncomeSchedule(
    incomeName: String,
    incomeAmountCents: Long?,
    incomeFrequency: String,
    nextPaydayDate: String,
    onIncomeNameChanged: (String) -> Unit,
    onIncomeAmountChanged: (Long?) -> Unit,
    onFrequencyChanged: (String) -> Unit,
    onNextPaydayChanged: (String) -> Unit,
    isValid: Boolean,
    onNext: () -> Unit
) {
    val frequencyOptions = listOf(
        "weekly" to "Weekly",
        "biweekly" to "Bi-weekly",
        "twice_monthly" to "Twice/Month",
        "monthly" to "Monthly",
        "one_time" to "One-time"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Set up your income",
            style = MaterialTheme.typography.headlineSmall,
            color = TextBright,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PanelBackground)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = incomeName,
                    onValueChange = onIncomeNameChanged,
                    label = { Text("Income Name (e.g., Salary, Freelance)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanAccent,
                        focusedLabelColor = CyanAccent
                    )
                )

                MoneyInputField(
                    value = incomeAmountCents,
                    onValueChanged = onIncomeAmountChanged,
                    label = "Income Amount",
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "How often do you get paid?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    frequencyOptions.forEachIndexed { index, (value, label) ->
                        SegmentedButton(
                            selected = incomeFrequency == value,
                            onClick = { onFrequencyChanged(value) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = frequencyOptions.size
                            ),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = CyanAccent,
                                activeContentColor = BackgroundDark,
                                inactiveContainerColor = PanelBackground,
                                inactiveContentColor = TextMuted
                            )
                        ) {
                            Text(label, fontSize = 12.sp)
                        }
                    }
                }

                OutlinedTextField(
                    value = nextPaydayDate,
                    onValueChange = onNextPaydayChanged,
                    label = { Text("Next Payday (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanAccent,
                        focusedLabelColor = CyanAccent
                    )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            enabled = isValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("chapter2_continue_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = CyanAccent,
                disabledContainerColor = PanelBackground,
                disabledContentColor = TextMuted
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Continue",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

// Chapter 3: Bills
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Chapter3Bills(
    bills: List<SetupBillDraft>,
    onAddBill: (SetupBillDraft) -> Unit,
    onRemoveBill: (SetupBillDraft) -> Unit,
    isValid: Boolean,
    onNext: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add your bills",
            style = MaterialTheme.typography.headlineSmall,
            color = TextBright,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Add at least one monthly bill. Protected bills have money set aside for payment.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextMuted
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PanelBackground)
        ) {
            if (bills.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "📄",
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "No bills added yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextMuted
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(bills) { bill ->
                        BillDraftCard(
                            bill = bill,
                            onRemove = { onRemoveBill(bill) }
                        )
                    }
                }
            }
        }

        OutlinedButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("add_bill_button"),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = CyanAccent
            ),
            border = BorderStroke(1.dp, CyanAccent)
        ) {
            Text("+ Add Bill", fontWeight = FontWeight.Medium)
        }

        Button(
            onClick = onNext,
            enabled = isValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("chapter3_continue_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = CyanAccent,
                disabledContainerColor = PanelBackground,
                disabledContentColor = TextMuted
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Continue",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }

    if (showAddDialog) {
        AddBillDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { bill ->
                onAddBill(bill)
                showAddDialog = false
            }
        )
    }
}

// Chapter 4: Savings
@Composable
private fun Chapter4Savings(
    savingsCents: Long?,
    onSavingsChanged: (Long?) -> Unit,
    isValid: Boolean,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Your savings",
            style = MaterialTheme.typography.headlineSmall,
            color = TextBright,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "How much do you currently have saved? This is separate from your daily spending money.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextMuted
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PanelBackground)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                MoneyInputField(
                    value = savingsCents,
                    onValueChanged = onSavingsChanged,
                    label = "Current Savings",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            enabled = isValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("chapter4_continue_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = CyanAccent,
                disabledContainerColor = PanelBackground,
                disabledContentColor = TextMuted
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Continue",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

// Chapter 5: Budgets
@Composable
private fun Chapter5Budgets(
    foodBudgetCents: Long?,
    wantsBudgetCents: Long?,
    onFoodBudgetChanged: (Long?) -> Unit,
    onWantsBudgetChanged: (Long?) -> Unit,
    isValid: Boolean,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Monthly budgets",
            style = MaterialTheme.typography.headlineSmall,
            color = TextBright,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Set spending limits for Food & Essentials and Wants & Extras.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextMuted
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PanelBackground)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MoneyInputField(
                    value = foodBudgetCents,
                    onValueChanged = onFoodBudgetChanged,
                    label = "🍽️ Food & Essentials",
                    modifier = Modifier.fillMaxWidth()
                )

                MoneyInputField(
                    value = wantsBudgetCents,
                    onValueChanged = onWantsBudgetChanged,
                    label = "🎮 Wants & Extras",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            enabled = isValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("chapter5_continue_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = CyanAccent,
                disabledContainerColor = PanelBackground,
                disabledContentColor = TextMuted
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Continue",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

// Chapter 6: Review & Activate
@Composable
private fun Chapter6Review(
    cashOnHandCents: Long?,
    incomeName: String,
    incomeAmountCents: Long?,
    incomeFrequency: String,
    nextPaydayDate: String,
    bills: List<SetupBillDraft>,
    savingsCents: Long?,
    foodBudgetCents: Long?,
    wantsBudgetCents: Long?,
    onActivate: () -> Unit,
    isActivating: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Review your setup",
            style = MaterialTheme.typography.headlineSmall,
            color = TextBright,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Everything look good? Activate BudgetShield to start tracking your safe-to-spend amount.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextMuted
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PanelBackground)
        ) {
            LazyColumn(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ReviewSectionTitle("💰 Cash & Income")
                    ReviewRow("Cash on Hand", formatCents(cashOnHandCents ?: 0))
                    ReviewRow("Income", incomeName)
                    ReviewRow("Amount", formatCents(incomeAmountCents ?: 0))
                    ReviewRow("Frequency", incomeFrequency.replace("_", " "))
                    ReviewRow("Next Payday", nextPaydayDate)
                }

                item {
                    ReviewSectionTitle("📄 Bills")
                    if (bills.isEmpty()) {
                        ReviewRow("No bills", "")
                    } else {
                        bills.forEach { bill ->
                            ReviewRow(bill.name, formatCents(bill.amountCents))
                        }
                    }
                }

                item {
                    ReviewSectionTitle("🎯 Savings & Budgets")
                    ReviewRow("Current Savings", formatCents(savingsCents ?: 0))
                    ReviewRow("Food Budget", formatCents(foodBudgetCents ?: 0))
                    ReviewRow("Wants Budget", formatCents(wantsBudgetCents ?: 0))
                }
            }
        }

        Button(
            onClick = onActivate,
            enabled = !isActivating,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("activate_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = CyanAccent,
                disabledContainerColor = PanelBackground
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isActivating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = TextBright
                )
            } else {
                Text(
                    "🛡️ Activate BudgetShield",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ReviewSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = CyanAccent,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun ReviewRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextBright,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Money input field that handles dollar-to-cents conversion.
 */
@Composable
private fun MoneyInputField(
    value: Long?,
    onValueChanged: (Long?) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var textValue by remember(value) {
        mutableStateOf(value?.let { cents ->
            val dollars = cents / 100
            val remainder = kotlin.math.abs(cents % 100)
            String.format("%d.%02d", dollars, remainder)
        } ?: "")
    }

    OutlinedTextField(
        value = textValue,
        onValueChange = { newText ->
            // Only allow numbers and decimal point
            val filtered = newText.filter { it.isDigit() || it == '.' }
            textValue = filtered

            // Parse to cents
            val parsed = try {
                if (filtered.contains(".")) {
                    val parts = filtered.split(".")
                    val dollars = parts[0].toLongOrNull() ?: 0
                    val cents = parts.getOrNull(1)?.take(2)?.padEnd(2, '0')?.toLongOrNull() ?: 0
                    dollars * 100 + cents
                } else {
                    (filtered.toLongOrNull() ?: 0) * 100
                }
            } catch (e: Exception) {
                null
            }
            onValueChanged(parsed)
        },
        label = { Text(label) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Next
        ),
        prefix = { Text("$", color = TextMuted) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyanAccent,
            focusedLabelColor = CyanAccent,
            unfocusedBorderColor = PanelBorder,
            unfocusedLabelColor = TextMuted
        )
    )
}

private fun formatCents(cents: Long): String {
    val dollars = cents / 100
    val remainder = kotlin.math.abs(cents % 100)
    return String.format("$%d.%02d", dollars, remainder)
}
