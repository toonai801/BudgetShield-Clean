package com.toonai.budgetshield

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Persistent Footer Runtime Verification Tests
 * Verifies footer is visible on all screens and maintains correct state
 * Required for beta-footer-qa release
 */
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class PersistentFooterTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        // Complete Setup Quest to get to Home
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Complete Setup (Temp)").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun footerVisibleOnHome() {
        // Verify footer exists and is displayed
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()

        // Verify all five tabs visible
        composeTestRule.onNodeWithTag("bottom_nav_home").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_treasure").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_stats").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_goals").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_settings").assertIsDisplayed()

        // Verify Home is selected
        composeTestRule.onNodeWithTag("bottom_nav_home").assertIsSelected()
    }

    @Test
    fun footerVisibleOnTreasure() {
        // Navigate to Treasure
        composeTestRule.onNodeWithTag("bottom_nav_treasure").performClick()
        composeTestRule.waitForIdle()

        // Verify footer still visible
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()

        // Verify Treasure is selected
        composeTestRule.onNodeWithTag("bottom_nav_treasure").assertIsSelected()
    }

    @Test
    fun footerVisibleOnStats() {
        // Navigate to Stats
        composeTestRule.onNodeWithTag("bottom_nav_stats").performClick()
        composeTestRule.waitForIdle()

        // Verify footer still visible
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()

        // Verify Stats is selected
        composeTestRule.onNodeWithTag("bottom_nav_stats").assertIsSelected()
    }

    @Test
    fun footerVisibleOnGoals() {
        // Navigate to Goals
        composeTestRule.onNodeWithTag("bottom_nav_goals").performClick()
        composeTestRule.waitForIdle()

        // Verify footer still visible
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()

        // Verify Goals is selected
        composeTestRule.onNodeWithTag("bottom_nav_goals").assertIsSelected()
    }

    @Test
    fun footerVisibleOnSettings() {
        // Navigate to Settings
        composeTestRule.onNodeWithTag("bottom_nav_settings").performClick()
        composeTestRule.waitForIdle()

        // Verify footer still visible
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()

        // Verify Settings is selected
        composeTestRule.onNodeWithTag("bottom_nav_settings").assertIsSelected()

        // Verify beta version marker shows correct version (check for version number only, ignore "Beta build" prefix)
        composeTestRule.onNodeWithTag("settings_beta_version_marker").assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_beta_version_marker").assertTextContains("1.1.1", substring = true)
    }

    @Test
    fun billNavigationFromHome() {
        // Tap Pay Bill button
        composeTestRule.onNodeWithTag("home_action_pay_bill").assertIsDisplayed()
        composeTestRule.onNodeWithTag("home_action_pay_bill").performClick()
        composeTestRule.waitForIdle()

        // Verify Bills screen displayed
        composeTestRule.onNodeWithTag("bills_screen").assertIsDisplayed()

        // Verify footer still visible on Bills
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()

        // Verify Home remains selected (Bills is secondary screen)
        composeTestRule.onNodeWithTag("bottom_nav_home").assertIsSelected()
    }

    @Test
    fun billEntryNavigationFromBills() {
        // Navigate to Bills
        composeTestRule.onNodeWithTag("home_action_pay_bill").performClick()
        composeTestRule.waitForIdle()

        // Tap Add Bill button
        composeTestRule.onNodeWithTag("bills_add_bill").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bills_add_bill").performClick()
        composeTestRule.waitForIdle()

        // Verify Bill Entry screen displayed
        composeTestRule.onNodeWithTag("bill_entry_screen").assertIsDisplayed()

        // Verify footer still visible on Bill Entry
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()

        // Verify Home remains selected (Bill Entry is secondary screen)
        composeTestRule.onNodeWithTag("bottom_nav_home").assertIsSelected()
    }

    @Test
    fun backNavigationFromBillEntryReturnsToBills() {
        // Navigate to Bills -> Bill Entry
        composeTestRule.onNodeWithTag("home_action_pay_bill").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bills_add_bill").performClick()
        composeTestRule.waitForIdle()

        // Verify Bill Entry is showing
        composeTestRule.onNodeWithTag("bill_entry_screen").assertIsDisplayed()

        // Press back
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        composeTestRule.waitForIdle()

        // Verify returned to Bills
        composeTestRule.onNodeWithTag("bills_screen").assertIsDisplayed()

        // Verify footer still visible
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
    }

    @Test
    fun footerBoundsRemainFixedAfterScrollingStats() {
        // Navigate to Stats
        composeTestRule.onNodeWithTag("bottom_nav_stats").performClick()
        composeTestRule.waitForIdle()

        // Verify footer is displayed before scroll
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()

        // Scroll the content
        composeTestRule.onNodeWithTag("stats_scroll_content").performTouchInput {
            swipeUp(startY = height * 0.8f, endY = height * 0.2f)
        }
        composeTestRule.waitForIdle()

        // Verify footer still visible after scroll
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
    }

    @Test
    fun footerBoundsRemainFixedAfterScrollingSettings() {
        // Navigate to Settings
        composeTestRule.onNodeWithTag("bottom_nav_settings").performClick()
        composeTestRule.waitForIdle()

        // Verify footer is displayed before scroll
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()

        // Scroll the content
        composeTestRule.onNodeWithTag("settings_scroll_content").performTouchInput {
            swipeUp(startY = height * 0.8f, endY = height * 0.2f)
        }
        composeTestRule.waitForIdle()

        // Verify footer still visible after scroll
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
    }

    @Test
    fun scrollableContentNotHiddenBehindFooter() {
        // Navigate to Settings (has scrollable content)
        composeTestRule.onNodeWithTag("bottom_nav_settings").performClick()
        composeTestRule.waitForIdle()

        // Verify footer is displayed
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        
        // Verify settings content is displayed
        composeTestRule.onNodeWithTag("settings_scroll_content").assertIsDisplayed()
    }
}
