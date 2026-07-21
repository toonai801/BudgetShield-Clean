package com.toonai.budgetshield.data.calculator

import com.toonai.budgetshield.data.model.Bill
import com.toonai.budgetshield.data.model.IncomeFrequency
import com.toonai.budgetshield.data.model.IncomeSchedule
import com.toonai.budgetshield.data.model.UserSettings
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for SafeNowCalculator covering all 9 worked examples
 * from SAFE_NOW_RULES.md.
 *
 * All amounts are in integer cents.
 * Starting balance is $1000.00 = 100000 cents unless otherwise specified.
 */
class SafeNowCalculatorTest {

    companion object {
        const val STARTING_CASH = 100000L // $1000.00
        const val TODAY = "2024-07-14"
        const val SELECTED_MONTH = "2024-07"
    }

    private fun createUserSettings(cashOnHandCents: Long = STARTING_CASH): UserSettings {
        return UserSettings(
            id = 1L,
            cashOnHandCents = cashOnHandCents,
            savingsBalanceCents = 0L,
            selectedMonth = SELECTED_MONTH
        )
    }

    private fun createBill(
        name: String,
        amountCents: Long,
        dueDate: String,
        isProtected: Boolean = true,
        paidAmountCents: Long = 0L,
        isPaid: Boolean = false
    ): Bill {
        return Bill(
            id = 0L,
            name = name,
            icon = "💰",
            amountCents = amountCents,
            paidAmountCents = paidAmountCents,
            dueDate = dueDate,
            isProtected = isProtected,
            isPaid = isPaid
        )
    }

    private fun createIncome(
        name: String,
        amountCents: Long,
        nextPayday: String,
        frequency: String = IncomeFrequency.BIWEEKLY,
        isConfirmed: Boolean = true
    ): IncomeSchedule {
        return IncomeSchedule(
            id = 0L,
            name = name,
            amountCents = amountCents,
            nextPayday = nextPayday,
            frequency = frequency,
            isConfirmed = isConfirmed
        )
    }

    // Example 1: Bill Due Before Next Payday
    @Test
    fun `example 1 - bill due before next payday`() {
        // Events:
        // - Today: July 14
        // - Bill due: July 20 (Rent $800.00, protected)
        // - Next payday: July 25 (confirmed $1500.00)

        val userSettings = createUserSettings()
        val bills = listOf(
            createBill("Rent", 80000, "2024-07-20", isProtected = true)
        )
        val income = listOf(
            createIncome("Paycheck", 150000, "2024-07-25")
        )

        val result = SafeNowCalculator.calculate(userSettings, bills, income, SELECTED_MONTH, TODAY)

        assertEquals("Safe Now should be $200.00", 20000L, result.safeNowCents)
        assertFalse("Should not have shortage", result.hasShortage)
        assertEquals("Shortage should be 0", 0L, result.shortageCents)
        assertNull("Should not have failing date", result.firstFailingDate)
    }

    // Example 2: Bill and Confirmed Income on Same Day
    @Test
    fun `example 2 - bill and confirmed income on same day`() {
        // Events:
        // - Today: July 14
        // - Confirmed income: July 20 ($500.00)
        // - Bill due: July 20 ($300.00, protected)

        val userSettings = createUserSettings()
        val bills = listOf(
            createBill("Bill", 30000, "2024-07-20", isProtected = true)
        )
        val income = listOf(
            createIncome("Paycheck", 50000, "2024-07-20")
        )

        val result = SafeNowCalculator.calculate(userSettings, bills, income, SELECTED_MONTH, TODAY)

        // Starting balance is minimum ($1000.00)
        assertEquals("Safe Now should be $1000.00", 100000L, result.safeNowCents)
        assertFalse("Should not have shortage", result.hasShortage)
    }

    // Example 3: Income Arriving One Day After a Bill
    @Test
    fun `example 3 - income arriving one day after a bill`() {
        // Events:
        // - Today: July 14
        // - Bill due: July 20 ($800.00, protected)
        // - Confirmed income: July 21 ($1500.00)

        val userSettings = createUserSettings()
        val bills = listOf(
            createBill("Bill", 80000, "2024-07-20", isProtected = true)
        )
        val income = listOf(
            createIncome("Paycheck", 150000, "2024-07-21")
        )

        val result = SafeNowCalculator.calculate(userSettings, bills, income, SELECTED_MONTH, TODAY)

        // Safe Now: $1000 - $800 = $200
        assertEquals("Safe Now should be $200.00", 20000L, result.safeNowCents)
        assertFalse("Should not have shortage", result.hasShortage)
    }

    // Example 4: Two Paychecks and Multiple Bills Across Two Months
    @Test
    fun `example 4 - two paychecks and multiple bills across two months`() {
        // Events:
        // - Today: July 14, Starting: $1000.00
        // - Payday 1: July 20 ($1500.00)
        // - Bill 1: July 25 (Rent $800.00, protected)
        // - Bill 2: July 28 (Utilities $150.00, protected)
        // - Payday 2: August 5 ($1500.00)
        // - Bill 3: August 10 (Insurance $200.00, protected)
        // - Bill 4: August 15 (Internet $80.00, protected)

        val userSettings = createUserSettings()
        val bills = listOf(
            createBill("Rent", 80000, "2024-07-25", isProtected = true),
            createBill("Utilities", 15000, "2024-07-28", isProtected = true),
            createBill("Insurance", 20000, "2024-08-10", isProtected = true),
            createBill("Internet", 8000, "2024-08-15", isProtected = true)
        )
        val income = listOf(
            createIncome("Paycheck 1", 150000, "2024-07-20"),
            createIncome("Paycheck 2", 150000, "2024-08-05")
        )

        val result = SafeNowCalculator.calculate(userSettings, bills, income, SELECTED_MONTH, TODAY)

        // Starting balance is minimum ($1000.00)
        assertEquals("Safe Now should be $1000.00", 100000L, result.safeNowCents)
        assertFalse("Should not have shortage", result.hasShortage)
    }

    // Example 5: Overdue Bill
    @Test
    fun `example 5 - overdue bill`() {
        // Events:
        // - Today: July 14
        // - Overdue bill: July 10 ($200.00, protected) - unpaid
        // - Next payday: July 20 ($500.00)

        val userSettings = createUserSettings()
        val bills = listOf(
            createBill("Overdue Bill", 20000, "2024-07-10", isProtected = true)
        )
        val income = listOf(
            createIncome("Paycheck", 50000, "2024-07-20")
        )

        val result = SafeNowCalculator.calculate(userSettings, bills, income, SELECTED_MONTH, TODAY)

        // Safe Now: $1000 - $200 = $800
        assertEquals("Safe Now should be $800.00", 80000L, result.safeNowCents)
        assertFalse("Should not have shortage", result.hasShortage)
    }

    // Example 6: Partially Paid Bill
    @Test
    fun `example 6 - partially paid bill`() {
        // Events:
        // - Today: July 14
        // - Bill due: July 20 ($500.00, protected)
        // - Partial payment already recorded: $200.00
        // - Remaining due: $300.00
        // - Next payday: July 25 ($800.00)

        val userSettings = createUserSettings()
        val bills = listOf(
            createBill("Bill", 50000, "2024-07-20", isProtected = true, paidAmountCents = 20000)
        )
        val income = listOf(
            createIncome("Paycheck", 80000, "2024-07-25")
        )

        val result = SafeNowCalculator.calculate(userSettings, bills, income, SELECTED_MONTH, TODAY)

        // Safe Now: $1000 - $300 = $700
        assertEquals("Safe Now should be $700.00", 70000L, result.safeNowCents)
        assertFalse("Should not have shortage", result.hasShortage)
    }

    // Example 7: Unconfirmed Side Income
    @Test
    fun `example 7 - unconfirmed side income`() {
        // Events:
        // - Today: July 14
        // - Bill due: July 20 ($800.00, protected)
        // - Unconfirmed income expected: July 19 ($500.00, not confirmed)
        // - Confirmed income: July 25 ($1000.00)

        val userSettings = createUserSettings()
        val bills = listOf(
            createBill("Bill", 80000, "2024-07-20", isProtected = true)
        )
        val income = listOf(
            createIncome("Side Gig", 50000, "2024-07-19", isConfirmed = false),
            createIncome("Paycheck", 100000, "2024-07-25", isConfirmed = true)
        )

        val result = SafeNowCalculator.calculate(userSettings, bills, income, SELECTED_MONTH, TODAY)

        // Safe Now: $1000 - $800 = $200 (unconfirmed income not used)
        assertEquals("Safe Now should be $200.00", 20000L, result.safeNowCents)
        assertFalse("Should not have shortage", result.hasShortage)
    }

    // Example 8: Unprotected Bill
    @Test
    fun `example 8 - unprotected bill`() {
        // Events:
        // - Today: July 14
        // - Protected bill: July 20 (Rent $800.00, protected)
        // - Unprotected bill: July 22 (Streaming $15.00, unprotected)
        // - Next payday: July 25 ($1000.00)

        val userSettings = createUserSettings()
        val bills = listOf(
            createBill("Rent", 80000, "2024-07-20", isProtected = true),
            createBill("Streaming", 1500, "2024-07-22", isProtected = false)
        )
        val income = listOf(
            createIncome("Paycheck", 100000, "2024-07-25")
        )

        val result = SafeNowCalculator.calculate(userSettings, bills, income, SELECTED_MONTH, TODAY)

        // Safe Now: $1000 - $800 = $200 (unprotected bill not included)
        assertEquals("Safe Now should be $200.00", 20000L, result.safeNowCents)
        assertFalse("Should not have shortage", result.hasShortage)
    }

    // Example 9: Spending Transaction Causes Underfunding
    @Test
    fun `example 9 - spending causes underfunding`() {
        // Initial Events:
        // - Today: July 14
        // - Starting cleared cash: $1000.00
        // - Bill due: July 20 (Insurance $900.00, protected)
        // - Confirmed income: July 18 ($500.00)
        // Initial Safe Now: $600.00

        // After spending $800 total:
        // - New cleared cash: $200.00
        // - Safe Now: $0
        // - Shortage: $200

        val reducedCash = 20000L // $200.00 after spending
        val userSettings = createUserSettings(cashOnHandCents = reducedCash)
        val bills = listOf(
            createBill("Insurance", 90000, "2024-07-20", isProtected = true)
        )
        val income = listOf(
            createIncome("Paycheck", 50000, "2024-07-18")
        )

        val result = SafeNowCalculator.calculate(userSettings, bills, income, SELECTED_MONTH, TODAY)

        assertEquals("Safe Now should be $0", 0L, result.safeNowCents)
        assertTrue("Should have shortage", result.hasShortage)
        assertEquals("Shortage should be $200.00", 20000L, result.shortageCents)
        assertEquals("Failing date should be July 20", "2024-07-20", result.firstFailingDate)
        assertTrue("Should have failing bills", result.failingBills.isNotEmpty())
    }

    // Additional edge cases

    @Test
    fun `safe now never goes negative`() {
        val userSettings = createUserSettings(cashOnHandCents = 10000) // $100
        val bills = listOf(
            createBill("Big Bill", 200000, "2024-07-20", isProtected = true)
        )
        val income = emptyList<IncomeSchedule>()

        val result = SafeNowCalculator.calculate(userSettings, bills, income, SELECTED_MONTH, TODAY)

        assertEquals("Safe Now should be 0, not negative", 0L, result.safeNowCents)
        assertTrue("Should have shortage", result.hasShortage)
    }

    @Test
    fun `no bills or income returns starting cash`() {
        val userSettings = createUserSettings()
        val bills = emptyList<Bill>()
        val income = emptyList<IncomeSchedule>()

        val result = SafeNowCalculator.calculate(userSettings, bills, income, SELECTED_MONTH, TODAY)

        assertEquals("Safe Now should be starting cash", STARTING_CASH, result.safeNowCents)
        assertFalse("Should not have shortage", result.hasShortage)
    }

    @Test
    fun `paid bills are not included in calculation`() {
        val userSettings = createUserSettings()
        val bills = listOf(
            createBill("Paid Bill", 50000, "2024-07-20", isProtected = true, isPaid = true)
        )
        val income = emptyList<IncomeSchedule>()

        val result = SafeNowCalculator.calculate(userSettings, bills, income, SELECTED_MONTH, TODAY)

        assertEquals("Safe Now should be starting cash", STARTING_CASH, result.safeNowCents)
        assertFalse("Should not have shortage", result.hasShortage)
    }

    @Test
    fun `multiple income on same day are cumulative`() {
        val userSettings = createUserSettings()
        val bills = listOf(
            createBill("Bill", 100000, "2024-07-25", isProtected = true)
        )
        val income = listOf(
            createIncome("Paycheck 1", 50000, "2024-07-20"),
            createIncome("Paycheck 2", 75000, "2024-07-20")
        )

        val result = SafeNowCalculator.calculate(userSettings, bills, income, SELECTED_MONTH, TODAY)

        // Jul 14: $1000
        // Jul 20: $1000 + $500 + $750 = $2250
        // Jul 25: $2250 - $1000 = $1250
        // Safe Now: $1000 (starting)
        assertEquals("Safe Now should be $1000.00", STARTING_CASH, result.safeNowCents)
        assertFalse("Should not have shortage", result.hasShortage)
    }
}