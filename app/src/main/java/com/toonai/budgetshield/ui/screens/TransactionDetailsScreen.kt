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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Premium gamified dark theme colors (matching Home)
private val BackgroundDark = Color(0xFF02070D)
private val PanelDark = Color(0xFF06121D)
private val PanelBorder = Color(0xFF14364A)
private val CyanAccent = Color(0xFF17E8F2)
private val GreenAccent = Color(0xFF2FE6A7)
private val GoldAccent = Color(0xFFFFC545)
private val BlueAccent = Color(0xFF1678B9)
private val PurpleAccent = Color(0xFF9D4EDD)
private val OrangeAccent = Color(0xFFFF8C42)
private val DangerColor = Color(0xFFFF553D)
private val TextPrimary = Color(0xFFF4F7FB)
private val TextMuted = Color(0xFFA6B1BF)

@Composable
fun TransactionDetailsScreen(
    transactionId: Long? = null,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToTreasure: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToGoals: () -> Unit
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                HeaderSection(transactionId = transactionId, onBack = onNavigateBack)

                // Transaction Detail Card
                TransactionDetailCard()

                // Recent Transactions
                RecentTransactionsSection()

                // Quick Navigation
                QuickNavSection(
                    onHome = onNavigateToHome,
                    onTreasure = onNavigateToTreasure,
                    onStats = onNavigateToStats,
                    onGoals = onNavigateToGoals
                )
            }
        }
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
private fun TransactionDetailCard() {
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
                        text = "-$950.00",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "🛡️",
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Protected",
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
                DetailRow("Date", "July 6, 2025")
                DetailRow("Time", "2:34 PM")
                DetailRow("Payment Method", "Bank Account ****4567")
                DetailRow("Transaction ID", "TXN-2025-07-06-789")
                DetailRow("Status", "Completed ✓")
            }

            // XP Earned
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
                            text = "XP Earned",
                            color = TextPrimary,
                            fontSize = 14.sp
                        )
                    }

                    Text(
                        text = "+50 XP",
                        color = GoldAccent,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
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
private fun RecentTransactionsSection() {
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

            // Transaction items
            TransactionItem(
                icon = "🏠",
                iconBg = BlueAccent,
                name = "Rent Payment",
                category = "Housing",
                amount = "-$950.00",
                date = "Jul 6",
                isNegative = true
            )

            TransactionItem(
                icon = "💰",
                iconBg = GreenAccent,
                name = "Paycheck",
                category = "Income",
                amount = "+$2,400.00",
                date = "Jul 5",
                isNegative = false
            )

            TransactionItem(
                icon = "🏦",
                iconBg = GoldAccent,
                name = "Savings Transfer",
                category = "Savings",
                amount = "-$300.00",
                date = "Jul 5",
                isNegative = true
            )

            TransactionItem(
                icon = "⚡",
                iconBg = OrangeAccent,
                name = "Utilities",
                category = "Bills",
                amount = "-$145.50",
                date = "Jul 4",
                isNegative = true
            )

            TransactionItem(
                icon = "🍔",
                iconBg = PurpleAccent,
                name = "Grocery Store",
                category = "Food",
                amount = "-$85.25",
                date = "Jul 3",
                isNegative = true
            )
        }
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
    isNegative: Boolean
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

@Composable
private fun QuickNavSection(
    onHome: () -> Unit,
    onTreasure: () -> Unit,
    onStats: () -> Unit,
    onGoals: () -> Unit
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
                text = "Quick Navigation",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NavCard(
                    modifier = Modifier.weight(1f),
                    icon = "🏠",
                    label = "Home",
                    onClick = onHome
                )

                NavCard(
                    modifier = Modifier.weight(1f),
                    icon = "🗝️",
                    label = "Treasure",
                    onClick = onTreasure
                )

                NavCard(
                    modifier = Modifier.weight(1f),
                    icon = "📊",
                    label = "Stats",
                    onClick = onStats
                )

                NavCard(
                    modifier = Modifier.weight(1f),
                    icon = "🎯",
                    label = "Goals",
                    onClick = onGoals
                )
            }
        }
    }
}

@Composable
private fun NavCard(
    modifier: Modifier = Modifier,
    icon: String,
    label: String,
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
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = icon,
                fontSize = 22.sp
            )
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
