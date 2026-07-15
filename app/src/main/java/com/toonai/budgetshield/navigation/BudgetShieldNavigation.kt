package com.toonai.budgetshield.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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

@Composable
fun BudgetShieldNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = SetupQuest
    ) {
        composable<SetupQuest> {
            SetupQuestScreen(
                onComplete = {
                    // Replace stack so Back from Home doesn't return to Setup Quest
                    navController.navigate(Home) {
                        popUpTo(SetupQuest) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<Home> {
            HomeScreen(
                onNavigateToTreasure = { navController.navigate(Treasure) },
                onNavigateToStats = { navController.navigate(Stats) },
                onNavigateToGoals = { navController.navigate(Goals) },
                onNavigateToSettings = { navController.navigate(Settings) },
                onNavigateToIncomeEntry = { navController.navigate(IncomeEntry) },
                onNavigateToBillEntry = { navController.navigate(BillEntry) },
                onNavigateToSavingsEntry = { navController.navigate(SavingsEntry) },
                onNavigateToTransactionDetails = { navController.navigate(TransactionDetails()) },
                onNavigateToShieldProgression = { navController.navigate(ShieldProgression) }
            )
        }

        composable<Treasure> {
            TreasureScreen(
                onNavigateToBillEntry = { navController.navigate(BillEntry) },
                onNavigateToBillPayment = { navController.navigate(BillPayment) },
                onNavigateToTransactionDetails = { navController.navigate(TransactionDetails()) },
                onNavigateToHome = { navController.navigate(Home) }
            )
        }

        composable<Stats> {
            StatsScreen(
                onNavigateToGoals = { navController.navigate(Goals) },
                onNavigateToSettings = { navController.navigate(Settings) },
                onNavigateToTransactionDetails = { navController.navigate(TransactionDetails()) }
            )
        }

        composable<Goals> {
            GoalsScreen(
                onNavigateToSavingsEntry = { navController.navigate(SavingsEntry) },
                onNavigateToTransactionDetails = { navController.navigate(TransactionDetails()) },
                onNavigateToShieldProgression = { navController.navigate(ShieldProgression) }
            )
        }

        composable<Settings> {
            SettingsScreen(
                onNavigateToSetupQuest = { navController.navigate(SetupQuest) },
                onNavigateToHome = { navController.navigate(Home) }
            )
        }

        composable<IncomeEntry> {
            IncomeEntryScreen(
                onNavigateToHome = { navController.navigate(Home) },
                onNavigateToSetupQuest = { navController.navigate(SetupQuest) }
            )
        }

        composable<BillEntry> {
            BillEntryScreen(
                onNavigateToTreasure = { navController.navigate(Treasure) },
                onNavigateToHome = { navController.navigate(Home) },
                onNavigateToSetupQuest = { navController.navigate(SetupQuest) }
            )
        }

        composable<BillPayment> {
            BillPaymentScreen(
                onPaymentComplete = { navController.navigate(BillProtected) },
                onCancel = { navController.popBackStack() }
            )
        }

        composable<SavingsEntry> {
            SavingsEntryScreen(
                onNavigateToGoals = { navController.navigate(Goals) },
                onNavigateToHome = { navController.navigate(Home) }
            )
        }

        composable<TransactionDetails> { backStackEntry ->
            TransactionDetailsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(Home) },
                onNavigateToTreasure = { navController.navigate(Treasure) },
                onNavigateToStats = { navController.navigate(Stats) },
                onNavigateToGoals = { navController.navigate(Goals) }
            )
        }

        composable<BillProtected> {
            BillProtectedScreen(
                onNavigateToHome = { navController.navigate(Home) },
                onNavigateToTreasure = { navController.navigate(Treasure) },
                onNavigateToShieldProgression = { navController.navigate(ShieldProgression) }
            )
        }

        composable<ShieldProgression> {
            ShieldProgressionScreen(
                onNavigateToHome = { navController.navigate(Home) },
                onNavigateToGoals = { navController.navigate(Goals) },
                onNavigateToSettings = { navController.navigate(Settings) }
            )
        }
    }
}