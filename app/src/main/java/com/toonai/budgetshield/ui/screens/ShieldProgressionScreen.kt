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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.toonai.budgetshield.theme.BackgroundDark
import com.toonai.budgetshield.theme.CyanAccent
import com.toonai.budgetshield.theme.GoldAccent
import com.toonai.budgetshield.theme.GreenAccent
import com.toonai.budgetshield.theme.OrangeAccent
import com.toonai.budgetshield.theme.PanelBorder
import com.toonai.budgetshield.theme.PanelDark
import com.toonai.budgetshield.theme.PurpleAccent
import com.toonai.budgetshield.theme.TextMuted
import com.toonai.budgetshield.theme.TextPrimary

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
                color = PurpleAccent
            )

            XPHistoryItem(
                icon = "🎯",
                action = "Reached Savings Goal",
                xp = "+50 XP",
                date = "Last week",
                color = OrangeAccent
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
