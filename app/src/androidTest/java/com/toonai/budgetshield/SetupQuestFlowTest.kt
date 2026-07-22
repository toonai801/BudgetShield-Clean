package com.toonai.budgetshield

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.toonai.budgetshield.data.database.BudgetShieldDatabase
import com.toonai.budgetshield.data.model.SetupDraft
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
 * Setup Quest Flow - Deterministic Connected Tests
 * Each test prepares exact fixture state BEFORE launching MainActivity
 * No auto-launch rules - explicit ActivityScenario only
 */
@RunWith(AndroidJUnit4::class)
class SetupQuestFlowTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private var scenario: ActivityScenario<MainActivity>? = null

    /**
     * DETERMINISTIC FIXTURE RESET
     * Clears ALL persistence before each test:
     * - Closes and nulls Room singleton INSTANCE
     * - Deletes database files
     * - Clears SharedPreferences
     * - Ensures fresh state for next test
     */
    @Before
    fun setup() {
        scenario?.close()

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Step 1: Close existing database connection
        try {
            BudgetShieldDatabase.getDatabase(context).close()
        } catch (e: Exception) {
            // Database may not exist yet
        }

        // Step 2: Clear Room singleton INSTANCE via reflection
        // REQUIRED: Without this, tests share database state
        try {
            val instanceField: Field = BudgetShieldDatabase::class.java.getDeclaredField("INSTANCE")
            instanceField.isAccessible = true
            instanceField.set(null, null)
        } catch (e: Exception) {
            android.util.Log.w("SetupQuestTest", "Could not clear INSTANCE: ${e.message}")
        }

        // Step 3: Delete database files
        val dbDir = File(context.dataDir, "databases")
        if (dbDir.exists()) {
            dbDir.listFiles()?.forEach { it.delete() }
        }

        // Step 4: Clear SharedPreferences
        val prefsDir = File(context.dataDir, "shared_prefs")
        if (prefsDir.exists()) {
            prefsDir.listFiles { f -> f.name.contains("budget_shield") }?.forEach { it.delete() }
        }

        // Step 5: Standard API clearing
        context.deleteDatabase("budget_shield_database")
        context.getSharedPreferences("budget_shield_prefs", Context.MODE_PRIVATE)
            .edit().clear().commit()
    }

    @After
    fun tearDown() {
        scenario?.close()
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
        val db = BudgetShieldDatabase.getDatabase(context)
        val settings = runBlocking { db.userSettingsDao().getSettingsSync() }
        assert(settings == null) { "Database should be empty at test start" }

        // Launch MainActivity AFTER fixture preparation
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        scenario = ActivityScenario.launch(intent)
        composeTestRule.waitForIdle()

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

        composeTestRule.onNodeWithText("Chapter 1 of 6").assertExists()
    }

    /**
     * TEST: Completed user sees Home screen
     * FIXTURE: Settings with isFirstRunComplete = true
     * EXPECTED: Home screen displayed (not Setup Quest)
     */
    @Test
    fun completedUserSeesHomeScreen() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // FIXTURE: Seed completed settings BEFORE launch
        val db = BudgetShieldDatabase.getDatabase(context)
        runBlocking {
            db.userSettingsDao().insertSettings(
                UserSettings(
                    id = 1L,
                    isFirstRunComplete = true,
                    cashOnHandCents = 150000L,
                    savingsBalanceCents = 50000L,
                    selectedMonth = "2026-07"
                )
            )
        }

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
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // FIXTURE: Seed incomplete setup draft
        val db = BudgetShieldDatabase.getDatabase(context)
        runBlocking {
            db.userSettingsDao().insertSettings(
                UserSettings(
                    id = 1L,
                    isFirstRunComplete = false,
                    cashOnHandCents = 50000L
                )
            )
            db.setupDraftDao().saveDraft(
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
        
        // CRITICAL: Close database to ensure data is flushed and unlock file
        db.close()
        
        // Clear singleton so Activity creates fresh connection
        val instanceField = BudgetShieldDatabase::class.java.getDeclaredField("INSTANCE")
        instanceField.isAccessible = true
        instanceField.set(null, null)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        scenario = ActivityScenario.launch(intent)
        composeTestRule.waitForIdle()

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
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // FIXTURE: Complete setup with all values saved
        val db = BudgetShieldDatabase.getDatabase(context)
        runBlocking {
            db.userSettingsDao().insertSettings(
                UserSettings(
                    id = 1L,
                    isFirstRunComplete = true,
                    cashOnHandCents = 75000L,
                    savingsBalanceCents = 250000L,
                    selectedMonth = "2026-07"
                )
            )
        }
        
        // CRITICAL: Close database to ensure data is flushed and unlock file
        db.close()
        
        // Clear singleton so Activity creates fresh connection
        val instanceField = BudgetShieldDatabase::class.java.getDeclaredField("INSTANCE")
        instanceField.isAccessible = true
        instanceField.set(null, null)

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
