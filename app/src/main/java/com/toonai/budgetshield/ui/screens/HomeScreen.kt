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
                    text = "🛡️",
                    fontSize = 18.sp
                )
            }

            Text(
                text = "BudgetShield",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Settings shortcut
        IconButton(onClick = { }) {
            Text(
                text = "⚙️",
                fontSize = 20.sp
            )
        }
    }
}

@Composable
private fun MonthSelector() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = { }) {
            Text(
                text = "‹",
                color = TextMuted,
                fontSize = 20.sp
            )
        }

        Text(
            text = "July 2026",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        TextButton(onClick = { }) {
            Text(
                text = "›",
                color = TextMuted,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
private fun HeroCard(
    safeAmount: String,
    onNavigateToShieldProgression: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    // Animated gradient background effect
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0A1E2E),
                                Color(0xFF06121D),
                                Color(0xFF051018)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, size.height)
                        )
                    )

                    // Glowing border effect
                    drawCircle(
                        color = CyanAccent.copy(alpha = 0.08f),
                        radius = size.width * 0.4f,
                        center = Offset(size.width * 0.8f, size.height * 0.3f)
                    )
                }
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Shield badge row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Active Shield badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            CyanAccent.copy(alpha = 0.3f),
                                            CyanAccent.copy(alpha = 0.1f)
                                        )
                                    )
                                )
                                .drawBehind {
                                    drawCircle(
                                        color = CyanAccent,
                                        radius = 18.dp.toPx(),
                                        style = Stroke(width = 2.dp.toPx())
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🛡️",
                                fontSize = 20.sp
                            )
                        }

                        Column {
                            Text(
                                text = "Active Shield",
                                color = CyanAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Level 3 • 85% XP",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Streak indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "🔥",
                            fontSize = 16.sp
                        )
                        Text(
                            text = "12",
                            color = GoldAccent,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Safe Now amount
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Safe Now",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                    Text(
                        text = safeAmount,
                        color = CyanAccent,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // XP Progress bar
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "XP to Level 4",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "850 / 1000",
                            color = CyanSoft,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(PanelBorder)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(CyanSoft, CyanAccent)
                                    )
                                )
                        )
                    }
                }

                // Tap to level up hint
                TextButton(
                    onClick = onNavigateToShieldProgression,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Tap to Level Up →",
                        color = CyanAccent,
                        fontSize = 13.sp
                    )
                }
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
        StatCard(
            modifier = Modifier.weight(1f),
            icon = "💰",
            value = "$2,450",
            label = "Earned",
            color = GreenAccent
        )

        StatCard(
            modifier = Modifier.weight(1f),
            icon = "🎯",
            value = "$180",
            label = "Saved",
            color = GoldAccent
        )

        StatCard(
            modifier = Modifier.weight(1f),
            icon = "✅",
            value = "8",
            label = "Bills Paid",
            color = CyanAccent
        )
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                text = "Daily Actions",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = "💵",
                    title = "Add Income",
                    subtitle = "Get paid",
                    color = GreenAccent,
                    onClick = onAddIncome
                )

                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = "🧾",
                    title = "Pay Bill",
                    subtitle = "Stay protected",
                    color = CyanAccent,
                    onClick = onPayBill
                )

                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = "🏦",
                    title = "Save",
                    subtitle = "Build stash",
                    color = GoldAccent,
                    onClick = onSaveMoney
                )
            }
        }
    }
}

@Composable
private fun ActionCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    subtitle: String,
    color: Color,
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 22.sp
                )
            }

            Text(
                text = title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

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
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Activity",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onViewAll) {
                    Text(
                        text = "View All →",
                        color = CyanAccent,
                        fontSize = 13.sp
                    )
                }
            }

            // Activity items
            ActivityItem(
                icon = "🎮",
                title = "XP Earned",
                subtitle = "Paid Rent Early",
                amount = "+50 XP",
                isPositive = true
            )

            ActivityItem(
                icon = "🛡️",
                title = "Shield Activated",
                subtitle = "All Bills Protected",
                amount = "",
                isPositive = true
            )

            ActivityItem(
                icon = "⚠️",
                title = "Payment Due",
                subtitle = "Electric Bill in 2 Days",
                amount = "$89.00",
                isPositive = false,
                showAlert = true
            )
        }
    }
}

@Composable
private fun ActivityItem(
    icon: String,
    title: String,
    subtitle: String,
    amount: String,
    isPositive: Boolean,
    showAlert: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (showAlert) DangerDot.copy(alpha = 0.15f)
                        else PanelBorder.copy(alpha = 0.5f)
                    ),
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

        if (amount.isNotEmpty()) {
            Text(
                text = amount,
                color = when {
                    showAlert -> DangerDot
                    isPositive -> GreenAccent
                    else -> TextPrimary
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
