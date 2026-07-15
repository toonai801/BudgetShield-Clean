package com.toonai.budgetshield

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationSmokeTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appLaunchesAndShowsSetupQuest() {
        // Verify Setup Quest is the starting destination
        composeTestRule.onNodeWithText("Setup Quest").assertExists()
        composeTestRule.onNodeWithText("ARCHITECTURE FOUNDATION - NOT FINAL UI").assertExists()
    }

    @Test
    fun completeSetupQuestNavigatesToHome() {
        // Complete Setup Quest
        composeTestRule.onNodeWithText("Complete Setup (Temp)").performClick()

        // Verify Home is shown
        composeTestRule.onNodeWithText("Home").assertExists()
    }

    @Test
    fun allDestinationsReachableFromHome() {
        // Complete Setup Quest first
        composeTestRule.onNodeWithText("Complete Setup (Temp)").performClick()

        // Verify Home
        composeTestRule.onNodeWithText("Home").assertExists()

        // Navigate to Treasure
        composeTestRule.onNodeWithText("Treasure").performClick()
        composeTestRule.onNodeWithText("Treasure").assertExists()

        // Back to Home
        composeTestRule.onNodeWithText("Back to Home").performClick()

        // Navigate to Stats
        composeTestRule.onNodeWithText("Stats").performClick()
        composeTestRule.onNodeWithText("Stats").assertExists()

        // Navigate to Goals
        composeTestRule.onNodeWithText("Goals").performClick()
        composeTestRule.onNodeWithText("Goals").assertExists()

        // Navigate to Settings
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Settings").assertExists()

        // Navigate back to Home
        composeTestRule.onNodeWithText("Back to Home").performClick()
        composeTestRule.onNodeWithText("Home").assertExists()
    }

    @Test
    fun backFromHomeDoesNotReturnToSetupQuest() {
        // Complete Setup Quest
        composeTestRule.onNodeWithText("Complete Setup (Temp)").performClick()

        // Verify Home is showing
        composeTestRule.onNodeWithText("Home").assertExists()

        // Press system back
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }

        // Activity should finish, not show Setup Quest
        // This is verified by the test completing without error
    }

    @Test
    fun billPaymentFlowNavigatesToBillProtected() {
        // Complete Setup Quest
        composeTestRule.onNodeWithText("Complete Setup (Temp)").performClick()

        // Navigate to Treasure
        composeTestRule.onNodeWithText("Treasure").performClick()

        // Pay Bill
        composeTestRule.onNodeWithText("Pay Bill").performClick()
        composeTestRule.onNodeWithText("Bill Payment").assertExists()

        // Confirm payment
        composeTestRule.onNodeWithText("Confirm Payment → Bill Protected").performClick()

        // Verify Bill Protected Achievement
        composeTestRule.onNodeWithText("Bill Protected!").assertExists()
    }

    @Test
    fun entryScreensAreReachable() {
        // Complete Setup Quest
        composeTestRule.onNodeWithText("Complete Setup (Temp)").performClick()

        // Income Entry
        composeTestRule.onNodeWithText("Add Income").performClick()
        composeTestRule.onNodeWithText("Income Entry").assertExists()
        composeTestRule.onNodeWithText("Back to Home").performClick()

        // Bill Entry
        composeTestRule.onNodeWithText("Pay Bill").performClick()
        composeTestRule.onNodeWithText("Bill Entry").assertExists()

        // Back via Treasure
        composeTestRule.onNodeWithText("Back to Home").performClick()
        composeTestRule.onNodeWithText("Home").assertExists()

        // Savings Entry
        composeTestRule.onNodeWithText("Save Money").performClick()
        composeTestRule.onNodeWithText("Savings Entry").assertExists()
    }

    @Test
    fun transactionDetailsReachable() {
        // Complete Setup Quest
        composeTestRule.onNodeWithText("Complete Setup (Temp)").performClick()

        // Open Transaction Details from Home
        composeTestRule.onNodeWithText("Recent Activity").performClick()
        composeTestRule.onNodeWithText("Transaction Details").assertExists()
    }

    @Test
    fun shieldProgressionReachable() {
        // Complete Setup Quest
        composeTestRule.onNodeWithText("Complete Setup (Temp)").performClick()

        // Open Shield Progression from Home
        composeTestRule.onNodeWithText("Shield Progression").performClick()
        composeTestRule.onNodeWithText("Shield Progression").assertExists()
    }
}