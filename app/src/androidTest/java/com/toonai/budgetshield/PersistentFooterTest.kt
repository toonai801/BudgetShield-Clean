package com.toonai.budgetshield

import android.content.Intent
import android.util.Log
import androidx.compose.ui.test.junit4.v2.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.toonai.budgetshield.data.database.BudgetShieldDatabase
import com.toonai.budgetshield.data.model.UserSettings
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

/**
 * Persistent Footer Tests - Deterministic with Hilt
 * Verifies footer visibility across different app states
 */
@HiltAndroidTest
class PersistentFooterTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    // The activity is launched manually after the database fixture is prepared.
    // An AndroidComposeRule would launch a competing MainActivity before @Before.
    val composeTestRule = createEmptyComposeRule()

    @Inject
    lateinit var database: BudgetShieldDatabase

    private var scenario: ActivityScenario<MainActivity>? = null

    @Before
    fun setup() {
        hiltRule.inject()
        database.clearAllTables()
    }

    @After
    fun tearDown() {
        scenario?.close()
        database.clearAllTables()
    }

    private fun launchWithCompletedSetup() {
        // Seed completed settings using injected database
        runBlocking {
            database.userSettingsDao().insertSettings(
                UserSettings(
                    id = 1L,
                    isFirstRunComplete = true,
                    cashOnHandCents = 100000L,
                    savingsBalanceCents = 50000L,
                    selectedMonth = "2026-07"
                )
            )
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        scenario = ActivityScenario.launch(intent)
        composeTestRule.waitForIdle()
    }

    @Test
    fun footerVisibleOnHome() {
        launchWithCompletedSetup()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertExists()
        composeTestRule.onNodeWithTag("bottom_nav_home").assertExists()
    }

    @Test
    fun footerVisibleOnTreasure() {
        launchWithCompletedSetup()
        composeTestRule.onNodeWithText("Treasure").performClick()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertExists()
    }

    @Test
    fun footerVisibleOnBills() {
        launchWithCompletedSetup()
        composeTestRule.onNodeWithTag("home_action_pay_bill").performClick()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertExists()
    }

    @Test
    fun footerVisibleOnStats() {
        launchWithCompletedSetup()
        composeTestRule.onNodeWithTag("bottom_nav_stats").performClick()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertExists()
    }

    @Test
    fun footerVisibleOnGoals() {
        launchWithCompletedSetup()
        composeTestRule.onNodeWithTag("bottom_nav_goals").performClick()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertExists()
    }

    @Test
    fun footerVisibleOnSettings() {
        launchWithCompletedSetup()
        composeTestRule.onNodeWithTag("bottom_nav_settings").performClick()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertExists()
    }

    @Test
    fun footerHiddenDuringSetupQuest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Fresh install - no setup completed
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        scenario = ActivityScenario.launch(intent)
        composeTestRule.waitForIdle()

        // Wait for loading screen to disappear
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithTag("loading_screen").fetchSemanticsNodes().isEmpty()
        }

        // Wait for any setup quest content to appear
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                composeTestRule.onNodeWithText("Setup Quest").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Footer should NOT exist during Setup Quest - verify Chapter 1 shows
        composeTestRule.onNodeWithText("Chapter 1: Cash on Hand").assertExists()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertDoesNotExist()
    }

    @Test
    fun footerShowsAfterSetupCompletion() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Launch fresh
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        scenario = ActivityScenario.launch(intent)
        composeTestRule.waitForIdle()

        // Wait for loading screen to disappear
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithTag("loading_screen").fetchSemanticsNodes().isEmpty()
        }

        // Wait for any setup quest content to appear
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                composeTestRule.onNodeWithText("Setup Quest").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Verify Setup Quest shows, no footer
        Log.d("FooterTest", "Chapter 1 loaded")
        composeTestRule.onNodeWithText("Chapter 1: Cash on Hand").assertExists()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertDoesNotExist()

        // Complete setup with explicit waits between each action
        Log.d("FooterTest", "Starting Chapter 1 input")
        composeTestRule.onNodeWithTag("chapter1_cash_input").performTextInput("500")
        composeTestRule.waitForIdle()
        Thread.sleep(500)
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(1500)

        Log.d("FooterTest", "Waiting for Chapter 2")
        composeTestRule.waitUntil(timeoutMillis = 20000) {
            try {
                composeTestRule.onNodeWithText("Chapter 2: Payday").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        
        Log.d("FooterTest", "Chapter 2 loaded, filling fields")
        composeTestRule.onNodeWithTag("chapter2_name_input").performTextInput("Job")
        composeTestRule.waitForIdle()
        Thread.sleep(300)
        composeTestRule.onNodeWithTag("chapter2_amount_input").performTextInput("2000")
        composeTestRule.waitForIdle()
        Thread.sleep(300)
        composeTestRule.onNodeWithTag("chapter2_date_input").performTextInput("08/15/2025")
        composeTestRule.waitForIdle()
        Thread.sleep(300)
        composeTestRule.onNodeWithText("Every 2 weeks").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(300)
        composeTestRule.onNodeWithTag("chapter2_confirmation_checkbox").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(1500)

        Log.d("FooterTest", "Waiting for Chapter 3")
        composeTestRule.waitUntil(timeoutMillis = 20000) {
            try {
                composeTestRule.onNodeWithText("Chapter 3: Bills").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(1000)

        Log.d("FooterTest", "Waiting for Chapter 4")
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            try {
                composeTestRule.onNodeWithText("Chapter 4: Savings").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        composeTestRule.onNodeWithTag("chapter4_savings_input").performTextInput("1000")
        composeTestRule.waitForIdle()
        Thread.sleep(300)
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(1000)

        Log.d("FooterTest", "Waiting for Chapter 5")
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            try {
                composeTestRule.onNodeWithText("Chapter 5: Monthly Budgets").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        composeTestRule.onNodeWithTag("chapter5_food_input").performTextInput("500")
        composeTestRule.waitForIdle()
        Thread.sleep(300)
        composeTestRule.onNodeWithTag("chapter5_wants_input").performTextInput("300")
        composeTestRule.waitForIdle()
        Thread.sleep(300)
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(1000)

        Log.d("FooterTest", "Waiting for Chapter 6")
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            try {
                composeTestRule.onNodeWithText("Chapter 6: Shield Review").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        Log.d("FooterTest", "Chapter 6 loaded, activating")
        composeTestRule.onNodeWithText("Activate My Shield").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(1500)

        // After activation, footer should appear - wait for navigation to complete
        Log.d("FooterTest", "Waiting for footer to appear")
        composeTestRule.waitUntil(timeoutMillis = 20000) {
            try {
                composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        
        Log.d("FooterTest", "SUCCESS: Footer found!")
    }
}
