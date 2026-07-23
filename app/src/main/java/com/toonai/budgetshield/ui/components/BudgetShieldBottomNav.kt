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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toonai.budgetshield.theme.BudgetShieldTheme
import com.toonai.budgetshield.theme.CyanAccent
import com.toonai.budgetshield.theme.PanelBorder
import com.toonai.budgetshield.theme.PanelDark
import com.toonai.budgetshield.theme.TextMuted
import com.toonai.budgetshield.theme.TextPrimary

import kotlin.reflect.KClass

/**
 * BudgetShield Shared Bottom Navigation Bar
 * Fixed footer visible across all screens with the five main destinations:
 * Home, Treasure, Stats, Goals, Settings
 * 
 * PHYSICAL PHONE FOOTER CLEARANCE FIX:
 * - Wrap in outer Surface with wrapContentHeight to ensure background covers complete area
 * - Inner container has navigationBarsPadding() PLUS explicit 8.dp bottom padding
 * - This ensures labels are not clipped on devices with hidden/zero bottom gesture inset
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
            .wrapContentHeight(),
        color = PanelDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 8.dp)  // explicit clearance for physical devices
                .testTag("budgetshield_bottom_nav")
        ) {
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
                    icon = "🏠",
                    label = "Home",
                    testTag = "bottom_nav_home",
                    labelTestTag = "bottom_nav_label_home",
                    isActive = currentDestination == MainDestination.HOME,
                    onClick = onNavigateToHome
                )

                // Treasure
                NavItem(
                    icon = "🧰",
                    label = "Treasure",
                    testTag = "bottom_nav_treasure",
                    labelTestTag = "bottom_nav_label_treasure",
                    isActive = currentDestination == MainDestination.TREASURE,
                    onClick = onNavigateToTreasure
                )

                // Stats
                NavItem(
                    icon = "📊",
                    label = "Stats",
                    testTag = "bottom_nav_stats",
                    labelTestTag = "bottom_nav_label_stats",
                    isActive = currentDestination == MainDestination.STATS,
                    onClick = onNavigateToStats
                )

                // Goals
                NavItem(
                    icon = "🎯",
                    label = "Goals",
                    testTag = "bottom_nav_goals",
                    labelTestTag = "bottom_nav_label_goals",
                    isActive = currentDestination == MainDestination.GOALS,
                    onClick = onNavigateToGoals
                )

                // Settings
                NavItem(
                    icon = "⚙️",
                    label = "Settings",
                    testTag = "bottom_nav_settings",
                    labelTestTag = "bottom_nav_label_settings",
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

// ============================================
// PREVIEWS
// ============================================

@Preview(
    name = "Bottom Nav - Home Selected",
    showBackground = true,
    backgroundColor = 0xFF02070D
)
@Composable
private fun BottomNavHomeSelectedPreview() {
    BudgetShieldTheme {
        BudgetShieldBottomNav(
            currentDestination = MainDestination.HOME,
            onNavigateToHome = {},
            onNavigateToTreasure = {},
            onNavigateToStats = {},
            onNavigateToGoals = {},
            onNavigateToSettings = {}
        )
    }
}

@Preview(
    name = "Bottom Nav - Treasure Selected",
    showBackground = true,
    backgroundColor = 0xFF02070D
)
@Composable
private fun BottomNavTreasureSelectedPreview() {
    BudgetShieldTheme {
        BudgetShieldBottomNav(
            currentDestination = MainDestination.TREASURE,
            onNavigateToHome = {},
            onNavigateToTreasure = {},
            onNavigateToStats = {},
            onNavigateToGoals = {},
            onNavigateToSettings = {}
        )
    }
}

@Preview(
    name = "Bottom Nav - Stats Selected",
    showBackground = true,
    backgroundColor = 0xFF02070D
)
@Composable
private fun BottomNavStatsSelectedPreview() {
    BudgetShieldTheme {
        BudgetShieldBottomNav(
            currentDestination = MainDestination.STATS,
            onNavigateToHome = {},
            onNavigateToTreasure = {},
            onNavigateToStats = {},
            onNavigateToGoals = {},
            onNavigateToSettings = {}
        )
    }
}

@Preview(
    name = "Bottom Nav - Goals Selected",
    showBackground = true,
    backgroundColor = 0xFF02070D
)
@Composable
private fun BottomNavGoalsSelectedPreview() {
    BudgetShieldTheme {
        BudgetShieldBottomNav(
            currentDestination = MainDestination.GOALS,
            onNavigateToHome = {},
            onNavigateToTreasure = {},
            onNavigateToStats = {},
            onNavigateToGoals = {},
            onNavigateToSettings = {}
        )
    }
}

@Preview(
    name = "Bottom Nav - Settings Selected",
    showBackground = true,
    backgroundColor = 0xFF02070D
)
@Composable
private fun BottomNavSettingsSelectedPreview() {
    BudgetShieldTheme {
        BudgetShieldBottomNav(
            currentDestination = MainDestination.SETTINGS,
            onNavigateToHome = {},
            onNavigateToTreasure = {},
            onNavigateToStats = {},
            onNavigateToGoals = {},
            onNavigateToSettings = {}
        )
    }
}

@Preview(
    name = "Bottom Nav - No Selection",
    showBackground = true,
    backgroundColor = 0xFF02070D
)
@Composable
private fun BottomNavNoSelectionPreview() {
    BudgetShieldTheme {
        BudgetShieldBottomNav(
            currentDestination = null,
            onNavigateToHome = {},
            onNavigateToTreasure = {},
            onNavigateToStats = {},
            onNavigateToGoals = {},
            onNavigateToSettings = {}
        )
    }
}

@Composable
private fun NavItem(
    icon: String,
    label: String,
    testTag: String,
    labelTestTag: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val color = if (isActive) CyanAccent else TextMuted

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .testTag(testTag)
            .semantics {
                contentDescription = label
                selected = isActive
            }
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
                    modifier = Modifier.testTag(labelTestTag),
                    color = color,
                    fontSize = 11.sp,
                    fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
                )
            }
        }
    }
}
