package com.toonai.budgetshield.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.toonai.budgetshield.data.model.BudgetCategory
import com.toonai.budgetshield.theme.BackgroundDark
import com.toonai.budgetshield.theme.CyanAccent
import com.toonai.budgetshield.theme.GreenAccent
import com.toonai.budgetshield.theme.PanelDark
import com.toonai.budgetshield.theme.TextPrimary
import com.toonai.budgetshield.theme.TextMuted
import com.toonai.budgetshield.ui.viewmodel.BudgetsViewModel
import com.toonai.budgetshield.util.MoneyParser

@Composable
fun BudgetsScreen(
    onBack: () -> Unit,
    onLogSpending: () -> Unit,
    viewModel: BudgetsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Load budgets when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadBudgets()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "Budgets",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onLogSpending) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Log Spending",
                        tint = CyanAccent
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // Month display
                Text(
                    text = uiState.currentMonthDisplay,
                    color = TextMuted,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Summary card
                BudgetSummaryCard(
                    totalBudgeted = uiState.totalBudgeted,
                    totalSpent = uiState.totalSpent,
                    totalRemaining = uiState.totalRemaining
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Categories header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Categories",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Button(
                        onClick = onLogSpending,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Spending")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Budget categories list
                if (uiState.budgets.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PanelDark),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📊",
                                fontSize = 48.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "No Budgets Yet",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Set up your monthly budgets in Settings",
                                color = TextMuted,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                } else {
                    uiState.budgets.forEach { budget ->
                        BudgetDetailCard(budget = budget)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun BudgetSummaryCard(
    totalBudgeted: Long,
    totalSpent: Long,
    totalRemaining: Long
) {
    val totalProgress = if (totalBudgeted > 0) {
        (totalSpent * 100 / totalBudgeted).toInt()
    } else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PanelDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Monthly Overview",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    label = "Budgeted",
                    amount = MoneyParser.formatCents(totalBudgeted),
                    color = TextPrimary
                )
                SummaryItem(
                    label = "Spent",
                    amount = MoneyParser.formatCents(totalSpent),
                    color = if (totalSpent > totalBudgeted) MaterialTheme.colorScheme.error else TextPrimary
                )
                SummaryItem(
                    label = "Remaining",
                    amount = MoneyParser.formatCents(totalRemaining),
                    color = if (totalRemaining < 0) MaterialTheme.colorScheme.error else GreenAccent
                )
            }

            if (totalBudgeted > 0) {
                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { totalProgress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = when {
                        totalProgress >= 100 -> MaterialTheme.colorScheme.error
                        totalProgress >= 80 -> MaterialTheme.colorScheme.tertiary
                        else -> CyanAccent
                    },
                    trackColor = CyanAccent.copy(alpha = 0.2f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$totalProgress% used",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = if (totalRemaining < 0) "Over budget!" else "${MoneyParser.formatCents(totalRemaining)} left",
                        color = if (totalRemaining < 0) MaterialTheme.colorScheme.error else TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    amount: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = amount,
            color = color,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = TextMuted,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun BudgetDetailCard(budget: BudgetCategory) {
    val progress = if (budget.plannedAmountCents > 0) {
        (budget.spentAmountCents * 100 / budget.plannedAmountCents).toInt()
    } else 0

    val progressColor = when {
        progress >= 100 -> MaterialTheme.colorScheme.error
        progress >= 80 -> MaterialTheme.colorScheme.tertiary
        else -> CyanAccent
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PanelDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = budget.icon,
                    fontSize = 28.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = budget.name,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${MoneyParser.formatCents(budget.spentAmountCents)}",
                        color = if (budget.isOverBudget) MaterialTheme.colorScheme.error else TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "of ${MoneyParser.formatCents(budget.plannedAmountCents)}",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = progressColor.copy(alpha = 0.2f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$progress% used",
                    color = if (budget.isOverBudget) MaterialTheme.colorScheme.error else TextMuted,
                    fontSize = 12.sp
                )
                Text(
                    text = "${MoneyParser.formatCents(budget.remainingCents)} left",
                    color = if (budget.remainingCents < 0) MaterialTheme.colorScheme.error else TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}
