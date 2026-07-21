package com.toonai.budgetshield.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.toonai.budgetshield.ui.components.BudgetShieldBottomNav
import com.toonai.budgetshield.ui.components.MainDestination
import com.toonai.budgetshield.ui.screens.BillEntryScreen
import com.toonai.budgetshield.ui.screens.BillPaymentScreen
import com.toonai.budgetshield.ui.screens.BillProtectedScreen
import com.toonai.budgetshield.ui.screens.BillsScreen
import com.toonai.budgetshield.ui.screens.GoalsScreen
import com.toonai.budgetshield.ui.screens.HomeScreen
import com.toonai.budgetshield.ui.screens.IncomeEntryScreen
import com.toonai.budgetshield.ui.screens.SavingsEntryScreen
import com.toonai.budgetshield.ui.screens.SettingsScreen
import com.toonai.budgetshield.ui.screens.SetupQuestScreen
import com.toonai.budgetshield.ui.screens.ShieldProgressionScreen
import com.toonai.budgetshield.ui.screens.StatsScreen
import com.toonai.budgetshield.ui.screens.TransactionDetailsScreen
import com.toonai.budgetshield.ui.screens.TreasureScreen

// Premium gamified dark theme - Background
private val BackgroundDark = Color(0xFF02070D)

/**
 * Determines the selected main destination based on the current navigation key.
 * Secondary screens (BillEntry, BillPayment, etc.) map to their owning main destination.
 */
fun getMainDestinationForKey(key: NavKey): MainDestination? {
    return when (key) {
        is Home -> MainDestination.HOME
        is Treasure -> MainDestination.TREASURE
        is Stats -> MainDestination.STATS
        is Goals -> MainDestination.GOALS
        is Settings -> MainDestination.SETTINGS
        // Secondary screens: Bills is Home-owned
        is Bills -> MainDestination.HOME
        // Other secondary screens also owned by Home
        is IncomeEntry -> MainDestination.HOME
        is BillEntry -> MainDestination.HOME
        is BillPayment -> MainDestination.HOME
        is BillPaymentWithId -> MainDestination.HOME
        is SavingsEntry -> MainDestination.HOME
        is TransactionDetails -> MainDestination.HOME
        is BillProtected -> MainDestination.HOME
        is ShieldProgression -> MainDestination.HOME
        // SetupQuest has no selected tab but still shows footer
        is SetupQuest -> null
        else -> null
    }
}

/**
 * BudgetShield Screen Content Renderer
 * Renders the actual screen content without the scaffold wrapper.
 */
@Composable
private fun BudgetShieldScreenContent(
    key: NavKey,
    onNavigate: (NavKey) -> Unit,
    onNavigateBack: () -> Unit,
    onReplaceStack: (NavKey) -> Unit
) {
    when (key) {
        is SetupQuest -> {
            SetupQuestScreen(
                onComplete = { onReplaceStack(Home) }
            )
        }
        is Home -> {
            HomeScreen(
                onNavigateToTreasure = { onNavigate(Treasure) },
                onNavigateToStats = { onNavigate(Stats) },
                onNavigateToGoals = { onNavigate(Goals) },
                onNavigateToSettings = { onNavigate(Settings) },
                onNavigateToIncomeEntry = { onNavigate(IncomeEntry) },
                onNavigateToBillEntry = { onNavigate(Bills) },
                onNavigateToSavingsEntry = { onNavigate(SavingsEntry) },
                onNavigateToTransactionDetails = { onNavigate(TransactionDetails()) },
                onNavigateToShieldProgression = { onNavigate(ShieldProgression) },
                onNavigateToRewardScreen = { /* TODO: implement rewards */ },
                onNavigateToMenu = { onNavigate(Settings) },
                onNavigateToCalendar = { onNavigate(Settings) }
            )
        }
        is Treasure -> {
            TreasureScreen(
                onNavigateToHome = { onNavigate(Home) }
            )
        }
        is Bills -> {
            BillsScreen(
                onNavigateToBillEntry = { onNavigate(BillEntry) },
                onNavigateToBillPayment = { billId -> onNavigate(BillPaymentWithId(billId)) },
                onNavigateToTransactionDetails = { onNavigate(TransactionDetails()) },
                onNavigateToHome = { onNavigate(Home) }
            )
        }
        is Stats -> {
            StatsScreen(
                onNavigateToTransactionDetails = { onNavigate(TransactionDetails()) }
            )
        }
        is Goals -> {
            GoalsScreen(
                onNavigateToSavingsEntry = { onNavigate(SavingsEntry) },
                onNavigateToTransactionDetails = { onNavigate(TransactionDetails()) },
                onNavigateToShieldProgression = { onNavigate(ShieldProgression) }
            )
        }
        is Settings -> {
            SettingsScreen(
                onNavigateToSetupQuest = { onNavigate(SetupQuest) }
            )
        }
        is IncomeEntry -> {
            IncomeEntryScreen(
                onNavigateToHome = { onNavigate(Home) },
                onNavigateToSetupQuest = { onNavigate(SetupQuest) }
            )
        }
        is BillEntry -> {
            BillEntryScreen(
                onNavigateToTreasure = { onNavigate(Bills) },
                onNavigateToHome = { onNavigate(Home) },
                onNavigateToSetupQuest = { onNavigate(SetupQuest) }
            )
        }
        is BillPayment -> {
            BillPaymentScreen(
                billId = null,
                onPaymentComplete = { onNavigate(BillProtected) },
                onCancel = { onNavigateBack() }
            )
        }
        is BillPaymentWithId -> {
            BillPaymentScreen(
                billId = key.billId,
                onPaymentComplete = { onNavigate(BillProtected) },
                onCancel = { onNavigateBack() }
            )
        }
        is SavingsEntry -> {
            SavingsEntryScreen(
                onNavigateToGoals = { onNavigate(Goals) },
                onNavigateToHome = { onNavigate(Home) }
            )
        }
        is TransactionDetails -> {
            TransactionDetailsScreen(
                transactionId = key.transactionId,
                onNavigateBack = { onNavigateBack() },
                onNavigateToHome = { onNavigate(Home) },
                onNavigateToTreasure = { onNavigate(Treasure) },
                onNavigateToStats = { onNavigate(Stats) },
                onNavigateToGoals = { onNavigate(Goals) }
            )
        }
        is BillProtected -> {
            BillProtectedScreen(
                onNavigateToHome = { onNavigate(Home) },
                onNavigateToTreasure = { onNavigate(Treasure) },
                onNavigateToShieldProgression = { onNavigate(ShieldProgression) }
            )
        }
        is ShieldProgression -> {
            ShieldProgressionScreen()
        }
        else -> {
            androidx.compose.material3.Text("Unknown screen: ${key::class.simpleName}")
        }
    }
}

/**
 * Creates the Navigation 3 entry provider with shared scaffold wrapper.
 * This wraps all main destinations (and Home-owned secondary destinations)
 * with the fixed bottom navigation bar.
 */
fun createBudgetShieldEntryProvider(
    onNavigate: (NavKey) -> Unit,
    onNavigateBack: () -> Unit,
    onReplaceStack: (NavKey) -> Unit
): (NavKey) -> NavEntry<NavKey> {
    return { key: NavKey ->
        NavEntry(key) {
            val selectedDestination = getMainDestinationForKey(key)

            // All registered routes show the shared footer
            // SetupQuest shows no selected tab (null), but footer still appears
            val isRegisteredRoute = BudgetShieldRouteRegistry.isValidDestination(key)

            if (isRegisteredRoute) {
                // Wrap with shared scaffold for all registered routes
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BudgetShieldBottomNav(
                            currentDestination = selectedDestination,
                            onNavigateToHome = { onNavigate(Home) },
                            onNavigateToTreasure = { onNavigate(Treasure) },
                            onNavigateToStats = { onNavigate(Stats) },
                            onNavigateToGoals = { onNavigate(Goals) },
                            onNavigateToSettings = { onNavigate(Settings) }
                        )
                    }
                ) { innerPadding ->
                    // Apply background and content padding
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
                            .padding(innerPadding)
                    ) {
                        // Render the actual screen content
                        BudgetShieldScreenContent(
                            key = key,
                            onNavigate = onNavigate,
                            onNavigateBack = onNavigateBack,
                            onReplaceStack = onReplaceStack
                        )
                    }
                }
            } else {
                // Unknown screens render without scaffold
                // Apply background only
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
                    BudgetShieldScreenContent(
                        key = key,
                        onNavigate = onNavigate,
                        onNavigateBack = onNavigateBack,
                        onReplaceStack = onReplaceStack
                    )
                }
            }
        }
    }
}
