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
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

/**
 * Persistent Footer Runtime Verification Tests
 * Verifies footer is visible on all screens and maintains correct state
 * PHYSICAL PHONE FOOTER CLEARANCE FIX: Tests for label visibility with explicit 8.dp bottom padding
 * Required for beta-footer-clearance release
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

    // ========== FOOTER LABEL CLEARANCE TESTS ==========

    /**
     * PHYSICAL PHONE CLEARANCE TEST: All nav items visible with proper bounds
     * Verifies all 5 footer nav items are displayed
     * The clearance is provided by navigationBarsPadding() + 8.dp padding in the component
     */
    @Test
    fun footerNavItemsHaveClearance_AllVisible() {
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
    fun footerVisibleOnSetupQuest() {
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
    }

    @Test
    fun footerTabsAllVisible() {
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_home").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_treasure").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_stats").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_goals").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_settings").assertIsDisplayed()
    }

    @Test
    fun homeTabNavigationAndSelection() {
        composeTestRule.onNodeWithTag("bottom_nav_treasure").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bottom_nav_home").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bottom_nav_home").assertIsSelected()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
    }

    @Test
    fun treasureTabNavigationAndSelection() {
        composeTestRule.onNodeWithTag("bottom_nav_treasure").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bottom_nav_treasure").assertIsSelected()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
    }

    @Test
    fun statsTabNavigationAndSelection() {
        composeTestRule.onNodeWithTag("bottom_nav_stats").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bottom_nav_stats").assertIsSelected()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
    }

    @Test
    fun goalsTabNavigationAndSelection() {
        composeTestRule.onNodeWithTag("bottom_nav_goals").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bottom_nav_goals").assertIsSelected()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
    }

    @Test
    fun settingsTabNavigationAndSelection() {
        composeTestRule.onNodeWithTag("bottom_nav_settings").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bottom_nav_settings").assertIsSelected()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_beta_version_marker").assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_beta_version_marker").assertTextContains("1.1.3", substring = true)
    }

    @Test
    fun footerVisibleOnBillsScreen() {
        composeTestRule.onNodeWithTag("home_action_pay_bill").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bills_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
    }

    @Test
    fun footerVisibleOnBillEntryScreen() {
        composeTestRule.onNodeWithTag("home_action_pay_bill").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bills_add_bill").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bill_entry_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
    }

    @Test
    fun footerVisibleOnHome() {
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
        composeTestRule.onNodeWithTag("bottom_nav_treasure").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_treasure").assertIsSelected()
    }

    @Test
    fun footerVisibleOnStats() {
        composeTestRule.onNodeWithTag("bottom_nav_stats").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_stats").assertIsSelected()
    }

    @Test
    fun footerVisibleOnGoals() {
        composeTestRule.onNodeWithTag("bottom_nav_goals").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_goals").assertIsSelected()
    }

    @Test
    fun footerVisibleOnSettings() {
        composeTestRule.onNodeWithTag("bottom_nav_settings").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_settings").assertIsSelected()
        composeTestRule.onNodeWithTag("settings_beta_version_marker").assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_beta_version_marker").assertTextContains("1.1.3", substring = true)
    }

    @Test
    fun billNavigationFromHome() {
        composeTestRule.onNodeWithTag("home_action_pay_bill").assertIsDisplayed()
        composeTestRule.onNodeWithTag("home_action_pay_bill").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("bills_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("budgetshield_bottom_nav").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_nav_home").assertIsSelected()
    }

    @Test
    fun billEntryNavigationFromBills() {
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
