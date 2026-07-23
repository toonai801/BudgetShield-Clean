package com.toonai.budgetshield

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

/**
 * BudgetShield Application with Hilt DI.
 */
@HiltAndroidApp
class BudgetShieldApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Application-level initialization
        
        // Catch any uncaught exceptions in the Application class
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("BudgetShield", "Uncaught exception in thread ${thread.name}", throwable)
            // Call the default handler
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
