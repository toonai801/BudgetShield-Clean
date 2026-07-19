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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
private data class TreasureSections(
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
                    isExpanded = sections.chestsExpanded,
                    onToggle = { sections = sections.copy(chestsExpanded = !sections.chestsExpanded) }
                ) {
                    TreasureChestsContent()
                }

                // Expandable: Achievements
                ExpandableTreasureSection(
                    title = "Achievements",
                    isExpanded = sections.achievementsExpanded,
                    onToggle = { sections = sections.copy(achievementsExpanded = !sections.achievementsExpanded) }
                ) {
                    AchievementsContent()
                }

                // Expandable: Reward History
                ExpandableTreasureSection(
                    title = "Reward History",
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
            // Treasure chest icon - Canvas drawn
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                GoldAccent.copy(alpha = 0.25f),
                                GoldDark.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = GoldAccent.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Simple chest shape drawn with Canvas
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .drawBehind {
                            // Chest base
                            drawRoundRect(
                                color = GoldAccent.copy(alpha = 0.8f),
                                size = size,
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                            )
                            // Chest lid line
                            drawLine(
                                color = GoldDark,
                                start = Offset(0f, size.height * 0.35f),
                                end = Offset(size.width, size.height * 0.35f),
                                strokeWidth = 2.dp.toPx()
                            )
                            // Lock
                            drawCircle(
                                color = GoldGlow,
                                radius = 4.dp.toPx(),
                                center = Offset(size.width / 2, size.height * 0.55f)
                            )
                        }
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
                text = "<",
                color = TextMuted,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
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
                        // Level badge - Canvas drawn shield
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
                            // Shield shape
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .drawBehind {
                                        val path = androidx.compose.ui.graphics.Path().apply {
                                            val width = size.width
                                            val height = size.height
                                            moveTo(width / 2, 0f)
                                            lineTo(width, height * 0.25f)
                                            lineTo(width, height * 0.6f)
                                            quadraticBezierTo(
                                                width * 0.5f, height,
                                                0f, height * 0.6f
                                            )
                                            lineTo(0f, height * 0.25f)
                                            close()
                                        }
                                        drawPath(
                                            path = path,
                                            color = CyanAccent.copy(alpha = 0.6f),
                                            style = Stroke(width = 2.dp.toPx())
                                        )
                                    }
                            )
                        }

                        Column {
                            Text(
                                text = "Shield Level",
                                color = CyanAccent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "No XP records",
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

                    // XP progress bar - empty state
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Background track only
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color(0xFF14364A))
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
                    // Streak flame - Canvas drawn
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
                        // Simple flame shape
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .drawBehind {
                                    val flamePath = androidx.compose.ui.graphics.Path().apply {
                                        val w = size.width
                                        val h = size.height
                                        moveTo(w / 2, 0f)
                                        // Left curve
                                        quadraticBezierTo(w * 0.2f, h * 0.4f, w * 0.3f, h * 0.8f)
                                        // Bottom
                                        lineTo(w * 0.7f, h * 0.8f)
                                        // Right curve
                                        quadraticBezierTo(w * 0.8f, h * 0.4f, w / 2, 0f)
                                        close()
                                    }
                                    drawPath(
                                        path = flamePath,
                                        color = GoldAccent.copy(alpha = 0.7f),
                                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                }
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
                            text = "No streak records",
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
                    // Section icon placeholder - simple circle
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0D1B26)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Simple indicator based on section title
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .drawBehind {
                                    when (title) {
                                        "Treasure Chests" -> {
                                            // Small chest indicator
                                            drawRoundRect(
                                                color = GoldAccent.copy(alpha = 0.6f),
                                                size = size,
                                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
                                            )
                                        }
                                        "Achievements" -> {
                                            // Trophy/star shape
                                            drawCircle(
                                                color = GoldAccent.copy(alpha = 0.6f),
                                                radius = size.minDimension / 2 - 2.dp.toPx()
                                            )
                                        }
                                        "Reward History" -> {
                                            // Scroll indicator
                                            drawRoundRect(
                                                color = CyanAccent.copy(alpha = 0.6f),
                                                size = size.copy(height = size.height * 0.8f),
                                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.dp.toPx())
                                            )
                                        }
                                        else -> {
                                            drawCircle(
                                                color = TextMuted.copy(alpha = 0.4f),
                                                radius = size.minDimension / 3
                                            )
                                        }
                                    }
                                }
                        )
                    }

                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
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
        // Honest empty state - no collectibles recorded
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
                // Chest icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .drawBehind {
                            drawRoundRect(
                                color = GoldAccent.copy(alpha = 0.4f),
                                size = size,
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                            )
                            drawLine(
                                color = GoldDark,
                                start = Offset(0f, size.height * 0.35f),
                                end = Offset(size.width, size.height * 0.35f),
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                )
                Text(
                    text = "No collectibles recorded",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun AchievementsContent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Honest empty state - no achievements recorded
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
                // Achievement icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .drawBehind {
                            drawCircle(
                                color = GoldAccent.copy(alpha = 0.4f),
                                radius = size.minDimension / 2 - 2.dp.toPx()
                            )
                        }
                )
                Text(
                    text = "No achievements recorded",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            }
        }
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
            // History icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .drawBehind {
                        drawRoundRect(
                            color = CyanAccent.copy(alpha = 0.4f),
                            size = size.copy(height = size.height * 0.8f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
                        )
                    }
            )
            Text(
                text = "No reward history",
                color = TextMuted,
                fontSize = 13.sp
            )
        }
    }
}
