package com.toonai.budgetshield.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import kotlin.reflect.KClass

// Premium gamified dark theme colors (matching Home)
private val PanelBorder = Color(0xFF14364A)
private val CyanAccent = Color(0xFF17E8F2)
private val TextMuted = Color(0xFFA6B1BF)

/**
 * BudgetShield Shared Bottom Navigation Bar
 * Fixed footer visible across all screens with the five main destinations:
 * Home, Treasure, Stats, Goals, Settings
 */
@Composable
fun BudgetShieldBottomNav(
    modifier: Modifier = Modifier,
    currentDestination: MainDestination?,
    onNavigateToHome: () -> Unit,
    onNavigateToTreasure: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToGoals: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .navigationBarsPadding(),
        color = Color(0xFF06121D)
    ) {
        Column {
            // Top border line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(PanelBorder)
            )

            // Nav items
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home
                NavItem(
                    icon = "\uD83C\uDFE0",
                    label = "Home",
                    isActive = currentDestination == MainDestination.HOME,
                    onClick = onNavigateToHome
                )

                // Treasure
                NavItem(
                    icon = "\uD83E\uDDF0",
                    label = "Treasure",
                    isActive = currentDestination == MainDestination.TREASURE,
                    onClick = onNavigateToTreasure
                )

                // Stats
                NavItem(
                    icon = "\uD83D\uDCCA",
                    label = "Stats",
                    isActive = currentDestination == MainDestination.STATS,
                    onClick = onNavigateToStats
                )

                // Goals
                NavItem(
                    icon = "\uD83C\uDFAF",
                    label = "Goals",
                    isActive = currentDestination == MainDestination.GOALS,
                    onClick = onNavigateToGoals
                )

                // Settings
                NavItem(
                    icon = "\u2699\uFE0F",
                    label = "Settings",
                    isActive = currentDestination == MainDestination.SETTINGS,
                    onClick = onNavigateToSettings
                )
            }
        }
    }
}

/**
 * Represents the five main bottom navigation destinations
 */
enum class MainDestination(
    val label: String
) {
    HOME("Home"),
    TREASURE("Treasure"),
    STATS("Stats"),
    GOALS("Goals"),
    SETTINGS("Settings");

    val routeClass: KClass<*>
        get() = when (this) {
            HOME -> com.toonai.budgetshield.navigation.Home::class
            TREASURE -> com.toonai.budgetshield.navigation.Treasure::class
            STATS -> com.toonai.budgetshield.navigation.Stats::class
            GOALS -> com.toonai.budgetshield.navigation.Goals::class
            SETTINGS -> com.toonai.budgetshield.navigation.Settings::class
        }
}

@Composable
private fun NavItem(
    icon: String,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val color = if (isActive) CyanAccent else TextMuted

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TextButton(
            onClick = onClick,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = icon,
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = label,
                    color = color,
                    fontSize = 11.sp,
                    fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
                )
            }
        }
    }
}
