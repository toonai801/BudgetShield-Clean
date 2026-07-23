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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.toonai.budgetshield.ui.viewmodel.TransactionViewModel
import com.toonai.budgetshield.util.DateParser

import com.toonai.budgetshield.theme.BackgroundDark
import com.toonai.budgetshield.theme.PanelDark
import com.toonai.budgetshield.theme.PanelBorder
import com.toonai.budgetshield.theme.CyanAccent
import com.toonai.budgetshield.theme.GreenAccent
import com.toonai.budgetshield.theme.GoldAccent
import com.toonai.budgetshield.theme.BlueAccent
import com.toonai.budgetshield.theme.PurpleAccent
import com.toonai.budgetshield.theme.OrangeAccent
import com.toonai.budgetshield.theme.TextPrimary
import com.toonai.budgetshield.theme.TextMuted
import com.toonai.budgetshield.theme.DangerDot


@Composable
fun TransactionDetailsScreen(
    viewModel: TransactionViewModel,
    transactionId: Long? = null,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToTreasure: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToGoals: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Load transaction when ID changes
    LaunchedEffect(transactionId) {
        transactionId?.let { viewModel.loadTransaction(it) }
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
                HeaderSection(
                    transactionId = transactionId ?: uiState.selectedTransaction?.id,
                    onBack = onNavigateBack
                )

                // Loading state
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CyanAccent)
                    }
                }

                // Error message
                if (uiState.errorMessage != null) {
                    ErrorBanner(
                        message = uiState.errorMessage!!,
                        onDismiss = { viewModel.clearError() }
                    )
                }

                // Transaction Detail Card
                val transaction = uiState.selectedTransaction
                if (transaction != null) {
                    TransactionDetailCard(
                        transaction = transaction,
                        onDelete = { showDeleteDialog = true }
                    )
                } else {
                    TransactionDetailCardPlaceholder()
                }

                // Recent Transactions
                RecentTransactionsSection(
                    transactions = uiState.transactions,
                    onTransactionClick = { id -> viewModel.loadTransaction(id) }
                )

                // Monthly Summary
                MonthlySummaryCard(
                    totalIncome = uiState.totalIncome,
                    totalExpenses = uiState.totalExpenses,
                    netAmount = uiState.netAmount
                )

                // Footer spacer - system provides bottom nav
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Transaction?", color = TextPrimary) },
            text = { Text("This action cannot be undone.", color = TextMuted) },
            confirmButton = {
                TextButton(
                    onClick = {
                        transactionId?.let {
                            viewModel.deleteTransaction(it)
                            showDeleteDialog = false
                            onNavigateBack()
                        }
                    }
                ) {
                    Text("Delete", color = DangerDot)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = CyanAccent)
                }
            },
            containerColor = PanelDark
        )
    }
}

@Composable
private fun HeaderSection(transactionId: Long?, onBack: () -> Unit) {
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
                    text = "📜",
                    fontSize = 20.sp
                )
            }

            Column {
                Text(
                    text = "Transaction Details",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ID: #${transactionId ?: "TRX-2025-001"}",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        TextButton(
            onClick = onBack,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "← Back",
                color = TextMuted,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ErrorBanner(message: String, onDismiss: () -> Unit) {
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = DangerDot,
                fontSize = 14.sp
            )
            TextButton(onClick = onDismiss) {
                Text("Dismiss", color = DangerDot)
            }
        }
    }
}

@Composable
private fun TransactionDetailCard(
    transaction: com.toonai.budgetshield.data.model.Transaction,
    onDelete: () -> Unit
) {
    val amountText = if (transaction.isIncome) {
        "+$${transaction.amountCents / 100}.${kotlin.math.abs(transaction.amountCents % 100).toString().padStart(2, '0')}"
    } else {
        "-$${kotlin.math.abs(transaction.amountCents) / 100}.${kotlin.math.abs(transaction.amountCents % 100).toString().padStart(2, '0')}"
    }
    val amountColor = if (transaction.isIncome) GreenAccent else TextPrimary
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
            // Transaction Header
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                            .background(BlueAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🏠",
                            fontSize = 28.sp
                        )
                    }

                    Column {
                        Text(
                            text = "Rent Payment",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Housing • Bill",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = amountText,
                        color = amountColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (transaction.category != null) {
                        Text(
                            text = transaction.category,
                            color = CyanAccent,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Divider line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(PanelBorder)
            )

            // Transaction Details
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailRow("Date", transaction.transactionDate)
                DetailRow("Description", transaction.description ?: "No description")
                DetailRow("Type", if (transaction.isIncome) "Income" else "Expense")
                DetailRow("Category", transaction.category ?: "Uncategorized")
                DetailRow("Status", if (transaction.id > 0) "Synced ✓" else "Pending")
                DetailRow("Transaction ID", "TXN-${transaction.id}")
            }

            // Delete button
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DangerDot.copy(alpha = 0.15f)
                ),
                border = BorderStroke(1.dp, DangerDot.copy(alpha = 0.3f)),
                onClick = onDelete
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🗑️ Delete Transaction",
                        color = DangerDot,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // XP indicator (if applicable)
            if (!transaction.isIncome && transaction.amountCents < 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = GoldAccent.copy(alpha = 0.1f)
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "⭐",
                                fontSize = 20.sp
                            )
                            Text(
                                text = "XP Available",
                                color = TextPrimary,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = "+${kotlin.math.abs(transaction.amountCents / 100)} XP",
                            color = GoldAccent,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionDetailCardPlaceholder() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Select a transaction to view details",
                color = TextMuted,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun RecentTransactionsSection(
    transactions: List<com.toonai.budgetshield.data.model.Transaction>,
    onTransactionClick: (Long) -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = { },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "View All →",
                        color = CyanAccent,
                        fontSize = 12.sp
                    )
                }
            }

            if (transactions.isEmpty()) {
                Text(
                    text = "No transactions yet",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            } else {
                transactions.take(5).forEach { transaction ->
                    val isIncome = transaction.isIncome
                    val isNegative = !isIncome && transaction.amountCents < 0
                    val amountText = if (isIncome) {
                        "+$${transaction.amountCents / 100}.${(transaction.amountCents % 100).toString().padStart(2, '0')}"
                    } else {
                        "-$${kotlin.math.abs(transaction.amountCents) / 100}.${kotlin.math.abs(transaction.amountCents % 100).toString().padStart(2, '0')}"
                    }

                    TransactionItem(
                        icon = getCategoryIcon(transaction.category),
                        iconBg = getCategoryColor(transaction.category),
                        name = transaction.description ?: "Transaction",
                        category = transaction.category ?: "Uncategorized",
                        amount = amountText,
                        date = transaction.transactionDate.substringAfterLast("-").toInt().toString() + " " + when(transaction.transactionDate.substring(5, 7).toInt()) {
                            1 -> "Jan"
                            2 -> "Feb"
                            3 -> "Mar"
                            4 -> "Apr"
                            5 -> "May"
                            6 -> "Jun"
                            7 -> "Jul"
                            8 -> "Aug"
                            9 -> "Sep"
                            10 -> "Oct"
                            11 -> "Nov"
                            12 -> "Dec"
                            else -> transaction.transactionDate
                        },
                        isNegative = !isIncome,
                        onClick = { onTransactionClick(transaction.id) }
                    )
                }
            }
        }
    }
}

private fun getCategoryIcon(category: String?): String = when (category) {
    "Food" -> "🍔"
    "Wants" -> "🎮"
    "Bills" -> "📄"
    "Savings" -> "🏦"
    "Income" -> "💰"
    else -> "💳"
}

private fun getCategoryColor(category: String?): Color = when (category) {
    "Food" -> PurpleAccent
    "Wants" -> GoldAccent
    "Bills" -> BlueAccent
    "Savings" -> GoldAccent
    "Income" -> GreenAccent
    else -> CyanAccent
}

@Composable
private fun MonthlySummaryCard(
    totalIncome: Long,
    totalExpenses: Long,
    netAmount: Long
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
                text = "This Month",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem("Income", totalIncome, GreenAccent)
                SummaryItem("Expenses", totalExpenses, DangerDot)
                SummaryItem("Net", netAmount, if (netAmount >= 0) GreenAccent else DangerDot)
            }
        }
    }
}

@Composable
private fun SummaryItem(label: String, amountCents: Long, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 12.sp
        )
        Text(
            text = "$${kotlin.math.abs(amountCents) / 100}.${kotlin.math.abs(amountCents % 100).toString().padStart(2, '0')}",
            color = color,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TransactionItem(
    icon: String,
    iconBg: Color,
    name: String,
    category: String,
    amount: String,
    date: String,
    isNegative: Boolean,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        onClick = onClick
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
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconBg.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        fontSize = 18.sp
                    )
                }

                Column {
                    Text(
                        text = name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$category • $date",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Text(
                text = amount,
                color = if (isNegative) TextPrimary else GreenAccent,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


