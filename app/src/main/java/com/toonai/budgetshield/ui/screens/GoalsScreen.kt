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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.toonai.budgetshield.theme.BackgroundDark
import com.toonai.budgetshield.theme.PanelDark
import com.toonai.budgetshield.theme.PanelBorder
import com.toonai.budgetshield.theme.CyanAccent
import com.toonai.budgetshield.theme.GreenAccent
import com.toonai.budgetshield.theme.GoldAccent
import com.toonai.budgetshield.theme.BlueAccent
import com.toonai.budgetshield.theme.PurpleAccent
import com.toonai.budgetshield.theme.OrangeAccent
import com.toonai.budgetshield.theme.TextPrimary
import com.toonai.budgetshield.theme.TextMuted
import com.toonai.budgetshield.ui.LocalXpRepository
import com.toonai.budgetshield.ui.LocalSavingsGoalRepository
import com.toonai.budgetshield.data.model.ShieldLevels


@Composable
fun GoalsScreen(
    onNavigateToSavingsEntry: () -> Unit,
    onNavigateToTransactionDetails: () -> Unit,
    onNavigateToShieldProgression: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToTreasure: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    onNavigateToGoals: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val xpRepository = LocalXpRepository.current
    val savingsGoalRepository = LocalSavingsGoalRepository.current

    // Collect real data from repositories
    val totalXp by xpRepository.totalXp.collectAsState(initial = 0)
    val currentLevel by xpRepository.currentLevel.collectAsState(initial = ShieldLevels.LEVELS.first())
    val xpToNextLevel by xpRepository.xpToNextLevel.collectAsState(initial = 500)
    val levelProgress by xpRepository.levelProgressPercent.collectAsState(initial = 0)
    val savingsGoals by savingsGoalRepository.allGoals.collectAsState(initial = emptyList())
    val userStreak by savingsGoalRepository.userStreak.collectAsState(initial = null)

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

                // Streak Card - uses real streak data
                StreakCardReal(streak = userStreak)

                // Shield Level - uses real XP data
                ShieldLevelCardReal(
                    currentLevel = currentLevel,
                    totalXp = totalXp,
                    xpToNextLevel = xpToNextLevel,
                    levelProgress = levelProgress,
                    onViewProgression = onNavigateToShieldProgression
                )

                // Savings Goals - uses real goals from repository
                SavingsGoalsSectionReal(
                    savingsGoals = savingsGoals,
                    onAddSavings = onNavigateToSavingsEntry
                )

                // Action Buttons
                BottomActionsSection(
                    onViewHistory = onNavigateToTransactionDetails,
                    onAddSavings = onNavigateToSavingsEntry
                )

                // Bottom spacer - no duplicate footer
                Spacer(modifier = Modifier.height(24.dp))
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
                    .background(GoldAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎯",
                    fontSize = 20.sp
                )
            }

            Column {
                Text(
                    text = "Goals",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Build your financial future",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun StreakCardReal(
    streak: com.toonai.budgetshield.data.model.UserStreak?
) {
    val streakDays = streak?.currentStreak ?: 0
    val bestStreak = streak?.bestStreak ?: 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A1F2C)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            OrangeAccent.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "🔥",
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Savings Streak",
                            color = OrangeAccent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = "$streakDays Days",
                        color = TextPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    if (streakDays > 0) {
                        Text(
                            text = "Best: $bestStreak days",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    } else {
                        Text(
                            text = "Start saving to build your streak",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                // Flame icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    OrangeAccent.copy(alpha = 0.3f),
                                    OrangeAccent.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔥",
                        fontSize = 40.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ShieldLevelCardReal(
    currentLevel: ShieldLevels.Level,
    totalXp: Int,
    xpToNextLevel: Int,
    levelProgress: Int,
    onViewProgression: () -> Unit
) {
    val nextLevelName = when (currentLevel.level) {
        1 -> "Apprentice Shield"
        2 -> "Guardian Shield"
        3 -> "Elite Shield"
        4 -> "Master Shield"
        else -> "Max Level"
    }

    val progressFloat = levelProgress / 100f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PanelDark
        ),
        onClick = onViewProgression
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(CyanAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🛡️",
                            fontSize = 24.sp
                        )
                    }

                    Column {
                        Text(
                            text = currentLevel.name + " Shield",
                            color = CyanAccent,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Level ${currentLevel.level}",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                TextButton(
                    onClick = onViewProgression,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "View →",
                        color = CyanAccent,
                        fontSize = 12.sp
                    )
                }
            }

            // XP Progress
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "XP Progress",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "$totalXp / ${totalXp + xpToNextLevel} XP",
                        color = TextPrimary,
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
                            .fillMaxWidth(progressFloat.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(CyanAccent, GreenAccent)
                                )
                            )
                    )
                }

                if (currentLevel.level < 5) {
                    Text(
                        text = "$xpToNextLevel XP until $nextLevelName",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                } else {
                    Text(
                        text = "Max level reached! 🎉",
                        color = GoldAccent,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SavingsGoalsSectionReal(
    savingsGoals: List<com.toonai.budgetshield.data.model.SavingsGoal>,
    onAddSavings: () -> Unit
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
                        text = "💰",
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Savings Goals",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = onAddSavings,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "+ Add",
                        color = GreenAccent,
                        fontSize = 12.sp
                    )
                }
            }

            if (savingsGoals.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🎯",
                            fontSize = 32.sp
                        )
                        Text(
                            text = "No savings goals yet",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Tap + Add to create your first goal",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                // Real goals from database
                savingsGoals.forEach { goal ->
                    GoalItemReal(goal = goal)
                }
            }
        }
    }
}

@Composable
private fun GoalItemReal(
    goal: com.toonai.budgetshield.data.model.SavingsGoal
) {
    val progress = if (goal.targetAmountCents > 0) {
        (goal.currentAmountCents.toFloat() / goal.targetAmountCents).coerceIn(0f, 1f)
    } else 0f

    val percentage = (progress * 100).toInt()

    // Determine color based on progress
    val color = when {
        progress >= 1.0f -> GreenAccent
        progress >= 0.5f -> GoldAccent
        else -> BlueAccent
    }

    val icon = goal.icon

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                        text = goal.name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$percentage% complete",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Text(
                text = goal.formattedCurrent + " / " + goal.formattedTarget,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
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
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(color)
            )
        }
    }
}

@Composable
private fun BottomActionsSection(
    onViewHistory: () -> Unit,
    onAddSavings: () -> Unit
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
            Text(
                text = "Quick Actions",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = "💵",
                    title = "Add Savings",
                    subtitle = "Build your goals",
                    color = GreenAccent,
                    onClick = onAddSavings
                )

                ActionCard(
                    modifier = Modifier.weight(1f),
                    icon = "📜",
                    title = "History",
                    subtitle = "View contributions",
                    color = CyanAccent,
                    onClick = onViewHistory
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
