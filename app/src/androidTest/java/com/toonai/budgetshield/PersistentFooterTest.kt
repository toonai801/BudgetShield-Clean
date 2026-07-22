package com.toonai.budgetshield

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

/**
 * Persistent Footer Runtime Verification Tests
 * Verifies footer is visible on all screens and maintains correct state
 * PHYSICAL PHONE FOOTER CLEARANCE FIX: Tests for label visibility with explicit 8.dp bottom padding
 * Required for beta-footer-clearance release
 * UPDATED: Uses real 6-chapter setup flow - no bypass button
 */
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class PersistentFooterTest {

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
        // Tests start fresh - wait for idle but don't assume starting state
        composeTestRule.waitForIdle()
    }

    /**
     * Helper: Complete the real 6-chapter setup to reach Home screen with footer
     */
    private fun completeSetupQuest() {
        // Chapter 1: Cash on Hand
        composeTestRule.onNodeWithText("Chapter 1: Cash on Hand").assertExists()
        composeTestRule.onNodeWithText("Cash on Hand").performTextInput("500")
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.waitForIdle()

        // Chapter 2: Your Payday
        composeTestRule.onNodeWithText("Chapter 2: Your Payday").assertExists()
        composeTestRule.onNodeWithText("Income Name").performTextInput("Test Job")
        composeTestRule.onNodeWithText("Amount").performTextInput("2000")
        composeTestRule.onNodeWithText("Next Payday").performTextInput("08/15/2025")
        composeTestRule.onNodeWithText("Every 2 weeks").performClick()
        composeTestRule.onNodeWithText("confirmed").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.waitForIdle()

        // Chapter 3: Your Bills - skip adding bills
        composeTestRule.onNodeWithText("Chapter 3: Your Bills").assertExists()
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.waitForIdle()

        // Chapter 4: Budget Categories
        composeTestRule.onNodeWithText("Chapter 4: Budget Categories").assertExists()
        composeTestRule.onNodeWithText("Food Budget").performTextInput("500")
        composeTestRule.onNodeWithText("Wants Budget").performTextInput("300")
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.waitForIdle()

        // Chapter 5: Review
        composeTestRule.onNodeWithText("Chapter 5: Review").assertExists()
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.waitForIdle()

        // Chapter 6: Shield Review - Activate
        composeTestRule.onNodeWithText("Chapter 6: Shield Review").assertExists()
        composeTestRule.onNodeWithText("Activate My Shield").performClick()
        composeTestRule.waitForIdle()

        // Verify Home is reached
        composeTestRule.onNodeWithTag("bottom_nav_home").assertExists()
    }

    /**
     * Helper: Complete setup to reach Home screen with footer
     */
    private fun ensureAtHomeWithFooter() {
        // Check if we need to complete setup first
        val isSetupQuest = try {
            composeTestRule.onNodeWithText("Setup Quest").assertExists()
            true
        } catch (e: AssertionError) {
            false
        }

        if (isSetupQuest) {
            // Complete the real 6-chapter setup
            completeSetupQuest()
        }

        // Verify we're at Home with footer
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
    }

    // ========== FOOTER LABEL CLEARANCE TESTS ==========

    /**
     * PHYSICAL PHONE CLEARANCE TEST: All nav items visible with proper bounds
     * Verifies all 5 footer nav items are displayed
     * The clearance is provided by navigationBarsPadding() + 8.dp padding in the component
     */
    @Test
    fun footerNavItemsHaveClearance_AllVisible() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        // Verify root and footer are displayed
        composeTestRule.onNodeWithTag("budgetshield_root").assertIsDisplayed()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()

        // Test each nav item is displayed
        val navItems = listOf(
            "bottom_nav_home" to "Home",
            "bottom_nav_treasure" to "Treasure",
            "bottom_nav_stats" to "Stats",
            "bottom_nav_goals" to "Goals",
            "bottom_nav_settings" to "Settings"
        )

        navItems.forEach { (testTag, labelName) ->
            // Verify nav item is displayed
            composeTestRule.onNodeWithTag(testTag).assertIsDisplayed()
            println("[CLEARANCE_TEST] $labelName nav item is displayed")
        }

        // Verify footer bounds - check that footer extends properly
        val rootBounds = getRootBounds()
        val footerBounds = composeTestRule.onNodeWithTag("budgetshield_bottom_nav").getBoundsInRoot()

        println("[CLEARANCE_TEST] rootHeightDp = ${rootBounds.bottom.value}")
        println("[CLEARANCE_TEST] footerTopDp = ${footerBounds.top.value}")
        println("[CLEARANCE_TEST] footerBottomDp = ${footerBounds.bottom.value}")
        println("[CLEARANCE_TEST] footerHeightDp = ${(footerBounds.bottom - footerBounds.top).value}")

        // Footer should fill width
        val footerWidth = footerBounds.right - footerBounds.left
        assertTrue(
            "Footer width ($footerWidth) should fill screen",
            footerWidth.value > (rootBounds.right.value * 0.9f)
        )

        // Footer height should be sufficient to contain nav items plus clearance
        val footerHeight = footerBounds.bottom - footerBounds.top
        assertTrue(
            "Footer height (${footerHeight.value}dp) should be sufficient for nav items plus clearance",
            footerHeight.value >= 50f
        )
    }

    /**
     * PHYSICAL PHONE CLEARANCE TEST: Labels maintain clearance after scrolling
     * Verifies footer labels maintain clearance after content scrolling
     */
    @Test
    fun footerLabelsMaintainClearanceAfterScrolling() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        // Navigate to Settings (has scrollable content)
        composeTestRule.onNodeWithTag("bottom_nav_settings").performClick()
        composeTestRule.waitForIdle()

        // Verify footer is visible before scrolling
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        val footerBoundsBefore = composeTestRule.onNodeWithTag("budgetshield_bottom_nav").getBoundsInRoot()

        // Scroll the content
        composeTestRule.onNodeWithTag("settings_scroll_content").performTouchInput {
            swipeUp(startY = height * 0.8f, endY = height * 0.2f)
        }
        composeTestRule.waitForIdle()

        // Verify footer is still displayed after scrolling
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        val footerBoundsAfter = composeTestRule.onNodeWithTag("budgetshield_bottom_nav").getBoundsInRoot()

        // Footer bounds should remain stable
        assertBoundsEqual(footerBoundsBefore, footerBoundsAfter, "Footer bounds changed after scrolling")

        // Verify all nav items are still displayed
        listOf("bottom_nav_home", "bottom_nav_treasure", "bottom_nav_stats", "bottom_nav_goals", "bottom_nav_settings")
            .forEach { testTag ->
                composeTestRule.onNodeWithTag(testTag).assertIsDisplayed()
            }
    }

    /**
     * PHYSICAL PHONE CLEARANCE TEST: Footer extends to bottom edge
     * Verifies the footer Surface extends properly to cover the bottom area
     * This ensures background covers complete area including insets
     */
    @Test
    fun footerExtendsToBottomEdge() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        // Capture root bounds (entire screen viewport)
        val rootBounds = getRootBounds()

        // Get footer bounds
        val footerBounds = composeTestRule.onNodeWithTag("budgetshield_bottom_nav").getBoundsInRoot()

        // Print footer dimensions
        println("[FOOTER_EXTENT_TEST] rootHeightDp = ${rootBounds.bottom.value}")
        println("[FOOTER_EXTENT_TEST] rootWidthDp = ${rootBounds.right.value}")
        println("[FOOTER_EXTENT_TEST] footerTopDp = ${footerBounds.top.value}")
        println("[FOOTER_EXTENT_TEST] footerBottomDp = ${footerBounds.bottom.value}")
        println("[FOOTER_EXTENT_TEST] footerHeightDp = ${(footerBounds.bottom - footerBounds.top).value}")
        println("[FOOTER_EXTENT_TEST] footerWidthDp = ${(footerBounds.right - footerBounds.left).value}")

        // Verify footer width fills screen (within reasonable tolerance)
        val footerWidth = footerBounds.right - footerBounds.left
        assertTrue(
            "Footer width ($footerWidth) should be close to root width (${rootBounds.right})",
            footerWidth.value > (rootBounds.right.value * 0.9f)
        )

        // Verify footer is positioned at the bottom of the screen
        // The footer should extend to or near the bottom of root
        val distanceFromBottom = rootBounds.bottom - footerBounds.bottom
        println("[FOOTER_EXTENT_TEST] distanceFromBottomDp = ${distanceFromBottom.value}")

        // Footer should be within reasonable distance from bottom (allowing for nav bar variations)
        assertTrue(
            "Footer bottom (${footerBounds.bottom.value}) should be close to root bottom (${rootBounds.bottom.value})",
            distanceFromBottom.value <= 40f
        )

        // Verify minimum footer height (should be tall enough to contain nav items plus clearance)
        val footerHeight = footerBounds.bottom - footerBounds.top
        println("[FOOTER_EXTENT_TEST] footerHeightDp = ${footerHeight.value}")
        assertTrue(
            "Footer height (${footerHeight.value}dp) should be at least 50dp to contain nav items",
            footerHeight.value >= 50f
        )

        // Verify footer is displayed
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
    }

    /**
     * Helper: Get root bounds (entire screen viewport)
     */
    private fun getRootBounds(): DpRect {
        return composeTestRule.onNodeWithTag("budgetshield_root").getBoundsInRoot()
    }

    // ========== EXISTING FOOTER TESTS ==========

    @Test
    fun footerAbsentOnSetupQuest() {
        // Verify we start on Setup Quest
        composeTestRule.onNodeWithText("Setup Quest").assertExists()
        
        // SetupQuest must NOT show footer - it's a first-run gate
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertDoesNotExist()
        composeTestRule.onNodeWithText("Home").assertDoesNotExist()
        composeTestRule.onNodeWithText("Treasure").assertDoesNotExist()
    }

    @Test
    fun footerTabsAllVisible() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_home").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_treasure").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_stats").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_goals").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_settings").assertIsDisplayed()
    }

    @Test
    fun homeTabNavigationAndSelection() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        composeTestRule.onNodeWithTag("bottom_nav_treasure").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bottom_nav_home").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bottom_nav_home").assertIsSelected()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
    }

    @Test
    fun treasureTabNavigationAndSelection() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        composeTestRule.onNodeWithTag("bottom_nav_treasure").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bottom_nav_treasure").assertIsSelected()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
    }

    @Test
    fun statsTabNavigationAndSelection() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        composeTestRule.onNodeWithTag("bottom_nav_stats").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bottom_nav_stats").assertIsSelected()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
    }

    @Test
    fun goalsTabNavigationAndSelection() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        composeTestRule.onNodeWithTag("bottom_nav_goals").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bottom_nav_goals").assertIsSelected()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
    }

    @Test
    fun settingsTabNavigationAndSelection() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        composeTestRule.onNodeWithTag("bottom_nav_settings").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bottom_nav_settings").assertIsSelected()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_beta_version_marker").assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_beta_version_marker").assertTextContains("1.2.0", substring = true)
    }

    @Test
    fun footerVisibleOnBillsScreen() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        composeTestRule.onNodeWithTag("home_action_pay_bill").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bills_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
    }

    @Test
    fun footerVisibleOnBillEntryScreen() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        composeTestRule.onNodeWithTag("home_action_pay_bill").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bills_add_bill").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bill_entry_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
    }

    @Test
    fun footerVisibleOnHome() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_home").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_treasure").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_stats").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_goals").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_settings").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_home").assertIsSelected()
    }

    @Test
    fun footerVisibleOnTreasure() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        composeTestRule.onNodeWithTag("bottom_nav_treasure").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_treasure").assertIsSelected()
    }

    @Test
    fun footerVisibleOnStats() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        composeTestRule.onNodeWithTag("bottom_nav_stats").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_stats").assertIsSelected()
    }

    @Test
    fun footerVisibleOnGoals() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        composeTestRule.onNodeWithTag("bottom_nav_goals").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_goals").assertIsSelected()
    }

    @Test
    fun footerVisibleOnSettings() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        composeTestRule.onNodeWithTag("bottom_nav_settings").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_settings").assertIsSelected()
        composeTestRule.onNodeWithTag("settings_beta_version_marker").assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_beta_version_marker").assertTextContains("1.2.0", substring = true)
    }

    @Test
    fun billNavigationFromHome() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        composeTestRule.onNodeWithTag("home_action_pay_bill").assertIsDisplayed()
        composeTestRule.onNodeWithTag("home_action_pay_bill").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bills_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_home").assertIsSelected()
    }

    @Test
    fun billEntryNavigationFromBills() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        composeTestRule.onNodeWithTag("home_action_pay_bill").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bills_add_bill").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bills_add_bill").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bill_entry_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_home").assertIsSelected()
    }

    @Test
    fun backNavigationFromBillEntryReturnsToBills() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        composeTestRule.onNodeWithTag("home_action_pay_bill").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bills_add_bill").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bill_entry_screen").assertIsDisplayed()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bills_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
    }

    @Test
    fun footerBoundsRemainFixedAfterScrollingStats() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        composeTestRule.onNodeWithTag("bottom_nav_stats").performClick()
        composeTestRule.waitForIdle()
        val boundsBefore = composeTestRule.onNodeWithTag("budgetshield_bottom_nav").getBoundsInRoot()
        composeTestRule.onNodeWithTag("stats_scroll_content").performTouchInput {
            swipeUp(startY = height * 0.8f, endY = height * 0.2f)
        }
        composeTestRule.waitForIdle()
        val boundsAfter = composeTestRule.onNodeWithTag("budgetshield_bottom_nav").getBoundsInRoot()
        assertBoundsEqual(boundsBefore, boundsAfter, "Stats footer bounds changed after scrolling")
    }

    @Test
    fun footerBoundsRemainFixedAfterScrollingSettings() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        composeTestRule.onNodeWithTag("bottom_nav_settings").performClick()
        composeTestRule.waitForIdle()
        val boundsBefore = composeTestRule.onNodeWithTag("budgetshield_bottom_nav").getBoundsInRoot()
        composeTestRule.onNodeWithTag("settings_scroll_content").performTouchInput {
            swipeUp(startY = height * 0.8f, endY = height * 0.2f)
        }
        composeTestRule.waitForIdle()
        val boundsAfter = composeTestRule.onNodeWithTag("budgetshield_bottom_nav").getBoundsInRoot()
        assertBoundsEqual(boundsBefore, boundsAfter, "Settings footer bounds changed after scrolling")
    }

    @Test
    fun scrollableContentNotHiddenBehindFooter() {
        // Ensure we're at Home with footer visible
        ensureAtHomeWithFooter()
        
        composeTestRule.onNodeWithTag("bottom_nav_settings").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_danger_zone_restart").performScrollTo()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("settings_danger_zone_restart").assertIsDisplayed()
        val finalContentBounds = composeTestRule.onNodeWithTag("settings_danger_zone_restart").getBoundsInRoot()
        val footerBounds = composeTestRule.onNodeWithTag("budgetshield_bottom_nav").getBoundsInRoot()
        println("[PERSISTENT_FOOTER_TEST] finalContent.top = ${finalContentBounds.top}")
        println("[PERSISTENT_FOOTER_TEST] finalContent.bottom = ${finalContentBounds.bottom}")
        println("[PERSISTENT_FOOTER_TEST] footer.top = ${footerBounds.top}")
        println("[PERSISTENT_FOOTER_TEST] footer.bottom = ${footerBounds.bottom}")
        assertTrue(
            "Final content top (${finalContentBounds.top}) is less than 0",
            finalContentBounds.top.value >= 0f
        )
        assertTrue(
            "Final content bottom (${finalContentBounds.bottom}) is hidden behind footer top (${footerBounds.top})",
            finalContentBounds.bottom.value <= footerBounds.top.value + 1f
        )
    }

    private fun assertBoundsEqual(expected: DpRect, actual: DpRect, message: String) {
        val tolerance = 1f.dp
        assertTrue("$message - left", abs((expected.left - actual.left).value) <= tolerance.value)
        assertTrue("$message - top", abs((expected.top - actual.top).value) <= tolerance.value)
        assertTrue("$message - right", abs((expected.right - actual.right).value) <= tolerance.value)
        assertTrue("$message - bottom", abs((expected.bottom - actual.bottom).value) <= tolerance.value)
    }
}