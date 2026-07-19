package com.toonai.budgetshield.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Premium gamified dark theme - Background
private val BackgroundDark = Color(0xFF02070D)

/**
 * BudgetShield Shared Navigation Scaffold
 * Wraps all screens with a fixed bottom navigation bar.
 * Content scrolls independently above the footer.
 * 
 * @param currentDestination The currently selected main destination (for highlighting)
 * @param onNavigateToHome Callback for Home navigation
 * @param onNavigateToTreasure Callback for Treasure navigation
 * @param onNavigateToStats Callback for Stats navigation
 * @param onNavigateToGoals Callback for Goals navigation
 * @param onNavigateToSettings Callback for Settings navigation
 * @param content The screen content (will be placed above the footer with appropriate padding)
 */
@Composable
fun BudgetShieldScaffold(
    currentDestination: MainDestination,
    onNavigateToHome: () -> Unit,
    onNavigateToTreasure: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToGoals: () -> Unit,
    onNavigateToSettings: () -> Unit,
    content: @Composable (contentPadding: PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BudgetShieldBottomNav(
                currentDestination = currentDestination,
                onNavigateToHome = onNavigateToHome,
                onNavigateToTreasure = onNavigateToTreasure,
                onNavigateToStats = onNavigateToStats,
                onNavigateToGoals = onNavigateToGoals,
                onNavigateToSettings = onNavigateToSettings
            )
        },
        content = { innerPadding ->
            // Apply the background gradient to the content area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                BackgroundDark,
                                Color(0xFF06121D),
                                Color(0xFF0A1A2E)
                            )
                        )
                    )
            ) {
                // Pass innerPadding which includes system navigation bar insets
                content(innerPadding)
            }
        }
    )
}

/**
 * Simple scaffold wrapper for screens that don't need the shared footer
 * (e.g., SetupQuest, secondary screens like BillEntry, BillPayment)
 */
@Composable
fun BudgetShieldSimpleScaffold(
    content: @Composable (contentPadding: PaddingValues) -> Unit
) {
    // Get system navigation bar insets to add bottom padding
    val navigationBarsPadding = WindowInsets.navigationBars.asPaddingValues()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundDark,
                        Color(0xFF06121D),
                        Color(0xFF0A1A2E)
                    )
                )
            )
    ) {
        // For non-main screens, we still need to handle system navigation insets
        // but without the fixed footer
        content(navigationBarsPadding)
    }
}
