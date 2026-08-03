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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.toonai.budgetshield.theme.BackgroundDark
import com.toonai.budgetshield.theme.BudgetShieldTheme
import com.toonai.budgetshield.theme.CardHeroBackground
import com.toonai.budgetshield.theme.CardPadding
import com.toonai.budgetshield.theme.CardPaddingLarge
import com.toonai.budgetshield.theme.CyanAccent
import com.toonai.budgetshield.theme.CyanAccent05
import com.toonai.budgetshield.theme.CyanAccent15
import com.toonai.budgetshield.theme.CyanAccent20
import com.toonai.budgetshield.theme.CyanAccent30
import com.toonai.budgetshield.theme.DangerDot
import com.toonai.budgetshield.theme.DangerDot20
import com.toonai.budgetshield.theme.GoldAccent
import com.toonai.budgetshield.theme.GoldAccent20
import com.toonai.budgetshield.theme.GradientCyanEnd
import com.toonai.budgetshield.theme.GradientCyanStart
import com.toonai.budgetshield.theme.GreenAccent
import com.toonai.budgetshield.theme.GreenAccent20
import com.toonai.budgetshield.theme.IconContainerHero
import com.toonai.budgetshield.theme.IconContainerLarge
import com.toonai.budgetshield.theme.IconContainerMedium
import com.toonai.budgetshield.theme.IconContainerSmall
import com.toonai.budgetshield.theme.IconContainerStandard
import com.toonai.budgetshield.theme.IconSizes
import com.toonai.budgetshield.theme.PanelDark
import com.toonai.budgetshield.theme.ShapeCircular
import com.toonai.budgetshield.theme.ShapeLarge
import com.toonai.budgetshield.theme.ShapeXLarge
import com.toonai.budgetshield.theme.ShapeXXLarge
import com.toonai.budgetshield.theme.Spacing
import com.toonai.budgetshield.theme.TextMuted
import com.toonai.budgetshield.theme.TextPrimary
import com.toonai.budgetshield.ui.viewmodel.HomeViewModel
import com.toonai.budgetshield.util.DateParser
import com.toonai.budgetshield.util.MoneyParser
import java.time.YearMonth

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
    onNavigateToLogSpending: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .testTag("budgetshield_root")
    ) {
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
            } else if (uiState.error != null) {
                SafeNowRepairPanel(
                    message = requireNotNull(uiState.error),
                    onReviewIncome = onNavigateToIncomeEntry,
                    onReviewBills = onNavigateToBillEntry,
                    onRetry = viewModel::loadHomeData
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    HomeContent(
                        uiState = uiState,
                        onNavigateToTreasure = onNavigateToTreasure,
                        onNavigateToSettings = onNavigateToSettings,
                        onNavigateToRewardScreen = onNavigateToRewardScreen,
                        onNavigateToMenu = onNavigateToMenu,
                        onNavigateToCalendar = onNavigateToCalendar,
                        onNavigateToShieldProgression = onNavigateToShieldProgression,
                        onNavigateToIncomeEntry = onNavigateToIncomeEntry,
                        onNavigateToBillEntry = onNavigateToBillEntry,
                        onNavigateToSavingsEntry = onNavigateToSavingsEntry,
                        onNavigateToTransactionDetails = onNavigateToTransactionDetails,
                        onNavigateToLogSpending = onNavigateToLogSpending,
                        onNavigateToBudgets = onNavigateToBudgets,
                        viewModel = viewModel,
                        modifier = Modifier.weight(1f)
                    )

                }
            }
        }
    }
}

@Composable
internal fun SafeNowRepairPanel(
    message: String,
    onReviewIncome: () -> Unit,
    onReviewBills: () -> Unit,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.large),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("safe_now_repair_panel"),
            colors = CardDefaults.cardColors(containerColor = PanelDark),
            shape = ShapeXLarge
        ) {
            Column(
                modifier = Modifier.padding(CardPaddingLarge),
                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                Text(
                    text = "Safe Now needs your attention",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "We found saved financial information that cannot be projected safely. Safe Now is blocked until it is repaired.",
                    color = TextMuted,
                    fontSize = 15.sp
                )
                Text(
                    text = message,
                    color = DangerDot,
                    fontSize = 14.sp,
                    modifier = Modifier.testTag("safe_now_repair_message")
                )
                TextButton(
                    onClick = onReviewIncome,
                    modifier = Modifier.testTag("safe_now_review_income")
                ) {
                    Text("Review income", color = CyanAccent)
                }
                TextButton(
                    onClick = onReviewBills,
                    modifier = Modifier.testTag("safe_now_review_bills")
                ) {
                    Text("Review protected bills", color = CyanAccent)
                }
                TextButton(
                    onClick = onRetry,
                    modifier = Modifier.testTag("safe_now_retry")
                ) {
                    Text("Try Safe Now again", color = TextPrimary)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    uiState: com.toonai.budgetshield.ui.viewmodel.HomeUiState,
    onNavigateToTreasure: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToRewardScreen: () -> Unit,
    onNavigateToMenu: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToShieldProgression: () -> Unit,
    onNavigateToIncomeEntry: () -> Unit,
    onNavigateToBillEntry: () -> Unit,
    onNavigateToSavingsEntry: () -> Unit,
    onNavigateToTransactionDetails: () -> Unit,
    onNavigateToLogSpending: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    // State for month picker dialog
    var showMonthPicker by remember { mutableStateOf(false) }
    var tempSelectedMonth by remember { mutableStateOf(uiState.selectedMonth) }
    
    // Month picker dialog
    if (showMonthPicker) {
        // Convert YearMonth to millis for the date picker
        val yearMonthMillis = tempSelectedMonth.atDay(1)
            .atStartOfDay(java.time.ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = yearMonthMillis
        )
        
        DatePickerDialog(
            onDismissRequest = { showMonthPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                            val newMonth = YearMonth.of(date.year, date.month)
                            viewModel.setSelectedMonth(newMonth)
                        }
                        showMonthPicker = false
                    }
                ) {
                    Text("Select")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMonthPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.screenHorizontal)
                .padding(top = Spacing.screenTop, bottom = Spacing.screenBottom),
            verticalArrangement = Arrangement.spacedBy(Spacing.large)
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
                onMonthPickerClick = { showMonthPicker = true },
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
                onSaveMoney = onNavigateToSavingsEntry,
                onLogSpending = onNavigateToLogSpending,
                onViewBudgets = onNavigateToBudgets
            )

            RecentActivitySection(
                recentTransactions = uiState.recentTransactions,
                onViewAll = onNavigateToTransactionDetails
            )
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
            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            Box(
                modifier = Modifier
                    .size(IconContainerSmall)
                    .clip(ShapeCircular)
                    .background(CyanAccent20),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🛡️", fontSize = IconSizes.header)
            }

            Row {
                Text(
                    text = "Budget ",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Shield",
                    color = CyanAccent,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            /* Reward button hidden until rewards system implemented
            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(
                    onClick = onRewardClick,
                    modifier = Modifier.testTag("home_reward_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(IconContainerMedium)
                            .clip(ShapeCircular)
                            .background(GoldAccent20),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🪙", fontSize = 18.sp)
                    }
                }
                if (hasUnreadRewards) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(ShapeCircular)
                            .background(DangerDot)
                    )
                }
            }
            */

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
        shape = ShapeXLarge,
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

            // Calendar button removed - month picker now handles calendar functionality
            /* Removed: Calendar icon button that was going to Settings */
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
        shape = ShapeXXLarge,
        colors = CardDefaults.cardColors(containerColor = CardHeroBackground),
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
                .padding(CardPaddingLarge)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(IconContainerHero)
                    .clip(ShapeLarge)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                GradientCyanStart,
                                GradientCyanEnd
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🗡️", fontSize = IconSizes.hero)
            }

            Column(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalArrangement = Arrangement.spacedBy(Spacing.xSmall)
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
    accentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = ShapeXLarge,
        colors = CardDefaults.cardColors(containerColor = PanelDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CardPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = IconSizes.card)
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
    onSaveMoney: () -> Unit,
    onLogSpending: () -> Unit,
    onViewBudgets: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeXLarge,
        colors = CardDefaults.cardColors(containerColor = PanelDark)
    ) {
        Column(modifier = Modifier.padding(CardPadding)) {
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
                ActionButton(icon = "💰", label = "Add Income", onClick = onAddIncome, testTag = "home_action_add_income")
                ActionButton(icon = "💳", label = "Pay Bill", onClick = onPayBill, testTag = "home_action_pay_bill")
                ActionButton(icon = "💎", label = "Save Money", onClick = onSaveMoney, testTag = "home_action_save_money")
                ActionButton(icon = "🧾", label = "Log Spending", onClick = onLogSpending, testTag = "home_action_log_spending")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = onViewBudgets,
                    modifier = Modifier.testTag("home_action_view_budgets")
                ) {
                    Text("📊 View Budgets", color = CyanAccent)
                }
            }
        }
    }
}

@Composable
private fun ActionButton(icon: String, label: String, onClick: () -> Unit, testTag: String = "") {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(IconContainerLarge)
                .clip(ShapeCircular)
                .background(CyanAccent15)
                .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
        ) {
            Text(text = icon, fontSize = IconSizes.action)
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
        shape = ShapeXLarge,
        colors = CardDefaults.cardColors(containerColor = PanelDark)
    ) {
        Column(modifier = Modifier.padding(CardPadding)) {
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
                .size(IconContainerStandard)
                .clip(ShapeCircular)
                .background(
                    when (transaction.type) {
                        TransactionType.INCOME -> GreenAccent20
                        TransactionType.BILL_PAYMENT -> DangerDot20
                        TransactionType.SAVINGS -> GoldAccent20
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

// ============================================
// PREVIEWS
// ============================================

@Preview(
    name = "Home Screen - Normal State",
    showBackground = true,
    backgroundColor = 0xFF02070D,
    device = "id:pixel_5"
)
@Composable
fun HomeScreenNormalPreview() {
    BudgetShieldTheme {
        Surface(color = BackgroundDark) {
            PreviewHomeContent(
                safeNowCents = 245000L,
                currentStreak = 12,
                shieldPower = 85,
                totalShieldedCents = 125000L,
                hasUnreadRewards = true,
                transactions = listOf(
                    TransactionUiModel(
                        id = 1,
                        name = "Salary Deposit",
                        amountDisplay = "+$3,500.00",
                        date = "Jan 15, 2026",
                        type = TransactionType.INCOME,
                        icon = "💰"
                    ),
                    TransactionUiModel(
                        id = 2,
                        name = "Electric Bill",
                        amountDisplay = "-$125.00",
                        date = "Jan 14, 2026",
                        type = TransactionType.BILL_PAYMENT,
                        icon = "⚡"
                    ),
                    TransactionUiModel(
                        id = 3,
                        name = "Emergency Fund",
                        amountDisplay = "+$200.00",
                        date = "Jan 13, 2026",
                        type = TransactionType.SAVINGS,
                        icon = "💎"
                    )
                )
            )
        }
    }
}

@Preview(
    name = "Home Screen - Empty State",
    showBackground = true,
    backgroundColor = 0xFF02070D,
    device = "id:pixel_5"
)
@Composable
fun HomeScreenEmptyPreview() {
    BudgetShieldTheme {
        Surface(color = BackgroundDark) {
            PreviewHomeContent(
                safeNowCents = 0,
                currentStreak = 0,
                shieldPower = 0,
                totalShieldedCents = 0,
                hasUnreadRewards = false,
                transactions = emptyList()
            )
        }
    }
}

@Preview(
    name = "Home Screen - Shortage",
    showBackground = true,
    backgroundColor = 0xFF02070D,
    device = "id:pixel_5"
)
@Composable
fun HomeScreenShortagePreview() {
    BudgetShieldTheme {
        Surface(color = BackgroundDark) {
            PreviewHomeContent(
                safeNowCents = -50000L,
                currentStreak = 0,
                shieldPower = 45,
                totalShieldedCents = 50000L,
                hasUnreadRewards = true,
                transactions = listOf(
                    TransactionUiModel(
                        id = 1,
                        name = "Rent Payment",
                        amountDisplay = "-$1,200.00",
                        date = "Jan 10, 2026",
                        type = TransactionType.BILL_PAYMENT,
                        icon = "🏠"
                    )
                )
            )
        }
    }
}

@Preview(
    name = "Home Screen - Loading",
    showBackground = true,
    backgroundColor = 0xFF02070D,
    device = "id:pixel_5"
)
@Composable
fun HomeScreenLoadingPreview() {
    BudgetShieldTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BackgroundDark
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = CyanAccent)
            }
        }
    }
}

@Composable
private fun PreviewHomeContent(
    safeNowCents: Long,
    currentStreak: Int,
    shieldPower: Int,
    totalShieldedCents: Long,
    hasUnreadRewards: Boolean,
    transactions: List<TransactionUiModel>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.screenHorizontal)
                .padding(top = Spacing.screenTop, bottom = Spacing.screenBottom),
            verticalArrangement = Arrangement.spacedBy(Spacing.large)
        ) {
            HeaderSection(
                onRewardClick = {},
                onMenuClick = {},
                hasUnreadRewards = hasUnreadRewards
            )

            MonthSelector(
                selectedMonth = YearMonth.of(2026, 1),
                onPreviousMonth = {},
                onNextMonth = {},
                onMonthPickerClick = {},
                onCalendarClick = {}
            )

            HeroCard(
                safeAmountCents = safeNowCents,
                onNavigateToShieldProgression = {}
            )

            StatsCardsRow(
                streakCount = currentStreak,
                shieldPower = shieldPower,
                totalShielded = totalShieldedCents
            )

            DailyActionsSection(
                onAddIncome = {},
                onPayBill = {},
                onSaveMoney = {},
                onLogSpending = {},
                onViewBudgets = {}
            )

            RecentActivitySection(
                recentTransactions = transactions,
                onViewAll = {}
            )
        }
    }
}
