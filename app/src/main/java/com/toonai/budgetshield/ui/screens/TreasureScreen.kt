package com.toonai.budgetshield.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Premium dark fantasy gamified theme - Treasure Hub Edition
private val BackgroundDark = Color(0xFF02070D)
private val BackgroundDeeper = Color(0xFF010509)
private val PanelDark = Color(0xFF06121D)
private val PanelBorder = Color(0xFF1A3A4A)
private val CyanAccent = Color(0xFF17E8F2)
private val CyanGlow = Color(0xFF5DEEFF)
private val CyanDark = Color(0xFF0D4B5C)
private val GoldAccent = Color(0xFFFFC545)
private val GoldGlow = Color(0xFFFFE066)
private val GoldDark = Color(0xFFB8860B)
private val PurpleAccent = Color(0xFF9D4EDD)
private val PurpleGlow = Color(0xFFE0AAFF)
private val GreenAccent = Color(0xFF2FE6A7)
private val RedAccent = Color(0xFFFF6B6B)
private val TextPrimary = Color(0xFFF4F7FB)
private val TextMuted = Color(0xFFA6B1BF)
private val TextDim = Color(0xFF6B7B8C)

// Section expansion states
data class TreasureSections(
    val chestsExpanded: Boolean = true,
    val achievementsExpanded: Boolean = false,
    val xpExpanded: Boolean = false,
    val streaksExpanded: Boolean = false,
    val historyExpanded: Boolean = false
)

@Composable
fun TreasureScreen(
    onNavigateToHome: () -> Unit = {}
) {
    var sections by remember { mutableStateOf(TreasureSections()) }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Deep space background with subtle gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BackgroundDark,
                            Color(0xFF06121D),
                            Color(0xFF081525),
                            BackgroundDeeper
                        )
                    )
                )
                .drawBehind {
                    // Subtle star field effect
                    for (i in 0..20) {
                        val x = (size.width * (i * 0.047f + 0.13f)) % size.width
                        val y = (size.height * (i * 0.063f + 0.07f)) % size.height
                        val alpha = 0.15f + (i % 5) * 0.03f
                        drawCircle(
                            color = CyanAccent.copy(alpha = alpha),
                            radius = (1 + i % 2).dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
        )

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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Treasure Hub Header
                TreasureHeader(onNavigateToHome = onNavigateToHome)

                // XP & Shield Level (always visible)
                XpAndLevelCard()

                // Current Streak
                StreakCard()

                // Expandable: Treasure Chests
                ExpandableTreasureSection(
                    title = "Treasure Chests",
                    icon = "🎁",
                    badge = "3",
                    isExpanded = sections.chestsExpanded,
                    onToggle = { sections = sections.copy(chestsExpanded = !sections.chestsExpanded) }
                ) {
                    TreasureChestsContent()
                }

                // Expandable: Achievements
                ExpandableTreasureSection(
                    title = "Achievements",
                    icon = "🏆",
                    badge = null,
                    isExpanded = sections.achievementsExpanded,
                    onToggle = { sections = sections.copy(achievementsExpanded = !sections.achievementsExpanded) }
                ) {
                    AchievementsContent()
                }

                // Expandable: Reward History
                ExpandableTreasureSection(
                    title = "Reward History",
                    icon = "📜",
                    badge = null,
                    isExpanded = sections.historyExpanded,
                    onToggle = { sections = sections.copy(historyExpanded = !sections.historyExpanded) }
                ) {
                    RewardHistoryContent()
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TreasureHeader(onNavigateToHome: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Animated treasure chest icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = GoldAccent
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                GoldAccent.copy(alpha = 0.25f),
                                GoldDark.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💎",
                    fontSize = 32.sp
                )
            }

            Column {
                Text(
                    text = "Treasure Vault",
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Your Rewards & Achievements",
                    color = GoldAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Close/Back button
        TextButton(
            onClick = onNavigateToHome,
            contentPadding = PaddingValues(8.dp)
        ) {
            Text(
                text = "✕",
                color = TextMuted,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
private fun XpAndLevelCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            CyanDark.copy(alpha = 0.4f),
                            Color(0xFF0D2430),
                            BackgroundDeeper
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            CyanAccent.copy(alpha = 0.4f),
                            PurpleAccent.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
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
                        // Level badge
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            CyanAccent.copy(alpha = 0.3f),
                                            CyanDark.copy(alpha = 0.2f)
                                        )
                                    )
                                )
                                .border(
                                    width = 2.dp,
                                    color = CyanAccent.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "LVL",
                                    color = CyanAccent.copy(alpha = 0.7f),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "—",
                                    color = CyanAccent,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Shield Level",
                                color = CyanAccent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Coming Soon",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // XP display
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "XP",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "— / —",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Progress bar area
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Progress to Next Level",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    // XP progress bar
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Background track
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color(0xFF14364A))
                        )
                        // Glowing progress fill
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.0f)
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            CyanAccent.copy(alpha = 0.6f),
                                            CyanGlow.copy(alpha = 0.8f),
                                            CyanAccent
                                        )
                                    )
                                )
                        )
                    }

                    Text(
                        text = "Complete bill payments and savings to earn XP",
                        color = TextDim,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StreakCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            PurpleAccent.copy(alpha = 0.15f),
                            Color(0xFF0D1B26)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = PurpleAccent.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Streak flame
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        RedAccent.copy(alpha = 0.3f),
                                        GoldAccent.copy(alpha = 0.1f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .border(
                                width = 2.dp,
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        GoldAccent.copy(alpha = 0.5f),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
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
                            text = "Current Streak",
                            color = PurpleAccent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "No active streak",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Save money daily to build your streak",
                            color = TextDim,
                            fontSize = 10.sp
                        )
                    }
                }

                // Streak count display
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "—",
                        color = TextPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "days",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpandableTreasureSection(
    title: String,
    icon: String,
    badge: String?,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        ),
        onClick = onToggle
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
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
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0D1B26)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = icon,
                            fontSize = 20.sp
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = title,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            badge?.let {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(GoldAccent.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = it,
                                        color = GoldAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Expand/collapse indicator
                val rotation by animateFloatAsState(
                    targetValue = if (isExpanded) 180f else 0f,
                    animationSpec = tween(200)
                )
                Text(
                    text = "▼",
                    color = CyanAccent,
                    fontSize = 14.sp,
                    modifier = Modifier.graphicsLayer { rotationZ = rotation }
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    content()
                }
            }
        }
    }
}

@Composable
private fun TreasureChestsContent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Empty state - no treasures unlocked yet
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0A1620)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🎁",
                    fontSize = 32.sp
                )
                Text(
                    text = "No treasures unlocked yet",
                    color = TextMuted,
                    fontSize = 13.sp
                )
                Text(
                    text = "Pay bills and reach savings goals to unlock chests",
                    color = TextDim,
                    fontSize = 11.sp
                )
            }
        }

        // Locked chest preview
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LockedChestPreview("Bronze", GoldDark, Modifier.weight(1f))
            LockedChestPreview("Silver", Color(0xFFC0C0C0), Modifier.weight(1f))
            LockedChestPreview("Gold", GoldAccent, Modifier.weight(1f))
        }
    }
}

@Composable
private fun LockedChestPreview(name: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF0D1B26))
                .border(
                    width = 1.dp,
                    color = color.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🔒",
                    fontSize = 20.sp
                )
                Text(
                    text = name,
                    color = color.copy(alpha = 0.6f),
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun AchievementsContent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Achievement empty state
        AchievementItem(
            icon = "🛡️",
            name = "Bill Protector",
            description = "Protect your first bill",
            progress = 0,
            total = 1,
            color = GreenAccent
        )
        
        AchievementItem(
            icon = "💰",
            name = "Savings Starter",
            description = "Make your first savings contribution",
            progress = 0,
            total = 1,
            color = GoldAccent
        )
        
        AchievementItem(
            icon = "🔥",
            name = "Streak Keeper",
            description = "Maintain a 7-day savings streak",
            progress = 0,
            total = 7,
            color = RedAccent
        )
    }
}

@Composable
private fun AchievementItem(
    icon: String,
    name: String,
    description: String,
    progress: Int,
    total: Int,
    color: Color
) {
    val isCompleted = progress >= total
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0D1B26))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Achievement icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (isCompleted) color.copy(alpha = 0.2f)
                    else Color(0xFF14364A)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 20.sp,
                modifier = Modifier.alpha(if (isCompleted) 1f else 0.5f)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    color = if (isCompleted) TextPrimary else TextMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(color.copy(alpha = 0.2f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "✓",
                            color = color,
                            fontSize = 9.sp
                        )
                    }
                }
            }
            
            Text(
                text = description,
                color = TextDim,
                fontSize = 11.sp
            )

            // Progress bar
            if (!isCompleted && total > 1) {
                LinearProgressIndicator(
                    progress = { progress.toFloat() / total },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = color,
                    trackColor = Color(0xFF14364A),
                    strokeCap = StrokeCap.Round
                )
            }
        }

        // Progress count
        Text(
            text = "$progress/$total",
            color = if (isCompleted) color else TextDim,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun RewardHistoryContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0A1620)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "📜",
                fontSize = 24.sp
            )
            Text(
                text = "No rewards earned yet",
                color = TextMuted,
                fontSize = 13.sp
            )
            Text(
                text = "Your reward history will appear here",
                color = TextDim,
                fontSize = 11.sp
            )
        }
    }
}
