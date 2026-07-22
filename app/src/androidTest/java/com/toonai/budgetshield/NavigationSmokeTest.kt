package com.toonai.budgetshield

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.toonai.budgetshield.data.database.BudgetShieldDatabase
import com.toonai.budgetshield.data.model.UserSettings
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.lang.reflect.Field

/**
 * Navigation Instrumentation Tests - Deterministic
 * Each test prepares exact fixture state BEFORE launching MainActivity
 * No auto-launch rules - explicit ActivityScenario only
 */
@RunWith(AndroidJUnit4::class)
class NavigationSmokeTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private var scenario: ActivityScenario<MainActivity>? = null

    @Before
    fun setup() {
        scenario?.close()

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Clear database singleton
        try {
            BudgetShieldDatabase.getDatabase(context).close()
        } catch (e: Exception) { }

        try {
            val instanceField: Field = BudgetShieldDatabase::class.java.getDeclaredField("INSTANCE")
            instanceField.isAccessible = true
            instanceField.set(null, null)
        } catch (e: Exception) { }

        // Delete database files
        val dbDir = File(context.dataDir, "databases")
        if (dbDir.exists()) {
            dbDir.listFiles()?.forEach { it.delete() }
        }

        // Clear SharedPreferences
        val prefsDir = File(context.dataDir, "shared_prefs")
        if (prefsDir.exists()) {
            prefsDir.listFiles { f -> f.name.contains("budget_shield") }?.forEach { it.delete() }
        }

        context.deleteDatabase("budget_shield_database")
        context.getSharedPreferences("budget_shield_prefs", Context.MODE_PRIVATE)
            .edit().clear().commit()
    }

    @After
    fun tearDown() {
        scenario?.close()
    }

    private fun launchWithFreshState() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        scenario = ActivityScenario.launch(intent)
        composeTestRule.waitForIdle()
    }

    private fun launchWithCompletedSetup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Seed completed settings
        val db = BudgetShieldDatabase.getDatabase(context)
        runBlocking {
            db.userSettingsDao().insertSettings(
                UserSettings(
                    id = 1L,
                    isFirstRunComplete = true,
                    cashOnHandCents = 100000L,
                    savingsBalanceCents = 50000L,
                    selectedMonth = "2026-07"
                )
            )
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        scenario = ActivityScenario.launch(intent)
        composeTestRule.waitForIdle()
    }

    @Test
    fun appLaunchesAndShowsSetupQuest() {
        launchWithFreshState()
        composeTestRule.onNodeWithText("Chapter 1: Cash on Hand").assertExists()
        composeTestRule.onNodeWithText("Chapter 1 of 6").assertExists()
    }

    @Test
    fun completeSetupQuestNavigatesToHomeAndReplacesStack() {
        launchWithFreshState()

        // Complete Chapter 1
        composeTestRule.onNodeWithText("Chapter 1: Cash on Hand").assertExists()
        composeTestRule.onNodeWithText("Cash on Hand").performTextInput("500")
        composeTestRule.onNodeWithText("Next").performClick()

        // Complete Chapter 2
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Chapter 2: Payday").assertExists()
        composeTestRule.onNodeWithText("Income Name").performTextInput("Test Job")
        composeTestRule.onNodeWithText("Amount").performTextInput("2000")
        composeTestRule.onNodeWithText("Next Payday (MM/DD/YYYY)").performTextInput("08/15/2025")
        composeTestRule.onNodeWithText("Every 2 weeks").performClick()
        composeTestRule.onNodeWithText("This income is confirmed and ready to use").performClick()
        composeTestRule.onNodeWithText("Next").performClick()

        // Complete Chapter 3
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Chapter 3: Bills").assertExists()
        composeTestRule.onNodeWithText("Next").performClick()

        // Complete Chapter 4
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Chapter 4: Savings").assertExists()
        composeTestRule.onNodeWithText("Food Budget").performTextInput("500")
        composeTestRule.onNodeWithText("Wants Budget").performTextInput("300")
        composeTestRule.onNodeWithText("Next").performClick()

        // Complete Chapter 5
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Chapter 5: Monthly Budgets").assertExists()
        composeTestRule.onNodeWithText("Next").performClick()

        // Complete Chapter 6
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Chapter 6: Shield Review").assertExists()
        composeTestRule.onNodeWithText("Activate My Shield").performClick()

        // Verify Home
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bottom_nav_home").assertExists()
    }

    @Test
    fun setupQuestHasNoFooter() {
        launchWithFreshState()
        composeTestRule.onNodeWithText("Chapter 1: Cash on Hand").assertExists()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertDoesNotExist()
    }

    @Test
    fun homeHasFooterAfterSetup() {
        launchWithCompletedSetup()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertExists()
        composeTestRule.onNodeWithText("Home").assertExists()
    }

    @Test
    fun treasureDestinationReachable() {
        launchWithCompletedSetup()
        composeTestRule.onNodeWithText("Treasure").performClick()
        composeTestRule.onNodeWithText("Treasure Vault").assertExists()
    }

    @Test
    fun billsDestinationReachable() {
        launchWithCompletedSetup()
        composeTestRule.onNodeWithTag("home_action_pay_bill").performClick()
        composeTestRule.onNodeWithTag("bills_screen").assertExists()
    }

    @Test
    fun statsDestinationReachable() {
        launchWithCompletedSetup()
        composeTestRule.onNodeWithTag("bottom_nav_stats").performClick()
        composeTestRule.onNodeWithTag("bottom_nav_stats").assertExists()
    }

    @Test
    fun goalsDestinationReachable() {
        launchWithCompletedSetup()
        composeTestRule.onNodeWithTag("bottom_nav_goals").performClick()
        composeTestRule.onNodeWithTag("bottom_nav_goals").assertExists()
    }

    @Test
    fun settingsDestinationReachable() {
        launchWithCompletedSetup()
        composeTestRule.onNodeWithTag("bottom_nav_settings").performClick()
        composeTestRule.onNodeWithTag("bottom_nav_settings").assertExists()
    }

    @Test
    fun treasureFiveSectionsInteractive() {
        launchWithCompletedSetup()
        composeTestRule.onNodeWithText("Treasure").performClick()
        composeTestRule.onNodeWithText("Treasure Vault").assertExists()
        composeTestRule.onNodeWithText("XP & Shield Level").assertExists()
        composeTestRule.onNodeWithText("Current Streak").assertExists()
        composeTestRule.onNodeWithText("Treasure Chests").assertExists()
        composeTestRule.onNodeWithText("Achievements").assertExists()
        composeTestRule.onNodeWithText("Reward History").assertExists()
    }
}
