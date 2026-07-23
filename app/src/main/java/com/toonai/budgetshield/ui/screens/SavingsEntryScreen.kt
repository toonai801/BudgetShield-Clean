package com.toonai.budgetshield.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.toonai.budgetshield.ui.viewmodel.SavingsEntryViewModel

// Premium gamified dark theme colors (matching Home)
private val BackgroundDark = Color(0xFF02070D)
private val PanelDark = Color(0xFF06121D)
private val PanelBorder = Color(0xFF14364A)
private val CyanAccent = Color(0xFF17E8F2)
private val GreenAccent = Color(0xFF2FE6A7)
private val GoldAccent = Color(0xFFFFC545)
private val BlueAccent = Color(0xFF1678B9)
private val PurpleAccent = Color(0xFF9D4EDD)
private val TextPrimary = Color(0xFFF4F7FB)
private val TextMuted = Color(0xFFA6B1BF)

@Composable
fun SavingsEntryScreen(
    viewModel: SavingsEntryViewModel,
    onNavigateToGoals: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedGoalId by remember { mutableStateOf<Long?>(null) }

    // Handle success navigation
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onNavigateToGoals()
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
                // Header
                HeaderSection()

                // Streak Banner
                StreakBanner(streakDays = uiState.currentStreak)

                // Error message
                if (uiState.errorMessage != null) {
                    ErrorBanner(message = uiState.errorMessage!!) {
                        viewModel.clearError()
                    }
                }

                // XP Preview (shows when about to save)
                if (uiState.xpEarned > 0) {
                    XPPreviewCard(xpAmount = uiState.xpEarned)
                }

                // Form Card
                FormCard(
                    amount = amount,
                    onAmountChange = { amount = it },
                    note = note,
                    onNoteChange = { note = it }
                )

                // Goal Selection
                GoalSelectionSection(
                    goals = uiState.goals,
                    selectedGoalId = selectedGoalId,
                    onGoalSelected = { selectedGoalId = it }
                )

                // Action Buttons
                ActionButtons(
                    isLoading = uiState.isLoading,
                    onSave = {
                        val amountCents = amount.replace("[^0-9.]", "").toDoubleOrNull()?.let {
                            (it * 100).toLong()
                        } ?: 0L
                        viewModel.saveMoney(
                            amountCents = amountCents,
                            note = note.takeIf { it.isNotBlank() },
                            goalId = selectedGoalId
                        )
                    },
                    onBackHome = onNavigateToHome
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
            containerColor = Color(0xFFFF553D).copy(alpha = 0.15f)
        ),
        border = BorderStroke(1.dp, Color(0xFFFF553D).copy(alpha = 0.5f))
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
                color = Color(0xFFFF553D),
                fontSize = 14.sp
            )
            TextButton(onClick = onDismiss) {
                Text("Dismiss", color = Color(0xFFFF553D))
            }
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
                    .background(GoldAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏦",
                    fontSize = 20.sp
                )
            }

            Column {
                Text(
                    text = "Save Money",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Build your financial future",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun StreakBanner(streakDays: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1200)
        ),
        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f))
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
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(GoldAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔥",
                        fontSize = 24.sp
                    )
                }

                Column {
                    Text(
                        text = "$streakDays Day Streak",
                        color = GoldAccent,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Keep it going! Save today.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Text(
                text = "+25 XP",
                color = GoldAccent,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FormCard(
    amount: String,
    onAmountChange: (String) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit
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
            // Amount
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Amount to Save",
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
                        focusedIndicatorColor = GoldAccent,
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

            // Quick Amount Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickAmountButton("$10", Modifier.weight(1f)) { onAmountChange("10") }
                QuickAmountButton("$25", Modifier.weight(1f)) { onAmountChange("25") }
                QuickAmountButton("$50", Modifier.weight(1f)) { onAmountChange("50") }
                QuickAmountButton("$100", Modifier.weight(1f)) { onAmountChange("100") }
            }

            // Note
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Note (Optional)",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                TextField(
                    value = note,
                    onValueChange = onNoteChange,
                    placeholder = { Text("What are you saving for?") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0D1B26),
                        unfocusedContainerColor = Color(0xFF0D1B26),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedIndicatorColor = GoldAccent,
                        unfocusedIndicatorColor = PanelBorder,
                        focusedPlaceholderColor = TextMuted,
                        unfocusedPlaceholderColor = TextMuted
                    ),
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun QuickAmountButton(
    amount: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0D1B26)
        ),
        border = BorderStroke(1.dp, PanelBorder),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = amount,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun GoalSelectionSection(
    goals: List<com.toonai.budgetshield.data.model.SavingsGoal>,
    selectedGoalId: Long?,
    onGoalSelected: (Long?) -> Unit
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Save Towards Goal",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            if (goals.isEmpty()) {
                Text(
                    text = "No savings goals yet. Create one in the Goals tab!",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            } else {
                // General Savings option
                GoalOption(
                    icon = "💰",
                    name = "General Savings",
                    current = "",
                    target = "",
                    selected = selectedGoalId == null,
                    onClick = { onGoalSelected(null) }
                )

                // Individual goals
                goals.forEach { goal ->
                    GoalOption(
                        icon = goal.icon,
                        name = goal.name,
                        current = "${goal.currentAmountCents / 100}.${goal.currentAmountCents % 100}",
                        target = "${goal.targetAmountCents / 100}.${goal.targetAmountCents % 100}",
                        selected = selectedGoalId == goal.id,
                        onClick = { onGoalSelected(goal.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalOption(
    icon: String,
    name: String,
    current: String,
    target: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) GoldAccent else PanelBorder
    val bgColor = if (selected) GoldAccent.copy(alpha = 0.1f) else Color(0xFF0D1B26)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = bgColor
        ),
        border = BorderStroke(1.dp, borderColor),
        onClick = onClick
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
                        .background(
                            if (selected) GoldAccent.copy(alpha = 0.2f) else PanelBorder.copy(alpha = 0.5f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        fontSize = 20.sp
                    )
                }

                Column {
                    Text(
                        text = name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (target.isNotEmpty()) {
                        Text(
                            text = "$$current of $$target",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            if (selected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(GoldAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = BackgroundDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(PanelBorder)
                )
            }
        }
    }
}

@Composable
private fun XPPreviewCard(xpAmount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = GreenAccent.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, GreenAccent.copy(alpha = 0.3f))
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
                        .background(GreenAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⭐",
                        fontSize = 20.sp
                    )
                }

                Column {
                    Text(
                        text = "XP Preview",
                        color = GreenAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "+$xpAmount XP for saving today",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Text(
                text = "+$xpAmount XP",
                color = GreenAccent,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ActionButtons(
    isLoading: Boolean,
    onSave: () -> Unit,
    onBackHome: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Save Button
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = GoldAccent
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
                        text = "💰 Save Money",
                        color = BackgroundDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Back Button
        Card(
            modifier = Modifier.fillMaxWidth(),
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
                    text = "← Back to Home",
                    color = TextPrimary,
                    fontSize = 14.sp
                )
            }
        }
    }
}
