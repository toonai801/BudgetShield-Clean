package com.toonai.budgetshield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.toonai.budgetshield.data.database.BudgetShieldDatabase
import com.toonai.budgetshield.data.repository.BillRepository
import com.toonai.budgetshield.navigation.BackStackPolicy
import com.toonai.budgetshield.navigation.Home
import com.toonai.budgetshield.navigation.SetupQuest
import com.toonai.budgetshield.navigation.createBudgetShieldEntryProvider
import com.toonai.budgetshield.theme.BudgetShieldTheme
import com.toonai.budgetshield.ui.LocalBillRepository

class MainActivity : ComponentActivity() {
    
    private lateinit var billRepository: BillRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize repository
        val database = BudgetShieldDatabase.getDatabase(this)
        billRepository = BillRepository(database.billDao())
        
        setContent {
            CompositionLocalProvider(
                LocalBillRepository provides billRepository
            ) {
                BudgetShieldTheme {
                    // Navigation 3: Use rememberNavBackStack for state management
                    val backStack: NavBackStack<NavKey> = rememberNavBackStack(SetupQuest)

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
                    // Root test tag added for footer clearance tests
                    NavDisplay(
                        backStack = backStack,
                        onBack = {
                            // Use production policy: exit when at root
                            if (BackStackPolicy.canExitFromRoot(backStack)) {
                                finish()
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
    }
}
