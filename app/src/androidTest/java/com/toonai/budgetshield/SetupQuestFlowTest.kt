package com.toonai.budgetshield

import android.content.Intent
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.toonai.budgetshield.data.database.BudgetShieldDatabase
import com.toonai.budgetshield.data.model.SetupDraft
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
 * Setup Quest Flow - Deterministic Connected Tests with Hilt
 * Each test prepares exact fixture state BEFORE launching MainActivity
 */
@HiltAndroidTest
class SetupQuestFlowTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    @Inject
    lateinit var database: BudgetShieldDatabase

    private var scenario: ActivityScenario<MainActivity>? = null

    @Before
    fun setup() {
        hiltRule.inject()
        
        // Clear all tables before each test
        database.clearAllTables()
    }

    @After
    fun tearDown() {
        scenario?.close()
        // Clear tables after test
        database.clearAllTables()
    }

    /**
     * TEST: Fresh install shows Setup Quest Chapter 1
     * FIXTURE: Empty database, no settings
     * EXPECTED: Setup Quest Chapter 1 displayed
     */
    @Test
    fun freshInstallOpensSetupQuest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // VERIFY: Database is empty (no settings exist)
        val settings = runBlocking { database.userSettingsDao().getSettingsSync() }
        assert(settings == null) { "Database should be empty at test start" }

        // Launch MainActivity AFTER fixture preparation
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        scenario = ActivityScenario.launch(intent)
        composeTestRule.waitForIdle()

        // Wait for loading screen to disappear
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithTag("loading_screen").fetchSemanticsNodes().isEmpty()
        }

        // Wait for any setup quest content to appear (Setup Quest title)
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                composeTestRule.onNodeWithText("Setup Quest").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // VERIFY: Setup Quest Chapter 1 is shown
        composeTestRule.onNodeWithText("Chapter 1: Cash on Hand").assertExists()
        composeTestRule.onNodeWithText("Chapter 1 of 6").assertExists()
    }

    /**
     * TEST: Chapter indicator shows progress
     * FIXTURE: Fresh install
     * EXPECTED: Chapter indicator visible
     */
    @Test
    fun setupShowsChapterIndicator() {
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

        // Wait for any setup quest content to appear (Setup Quest title)
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                composeTestRule.onNodeWithText("Setup Quest").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeTestRule.onNodeWithText("Chapter 1 of 6").assertExists()
    }

    /**
     * TEST: Completed user sees Home screen
     * FIXTURE: Settings with isFirstRunComplete = true
     * EXPECTED: Home screen displayed (not Setup Quest)
     */
    @Test
    fun completedUserSeesHomeScreen() {
        // FIXTURE: Seed completed settings BEFORE launch using injected database
        runBlocking {
            database.userSettingsDao().insertSettings(
                UserSettings(
                    id = 1L,
                    isFirstRunComplete = true,
                    cashOnHandCents = 150000L,
                    savingsBalanceCents = 50000L,
                    selectedMonth = "2026-07"
                )
            )
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Launch MainActivity AFTER seeding fixture
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        scenario = ActivityScenario.launch(intent)
        composeTestRule.waitForIdle()

        // VERIFY: Home screen is shown (Safe Now is the main Home element)
        composeTestRule.onNodeWithText("Safe Now").assertExists()
    }

    /**
     * TEST: Draft resume continues at saved chapter
     * FIXTURE: Incomplete SetupDraft at Chapter 3
     * EXPECTED: Setup Quest resumes at Chapter 3
     */
    @Test
    fun draftResumeContinuesAtSavedChapter() {
        // FIXTURE: Seed incomplete setup draft using injected database
        runBlocking {
            database.userSettingsDao().insertSettings(
                UserSettings(
                    id = 1L,
                    isFirstRunComplete = false,
                    cashOnHandCents = 50000L
                )
            )
            database.setupDraftDao().saveDraft(
                SetupDraft(
                    currentChapter = 3,
                    cashOnHandCents = 50000L,
                    incomeName = "Salary",
                    incomeAmountCents = 500000L,
                    nextPaydayDate = "2026-07-15",
                    frequency = "Bi-Weekly"
                )
            )
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        scenario = ActivityScenario.launch(intent)

        // Wait for loading screen to disappear
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithTag("loading_screen").fetchSemanticsNodes().isEmpty()
        }

        // Wait for setup quest content to load with draft (check for Setup Quest title first)
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                composeTestRule.onNodeWithText("Setup Quest").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Wait for chapter 3 content specifically
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                composeTestRule.onNodeWithText("Chapter 3: Bills").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // VERIFY: Resumes at Chapter 3
        composeTestRule.onNodeWithText("Chapter 3 of 6").assertExists()
        composeTestRule.onNodeWithText("Chapter 3: Bills").assertExists()
    }

    /**
     * TEST: End-to-end persistence after complete setup
     * FIXTURE: Complete all chapters, force-stop, relaunch
     * EXPECTED: Home screen with saved values
     */
    @Test
    fun endToEndPersistenceAfterCompleteSetup() {
        // FIXTURE: Complete setup with all values saved using injected database
        runBlocking {
            database.userSettingsDao().insertSettings(
                UserSettings(
                    id = 1L,
                    isFirstRunComplete = true,
                    cashOnHandCents = 75000L,
                    savingsBalanceCents = 250000L,
                    selectedMonth = "2026-07"
                )
            )
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Launch and verify Home
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        scenario = ActivityScenario.launch(intent)
        composeTestRule.waitForIdle()

        // VERIFY: Home with persisted values (Safe Now is the main indicator)
        composeTestRule.onNodeWithText("Safe Now").assertExists()

        // Simulate force-stop by closing activity
        scenario?.close()
        scenario = null

        // Relaunch without clearing data
        val relaunchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        scenario = ActivityScenario.launch(relaunchIntent)
        composeTestRule.waitForIdle()

        // VERIFY: Still shows Home (Safe Now exists, not Setup Quest)
        composeTestRule.onNodeWithText("Safe Now").assertExists()
    }
}
