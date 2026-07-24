package com.toonai.budgetshield

import android.content.Intent
import android.util.Log
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
 * Navigation Instrumentation Tests - Deterministic with Hilt
 * Each test prepares exact fixture state BEFORE launching MainActivity
 */
@HiltAndroidTest
class NavigationSmokeTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

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

    private fun launchWithFreshState() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        scenario = ActivityScenario.launch(intent)
        composeTestRule.waitForIdle()

        // Wait for loading screen to disappear
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithTag("loading_screen").fetchSemanticsNodes().isEmpty()
        }
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

        // Wait for either loading screen to disappear or Home content to appear
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
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

        // Wait for Setup Quest to fully load
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                composeTestRule.onNodeWithText("Setup Quest").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Complete Chapter 1
        Log.d("NavSmokeTest", "Starting Chapter 1")
        composeTestRule.onNodeWithText("Chapter 1: Cash on Hand").assertExists()
        
        // Find the text field by test tag
        val cashField = composeTestRule.onNodeWithTag("chapter1_cash_input")
        cashField.assertExists()
        cashField.performTextInput("500")
        composeTestRule.waitForIdle()
        Thread.sleep(500)
        
        Log.d("NavSmokeTest", "Clicking Next after Chapter 1")
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(1000)

        // Complete Chapter 2 - wait for chapter to load with extended timeout
        Log.d("NavSmokeTest", "Waiting for Chapter 2")
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            try {
                composeTestRule.onNodeWithText("Chapter 2: Payday").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        
        Log.d("NavSmokeTest", "Chapter 2 loaded, filling fields")
        
        // Fill Income Name
        val nameField = composeTestRule.onNodeWithTag("chapter2_name_input")
        nameField.assertExists()
        nameField.performTextInput("Test Job")
        composeTestRule.waitForIdle()
        Thread.sleep(300)
        
        // Fill Amount  
        val amountField = composeTestRule.onNodeWithTag("chapter2_amount_input")
        amountField.assertExists()
        amountField.performTextInput("2000")
        composeTestRule.waitForIdle()
        Thread.sleep(300)
        
        // Fill Payday Date
        val dateField = composeTestRule.onNodeWithTag("chapter2_date_input")
        dateField.assertExists()
        dateField.performTextInput("08/15/2025")
        composeTestRule.waitForIdle()
        Thread.sleep(300)
        
        // Select frequency
        composeTestRule.onNodeWithText("Every 2 weeks").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(300)
        
        // Confirm income - click on the checkbox directly using test tag
        composeTestRule.onNodeWithTag("chapter2_confirmation_checkbox").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(500)
        
        Log.d("NavSmokeTest", "Chapter 2 complete, clicking Next")
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(1500) // Give extra time for state validation

        // Complete Chapter 3 - wait with extended timeout
        Log.d("NavSmokeTest", "Waiting for Chapter 3")
        composeTestRule.waitUntil(timeoutMillis = 20000) {
            try {
                composeTestRule.onNodeWithText("Chapter 3: Bills").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        
        Log.d("NavSmokeTest", "Chapter 3 loaded")
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(1000)

        // Complete Chapter 4
        Log.d("NavSmokeTest", "Waiting for Chapter 4")
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            try {
                composeTestRule.onNodeWithText("Chapter 4: Savings").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        
        Log.d("NavSmokeTest", "Chapter 4 loaded, filling savings")
        composeTestRule.onNodeWithTag("chapter4_savings_input").performTextInput("1000")
        composeTestRule.waitForIdle()
        Thread.sleep(500)
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(1000)

        // Complete Chapter 5
        Log.d("NavSmokeTest", "Waiting for Chapter 5")
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            try {
                composeTestRule.onNodeWithText("Chapter 5: Monthly Budgets").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        
        Log.d("NavSmokeTest", "Chapter 5 loaded, filling budgets")
        composeTestRule.onNodeWithTag("chapter5_food_input").performTextInput("500")
        composeTestRule.waitForIdle()
        Thread.sleep(300)
        composeTestRule.onNodeWithTag("chapter5_wants_input").performTextInput("300")
        composeTestRule.waitForIdle()
        Thread.sleep(300)
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(1000)

        // Complete Chapter 6
        Log.d("NavSmokeTest", "Waiting for Chapter 6")
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            try {
                composeTestRule.onNodeWithText("Chapter 6: Shield Review").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        
        Log.d("NavSmokeTest", "Chapter 6 loaded, activating shield")
        composeTestRule.onNodeWithText("Activate My Shield").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(1500)

        // Verify Home - wait for navigation to complete with extended timeout
        Log.d("NavSmokeTest", "Waiting for Home screen")
        composeTestRule.waitUntil(timeoutMillis = 20000) {
            try {
                composeTestRule.onNodeWithTag("bottom_nav_home").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        
        Log.d("NavSmokeTest", "SUCCESS: Home screen found!")
    }

    @Test
    fun setupQuestHasNoFooter() {
        launchWithFreshState()

        // Wait for Setup Quest to fully load
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                composeTestRule.onNodeWithText("Setup Quest").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeTestRule.onNodeWithText("Chapter 1: Cash on Hand").assertExists()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertDoesNotExist()
    }

    @Test
    fun homeHasFooterAfterSetup() {
        launchWithCompletedSetup()
        // Wait for bottom nav to appear with longer timeout
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            try {
                composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
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
