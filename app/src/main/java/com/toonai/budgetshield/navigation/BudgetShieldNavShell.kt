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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.toonai.budgetshield.AppRepositories
import com.toonai.budgetshield.ui.components.BudgetShieldBottomNav
import com.toonai.budgetshield.ui.components.MainDestination
import com.toonai.budgetshield.ui.screens.BillEntryScreen
import com.toonai.budgetshield.ui.screens.BillPaymentScreen
import com.toonai.budgetshield.ui.screens.BillProtectedScreen
import com.toonai.budgetshield.ui.screens.BillsScreen
import com.toonai.budgetshield.ui.screens.BudgetMenuScreen
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
import com.toonai.budgetshield.ui.viewmodel.BillsViewModel
import com.toonai.budgetshield.ui.viewmodel.GoalsViewModel
import com.toonai.budgetshield.ui.viewmodel.IncomeEntryViewModel
import com.toonai.budgetshield.ui.viewmodel.SavingsEntryViewModel
import com.toonai.budgetshield.ui.viewmodel.SettingsViewModel
import com.toonai.budgetshield.ui.viewmodel.StatsViewModel
import com.toonai.budgetshield.ui.viewmodel.TransactionViewModel

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
        is BudgetMenu -> MainDestination.HOME
        // SetupQuest has no selected tab but still shows footer
        is SetupQuest -> null  // SetupQuest has no footer
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
    onReplaceStack: (NavKey) -> Unit,
    repositories: AppRepositories
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
                onNavigateToRewardScreen = { /* Rewards not implemented - button hidden in UI */ },
                onNavigateToMenu = { onNavigate(BudgetMenu) },
                onNavigateToCalendar = { /* Calendar button removed - use month picker */ }
            )
        }
        is BudgetMenu -> {
            BudgetMenuScreen(
                onNavigateToBills = { onNavigate(Bills) },
                onNavigateToIncome = { onNavigate(IncomeEntry) },
                onNavigateToSavings = { onNavigate(SavingsEntry) },
                onNavigateToSettings = { onNavigate(Settings) },
                onDismiss = { onNavigateBack() }
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
                viewModel = viewModel(
                    factory = IncomeEntryViewModel.Factory(
                        repositories.incomeRepository,
                        repositories.xpRepository
                    )
                ),
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
                viewModel = viewModel(
                    factory = SavingsEntryViewModel.Factory(
                        repositories.savingsGoalRepository,
                        repositories.transactionRepository,
                        repositories.xpRepository
                    )
                ),
                onNavigateToGoals = { onNavigate(Goals) },
                onNavigateToHome = { onNavigate(Home) }
            )
        }
        is TransactionDetails -> {
            TransactionDetailsScreen(
                viewModel = viewModel(
                    factory = TransactionViewModel.Factory(repositories.transactionRepository)
                ),
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
    onReplaceStack: (NavKey) -> Unit,
    repositories: AppRepositories
): (NavKey) -> NavEntry<NavKey> {
    return { key: NavKey ->
        NavEntry(key) {
            val selectedDestination = getMainDestinationForKey(key)

            // SetupQuest hides the footer entirely - it's a first-run gate
            val showFooter = key !is SetupQuest && BudgetShieldRouteRegistry.isValidDestination(key)

            if (showFooter) {
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
                            onReplaceStack = onReplaceStack,
                            repositories = repositories
                        )
                    }
                }
            } else if (key is SetupQuest) {
                // SetupQuest renders full-screen without footer or scaffold wrapper
                // User cannot navigate away until setup is complete
                BudgetShieldScreenContent(
                    key = key,
                    onNavigate = onNavigate,
                    onNavigateBack = onNavigateBack,
                    onReplaceStack = onReplaceStack,
                    repositories = repositories
                )
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
                        onReplaceStack = onReplaceStack,
                        repositories = repositories
                    )
                }
            }
        }
    }
}
