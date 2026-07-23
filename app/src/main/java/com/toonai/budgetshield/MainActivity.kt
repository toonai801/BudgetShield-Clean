package com.toonai.budgetshield

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * MainActivity with non-bypassable first-run gate.
 * Shows themed loading state while checking first-run status.
 * Shows SetupQuest on first launch until user completes setup.
 * Footer is completely hidden during setup - no Home flash.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            BudgetShieldAppWithLoading()
        }
    }
}

@Composable
private fun BudgetShieldAppWithLoading() {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    BudgetShieldTheme {
        var isLoading by remember { mutableStateOf(true) }
        var isFirstRunComplete by remember { mutableStateOf(false) }
        var hasError by remember { mutableStateOf(false) }
        var billRepository by remember { mutableStateOf<BillRepository?>(null) }
        var userSettingsRepository by remember { mutableStateOf<UserSettingsRepository?>(null) }
        
        LaunchedEffect(Unit) {
            try {
                // Initialize database and repositories on IO thread
                val (billRepo, userSettingsRepo, settings) = withContext(Dispatchers.IO) {
                    val database = BudgetShieldDatabase.getDatabase(context)
                    val billRepo = BillRepository(database.billDao())
                    val userSettingsRepo = UserSettingsRepository(database.userSettingsDao())
                    val settings = userSettingsRepo.getSettings()
                    Triple(billRepo, userSettingsRepo, settings)
                }
                
                billRepository = billRepo
                userSettingsRepository = userSettingsRepo
                isFirstRunComplete = settings?.isFirstRunComplete == true
                isLoading = false
            } catch (e: Exception) {
                e.printStackTrace()
                hasError = true
                isLoading = false
            }
        }
        
        when {
            isLoading -> ThemedLoadingScreen()
            hasError -> ErrorScreen()
            billRepository != null && userSettingsRepository != null -> BudgetShieldApp(
                billRepository = billRepository!!,
                userSettingsRepository = userSettingsRepository!!,
                initialDestination = if (isFirstRunComplete) Home else SetupQuest
            )
            else -> ThemedLoadingScreen()
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
private fun ErrorScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Something went wrong. Please restart the app.",
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun BudgetShieldApp(
    billRepository: BillRepository,
    userSettingsRepository: UserSettingsRepository,
    initialDestination: NavKey
) {
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
