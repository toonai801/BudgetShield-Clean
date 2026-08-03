package com.toonai.budgetshield

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.toonai.budgetshield.theme.BudgetShieldTheme
import com.toonai.budgetshield.ui.screens.SafeNowRepairPanel
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeRepairPanelTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun repairPanelBlocksResultAndWiresEveryRecoveryAction() {
        var reviewedIncome = false
        var reviewedBills = false
        var retried = false

        composeRule.setContent {
            BudgetShieldTheme {
                SafeNowRepairPanel(
                    message = "Twice-monthly income requires two payday days",
                    onReviewIncome = { reviewedIncome = true },
                    onReviewBills = { reviewedBills = true },
                    onRetry = { retried = true }
                )
            }
        }

        composeRule.onNodeWithTag("safe_now_repair_panel").assertIsDisplayed()
        composeRule.onNodeWithTag("safe_now_repair_message").assertIsDisplayed()
        composeRule.onNodeWithTag("safe_now_review_income").performClick()
        composeRule.onNodeWithTag("safe_now_review_bills").performClick()
        composeRule.onNodeWithTag("safe_now_retry").performClick()

        assertTrue(reviewedIncome)
        assertTrue(reviewedBills)
        assertTrue(retried)
    }
}
