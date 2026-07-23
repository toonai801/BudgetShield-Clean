package com.toonai.budgetshield.ui.screens

import com.toonai.budgetshield.ui.LocalBillRepository
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toonai.budgetshield.ui.viewmodel.BillEntryViewModel
import com.toonai.budgetshield.util.DateParser
import com.toonai.budgetshield.util.MoneyParser
import kotlinx.coroutines.launch

import com.toonai.budgetshield.theme.BackgroundDark
import com.toonai.budgetshield.theme.PanelDark
import com.toonai.budgetshield.theme.PanelBorder
import com.toonai.budgetshield.theme.CyanAccent
import com.toonai.budgetshield.theme.GreenAccent
import com.toonai.budgetshield.theme.GoldAccent
import com.toonai.budgetshield.theme.BlueAccent
import com.toonai.budgetshield.theme.TextPrimary
import com.toonai.budgetshield.theme.TextMuted
import com.toonai.budgetshield.theme.DangerDot


@Composable
fun BillEntryScreen(
    viewModel: BillEntryViewModel = viewModel(factory = BillEntryViewModel.Factory(LocalBillRepository.current)),
    onNavigateToTreasure: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSetupQuest: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var isProtected by remember { mutableStateOf(true) }
    var selectedIcon by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("bill_entry_screen"),
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
                // Header
                HeaderSection()

                // Error message
                if (errorMessage != null) {
                    ErrorBanner(message = errorMessage!!)
                }

                // Form Card
                FormCard(
                    name = name,
                    onNameChange = {
                        name = it
                        // Auto-select icon based on name
                        selectedIcon = when {
                            it.contains("rent", ignoreCase = true) -> "🏠"
                            it.contains("electric", ignoreCase = true) ||
                            it.contains("gas", ignoreCase = true) ||
                            it.contains("water", ignoreCase = true) ||
                            it.contains("utility", ignoreCase = true) -> "⚡"
                            it.contains("internet", ignoreCase = true) ||
                            it.contains("wifi", ignoreCase = true) ||
                            it.contains("cable", ignoreCase = true) -> "🌐"
                            it.contains("phone", ignoreCase = true) ||
                            it.contains("mobile", ignoreCase = true) -> "📱"
                            it.contains("insurance", ignoreCase = true) -> "🛡️"
                            it.contains("car", ignoreCase = true) ||
                            it.contains("auto", ignoreCase = true) -> "🚗"
                            it.contains("grocery", ignoreCase = true) ||
                            it.contains("food", ignoreCase = true) -> "🛒"
                            it.contains("subscription", ignoreCase = true) ||
                            it.contains("netflix", ignoreCase = true) ||
                            it.contains("spotify", ignoreCase = true) -> "📺"
                            else -> "📄"
                        }
                    },
                    amount = amount,
                    onAmountChange = {
                        // Only allow valid decimal input patterns
                        if (MoneyParser.isValidInputPattern(it) || it.isEmpty()) {
                            amount = it
                        }
                    },
                    dueDate = dueDate,
                    onDueDateChange = { dueDate = it }
                )

                // Icon selector
                IconSelector(
                    selectedIcon = selectedIcon,
                    onIconSelected = { selectedIcon = it }
                )

                // Protection Toggle
                ProtectionToggleCard(
                    isProtected = isProtected,
                    onToggle = { isProtected = it }
                )

                // Action Buttons
                ActionButtons(
                    isEnabled = name.isNotBlank() && amount.isNotBlank() && dueDate.isNotBlank() && !isSaving,
                    isSaving = isSaving,
                    onSave = {
                        scope.launch {
                            isSaving = true
                            errorMessage = null

                            // Parse amount using exact money parser
                            val amountResult = MoneyParser.parseToCents(amount)
                            if (amountResult.isFailure) {
                                errorMessage = amountResult.exceptionOrNull()?.message ?: "Invalid amount"
                                isSaving = false
                                return@launch
                            }

                            val amountCents = amountResult.getOrNull()!!

                            // Validate amount is positive
                            if (amountCents <= 0) {
                                errorMessage = "Amount must be greater than $0.00"
                                isSaving = false
                                return@launch
                            }

                            // Parse date using strict date parser
                            val dateResult = DateParser.parseToIsoDate(dueDate)
                            if (dateResult.isFailure) {
                                errorMessage = dateResult.exceptionOrNull()?.message ?: "Invalid date"
                                isSaving = false
                                return@launch
                            }

                            val formattedDueDate = dateResult.getOrNull()!!
                            val icon = selectedIcon.ifEmpty { "📄" }

                            // Create bill and check result
                            val createResult = viewModel.createBill(
                                name = name.trim(),
                                icon = icon,
                                amountCents = amountCents,
                                dueDate = formattedDueDate,
                                isProtected = isProtected
                            )

                            // Only navigate on success
                            createResult.fold(
                                onSuccess = { billId ->
                                    if (billId > 0) {
                                        // Success: navigate to Treasure
                                        onNavigateToTreasure()
                                    } else {
                                        errorMessage = "Failed to save bill: invalid ID"
                                        isSaving = false
                                    }
                                },
                                onFailure = { error ->
                                    errorMessage = "Failed to save bill: ${error.message}"
                                    isSaving = false
                                }
                            )
                        }
                    },
                    onBackHome = onNavigateToHome,
                    onBackSetup = onNavigateToSetupQuest
                )
            }
        }
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DangerDot.copy(alpha = 0.15f)
        ),
        border = BorderStroke(1.dp, DangerDot.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("⚠️", fontSize = 20.sp)
            Text(
                text = message,
                color = DangerDot,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun HeaderSection() {
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
                    .background(BlueAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📄",
                    fontSize = 20.sp
                )
            }

            Column {
                Text(
                    text = "Add Bill",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Protect your expenses",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun IconSelector(
    selectedIcon: String,
    onIconSelected: (String) -> Unit
) {
    val icons = listOf("🏠", "⚡", "🌐", "📱", "🛡️", "🚗", "🛒", "📺", "📄")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Choose Icon",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                icons.forEach { icon ->
                    val isSelected = icon == selectedIcon
                    Card(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) CyanAccent.copy(alpha = 0.3f)
                                            else Color(0xFF0D1B26)
                        ),
                        border = if (isSelected) BorderStroke(2.dp, CyanAccent)
                                else BorderStroke(1.dp, PanelBorder),
                        onClick = { onIconSelected(icon) }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = icon, fontSize = 24.sp)
                        }
                    }
                }
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
    dueDate: String,
    onDueDateChange: (String) -> Unit
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
            // Bill Name
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Bill Name *",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                TextField(
                    value = name,
                    onValueChange = onNameChange,
                    placeholder = { Text("e.g. Rent, Utilities") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0D1B26),
                        unfocusedContainerColor = Color(0xFF0D1B26),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedIndicatorColor = CyanAccent,
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
                    text = "Amount Due *",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                TextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    placeholder = { Text("0.00") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0D1B26),
                        unfocusedContainerColor = Color(0xFF0D1B26),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedIndicatorColor = CyanAccent,
                        unfocusedIndicatorColor = PanelBorder,
                        focusedPlaceholderColor = TextMuted,
                        unfocusedPlaceholderColor = TextMuted
                    ),
                    singleLine = true,
                    leadingIcon = {
                        Text(
                            text = "$",
                            color = CyanAccent,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }

            // Due Date
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Due Date *",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                TextField(
                    value = dueDate,
                    onValueChange = onDueDateChange,
                    placeholder = { Text("MM/DD/YYYY") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0D1B26),
                        unfocusedContainerColor = Color(0xFF0D1B26),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedIndicatorColor = CyanAccent,
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
private fun ProtectionToggleCard(
    isProtected: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isProtected) CyanAccent.copy(alpha = 0.1f) else PanelDark
        ),
        border = BorderStroke(1.dp, if (isProtected) CyanAccent.copy(alpha = 0.3f) else PanelBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isProtected) CyanAccent.copy(alpha = 0.2f) else Color(0xFF0D1B26)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🛡️",
                        fontSize = 20.sp
                    )
                }

                Column {
                    Text(
                        text = "Protect this bill",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Set money aside for this obligation",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            // Toggle
            Card(
                modifier = Modifier.size(48.dp, 28.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isProtected) CyanAccent else Color(0xFF14364A)
                ),
                onClick = { onToggle(!isProtected) }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .align(if (isProtected) Alignment.CenterEnd else Alignment.CenterStart)
                            .padding(2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(
    isEnabled: Boolean,
    isSaving: Boolean,
    onSave: () -> Unit,
    onBackHome: () -> Unit,
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
                containerColor = if (isEnabled && !isSaving) CyanAccent else CyanAccent.copy(alpha = 0.3f)
            ),
            onClick = { if (isEnabled && !isSaving) onSave() }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSaving) "💾 Saving..." else "💾 Save Bill",
                    color = BackgroundDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
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
                onClick = onBackHome
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "← Cancel",
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }
            }

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
