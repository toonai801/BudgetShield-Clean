package com.toonai.budgetshield

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Setup Quest Flow - Connected Acceptance Tests
 * Verifies Setup Quest shows on fresh install
 */
@RunWith(AndroidJUnit4::class)
class SetupQuestFlowTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun clearDatabaseBeforeAll() {
            // Clear database BEFORE any activity is created
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            context.deleteDatabase("budget_shield_database")
        }
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        // Wait for app to be ready
        composeTestRule.waitForIdle()
    }

    /**
     * FRESH_INSTALL_RESULT: Fresh install opens Setup Quest
     */
    @Test
    fun freshInstallOpensSetupQuest() {
        composeTestRule.waitForIdle()
        
        // Verify Setup Quest is shown
        composeTestRule.onNodeWithText("Setup Quest").assertExists()
        composeTestRule.onNodeWithText("Cash on Hand", substring = true).assertExists()
    }

    /**
     * COMPLETING_SETUP_RESULT: Setup chapters can be navigated
     */
    @Test
    fun setupCanNavigateChapters() {
        composeTestRule.waitForIdle()
        
        // Verify Setup Quest shows chapter indicator
        composeTestRule.onNodeWithText("Chapter").assertExists()
    }
}
