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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Premium dark fantasy gamified theme - Treasure Hub Edition
private val BackgroundDark = Color(0xFF02070D)
private val BackgroundDeeper = Color(0xFF010509)
private val PanelDark = Color(0xFF06121D)
private val CyanAccent = Color(0xFF17E8F2)
private val CyanDark = Color(0xFF0D4B5C)
private val GoldAccent = Color(0xFFFFC545)
private val GoldDark = Color(0xFFB8860B)
private val GoldGlow = Color(0xFFFFE066)
private val PurpleAccent = Color(0xFF9D4EDD)
private val RedAccent = Color(0xFFFF6B6B)
private val TextPrimary = Color(0xFFF4F7FB)
private val TextMuted = Color(0xFFA6B1BF)
private val TextDim = Color(0xFF6B7B8C)

// Section expansion states - all five sections
private data class TreasureSections(
    val xpExpanded: Boolean = false,
    val streaksExpanded: Boolean = false,
    val chestsExpanded: Boolean = false,
    val achievementsExpanded: Boolean = false,
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Treasure Hub Header
                TreasureHeader(onNavigateToHome = onNavigateToHome)

                // Expandable: XP & Shield Level
                ExpandableTreasureSection(
                    title = "XP & Shield Level",
                    isExpanded = sections.xpExpanded,
                    onToggle = { sections = sections.copy(xpExpanded = !sections.xpExpanded) },
                    icon = SectionIcon.XP
                ) {
                    XpContent()
                }

                // Expandable: Current Streak
                ExpandableTreasureSection(
                    title = "Current Streak",
                    isExpanded = sections.streaksExpanded,
                    onToggle = { sections = sections.copy(streaksExpanded = !sections.streaksExpanded) },
                    icon = SectionIcon.STREAK
                ) {
                    StreakContent()
                }

                // Expandable: Treasure Chests
                ExpandableTreasureSection(
                    title = "Treasure Chests",
                    isExpanded = sections.chestsExpanded,
                    onToggle = { sections = sections.copy(chestsExpanded = !sections.chestsExpanded) },
                    icon = SectionIcon.CHEST
                ) {
                    ChestsContent()
                }

                // Expandable: Achievements
                ExpandableTreasureSection(
                    title = "Achievements",
                    isExpanded = sections.achievementsExpanded,
                    onToggle = { sections = sections.copy(achievementsExpanded = !sections.achievementsExpanded) },
                    icon = SectionIcon.ACHIEVEMENT
                ) {
                    AchievementsContent()
                }

                // Expandable: Reward History
                ExpandableTreasureSection(
                    title = "Reward History",
                    isExpanded = sections.historyExpanded,
                    onToggle = { sections = sections.copy(historyExpanded = !sections.historyExpanded) },
                    icon = SectionIcon.HISTORY
                ) {
                    HistoryContent()
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

private enum class SectionIcon {
    XP, STREAK, CHEST, ACHIEVEMENT, HISTORY
}

@Composable
private fun ExpandableTreasureSection(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    icon: SectionIcon,
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
                    // Section icon - Canvas drawn
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when (icon) {
                                    SectionIcon.XP -> CyanDark.copy(alpha = 0.3f)
                                    SectionIcon.STREAK -> PurpleAccent.copy(alpha = 0.2f)
                                    SectionIcon.CHEST -> GoldDark.copy(alpha = 0.3f)
                                    SectionIcon.ACHIEVEMENT -> GoldDark.copy(alpha = 0.2f)
                                    SectionIcon.HISTORY -> CyanDark.copy(alpha = 0.2f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Icon based on section type
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .drawBehind {
                                    when (icon) {
                                        SectionIcon.XP -> {
                                            // Shield shape for XP
                                            val path = androidx.compose.ui.graphics.Path().apply {
                                                val w = size.width
                                                val h = size.height
                                                moveTo(w / 2, 0f)
                                                lineTo(w, h * 0.25f)
                                                lineTo(w, h * 0.6f)
                                                quadraticBezierTo(w * 0.5f, h, 0f, h * 0.6f)
                                                lineTo(0f, h * 0.25f)
                                                close()
                                            }
                                            drawPath(
                                                path = path,
                                                color = CyanAccent.copy(alpha = 0.6f)
                                            )
                                        }
                                        SectionIcon.STREAK -> {
                                            // Flame shape
                                            val flamePath = androidx.compose.ui.graphics.Path().apply {
                                                val w = size.width
                                                val h = size.height
                                                moveTo(w / 2, 0f)
                                                quadraticBezierTo(w * 0.2f, h * 0.4f, w * 0.3f, h * 0.8f)
                                                lineTo(w * 0.7f, h * 0.8f)
                                                quadraticBezierTo(w * 0.8f, h * 0.4f, w / 2, 0f)
                                                close()
                                            }
                                            drawPath(
                                                path = flamePath,
                                                color = RedAccent.copy(alpha = 0.6f)
                                            )
                                        }
                                        SectionIcon.CHEST -> {
                                            // Chest
                                            drawRoundRect(
                                                color = GoldAccent.copy(alpha = 0.6f),
                                                size = size,
                                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
                                            )
                                            drawLine(
                                                color = GoldDark,
                                                start = Offset(0f, size.height * 0.35f),
                                                end = Offset(size.width, size.height * 0.35f),
                                                strokeWidth = 2.dp.toPx()
                                            )
                                        }
                                        SectionIcon.ACHIEVEMENT -> {
                                            // Star/circle
                                            drawCircle(
                                                color = GoldAccent.copy(alpha = 0.6f),
                                                radius = size.minDimension / 2 - 2.dp.toPx()
                                            )
                                        }
                                        SectionIcon.HISTORY -> {
                                            // Scroll
                                            drawRoundRect(
                                                color = CyanAccent.copy(alpha = 0.6f),
                                                size = size.copy(height = size.height * 0.8f),
                                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.dp.toPx())
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
private fun XpContent() {
    // Honest empty state - no XP records
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
            // Shield icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .drawBehind {
                        val path = androidx.compose.ui.graphics.Path().apply {
                            val w = size.width
                            val h = size.height
                            moveTo(w / 2, 0f)
                            lineTo(w, h * 0.25f)
                            lineTo(w, h * 0.6f)
                            quadraticBezierTo(w * 0.5f, h, 0f, h * 0.6f)
                            lineTo(0f, h * 0.25f)
                            close()
                        }
                        drawPath(
                            path = path,
                            color = CyanAccent.copy(alpha = 0.4f)
                        )
                    }
            )
            Text(
                text = "No XP records",
                color = TextMuted,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun StreakContent() {
    // Honest empty state - no streak records
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
            // Flame icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .drawBehind {
                        val flamePath = androidx.compose.ui.graphics.Path().apply {
                            val w = size.width
                            val h = size.height
                            moveTo(w / 2, 0f)
                            quadraticBezierTo(w * 0.2f, h * 0.4f, w * 0.3f, h * 0.8f)
                            lineTo(w * 0.7f, h * 0.8f)
                            quadraticBezierTo(w * 0.8f, h * 0.4f, w / 2, 0f)
                            close()
                        }
                        drawPath(
                            path = flamePath,
                            color = RedAccent.copy(alpha = 0.4f)
                        )
                    }
            )
            Text(
                text = "No streak records",
                color = TextMuted,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun ChestsContent() {
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

@Composable
private fun AchievementsContent() {
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

@Composable
private fun HistoryContent() {
    // Honest empty state - no reward history
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
