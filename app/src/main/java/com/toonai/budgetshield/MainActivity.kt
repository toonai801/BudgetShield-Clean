package com.toonai.budgetshield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.toonai.budgetshield.navigation.Home
import com.toonai.budgetshield.navigation.SetupQuest
import com.toonai.budgetshield.navigation.createBudgetShieldEntryProvider
import com.toonai.budgetshield.theme.BudgetShieldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BudgetShieldTheme {
                // Navigation 3: Use rememberNavBackStack for state management
                val backStack: NavBackStack<NavKey> = rememberNavBackStack(SetupQuest)

                // Create the entry provider with navigation callbacks
                val entryProvider = createBudgetShieldEntryProvider(
                    onNavigate = { key ->
                        backStack.add(key)
                    },
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onReplaceStack = { key ->
                        // Clear the stack and add only the new key
                        backStack.clear()
                        backStack.add(key)
                    }
                )

                // Navigation 3: NavDisplay renders the current entry
                NavDisplay(
                    backStack = backStack,
                    onBack = {
                        // When back stack has only one item, finish the activity
                        if (backStack.size <= 1) {
                            finish()
                        } else {
                            backStack.removeLastOrNull()
                        }
                    },
                    entryProvider = entryProvider,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
