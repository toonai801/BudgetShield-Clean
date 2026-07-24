package com.toonai.budgetshield.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.toonai.budgetshield.BuildConfig
import com.toonai.budgetshield.data.model.BudgetCategory
import com.toonai.budgetshield.theme.*
import com.toonai.budgetshield.ui.viewmodel.SettingsViewModel
import com.toonai.budgetshield.util.MoneyParser

@Composable
fun SettingsScreen(
    onNavigateToSetupQuest: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToTreasure: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    onNavigateToGoals: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToTransactionHistory: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadBudgets()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_scroll_content"),
        color = BackgroundDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
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

                // Profile Card
                ProfileCard()

                // Budget Setup Section
                BudgetSetupSection(
                    budgets = uiState.budgets,
                    onUpdateBudget = { categoryId, amount ->
                        viewModel.updateBudgetAmount(categoryId, amount)
                    },
                    onNavigateToTransactionHistory = onNavigateToTransactionHistory
                )

                // Preferences
                PreferencesSection()

                // Notifications
                NotificationsSection()

                // Data Management
                DataManagementSection()

                // Danger Zone
                DangerZoneSection(onRestartSetup = onNavigateToSetupQuest)

                // Bottom spacer
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun BudgetSetupSection(
    budgets: List<BudgetCategory>,
    onUpdateBudget: (Long, Long) -> Unit,
    onNavigateToTransactionHistory: () -> Unit
) {
    var foodAmount by remember { mutableStateOf("") }
    var wantsAmount by remember { mutableStateOf("") }
    var otherAmount by remember { mutableStateOf("") }

    // Initialize from existing budgets
    LaunchedEffect(budgets) {
        budgets.find { it.name == "Food" }?.let {
            foodAmount = MoneyParser.centsToDollarsString(it.plannedAmountCents)
        }
        budgets.find { it.name == "Wants" }?.let {
            wantsAmount = MoneyParser.centsToDollarsString(it.plannedAmountCents)
        }
    }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monthly Budgets",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToTransactionHistory) {
                    Text(
                        text = "View History →",
                        color = CyanAccent,
                        fontSize = 12.sp
                    )
                }
            }

            // Food Budget
            BudgetInputRow(
                icon = "🍽️",
                label = "Food Budget",
                value = foodAmount,
                onValueChange = { foodAmount = it },
                onSave = {
                    val cents = MoneyParser.parseToCents(foodAmount).getOrNull() ?: 0L
                    budgets.find { it.name == "Food" }?.let { onUpdateBudget(it.id, cents) }
                }
            )

            // Wants Budget
            BudgetInputRow(
                icon = "🎁",
                label = "Wants Budget",
                value = wantsAmount,
                onValueChange = { wantsAmount = it },
                onSave = {
                    val cents = MoneyParser.parseToCents(wantsAmount).getOrNull() ?: 0L
                    budgets.find { it.name == "Wants" }?.let { onUpdateBudget(it.id, cents) }
                }
            )

            // Summary
            val totalBudgeted = budgets.sumOf { it.plannedAmountCents }
            if (totalBudgeted > 0) {
                Text(
                    text = "Total Budgeted: ${MoneyParser.formatCents(totalBudgeted)}",
                    color = CyanAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun BudgetInputRow(
    icon: String,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(CyanAccent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 20.sp)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            OutlinedTextField(
                value = value,
                onValueChange = { newValue ->
                    val cleaned = newValue.replace(Regex("[^0-9.]"), "")
                    onValueChange(cleaned)
                },
                prefix = { Text("$") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    color = TextPrimary,
                    fontSize = 16.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = PanelBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        }

        TextButton(
            onClick = {
                onSave()
                isEditing = false
            },
            colors = ButtonDefaults.textButtonColors(contentColor = GreenAccent)
        ) {
            Text("Save", fontWeight = FontWeight.Bold)
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
                    .background(CyanAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚙️",
                    fontSize = 20.sp
                )
            }

            Column {
                Text(
                    text = "Settings",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Beta build ${BuildConfig.VERSION_NAME}",
                    color = CyanAccent,
                    fontSize = 11.sp,
                    modifier = Modifier.testTag("settings_beta_version_marker")
                )
            }
        }
    }
}

@Composable
private fun ProfileCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(CyanAccent, BlueAccent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👤",
                        fontSize = 28.sp
                    )
                }

                Column {
                    Text(
                        text = "Budget Buddy",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Level 1 • Novice Shield",
                        color = CyanAccent,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Member since July 2025",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Text(
                text = "›",
                color = TextMuted,
                fontSize = 24.sp
            )
        }
    }
}

@Composable
private fun PreferencesSection() {
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
                text = "Preferences",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            PreferenceItem(
                icon = "💵",
                title = "Currency",
                subtitle = "USD ($)",
                trailing = "›"
            )

            PreferenceItem(
                icon = "🌍",
                title = "Region",
                subtitle = "United States",
                trailing = "›"
            )

            PreferenceItem(
                icon = "🎨",
                title = "Theme",
                subtitle = "Dark",
                trailing = "›"
            )

            PreferenceItem(
                icon = "🔢",
                title = "Number Format",
                subtitle = "1,234.56",
                trailing = "›"
            )
        }
    }
}

@Composable
private fun NotificationsSection() {
    var billReminders by remember { mutableStateOf(true) }
    var weeklyReport by remember { mutableStateOf(true) }
    var goalMilestones by remember { mutableStateOf(true) }
    var streakAlerts by remember { mutableStateOf(true) }

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
                text = "Notifications",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            ToggleItem(
                icon = "📅",
                title = "Bill Reminders",
                subtitle = "3 days before due",
                checked = billReminders,
                onCheckedChange = { billReminders = it }
            )

            ToggleItem(
                icon = "📊",
                title = "Weekly Reports",
                subtitle = "Every Sunday",
                checked = weeklyReport,
                onCheckedChange = { weeklyReport = it }
            )

            ToggleItem(
                icon = "🎯",
                title = "Goal Milestones",
                subtitle = "25%, 50%, 75%, 100%",
                checked = goalMilestones,
                onCheckedChange = { goalMilestones = it }
            )

            ToggleItem(
                icon = "🔥",
                title = "Streak Alerts",
                subtitle = "Daily reminders",
                checked = streakAlerts,
                onCheckedChange = { streakAlerts = it }
            )
        }
    }
}

@Composable
private fun DataManagementSection() {
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
                text = "Data & Privacy",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            ActionItem(
                icon = "📤",
                title = "Export Data",
                subtitle = "Download your data as JSON",
                color = GreenAccent
            )

            ActionItem(
                icon = "📥",
                title = "Import Data",
                subtitle = "Restore from backup",
                color = BlueAccent
            )

            ActionItem(
                icon = "🔒",
                title = "Privacy Settings",
                subtitle = "Manage data sharing",
                color = PurpleAccent
            )

            ActionItem(
                icon = "🗑️",
                title = "Clear Cache",
                subtitle = "Free up space",
                color = TextMuted
            )
        }
    }
}

@Composable
private fun DangerZoneSection(onRestartSetup: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A0A0A)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "⚠️",
                    fontSize = 16.sp
                )
                Text(
                    text = "Danger Zone",
                    color = DangerDot,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "These actions cannot be undone. Be careful!",
                color = TextMuted,
                fontSize = 12.sp
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_danger_zone_restart"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DangerDot.copy(alpha = 0.15f)
                ),
                onClick = onRestartSetup
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(DangerDot.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🔄",
                                fontSize = 18.sp
                            )
                        }

                        Column {
                            Text(
                                text = "Restart Setup Quest",
                                color = DangerDot,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Reset and start over",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = "›",
                        color = DangerDot,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PreferenceItem(
    icon: String,
    title: String,
    subtitle: String,
    trailing: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BlueAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 18.sp
                )
            }

            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        Text(
            text = trailing,
            color = TextMuted,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun ToggleItem(
    icon: String,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CyanAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 18.sp
                )
            }

            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyanAccent,
                checkedTrackColor = CyanAccent.copy(alpha = 0.5f),
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = PanelBorder
            )
        )
    }
}

@Composable
private fun ActionItem(
    icon: String,
    title: String,
    subtitle: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 18.sp
                )
            }

            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        Text(
            text = "›",
            color = TextMuted,
            fontSize = 20.sp
        )
    }
}
