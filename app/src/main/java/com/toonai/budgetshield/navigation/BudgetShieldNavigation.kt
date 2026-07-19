package com.toonai.budgetshield.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.toonai.budgetshield.ui.screens.BillEntryScreen
import com.toonai.budgetshield.ui.screens.BillPaymentScreen
import com.toonai.budgetshield.ui.screens.BillProtectedScreen
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

/**
 * Creates a Navigation 3 entry for the given key.
 * This is used with NavDisplay and rememberNavBackStack.
 */
@Composable
fun BudgetShieldEntry(
    key: NavKey,
    onNavigate: (NavKey) -> Unit,
    onNavigateBack: () -> Unit,
    onReplaceStack: (NavKey) -> Unit
) {
    when (key) {
        is SetupQuest -> {
            SetupQuestScreen(
                onComplete = {
                    // Replace stack so Back from Home doesn't return to Setup Quest
                    onReplaceStack(Home)
                }
            )
        }
        is Home -> {
            HomeScreen(
                onNavigateToTreasure = { onNavigate(Treasure) },
                onNavigateToStats = { onNavigate(Stats) },
                onNavigateToGoals = { onNavigate(Goals) },
                onNavigateToSettings = { onNavigate(Settings) },
                onNavigateToIncomeEntry = { onNavigate(IncomeEntry) },
                onNavigateToBillEntry = { onNavigate(BillEntry) },
                onNavigateToSavingsEntry = { onNavigate(SavingsEntry) },
                onNavigateToTransactionDetails = { onNavigate(TransactionDetails()) },
                onNavigateToShieldProgression = { onNavigate(ShieldProgression) }
            )
        }
        is Treasure -> {
            TreasureScreen(
                onNavigateToBillEntry = { onNavigate(BillEntry) },
                onNavigateToBillPayment = { billId -> onNavigate(BillPaymentWithId(billId)) },
                onNavigateToTransactionDetails = { onNavigate(TransactionDetails()) },
                onNavigateToHome = { onNavigate(Home) }
            )
        }
        is Stats -> {
            StatsScreen(
                onNavigateToGoals = { onNavigate(Goals) },
                onNavigateToSettings = { onNavigate(Settings) },
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
                onNavigateToSetupQuest = { onNavigate(SetupQuest) },
                onNavigateToHome = { onNavigate(Home) }
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
                onNavigateToTreasure = { onNavigate(Treasure) },
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
            ShieldProgressionScreen(
                onNavigateToHome = { onNavigate(Home) },
                onNavigateToGoals = { onNavigate(Goals) },
                onNavigateToSettings = { onNavigate(Settings) }
            )
        }
        else -> {
            // Fallback for any unknown keys
            androidx.compose.material3.Text("Unknown screen: ${key::class.simpleName}")
        }
    }
}

/**
 * Creates the Navigation 3 entry provider for all 13 destinations.
 * Returns a function that creates NavEntry for a given key.
 */
fun createBudgetShieldEntryProvider(
    onNavigate: (NavKey) -> Unit,
    onNavigateBack: () -> Unit,
    onReplaceStack: (NavKey) -> Unit
): (NavKey) -> NavEntry<NavKey> {
    return { key: NavKey ->
        NavEntry(key) {
            BudgetShieldEntry(
                key = key,
                onNavigate = onNavigate,
                onNavigateBack = onNavigateBack,
                onReplaceStack = onReplaceStack
            )
        }
    }
}
