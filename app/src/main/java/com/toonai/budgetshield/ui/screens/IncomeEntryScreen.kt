package com.toonai.budgetshield.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.toonai.budgetshield.ui.viewmodel.IncomeEntryViewModel
import com.toonai.budgetshield.util.DateParser

// Premium gamified dark theme colors (matching Home)
private val BackgroundDark = Color(0xFF02070D)
private val PanelDark = Color(0xFF06121D)
private val PanelBorder = Color(0xFF14364A)
private val CyanAccent = Color(0xFF17E8F2)
private val GreenAccent = Color(0xFF2FE6A7)
private val GoldAccent = Color(0xFFFFC545)
private val BlueAccent = Color(0xFF1678B9)
private val TextPrimary = Color(0xFFF4F7FB)
private val TextMuted = Color(0xFFA6B1BF)
private val DangerColor = Color(0xFFFF553D)

@Composable
fun IncomeEntryScreen(
    viewModel: IncomeEntryViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToSetupQuest: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var amount by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var payday by remember { mutableStateOf("") }
    var selectedFrequency by remember { mutableStateOf("semimonthly") }

    // Load primary income on first launch
    LaunchedEffect(Unit) {
        viewModel.loadPrimaryIncome()
    }

    // Pre-fill from primary income when loaded
    LaunchedEffect(uiState.primaryIncome) {
        uiState.primaryIncome?.let { income ->
            if (!uiState.isAddingNew) {
                name = income.name
                amount = (income.amountCents / 100.0).toString()
                payday = income.nextPayday
                selectedFrequency = income.frequency
            }
        }
    }

    // Handle success navigation
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onNavigateToHome()
            viewModel.resetSuccess()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header - show Manage Income if primary exists, else Add Income
                HeaderSection(
                    hasPrimaryIncome = uiState.hasPrimaryIncome,
                    isAddingNew = uiState.isAddingNew
                )

                // Toggle: Edit Primary vs Add New
                if (uiState.hasPrimaryIncome) {
                    IncomeModeToggle(
                        isAddingNew = uiState.isAddingNew,
                        onToggle = { viewModel.setAddingNew(!uiState.isAddingNew) },
                        primaryName = uiState.primaryIncome?.name ?: ""
                    )
                }

                // Error message
                if (uiState.errorMessage != null) {
                    ErrorBanner(
                        message = uiState.errorMessage!!,
                        onDismiss = { viewModel.clearError() }
                    )
                }

                // Form Card
                FormCard(
                    name = name,
                    onNameChange = { name = it },
                    amount = amount,
                    onAmountChange = { amount = it },
                    payday = payday,
                    onPaydayChange = { payday = it },
                    selectedFrequency = selectedFrequency,
                    onFrequencyChange = { selectedFrequency = it }
                )

                // Income Type
                IncomeTypeSection()

                // Action Buttons
                ActionButtons(
                    isLoading = uiState.isLoading,
                    isAddingNew = uiState.isAddingNew,
                    hasPrimaryIncome = uiState.hasPrimaryIncome,
                    onSave = {
                        val amountCents = amount.replace("[^0-9.]", "").toDoubleOrNull()?.let {
                            (it * 100).toLong()
                        } ?: 0L
                        if (uiState.hasPrimaryIncome && !uiState.isAddingNew) {
                            // Update existing primary income
                            uiState.primaryIncome?.id?.let { id ->
                                viewModel.updateIncome(
                                    incomeId = id,
                                    name = name,
                                    amountCents = amountCents,
                                    nextPayday = payday.ifBlank { DateParser.today() },
                                    frequency = selectedFrequency
                                )
                            }
                        } else {
                            // Create new income
                            viewModel.saveIncome(
                                name = name,
                                amountCents = amountCents,
                                nextPayday = payday.ifBlank { DateParser.today() },
                                frequency = selectedFrequency
                            )
                        }
                    },
                    onBackSetup = onNavigateToSetupQuest
                )
            }
        }
    }
}

@Composable
private fun ErrorBanner(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DangerColor.copy(alpha = 0.15f)
        ),
        border = BorderStroke(1.dp, DangerColor.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = DangerColor,
                fontSize = 14.sp
            )
            TextButton(onClick = onDismiss) {
                Text("Dismiss", color = DangerColor)
            }
        }
    }
}

@Composable
private fun HeaderSection(
    hasPrimaryIncome: Boolean = false,
    isAddingNew: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GreenAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (hasPrimaryIncome && !isAddingNew) "✏️" else "⬇️",
                    fontSize = 20.sp
                )
            }

            Column {
                Text(
                    text = when {
                        hasPrimaryIncome && !isAddingNew -> "Manage Income"
                        hasPrimaryIncome && isAddingNew -> "Add New Income"
                        else -> "Add Income"
                    },
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = when {
                        hasPrimaryIncome && !isAddingNew -> "Update your primary income"
                        else -> "Track money coming in"
                    },
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun IncomeModeToggle(
    isAddingNew: Boolean,
    onToggle: () -> Unit,
    primaryName: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        ),
        border = BorderStroke(1.dp, PanelBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!isAddingNew) {
                // Showing primary income summary
                Text(
                    text = "Primary Income",
                    color = CyanAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = primaryName.ifBlank { "Income saved during setup" },
                    color = TextPrimary,
                    fontSize = 16.sp
                )
                Text(
                    text = "Tap below to add another income stream",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            
            TextButton(
                onClick = onToggle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isAddingNew) "← Back to Primary Income" else "+ Add New Income Stream",
                    color = if (isAddingNew) TextMuted else CyanAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun FormCard(
    name: String,
    onNameChange: (String) -> Unit,
    amount: String,
    onAmountChange: (String) -> Unit,
    payday: String,
    onPaydayChange: (String) -> Unit,
    selectedFrequency: String,
    onFrequencyChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Income Name
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Income Name",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                TextField(
                    value = name,
                    onValueChange = onNameChange,
                    placeholder = { Text("e.g. Salary, Freelance") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0D1B26),
                        unfocusedContainerColor = Color(0xFF0D1B26),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedIndicatorColor = GreenAccent,
                        unfocusedIndicatorColor = PanelBorder,
                        focusedPlaceholderColor = TextMuted,
                        unfocusedPlaceholderColor = TextMuted
                    ),
                    singleLine = true
                )
            }

            // Amount
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Amount",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                TextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    placeholder = { Text("$0.00") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0D1B26),
                        unfocusedContainerColor = Color(0xFF0D1B26),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedIndicatorColor = GreenAccent,
                        unfocusedIndicatorColor = PanelBorder,
                        focusedPlaceholderColor = TextMuted,
                        unfocusedPlaceholderColor = TextMuted
                    ),
                    singleLine = true,
                    leadingIcon = {
                        Text(
                            text = "$",
                            color = TextMuted,
                            fontSize = 16.sp
                        )
                    }
                )
            }

            // Payday Schedule
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Payday Schedule",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaydayChip("15th & 30th", selectedFrequency == "semimonthly") {
                        onFrequencyChange("semimonthly")
                    }
                    PaydayChip("Weekly", selectedFrequency == "weekly") {
                        onFrequencyChange("weekly")
                    }
                    PaydayChip("Biweekly", selectedFrequency == "biweekly") {
                        onFrequencyChange("biweekly")
                    }
                    PaydayChip("Monthly", selectedFrequency == "monthly") {
                        onFrequencyChange("monthly")
                    }
                }
            }

            // Start Date
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Next Payday",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                TextField(
                    value = payday,
                    onValueChange = onPaydayChange,
                    placeholder = { Text("YYYY-MM-DD") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0D1B26),
                        unfocusedContainerColor = Color(0xFF0D1B26),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedIndicatorColor = GreenAccent,
                        unfocusedIndicatorColor = PanelBorder,
                        focusedPlaceholderColor = TextMuted,
                        unfocusedPlaceholderColor = TextMuted
                    ),
                    singleLine = true,
                    leadingIcon = {
                        Text(
                            text = "📅",
                            fontSize = 16.sp
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun PaydayChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (selected) GreenAccent.copy(alpha = 0.2f) else Color(0xFF0D1B26)
    val textColor = if (selected) GreenAccent else TextMuted
    val borderColor = if (selected) GreenAccent else PanelBorder

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = bgColor
        ),
        border = BorderStroke(1.dp, borderColor),
        onClick = onClick
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun IncomeTypeSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Income Type",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IncomeTypeCard(
                    modifier = Modifier.weight(1f),
                    icon = "💼",
                    title = "Salary",
                    subtitle = "Regular income",
                    selected = true
                )

                IncomeTypeCard(
                    modifier = Modifier.weight(1f),
                    icon = "💻",
                    title = "Freelance",
                    subtitle = "Variable income",
                    selected = false
                )

                IncomeTypeCard(
                    modifier = Modifier.weight(1f),
                    icon = "🎁",
                    title = "Other",
                    subtitle = "One-time",
                    selected = false
                )
            }
        }
    }
}

@Composable
private fun IncomeTypeCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    subtitle: String,
    selected: Boolean
) {
    val bgColor = if (selected) GreenAccent.copy(alpha = 0.15f) else Color(0xFF0D1B26)
    val borderColor = if (selected) GreenAccent else PanelBorder

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = bgColor
        ),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
            Text(
                text = title,
                color = if (selected) GreenAccent else TextPrimary,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun ActionButtons(
    isLoading: Boolean,
    isAddingNew: Boolean,
    hasPrimaryIncome: Boolean,
    onSave: () -> Unit,
    onBackSetup: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Save Button
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = GreenAccent
            ),
            enabled = !isLoading,
            onClick = onSave
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = BackgroundDark,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = if (isAddingNew) "💾 Save New Income" else "💾 Update Income",
                        color = BackgroundDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Secondary Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PanelDark
                ),
                onClick = onBackSetup
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "← Setup",
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
