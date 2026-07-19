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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toonai.budgetshield.ui.viewmodel.BillEntryViewModel
import kotlinx.coroutines.launch

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
private val ErrorColor = Color(0xFFFF553D)

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
                        // Only allow valid decimal input
                        if (it.matches(Regex("^\\d*\\.?\\d{0,2}$")) || it.isEmpty()) {
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
                    isEnabled = name.isNotBlank() && amount.isNotBlank() && amount.toDoubleOrNull() != null && dueDate.isNotBlank(),
                    isSaving = isSaving,
                    onSave = {
                        scope.launch {
                            isSaving = true
                            errorMessage = null
                            
                            try {
                                val amountCents = (amount.toDouble() * 100).toLong()
                                
                                // Validate
                                if (amountCents <= 0) {
                                    errorMessage = "Amount must be greater than $0.00"
                                    isSaving = false
                                    return@launch
                                }
                                
                                // Parse and format due date
                                val formattedDueDate = parseDueDate(dueDate)
                                if (formattedDueDate == null) {
                                    errorMessage = "Invalid date format. Use MM/DD/YYYY or YYYY-MM-DD"
                                    isSaving = false
                                    return@launch
                                }
                                
                                val icon = selectedIcon.ifEmpty { "📄" }
                                
                                viewModel.createBill(
                                    name = name.trim(),
                                    icon = icon,
                                    amountCents = amountCents,
                                    dueDate = formattedDueDate,
                                    isProtected = isProtected
                                )
                                
                                // Navigate back on success
                                onNavigateToTreasure()
                            } catch (e: Exception) {
                                errorMessage = "Failed to save bill: ${e.message}"
                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    onBackHome = onNavigateToHome,
                    onBackSetup = onNavigateToSetupQuest
                )
            }
        }
    }
}

private fun parseDueDate(input: String): String? {
    return try {
        when {
            // Already in YYYY-MM-DD format
            input.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$")) -> input
            // MM/DD/YYYY format
            input.matches(Regex("^\\d{1,2}/\\d{1,2}/\\d{4}$")) -> {
                val parts = input.split("/")
                val month = parts[0].padStart(2, '0')
                val day = parts[1].padStart(2, '0')
                val year = parts[2]
                "$year-$month-$day"
            }
            // M-D-YY format
            input.matches(Regex("^\\d{1,2}-\\d{1,2}-\\d{2,4}$")) -> {
                val parts = input.split("-")
                val month = parts[0].padStart(2, '0')
                val day = parts[1].padStart(2, '0')
                val year = if (parts[2].length == 2) "20${parts[2]}" else parts[2]
                "$year-$month-$day"
            }
            else -> null
        }
    } catch (e: Exception) {
        null
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = ErrorColor.copy(alpha = 0.15f)
        ),
        border = BorderStroke(1.dp, ErrorColor.copy(alpha = 0.5f))
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
                color = ErrorColor,
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
                containerColor = if (isEnabled) CyanAccent else CyanAccent.copy(alpha = 0.3f)
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
