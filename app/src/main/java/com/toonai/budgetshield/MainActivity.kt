package com.toonai.budgetshield

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.toonai.budgetshield.data.database.BudgetShieldDatabase
import com.toonai.budgetshield.data.repository.BillRepository
import com.toonai.budgetshield.data.repository.BudgetRepository
import com.toonai.budgetshield.data.repository.IncomeRepository
import com.toonai.budgetshield.data.repository.SavingsGoalRepository
import com.toonai.budgetshield.data.repository.TransactionRepository
import com.toonai.budgetshield.data.repository.UserSettingsRepository
import com.toonai.budgetshield.data.repository.XpRepository
import com.toonai.budgetshield.navigation.BackStackPolicy
import com.toonai.budgetshield.navigation.Home
import com.toonai.budgetshield.navigation.SetupQuest
import com.toonai.budgetshield.navigation.createBudgetShieldEntryProvider
import com.toonai.budgetshield.theme.BudgetShieldTheme
import com.toonai.budgetshield.ui.LocalBillRepository
import com.toonai.budgetshield.ui.LocalIncomeRepository
import com.toonai.budgetshield.ui.LocalTransactionRepository
import com.toonai.budgetshield.ui.LocalXpRepository
import com.toonai.budgetshield.ui.LocalSavingsGoalRepository
import com.toonai.budgetshield.ui.LocalBudgetRepository
import com.toonai.budgetshield.ui.LocalUserSettingsRepository
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var repositories: AppRepositories

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            Log.d("BudgetShield", "Starting MainActivity onCreate")

            // Initialize repositories
            val database = BudgetShieldDatabase.getDatabase(this)
            Log.d("BudgetShield", "Database initialized")

            repositories = AppRepositories(
                billRepository = BillRepository(database.billDao()),
                incomeRepository = IncomeRepository(database.incomeScheduleDao()),
                transactionRepository = TransactionRepository(database.transactionDao()),
                xpRepository = XpRepository(database.xpEntryDao(), database.achievementDao()),
                savingsGoalRepository = SavingsGoalRepository(database.savingsGoalDao(), database.userStreakDao()),
                budgetRepository = BudgetRepository(database.budgetCategoryDao()),
                userSettingsRepository = UserSettingsRepository(database.userSettingsDao())
            )
            Log.d("BudgetShield", "Repositories initialized")

            setContent {
                BudgetShieldAppWithLoading(repositories = repositories)
            }
        } catch (e: Exception) {
            Log.e("BudgetShield", "Fatal error in onCreate", e)
            // Database initialization failed - show simple error UI
            setContent {
                BudgetShieldTheme {
                    ErrorScreenWithRetry(error = e.message ?: "Unknown error")
                }
            }
        }
    }
}

@Composable
private fun BudgetShieldAppWithLoading(
    repositories: AppRepositories
) {
    BudgetShieldTheme {
        var isLoading by remember { mutableStateOf(true) }
        var isFirstRunComplete by remember { mutableStateOf(false) }
        var hasError by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            try {
                Log.d("BudgetShield", "Loading settings...")
                val settings = try {
                    repositories.userSettingsRepository.getSettings()
                } catch (e: Exception) {
                    Log.w("BudgetShield", "Failed to load settings, initializing defaults", e)
                    repositories.userSettingsRepository.initializeDefaultSettings()
                    repositories.userSettingsRepository.getSettings()
                }
                isFirstRunComplete = settings?.isFirstRunComplete == true
                Log.d("BudgetShield", "First run complete: $isFirstRunComplete")
                isLoading = false
            } catch (e: Exception) {
                Log.e("BudgetShield", "Error loading settings", e)
                hasError = true
                errorMessage = e.message ?: "Unknown error"
                isLoading = false
            }
        }

        when {
            isLoading -> ThemedLoadingScreen()
            hasError -> ErrorScreenWithRetry(error = errorMessage)
            else -> BudgetShieldApp(
                repositories = repositories,
                initialDestination = if (isFirstRunComplete) Home else SetupQuest
            )
        }
    }
}

@Composable
private fun ThemedLoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("loading_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Budget Shield",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ErrorScreenWithRetry(error: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "⚠️ Error",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Please restart the app",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun BudgetShieldApp(
    repositories: AppRepositories,
    initialDestination: NavKey
) {
    CompositionLocalProvider(
        LocalBillRepository provides repositories.billRepository,
        LocalIncomeRepository provides repositories.incomeRepository,
        LocalTransactionRepository provides repositories.transactionRepository,
        LocalXpRepository provides repositories.xpRepository,
        LocalSavingsGoalRepository provides repositories.savingsGoalRepository,
        LocalBudgetRepository provides repositories.budgetRepository,
        LocalUserSettingsRepository provides repositories.userSettingsRepository
    ) {
        // Navigation 3: Use rememberNavBackStack for state management
        val backStack: NavBackStack<NavKey> = rememberNavBackStack(initialDestination)

        // Create the entry provider with navigation callbacks using production policy
        val entryProvider = createBudgetShieldEntryProvider(
            onNavigate = { key ->
                BackStackPolicy.navigateSingleTop(backStack, key)
            },
            onNavigateBack = {
                BackStackPolicy.popNested(backStack)
            },
            onReplaceStack = { key ->
                BackStackPolicy.completeSetup(backStack)
            },
            repositories = repositories
        )

        // Navigation 3: NavDisplay renders the current entry
        NavDisplay(
            backStack = backStack,
            onBack = {
                // Use production policy: exit when at root
                if (BackStackPolicy.canExitFromRoot(backStack)) {
                    // Activity finish handled by framework
                } else {
                    BackStackPolicy.popNested(backStack)
                }
            },
            entryProvider = entryProvider,
            modifier = Modifier.fillMaxSize().testTag("budgetshield_root")
        )
    }
}
