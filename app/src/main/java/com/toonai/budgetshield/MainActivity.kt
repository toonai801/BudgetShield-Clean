package com.toonai.budgetshield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.toonai.budgetshield.data.database.BudgetShieldDatabase
import com.toonai.budgetshield.data.repository.BillRepository
import com.toonai.budgetshield.data.repository.UserSettingsRepository
import com.toonai.budgetshield.navigation.BackStackPolicy
import com.toonai.budgetshield.navigation.Home
import com.toonai.budgetshield.navigation.SetupQuest
import com.toonai.budgetshield.navigation.createBudgetShieldEntryProvider
import com.toonai.budgetshield.theme.BudgetShieldTheme
import com.toonai.budgetshield.ui.LocalBillRepository
import kotlinx.coroutines.runBlocking

/**
 * MainActivity with non-bypassable first-run gate.
 * Shows SetupQuest on first launch until user completes setup.
 * Footer is completely hidden during setup - no Home flash.
 */
class MainActivity : ComponentActivity() {
    
    private lateinit var billRepository: BillRepository
    private lateinit var userSettingsRepository: UserSettingsRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize repositories
        val database = BudgetShieldDatabase.getDatabase(this)
        billRepository = BillRepository(database.billDao())
        userSettingsRepository = UserSettingsRepository(database.userSettingsDao())
        
        // Check first-run status SYNCHRONOUSLY before setting content
        // This ensures SetupQuest is shown immediately on first run - non-bypassable
        val isFirstRunComplete = runBlocking {
            checkFirstRunStatus()
        }
        
        setContent {
            BudgetShieldApp(
                billRepository = billRepository,
                userSettingsRepository = userSettingsRepository,
                initialDestination = if (isFirstRunComplete) Home else SetupQuest
            )
        }
    }
    
    /**
     * Check if user has completed first-run setup.
     * Returns true if setup is complete, false otherwise.
     */
    private suspend fun checkFirstRunStatus(): Boolean {
        return try {
            val settings = userSettingsRepository.getSettings()
            settings?.isFirstRunComplete == true
        } catch (e: Exception) {
            // Default to Setup Quest on error (safe default)
            false
        }
    }
}

@Composable
private fun BudgetShieldApp(
    billRepository: BillRepository,
    userSettingsRepository: UserSettingsRepository,
    initialDestination: NavKey
) {
    BudgetShieldTheme {
        CompositionLocalProvider(
            LocalBillRepository provides billRepository
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
                }
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
}
