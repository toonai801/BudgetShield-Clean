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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.flowOf

import com.toonai.budgetshield.theme.BackgroundDark
import com.toonai.budgetshield.theme.PanelDark
import com.toonai.budgetshield.theme.PanelBorder
import com.toonai.budgetshield.theme.CyanAccent
import com.toonai.budgetshield.theme.GreenAccent
import com.toonai.budgetshield.theme.GoldAccent
import com.toonai.budgetshield.theme.BlueAccent
import com.toonai.budgetshield.theme.TextPrimary
import com.toonai.budgetshield.theme.TextMuted
import com.toonai.budgetshield.ui.LocalBillRepository
import com.toonai.budgetshield.data.model.Bill as BillEntity


@Composable
fun BillProtectedScreen(
    billId: Long? = null,
    onNavigateToHome: () -> Unit,
    onNavigateToTreasure: () -> Unit,
    onNavigateToShieldProgression: () -> Unit
) {
    val billRepository = LocalBillRepository.current
    val billFlow = if (billId != null) {
        billRepository.getBillByIdFlow(billId)
    } else {
        flowOf(null)
    }
    val billState = billFlow.collectAsState(initial = null)
    val bill = billState.value
    
    // Default values for when bill is not found
    val billName = bill?.name ?: "Protected Bill"
    val amountCents = bill?.amountCents ?: 0L
    val billAmount = if (amountCents > 0) {
        "${'$'}${amountCents / 100}.${(amountCents % 100).toString().padStart(2, '0')}"
    } else {
        "$0.00"
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
                    .padding(top = 48.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success Icon
                SuccessIcon()

                // Achievement Card
                AchievementCard(bill = bill)

                // XP Earned
                XPEarnedCard()

                // Streak Maintained
                StreakCard()

                // Quick Actions
                QuickActionsSection(
                    onHome = onNavigateToHome,
                    onTreasure = onNavigateToTreasure,
                    onProgression = onNavigateToShieldProgression
                )
            }
        }
    }
}

@Composable
private fun SuccessIcon() {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        CyanAccent.copy(alpha = 0.3f),
                        CyanAccent.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            CyanAccent.copy(alpha = 0.4f),
                            BlueAccent.copy(alpha = 0.2f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🛡️",
                fontSize = 48.sp
            )
        }

        // Sparkle decorations
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Text(
                text = "✨",
                fontSize = 24.sp
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        ) {
            Text(
                text = "⭐",
                fontSize = 20.sp
            )
        }
    }
}

@Composable
private fun AchievementCard(bill: BillEntity? = null) {
    // Default values for when bill is not found
    val billName = bill?.name ?: "Protected Bill"
    val billAmount = bill?.let { "${'$'}${it.amountCents / 100}.${(it.amountCents % 100).toString().padStart(2, '0')}" } ?: "$0.00"
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Bill Protected!",
            color = CyanAccent,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = billName,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "$billAmount paid on time",
            color = TextMuted,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun XPEarnedCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = GoldAccent.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "⭐",
                    fontSize = 28.sp
                )
                Text(
                    text = "+50 XP",
                    color = GoldAccent,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Text(
                text = "Earned for protected bill payment",
                color = TextMuted,
                fontSize = 14.sp
            )

            // XP Progress mini bar
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "245 → 295 XP",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "59%",
                        color = GoldAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(PanelBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.59f)
                            .fillMaxHeight()
                            .background(GoldAccent)
                    )
                }
            }
        }
    }
}

@Composable
private fun StreakCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFF6B35).copy(alpha = 0.3f),
                                    Color(0xFFFF6B35).copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔥",
                        fontSize = 28.sp
                    )
                }

                Column {
                    Text(
                        text = "12 Day Streak",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Keep the streak alive!",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }

            Text(
                text = "+25 XP",
                color = GoldAccent,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    onHome: () -> Unit,
    onTreasure: () -> Unit,
    onProgression: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "What's Next?",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        // Home Button
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = CyanAccent
            ),
            onClick = onHome
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🏠",
                    fontSize = 20.sp
                )
                Text(
                    text = "Go Home",
                    color = BackgroundDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Secondary Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PanelDark
                ),
                onClick = onTreasure
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "🗝️",
                        fontSize = 24.sp
                    )
                    Text(
                        text = "Treasure",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PanelDark
                ),
                onClick = onProgression
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "🛡️",
                        fontSize = 24.sp
                    )
                    Text(
                        text = "Progress",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
