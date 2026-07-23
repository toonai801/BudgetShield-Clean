package com.toonai.budgetshield.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
import com.toonai.budgetshield.ui.LocalBillRepository
import com.toonai.budgetshield.ui.LocalIncomeRepository
import com.toonai.budgetshield.ui.LocalSavingsGoalRepository
import java.time.YearMonth


@Composable
fun StatsScreen(
    onNavigateToTransactionDetails: () -> Unit
) {
    val billRepository = LocalBillRepository.current
    val incomeRepository = LocalIncomeRepository.current
    val savingsGoalRepository = LocalSavingsGoalRepository.current

    // Collect real data from repositories
    val bills by billRepository.allBills.collectAsState(initial = emptyList())
    val incomes by incomeRepository.getAllActiveSchedules().collectAsState(initial = emptyList())
    val savingsGoals by savingsGoalRepository.allGoals.collectAsState(initial = emptyList())
    val totalSavings by savingsGoalRepository.totalSavings.collectAsState(initial = 0L)

    // Calculate real stats
    val totalBillsAmount = bills.sumOf { it.amountCents }
    val protectedBillsCount = bills.count { it.isProtected }
    val savingsGoalsCount = savingsGoals.size
    val totalIncome = incomes.sumOf { it.amountCents }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("stats_scroll_content"),
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

                // Monthly Overview - uses real bill and income data
                MonthlyOverviewCardReal(
                    totalBills = totalBillsAmount,
                    totalIncome = totalIncome,
                    protectedCount = protectedBillsCount,
                    billsCount = bills.size
                )

                // Bills Breakdown - shows real bills grouped by icon/category
                BillsBreakdownSectionReal(
                    bills = bills
                )

                // Quick Stats - uses real data
                QuickStatsSectionReal(
                    billsCount = bills.size,
                    protectedCount = protectedBillsCount,
                    savingsGoalsCount = savingsGoalsCount,
                    totalSavings = totalSavings
                )

                // Bottom Actions
                BottomActionsSection(
                    onViewTransactions = onNavigateToTransactionDetails
                )

                // Bottom spacer - no duplicate footer
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    val currentMonth = YearMonth.now().format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy"))

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
                    text = "📊",
                    fontSize = 20.sp
                )
            }

            Column {
                Text(
                    text = "Statistics",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$currentMonth Overview",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun MonthlyOverviewCardReal(
    totalBills: Long,
    totalIncome: Long,
    protectedCount: Int,
    billsCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Monthly Overview",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Income
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Income",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${totalIncome / 100}.${(totalIncome % 100).toString().padStart(2, '0')}",
                        color = GreenAccent,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(PanelBorder)
                )

                // Bills
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Bills",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${totalBills / 100}.${(totalBills % 100).toString().padStart(2, '0')}",
                        color = CyanAccent,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(PanelBorder)
                )

                // Protected
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Protected",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "$protectedCount",
                        color = GoldAccent,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Legend
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LegendItem(color = GreenAccent, label = "Income")
                LegendItem(color = CyanAccent, label = "Bills")
                LegendItem(color = GoldAccent, label = "Protected")
            }
        }
    }
}

@Composable
private fun BillsBreakdownSectionReal(
    bills: List<com.toonai.budgetshield.data.model.Bill>
) {
    // Group bills by icon and calculate totals
    val billsByIcon = bills.groupBy { it.icon }
    val iconTotals = billsByIcon.map { (icon, billsList) ->
        Triple(icon, billsList.sumOf { it.amountCents }, billsList.size)
    }.sortedByDescending { it.second }

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
                text = "Bills by Type",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            if (iconTotals.isEmpty()) {
                Text(
                    text = "No bills added yet",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            } else {
                val maxAmount = iconTotals.maxOfOrNull { it.second } ?: 1

                iconTotals.forEach { (icon, amountCents, count) ->
                    val percentage = if (maxAmount > 0) amountCents.toFloat() / maxAmount else 0f
                    val (categoryName, color) = when (icon) {
                        "🏠" -> "Housing" to BlueAccent
                        "🍔", "🍽️" -> "Food" to OrangeAccent
                        "⚡", "💡", "💧" -> "Utilities" to CyanAccent
                        "📱", "🌐", "📺" -> "Services" to PurpleAccent
                        "🚗", "⛽", "🚌" -> "Transport" to GreenAccent
                        "🎮", "🎬", "🎵" -> "Entertainment" to GoldAccent
                        "🏥", "💊" -> "Health" to PurpleAccent
                        else -> "Other" to BlueAccent
                    }

                    CategoryItemReal(
                        icon = icon,
                        name = categoryName,
                        amountCents = amountCents,
                        percentage = percentage,
                        color = color,
                        count = count
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryItemReal(
    icon: String,
    name: String,
    amountCents: Long,
    percentage: Float,
    color: Color,
    count: Int
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        fontSize = 16.sp
                    )
                }

                Column {
                    Text(
                        text = name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$count bills",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Text(
                text = "${amountCents / 100}.${(amountCents % 100).toString().padStart(2, '0')}",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(PanelBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(color)
            )
        }
    }
}

@Composable
private fun QuickStatsSectionReal(
    billsCount: Int,
    protectedCount: Int,
    savingsGoalsCount: Int,
    totalSavings: Long
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
                text = "Quick Stats",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = "📄",
                    value = billsCount.toString(),
                    label = "Total Bills",
                    color = CyanAccent
                )

                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = "🛡️",
                    value = protectedCount.toString(),
                    label = "Protected",
                    color = GreenAccent
                )

                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = "🎯",
                    value = savingsGoalsCount.toString(),
                    label = "Goals",
                    color = GoldAccent
                )
            }

            // Savings summary
            if (totalSavings > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF0D1B26)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "💰",
                                fontSize = 20.sp
                            )
                            Text(
                                text = "${totalSavings / 100}.${(totalSavings % 100).toString().padStart(2, '0')}",
                                color = GreenAccent,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Total Savings",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: String,
    value: String,
    label: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0D1B26)
        )
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
                fontSize = 20.sp
            )
            Text(
                text = value,
                color = color,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = TextMuted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            color = TextMuted,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun BottomActionsSection(
    onViewTransactions: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextButton(
            onClick = onViewTransactions,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "View All Transactions →",
                color = CyanAccent,
                fontSize = 14.sp
            )
        }
    }
}
