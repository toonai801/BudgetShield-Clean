package com.toonai.budgetshield.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
private val TextPrimary = Color(0xFFF4F7FB)
private val TextMuted = Color(0xFFA6B1BF)

@Composable
fun ShieldProgressionScreen() {
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
                HeaderSection()

                // Current Level
                CurrentLevelCard()

                // XP Progress
                XPProgressCard()

                // Level Rewards
                LevelRewardsSection()

                // XP History
                XPHistorySection()
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
                    text = "🛡️",
                    fontSize = 20.sp
                )
            }

            Column {
                Text(
                    text = "Shield Progression",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Level up by protecting bills",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun CurrentLevelCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A1F2C)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            CyanAccent.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Level 1",
                            color = CyanAccent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Novice Shield",
                            color = TextPrimary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(CyanAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🛡️",
                            fontSize = 36.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🔥",
                        fontSize = 16.sp
                    )
                    Text(
                        text = "12 Day Streak",
                        color = GoldAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun XPProgressCard() {
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
                Column {
                    Text(
                        text = "XP Progress",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "245 / 500 XP to next level",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(GoldAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "49%",
                            color = GoldAccent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(PanelBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.49f)
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(CyanAccent, GreenAccent)
                            )
                        )
                )
            }

            // XP needed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Current: 245 XP",
                    color = TextMuted,
                    fontSize = 12.sp
                )
                Text(
                    text = "Need: 255 XP",
                    color = CyanAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun LevelRewardsSection() {
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
                text = "Level Rewards",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // Level progression
            LevelItem(
                level = 1,
                name = "Novice Shield",
                xpRequired = "0 XP",
                status = LevelStatus.CURRENT,
                rewards = listOf("Basic protection", "Daily streak tracking")
            )

            LevelItem(
                level = 2,
                name = "Apprentice Shield",
                xpRequired = "500 XP",
                status = LevelStatus.NEXT,
                rewards = listOf("XP boost +10%", "Custom themes")
            )

            LevelItem(
                level = 3,
                name = "Guardian Shield",
                xpRequired = "1500 XP",
                status = LevelStatus.LOCKED,
                rewards = listOf("XP boost +25%", "Priority support")
            )

            LevelItem(
                level = 4,
                name = "Champion Shield",
                xpRequired = "3000 XP",
                status = LevelStatus.LOCKED,
                rewards = listOf("XP boost +50%", "Exclusive badge")
            )
        }
    }
}

enum class LevelStatus {
    CURRENT, NEXT, LOCKED
}

@Composable
private fun LevelItem(
    level: Int,
    name: String,
    xpRequired: String,
    status: LevelStatus,
    rewards: List<String>
) {
    val (bgColor, borderColor, icon) = when (status) {
        LevelStatus.CURRENT -> Triple(
            CyanAccent.copy(alpha = 0.15f),
            CyanAccent.copy(alpha = 0.5f),
            "✓"
        )
        LevelStatus.NEXT -> Triple(
            GoldAccent.copy(alpha = 0.1f),
            GoldAccent.copy(alpha = 0.3f),
            "→"
        )
        LevelStatus.LOCKED -> Triple(
            PanelBorder.copy(alpha = 0.5f),
            PanelBorder,
            "🔒"
        )
    }

    val textColor = when (status) {
        LevelStatus.CURRENT -> CyanAccent
        LevelStatus.NEXT -> GoldAccent
        LevelStatus.LOCKED -> TextMuted
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = bgColor
        ),
        border = BorderStroke(1.dp, borderColor)
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
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(textColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (status == LevelStatus.LOCKED) icon else level.toString(),
                        color = textColor,
                        fontSize = if (status == LevelStatus.LOCKED) 16.sp else 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text(
                        text = name,
                        color = if (status == LevelStatus.LOCKED) TextMuted else TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = rewards.joinToString(" • "),
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Text(
                text = xpRequired,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun XPHistorySection() {
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
                text = "Recent XP Earned",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            XPHistoryItem(
                icon = "🛡️",
                action = "Protected Rent Bill",
                xp = "+50 XP",
                date = "Today",
                color = CyanAccent
            )

            XPHistoryItem(
                icon = "💰",
                action = "Added Savings",
                xp = "+25 XP",
                date = "Yesterday",
                color = GreenAccent
            )

            XPHistoryItem(
                icon = "🔥",
                action = "7-Day Streak Bonus",
                xp = "+100 XP",
                date = "2 days ago",
                color = GoldAccent
            )

            XPHistoryItem(
                icon = "📊",
                action = "Completed Weekly Review",
                xp = "+20 XP",
                date = "3 days ago",
                color = BlueAccent
            )

            XPHistoryItem(
                icon = "🎯",
                action = "Reached Savings Goal",
                xp = "+50 XP",
                date = "Last week",
                color = PurpleAccent
            )
        }
    }
}

@Composable
private fun XPHistoryItem(
    icon: String,
    action: String,
    xp: String,
    date: String,
    color: Color
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
                    .size(36.dp)
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
                    text = action,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = date,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Text(
            text = xp,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
