package com.toonai.budgetshield

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * BudgetShield Application with Hilt DI.
 */
@HiltAndroidApp
class BudgetShieldApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Application-level initialization
    }
}
