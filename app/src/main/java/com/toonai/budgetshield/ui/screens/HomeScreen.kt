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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    onNavigateToShieldProgression: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BackgroundDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Main content with padding
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    HeaderSection()

                    // Month selector
                    MonthSelector()

                    // Hero Safe Now card
                    HeroCard(
                        safeAmount = "$1,250",
                        onNavigateToShieldProgression = onNavigateToShieldProgression
                    )

                    // Stats cards row
                    StatsCardsRow()

                    // Daily Actions section
                    DailyActionsSection(
                        onAddIncome = onNavigateToIncomeEntry,
                        onPayBill = onNavigateToBillEntry,
                        onSaveMoney = onNavigateToSavingsEntry
                    )

                    // Recent Activity section
                    RecentActivitySection(
                        onViewAll = onNavigateToTransactionDetails
                    )
                }
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
        // Logo with shield
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Shield icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(CyanAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "\uD83D\uDEE1️",
                    fontSize = 18.sp
                )
            }

            // Logo text
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

        // Right icons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reward coin with notification dot
            Box(
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GoldAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "\uD83E\uDE99",
                        fontSize = 18.sp
                    )
                }
                // Notification dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(DangerDot)
                        .padding(1.dp)
                )
            }

            // Menu icon
            IconButton(onClick = { }) {
                Text(
                    text = "\u2630",
                    color = TextPrimary,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
private fun MonthSelector() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left chevron
            IconButton(onClick = { }) {
                Text(
                    text = "\u2039",
                    color = TextMuted,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Month with dropdown
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "July 2025",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "\u25BC",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            // Right chevron
            IconButton(onClick = { }) {
                Text(
                    text = "\u203A",
                    color = TextMuted,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Calendar button
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PanelDark
                )
            ) {
                IconButton(onClick = { }) {
                    Text(
                        text = "\uD83D\uDCC5",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroCard(
    safeAmount: String,
    onNavigateToShieldProgression: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A1F2C)
        ),
        onClick = onNavigateToShieldProgression
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    // Cyan glow border effect
                    drawRect(
                        color = CyanAccent.copy(alpha = 0.3f),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
                .padding(20.dp)
        ) {
            // Background treasure illustration placeholder
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
                Text(
                    text = "\uD83E\uDDF0",
                    fontSize = 48.sp
                )
            }

            // Content
            Column(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Safe Now label
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "\uD83D\uDEE1️",
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Safe Now",
                        color = CyanAccent,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Amount
                Text(
                    text = safeAmount,
                    color = TextPrimary,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                // Money Shield label
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "\uD83D\uDEE1️",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Money Shield",
                        color = CyanAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Supporting text
                Text(
                    text = "You're protected and in control.",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun StatsCardsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Bills Protected card
        StatsCard(
            modifier = Modifier.weight(1f),
            icon = "\uD83D\uDEE1️",
            title = "Bills Protected",
            progressValue = "85%",
            progressColor = CyanAccent,
            subtitle = "$1,700 of $2,000",
            progress = 0.85f
        )

        // Savings Streak card
        StatsCard(
            modifier = Modifier.weight(1f),
            icon = "\u2B50",
            title = "Savings Streak",
            progressValue = "12",
            progressColor = GoldAccent,
            subtitle = "days",
            subtitle2 = "Best: 18 days",
            progress = 0.67f
        )

        // Wants Left card
        StatsCard(
            modifier = Modifier.weight(1f),
            icon = "\uD83C\uDF81",
            title = "Wants Left",
            progressValue = "40%",
            progressColor = GreenAccent,
            subtitle = "$120 of $300",
            progress = 0.40f
        )
    }
}

@Composable
private fun StatsCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    progressValue: String,
    progressColor: Color,
    subtitle: String,
    subtitle2: String? = null,
    progress: Float
) {
    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Title with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = icon, fontSize = 10.sp)
                Text(
                    text = title,
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Progress ring placeholder
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background ring
                CircularProgressIndicator(
                    progress = 1f,
                    color = PanelBorder,
                    strokeWidth = 4.dp
                )
                // Progress ring
                CircularProgressIndicator(
                    progress = progress,
                    color = progressColor,
                    strokeWidth = 4.dp
                )
                // Center value
                if (subtitle2 != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = progressValue,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = subtitle,
                            color = progressColor,
                            fontSize = 8.sp
                        )
                    }
                } else {
                    Text(
                        text = progressValue,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 10.sp
                )
                if (subtitle2 != null) {
                    Text(
                        text = subtitle2,
                        color = TextMuted,
                        fontSize = 9.sp
                    )
                }
            }

            // Chevron
            Text(
                text = "\u203A",
                color = TextMuted,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun CircularProgressIndicator(
    progress: Float,
    color: Color,
    strokeWidth: androidx.compose.ui.unit.Dp
) {
    // Simple circular progress using Canvas would be ideal
    // For now using a box representation
    Box(
        modifier = Modifier
            .size(48.dp)
            .drawBehind {
                // Draw arc for progress
                val sweepAngle = progress * 360f
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth.toPx())
                )
            }
    )
}

@Composable
private fun DailyActionsSection(
    onAddIncome: () -> Unit,
    onPayBill: () -> Unit,
    onSaveMoney: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Daily Actions",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "\u2728", fontSize = 14.sp)
                }

                TextButton(onClick = { }) {
                    Text(
                        text = "See All \u203A",
                        color = CyanAccent,
                        fontSize = 14.sp
                    )
                }
            }

            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = "\u2B07\uFE0F",
                    iconBg = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2A9D8F), Color(0xFF264653))
                    ),
                    title = "Add Income",
                    subtitle = "Track money in",
                    onClick = onAddIncome
                )

                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = "\uD83D\uDCC4",
                    iconBg = Brush.verticalGradient(
                        colors = listOf(Color(0xFF4A90D9), Color(0xFF1E3A5F))
                    ),
                    title = "Pay Bill",
                    subtitle = "Stay protected",
                    onClick = onPayBill
                )

                ActionButton(
                    modifier = Modifier.weight(1f),
                    icon = "\uD83C\uDFFA",
                    iconBg = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2FE6A7), Color(0xFF1A5F4A))
                    ),
                    title = "Save Money",
                    subtitle = "Build your stash",
                    onClick = onSaveMoney
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
    icon: String,
    iconBg: Brush,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0D1B26)
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Icon background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 24.sp
                )
            }

            // Title
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            // Subtitle
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun RecentActivitySection(
    onViewAll: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Activity",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onViewAll) {
                    Text(
                        text = "View All \u203A",
                        color = CyanAccent,
                        fontSize = 14.sp
                    )
                }
            }

            // Activity items
            ActivityItem(
                icon = "\uD83D\uDEE1️",
                iconBg = BlueAccent,
                title = "Rent Payment",
                category = "Bills",
                amount = "-$950.00",
                amountColor = TextPrimary,
                date = "Jul 6"
            )

            ActivityItem(
                icon = "\u2B07\uFE0F",
                iconBg = GreenAccent,
                title = "Paycheck",
                category = "Income",
                amount = "+$2,400.00",
                amountColor = GreenAccent,
                date = "Jul 5"
            )

            ActivityItem(
                icon = "\u2B50",
                iconBg = GoldAccent,
                title = "Streak Bonus",
                category = "Savings",
                amount = "+$10.00",
                amountColor = GreenAccent,
                date = "Jul 5"
            )
        }
    }
}

@Composable
private fun ActivityItem(
    icon: String,
    iconBg: Color,
    title: String,
    category: String,
    amount: String,
    amountColor: Color,
    date: String
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
            // Icon
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

            // Title and category
            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = category,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Amount and date
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = amount,
                color = amountColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = date,
                color = TextMuted,
                fontSize = 12.sp
            )
        }
    }
}

