package com.toonai.budgetshield.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toonai.budgetshield.ui.viewmodel.HomeViewModel
import com.toonai.budgetshield.util.DateParser
import com.toonai.budgetshield.util.MoneyParser
import java.time.YearMonth

// Premium gamified dark theme colors
private val BackgroundDark = Color(0xFF02070D)
private val PanelDark = Color(0xFF06121D)
private val PanelBorder = Color(0xFF14364A)
private val CyanAccent = Color(0xFF17E8F2)
private val CyanSoft = Color(0xFF10CDD9)
private val GreenAccent = Color(0xFF2FE6A7)
private val GoldAccent = Color(0xFFFFC545)
private val BlueAccent = Color(0xFF1678B9)
private val TextPrimary = Color(0xFFF4F7FB)
private val TextMuted = Color(0xFFA6B1BF)
private val DangerDot = Color(0xFFFF553D)

@Composable
fun HomeScreen(
    onNavigateToTreasure: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToGoals: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToIncomeEntry: () -> Unit,
    onNavigateToBillEntry: () -> Unit,
    onNavigateToSavingsEntry: () -> Unit,
    onNavigateToTransactionDetails: () -> Unit,
    onNavigateToShieldProgression: () -> Unit,
    onNavigateToRewardScreen: () -> Unit,
    onNavigateToMenu: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BackgroundDark
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CyanAccent)
                }
            } else {
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
                        HeaderSection(
                            onRewardClick = onNavigateToRewardScreen,
                            onMenuClick = onNavigateToMenu,
                            hasUnreadRewards = uiState.hasUnreadRewards
                        )

                        MonthSelector(
                            selectedMonth = uiState.selectedMonth,
                            onPreviousMonth = viewModel::goToPreviousMonth,
                            onNextMonth = viewModel::goToNextMonth,
                            onMonthPickerClick = onNavigateToSettings,
                            onCalendarClick = onNavigateToCalendar
                        )

                        HeroCard(
                            safeAmountCents = uiState.safeNowCents,
                            onNavigateToShieldProgression = onNavigateToShieldProgression
                        )

                        StatsCardsRow(
                            streakCount = uiState.currentStreak,
                            shieldPower = uiState.shieldPower,
                            totalShielded = uiState.totalShieldedCents
                        )

                        DailyActionsSection(
                            onAddIncome = onNavigateToIncomeEntry,
                            onPayBill = onNavigateToBillEntry,
                            onSaveMoney = onNavigateToSavingsEntry
                        )

                        RecentActivitySection(
                            recentTransactions = uiState.recentTransactions,
                            onViewAll = onNavigateToTransactionDetails
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(
    onRewardClick: () -> Unit,
    onMenuClick: () -> Unit,
    hasUnreadRewards: Boolean
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
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(CyanAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🛡️", fontSize = 18.sp)
            }

            Row {
                Text(
                    text = "Budget ",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Buddy",
                    color = CyanAccent,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(
                    onClick = onRewardClick,
                    modifier = Modifier.testTag("home_reward_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GoldAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🪙", fontSize = 18.sp)
                    }
                }
                if (hasUnreadRewards) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(DangerDot)
                    )
                }
            }

            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.testTag("home_menu_button")
            ) {
                Text(text = "☰", color = TextPrimary, fontSize = 20.sp)
            }
        }
    }
}

@Composable
private fun MonthSelector(
    selectedMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onMonthPickerClick: () -> Unit,
    onCalendarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PanelDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPreviousMonth,
                modifier = Modifier.testTag("home_month_previous")
            ) {
                Text(
                    text = "‹",
                    color = TextMuted,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TextButton(onClick = onMonthPickerClick) {
                    Text(
                        text = DateParser.formatMonthYear(selectedMonth),
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = " ▼",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            IconButton(
                onClick = onNextMonth,
                modifier = Modifier.testTag("home_month_next")
            ) {
                Text(
                    text = "›",
                    color = TextMuted,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PanelDark)
            ) {
                IconButton(
                    onClick = onCalendarClick,
                    modifier = Modifier.testTag("home_calendar_button")
                ) {
                    Text(text = "📅", fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
private fun HeroCard(
    safeAmountCents: Long,
    onNavigateToShieldProgression: () -> Unit
) {
    val hasShortage = safeAmountCents < 0
    val displayAmount = MoneyParser.formatCents(if (hasShortage) 0 else safeAmountCents)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_safe_now_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1F2C)),
        onClick = onNavigateToShieldProgression
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRect(
                        color = if (hasShortage) DangerDot else CyanAccent,
                        style = Stroke(width = 1.dp.toPx()),
                        alpha = 0.3f
                    )
                }
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                CyanAccent.copy(alpha = 0.2f),
                                CyanAccent.copy(alpha = 0.05f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🗡️", fontSize = 48.sp)
            }

            Column(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "🛡️", fontSize = 14.sp)
                    Text(
                        text = if (hasShortage) "SHORTAGE" else "Safe Now",
                        color = if (hasShortage) DangerDot else CyanAccent,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = displayAmount,
                    color = if (hasShortage) DangerDot else TextPrimary,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = if (hasShortage) "Tap to see options" else "Safe to spend right now",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun StatsCardsRow(
    streakCount: Int,
    shieldPower: Int,
    totalShielded: Long
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            icon = "🔥",
            value = if (streakCount > 0) streakCount.toString() else "—",
            label = "Day Streak",
            accentColor = GoldAccent,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = "⚔️",
            value = "$shieldPower%",
            label = "Shield Power",
            accentColor = CyanAccent,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = "🛡️",
            value = MoneyParser.formatCompactCents(totalShielded),
            label = "Shielded",
            accentColor = GreenAccent,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    icon: String,
    value: String,
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PanelDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun DailyActionsSection(
    onAddIncome: () -> Unit,
    onPayBill: () -> Unit,
    onSaveMoney: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PanelDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Daily Actions",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(icon = "💰", label = "Add Income", onClick = onAddIncome)
                ActionButton(icon = "💳", label = "Pay Bill", onClick = onPayBill)
                ActionButton(icon = "💎", label = "Save Money", onClick = onSaveMoney)
            }
        }
    }
}

@Composable
private fun ActionButton(icon: String, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(CyanAccent.copy(alpha = 0.15f))
        ) {
            Text(text = icon, fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = TextMuted,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun RecentActivitySection(
    recentTransactions: List<TransactionUiModel>,
    onViewAll: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PanelDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Activity",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(onClick = onViewAll) {
                    Text(
                        text = "View All",
                        color = CyanAccent,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (recentTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No activity yet",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
            } else {
                recentTransactions.forEach { transaction ->
                    ActivityItem(transaction = transaction)
                }
            }
        }
    }
}

@Composable
private fun ActivityItem(transaction: TransactionUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    when (transaction.type) {
                        TransactionType.INCOME -> GreenAccent.copy(alpha = 0.2f)
                        TransactionType.BILL_PAYMENT -> DangerDot.copy(alpha = 0.2f)
                        TransactionType.SAVINGS -> GoldAccent.copy(alpha = 0.2f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = transaction.icon, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.name,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = transaction.date,
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        Text(
            text = transaction.amountDisplay,
            color = when (transaction.type) {
                TransactionType.INCOME -> GreenAccent
                TransactionType.BILL_PAYMENT -> DangerDot
                TransactionType.SAVINGS -> GoldAccent
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// UI Models
data class TransactionUiModel(
    val id: Long,
    val name: String,
    val amountDisplay: String,
    val date: String,
    val type: TransactionType,
    val icon: String
)

enum class TransactionType {
    INCOME, BILL_PAYMENT, SAVINGS
}
