package com.toonai.budgetshield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.toonai.budgetshield.navigation.BudgetShieldNavigation
import com.toonai.budgetshield.theme.BudgetShieldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BudgetShieldTheme {
                val navController = rememberNavController()
                BudgetShieldNavigation(navController = navController)
            }
        }
    }
}