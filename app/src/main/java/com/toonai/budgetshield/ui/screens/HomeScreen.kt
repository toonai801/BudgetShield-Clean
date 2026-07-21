package com.toonai.budgetshield.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.toonai.budgetshield.data.model.Bill
import java.time.format.DateTimeFormatter

// Premium gamified dark theme colors
private val BackgroundDark = Color(0xFF02070D)
private val PanelBackground = Color(0xFF0A1A2E)
private val PanelBorder = Color(0xFF14364A)
private val CyanAccent = Color(0xFF17E8F2)
private val GoldAccent = Color(0xFFE8B923)
private val GreenAccent = Color(0xFF17F253)
private val TextMuted = Color(0xFFA6B1BF)
private val TextBright = Color(0xFFFFFFFF)
private val PositiveGreen = Color(0xFF17F253)
private val NegativeRed = Color(0xFFFF6B6B)
private val YellowWarning = Color(0xFFE8B923)

/**
 * HomeScreen - Live data from SetupQuest and real calculation engine.
 * No hardcoded values - all data comes from HomeViewModel.
 */
@Composable
fun HomeScreen(
    onNavigateToTreasure: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    onNavigateToGoals: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToIncomeEntry: () -> Unit = {},
    onNavigateToBillEntry: () -> Unit = {},
    onNavigateToSavingsEntry: () -> Unit = {},
    onNavigateToTransactionDetails: () -> Unit = {},
    onNavigateToShieldProgression: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Load data on first composition
    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // Header
        item {
            HomeHeader(
                selectedMonth = uiState.currentMonth,
                onPreviousMonth = { viewModel.previousMonth() },
                onNextMonth = { viewModel.nextMonth() },
                onOpenMenu = { /* Menu action */ }
            )
        }

        // Safe Now Card (Primary CTA)
        item {
            SafeNowCard(
                safeNowAmount = uiState.safeNowAmount,
                status = uiState.safeNowStatus,
                projectedDate = uiState.projectedDate,
                shieldPercentage = uiState.shieldPercentage,
                streakDays = uiState.streakDays,
                onClick = onNavigateToShieldProgression,
                onIncomeClick = onNavigateToIncomeEntry,
                onBillsClick = onNavigateToBillEntry,
                onSavingsClick = onNavigateToSavingsEntry
            )
        }

        // Protected Bills Section
        item {
            ProtectedBillsSection(
                bills = uiState.protectedBills,
                totalProtected = uiState.totalProtectedAmount,
                onPayBill = { billId -> viewModel.payBill(billId) },
                onViewAll = onNavigateToBillEntry,
                onAddBill = onNavigateToBillEntry
            )
        }

        // Recent Activity
        if (uiState.recentTransactions.isNotEmpty()) {
            item {
                RecentActivitySection(
                    transactions = uiState.recentTransactions,
                    onViewAll = onNavigateToTransactionDetails
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    selectedMonth: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onOpenMenu: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_header"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Month Navigation
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onPreviousMonth,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Previous month",
                    tint = TextMuted,
                    modifier = Modifier.rotate(180f)
                )
            }

            Text(
                text = selectedMonth,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = TextBright,
                modifier = Modifier.testTag("home_month_display")
            )

            IconButton(
                onClick = onNextMonth,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next month",
                    tint = TextMuted
                )
            }
        }

        // Menu
        IconButton(onClick = onOpenMenu) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Menu",
                tint = TextMuted
            )
        }
    }
}

@Composable
private fun SafeNowCard(
    safeNowAmount: Long,
    status: SafeNowStatus,
    projectedDate: String?,
    shieldPercentage: Int,
    streakDays: Int,
    onClick: () -> Unit,
    onIncomeClick: () -> Unit,
    onBillsClick: () -> Unit,
    onSavingsClick: () -> Unit
) {
    val cardGradient = when (status) {
        SafeNowStatus.SECURE -> Brush.verticalGradient(
            colors = listOf(Color(0xFF0A3D2A), Color(0xFF061A12))
        )
        SafeNowStatus.WARNING -> Brush.verticalGradient(
            colors = listOf(Color(0xFF3D3A0A), Color(0xFF1A1806))
        )
        SafeNowStatus.CRITICAL -> Brush.verticalGradient(
            colors = listOf(Color(0xFF3D0A0A), Color(0xFF1A0606))
        )
    }

    val accentColor = when (status) {
        SafeNowStatus.SECURE -> GreenAccent
        SafeNowStatus.WARNING -> YellowWarning
        SafeNowStatus.CRITICAL -> NegativeRed
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("safe_now_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(cardGradient)
                .border(1.dp, PanelBorder, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Header with status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Status indicator
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Safe to Spend",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextBright
                        )
                    }

                    // Streak badge
                    if (streakDays > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(PanelBackground)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("🔥", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$streakDays",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = GoldAccent
                            )
                        }
                    }
                }

                // Amount display
                Column {
                    val dollars = safeNowAmount / 100
                    val cents = kotlin.math.abs(safeNowAmount % 100)
                    Text(
                        text = buildAnnotatedString {
                            append("$")
                            withStyle(SpanStyle(fontSize = 48.sp, fontWeight = FontWeight.Bold)) {
                                append("$dollars")
                            }
                            withStyle(SpanStyle(fontSize = 24.sp)) {
                                append(String.format(".%02d", cents))
                            }
                        },
                        color = if (safeNowAmount >= 0) accentColor else NegativeRed,
                        modifier = Modifier.testTag("safe_now_amount")
                    )

                    if (projectedDate != null && safeNowAmount >= 0) {
                        Text(
                            text = "Projected through $projectedDate",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }

                // Quick actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionButton(
                        icon = "💰",
                        label = "Income",
                        onClick = onIncomeClick,
                        testTag = "quick_action_income"
                    )
                    QuickActionButton(
                        icon = "📄",
                        label = "Bills",
                        onClick = onBillsClick,
                        testTag = "quick_action_bills"
                    )
                    QuickActionButton(
                        icon = "🐷",
                        label = "Savings",
                        onClick = onSavingsClick,
                        testTag = "quick_action_savings"
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: String,
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
            .testTag(testTag)
    ) {
        Text(text = icon, fontSize = 24.sp)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
    }
}

@Composable
private fun ProtectedBillsSection(
    bills: List<Bill>,
    totalProtected: Long,
    onPayBill: (Long) -> Unit,
    onViewAll: () -> Unit,
    onAddBill: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.testTag("protected_bills_section")
    ) {
        // Section header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🛡️",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Column {
                    Text(
                        text = "Protected Bills",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextBright
                    )
                    Text(
                        text = formatCents(totalProtected) + " set aside",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            Row {
                TextButton(onClick = onViewAll) {
                    Text("View All", color = CyanAccent)
                }
                IconButton(onClick = onAddBill) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add bill",
                        tint = CyanAccent
                    )
                }
            }
        }

        if (bills.isEmpty()) {
            // Empty state
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PanelBackground)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📄",
                        fontSize = 32.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "No protected bills yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                    TextButton(onClick = onAddBill) {
                        Text("Add your first bill", color = CyanAccent)
                    }
                }
            }
        } else {
            // Bill list (max 3 visible)
            bills.take(3).forEach { bill ->
                BillItem(
                    bill = bill,
                    onPay = { onPayBill(bill.id) }
                )
            }

            if (bills.size > 3) {
                TextButton(
                    onClick = onViewAll,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("+${bills.size - 3} more", color = TextMuted)
                }
            }
        }
    }
}

@Composable
private fun BillItem(
    bill: Bill,
    onPay: () -> Unit
) {
    val isPaid = bill.isPaid
    val alpha = if (isPaid) 0.6f else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PanelBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = bill.icon,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = bill.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = TextBright
                    )
                    Text(
                        text = "Due ${formatDate(bill.dueDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    if (isPaid) {
                        Text(
                            text = "PAID",
                            style = MaterialTheme.typography.labelSmall,
                            color = PositiveGreen,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = bill.formattedRemainingDue,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = if (bill.remainingDueCents < bill.amountCents) YellowWarning else TextBright
                        )
                        if (bill.paidAmountCents > 0) {
                            Text(
                                text = "of ${bill.formattedAmount}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }

                if (!isPaid) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onPay,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanAccent.copy(alpha = 0.2f),
                            contentColor = CyanAccent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Pay", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentActivitySection(
    transactions: List<RecentTransaction>,
    onViewAll: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.testTag("recent_activity_section")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "📝",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextBright
                )
            }

            TextButton(onClick = onViewAll) {
                Text("View All", color = CyanAccent)
            }
        }

        transactions.take(5).forEach { transaction ->
            TransactionItem(transaction = transaction)
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: RecentTransaction
) {
    val amountColor = when (transaction.type) {
        TransactionType.INCOME -> PositiveGreen
        TransactionType.EXPENSE -> NegativeRed
        TransactionType.TRANSFER -> CyanAccent
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PanelBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(transaction.categoryColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = transaction.icon,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = transaction.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextBright
                    )
                    Text(
                        text = transaction.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            Text(
                text = transaction.formattedAmount,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = amountColor
            )
        }
    }
}

// Data classes for UI
enum class SafeNowStatus {
    SECURE,    // Green - bills covered with buffer
    WARNING,   // Yellow - tight but covered
    CRITICAL   // Red - shortage detected
}

data class RecentTransaction(
    val id: Long,
    val description: String,
    val amountCents: Long,
    val type: TransactionType,
    val date: String,
    val icon: String,
    val categoryColor: Color
) {
    val formattedAmount: String
        get() {
            val prefix = if (amountCents >= 0) "+" else ""
            return prefix + formatCents(amountCents)
        }
}

enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}

// Helper functions
private fun formatCents(cents: Long): String {
    val dollars = cents / 100
    val remainder = kotlin.math.abs(cents % 100)
    return String.format("$%d.%02d", dollars, remainder)
}

private fun formatDate(dateString: String): String {
    return try {
        val date = java.time.LocalDate.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("MMM d")
        date.format(formatter)
    } catch (e: Exception) {
        dateString
    }
}
