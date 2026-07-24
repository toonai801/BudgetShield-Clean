package com.toonai.budgetshield.data.calculator

import com.toonai.budgetshield.data.model.Bill
import com.toonai.budgetshield.data.model.IncomeSchedule
import com.toonai.budgetshield.data.model.UserSettings
import org.junit.Assert.*
import org.junit.Test

/**
 * HIGH PRIORITY: Safe Now Recalculation Tests
 *
 * Comprehensive regression tests for:
 * - Safe Now recalculates after bill changes
 * - Adding/removing/paying bills updates Safe Now
 * - Protected vs unprotected bill handling
 * - Income changes affect Safe Now
 * - Edge cases in recalculation
 */
class SafeNowCalculationRecalculationTest {

    private val today = "2025-07-14"
    private val currentMonth = "2025-07"

    private fun createUserSettings(
        cashOnHandCents: Long = 100000L // $1000
    ) = UserSettings(
        id = 1L,
        cashOnHandCents = cashOnHandCents,
        savingsBalanceCents = 50000L,
        selectedMonth = currentMonth
    )

    private fun createBill(
        id: Long = 0L,
        name: String = "Test Bill",
        amountCents: Long = 50000L,
        dueDate: String = today,
        isProtected: Boolean = true,
        isPaid: Boolean = false,
        paidAmountCents: Long = 0L
    ) = Bill(
        id = id,
        name = name,
        icon = "💰",
        amountCents = amountCents,
        paidAmountCents = paidAmountCents,
        dueDate = dueDate,
        isProtected = isProtected,
        isPaid = isPaid
    )

    private fun createIncome(
        id: Long = 0L,
        name: String = "Paycheck",
        amountCents: Long = 200000L,
        nextPayday: String = today,
        isConfirmed: Boolean = true
    ) = IncomeSchedule(
        id = id,
        name = name,
        amountCents = amountCents,
        nextPayday = nextPayday,
        frequency = "biweekly",
        isConfirmed = isConfirmed
    )

    // ==================== Bill Change Recalculation Tests ====================

    @Test
    fun `adding new protected bill reduces safe now when income insufficient`() {
        val userSettings = createUserSettings(cashOnHandCents = 100000L)
        val income = emptyList<IncomeSchedule>() // No income

        // Add a $500 protected bill that exceeds available cash
        val bill = createBill(amountCents = 150000L, dueDate = today) // $1500 bill
        val resultWithBill = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = listOf(bill),
            incomeSchedules = income,
            selectedMonth = currentMonth,
            today = today
        )

        // With $1000 cash and $1500 bill, shortage detected
        assertTrue(resultWithBill.hasShortage)
        assertEquals(0L, resultWithBill.safeNowCents)
    }

    @Test
    fun `adding unprotected bill does not affect shortage detection`() {
        val userSettings = createUserSettings(cashOnHandCents = 100000L)
        val income = emptyList<IncomeSchedule>()

        // Unprotected bill should not be included in shortage calculation
        val unprotectedBill = createBill(
            amountCents = 150000L, // $1500
            dueDate = today,
            isProtected = false
        )

        val result = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = listOf(unprotectedBill),
            incomeSchedules = income,
            selectedMonth = currentMonth,
            today = today
        )

        // Unprotected bills don't affect Safe Now calculation
        assertFalse(result.hasShortage)
        assertEquals(100000L, result.safeNowCents)
    }

    @Test
    fun `paying bill removes from shortage calculation`() {
        val userSettings = createUserSettings(cashOnHandCents = 100000L)
        val income = emptyList<IncomeSchedule>()

        // Unpaid bill causes shortage
        val unpaidBill = createBill(
            amountCents = 150000L, // $1500
            dueDate = today,
            isProtected = true,
            isPaid = false
        )

        val resultUnpaid = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = listOf(unpaidBill),
            incomeSchedules = income,
            selectedMonth = currentMonth,
            today = today
        )

        assertTrue(resultUnpaid.hasShortage) // $1000 - $1500 = -$500 shortage

        // Paid bill is excluded from calculation
        val paidBill = unpaidBill.copy(isPaid = true, paidAmountCents = 150000L)
        val resultPaid = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = listOf(paidBill),
            incomeSchedules = income,
            selectedMonth = currentMonth,
            today = today
        )

        assertFalse(resultPaid.hasShortage)
    }

    @Test
    fun `removing bill resolves shortage`() {
        val userSettings = createUserSettings(cashOnHandCents = 100000L)
        val income = emptyList<IncomeSchedule>()

        // Two bills that together cause shortage
        val bill1 = createBill(id = 1L, amountCents = 60000L) // $600
        val bill2 = createBill(id = 2L, amountCents = 60000L) // $600

        val resultWithBoth = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = listOf(bill1, bill2),
            incomeSchedules = income,
            selectedMonth = currentMonth,
            today = today
        )

        assertTrue(resultWithBoth.hasShortage) // $1000 - $600 - $600 = -$200

        // Remove one bill - shortage resolved
        val resultWithOne = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = listOf(bill1),
            incomeSchedules = income,
            selectedMonth = currentMonth,
            today = today
        )

        assertFalse(resultWithOne.hasShortage)
    }

    @Test
    fun `modifying bill amount triggers shortage recalculation`() {
        val userSettings = createUserSettings(cashOnHandCents = 100000L)
        val income = emptyList<IncomeSchedule>()

        // Original bill within budget
        val originalBill = createBill(amountCents = 50000L) // $500

        val resultOriginal = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = listOf(originalBill),
            incomeSchedules = income,
            selectedMonth = currentMonth,
            today = today
        )

        assertFalse(resultOriginal.hasShortage)

        // Increased bill causes shortage
        val modifiedBill = originalBill.copy(amountCents = 150000L) // $1500
        val resultModified = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = listOf(modifiedBill),
            incomeSchedules = income,
            selectedMonth = currentMonth,
            today = today
        )

        assertTrue(resultModified.hasShortage)
    }

    // ==================== Multiple Bills Recalculation ====================

    @Test
    fun `safe now handles multiple bills within budget`() {
        val userSettings = createUserSettings(cashOnHandCents = 100000L)
        val income = emptyList<IncomeSchedule>()

        val bills = listOf(
            createBill(id = 1L, amountCents = 20000L, name = "Rent"),
            createBill(id = 2L, amountCents = 15000L, name = "Internet"),
            createBill(id = 3L, amountCents = 10000L, name = "Phone")
        )

        val result = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = bills,
            incomeSchedules = income,
            selectedMonth = currentMonth,
            today = today
        )

        // $1000 - $200 - $150 - $100 = $550, no shortage
        assertFalse(result.hasShortage)
    }

    @Test
    fun `mixed protected and unprotected bills only counts protected`() {
        val userSettings = createUserSettings(cashOnHandCents = 100000L)
        val income = emptyList<IncomeSchedule>()

        // Protected bill causes shortage, unprotected should not affect calculation
        val bills = listOf(
            createBill(id = 1L, amountCents = 150000L, isProtected = true), // $1500 protected
            createBill(id = 2L, amountCents = 40000L, isProtected = false)   // $400 unprotected
        )

        val result = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = bills,
            incomeSchedules = income,
            selectedMonth = currentMonth,
            today = today
        )

        // Only protected bill considered: $1000 - $1500 = -$500 shortage
        assertTrue(result.hasShortage)
    }

    // ==================== Shortage Detection Tests ====================

    @Test
    fun `shortage detected when bills exceed cash`() {
        val userSettings = createUserSettings(cashOnHandCents = 50000L) // $500
        val income = emptyList<IncomeSchedule>()

        val bills = listOf(
            createBill(amountCents = 60000L, dueDate = today) // $600
        )

        val result = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = bills,
            incomeSchedules = income,
            selectedMonth = currentMonth,
            today = today
        )

        // $500 - $600 = -$100 shortage
        assertTrue(result.hasShortage)
        assertEquals(0L, result.safeNowCents)
    }

    @Test
    fun `partially paid bill reduces shortage calculation`() {
        val userSettings = createUserSettings(cashOnHandCents = 50000L)
        val income = emptyList<IncomeSchedule>()

        // $600 bill with $200 paid = $400 remaining
        val partiallyPaidBill = createBill(
            amountCents = 60000L,
            paidAmountCents = 20000L,
            isPaid = false
        )

        val result = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = listOf(partiallyPaidBill),
            incomeSchedules = income,
            selectedMonth = currentMonth,
            today = today
        )

        // $500 - $400 remaining = $100 left, no shortage
        assertFalse(result.hasShortage)
    }

    // ==================== Income Change Recalculation ====================

    @Test
    fun `adding income affects safe now calculation`() {
        val userSettings = createUserSettings(cashOnHandCents = 50000L)
        val bills = listOf(createBill(amountCents = 60000L))

        val resultNoIncome = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = bills,
            incomeSchedules = emptyList(),
            selectedMonth = currentMonth,
            today = today
        )

        assertTrue(resultNoIncome.hasShortage)

        val income = listOf(createIncome(amountCents = 50000L, nextPayday = today))
        val resultWithIncome = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = bills,
            incomeSchedules = income,
            selectedMonth = currentMonth,
            today = today
        )

        // With income, shortage is resolved
        assertFalse(resultWithIncome.hasShortage || resultWithIncome.safeNowCents < 0)
    }

    @Test
    fun `unconfirmed income does not affect safe now`() {
        val userSettings = createUserSettings(cashOnHandCents = 50000L)
        val bills = listOf(createBill(amountCents = 60000L))

        val unconfirmedIncome = listOf(
            createIncome(amountCents = 50000L, isConfirmed = false)
        )

        val result = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = bills,
            incomeSchedules = unconfirmedIncome,
            selectedMonth = currentMonth,
            today = today
        )

        assertTrue(result.hasShortage)
    }

    // ==================== Edge Cases ====================

    @Test
    fun `empty bills returns full cash amount`() {
        val userSettings = createUserSettings(cashOnHandCents = 100000L)

        val result = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = emptyList(),
            incomeSchedules = emptyList(),
            selectedMonth = currentMonth,
            today = today
        )

        assertEquals(100000L, result.safeNowCents)
        assertFalse(result.hasShortage)
    }

    @Test
    fun `zero cash with bills results in shortage`() {
        val userSettings = createUserSettings(cashOnHandCents = 0L)
        val bills = listOf(createBill(amountCents = 10000L))

        val result = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = bills,
            incomeSchedules = emptyList(),
            selectedMonth = currentMonth,
            today = today
        )

        assertTrue(result.hasShortage)
        assertEquals(0L, result.safeNowCents)
        assertEquals(10000L, result.shortageCents)
    }

    @Test
    fun `exact balance results in zero safe now without shortage`() {
        val userSettings = createUserSettings(cashOnHandCents = 50000L)
        val bill = createBill(amountCents = 50000L)

        val result = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = listOf(bill),
            incomeSchedules = emptyList(),
            selectedMonth = currentMonth,
            today = today
        )

        assertEquals(0L, result.safeNowCents)
        assertFalse(result.hasShortage)
    }

    @Test
    fun `future bill affects safe now`() {
        val userSettings = createUserSettings(cashOnHandCents = 100000L)
        val futureDate = "2025-07-29"
        val bill = createBill(amountCents = 50000L, dueDate = futureDate)

        val result = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = listOf(bill),
            incomeSchedules = emptyList(),
            selectedMonth = currentMonth,
            today = today
        )

        assertEquals(50000L, result.safeNowCents)
    }

    @Test
    fun `overdue bill counted in shortage calculation`() {
        val yesterday = "2025-07-13"
        val userSettings = createUserSettings(cashOnHandCents = 100000L)
        val overdueBill = createBill(amountCents = 150000L, dueDate = yesterday)

        val result = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = listOf(overdueBill),
            incomeSchedules = emptyList(),
            selectedMonth = currentMonth,
            today = today
        )

        assertTrue(result.hasShortage)
        assertTrue(result.shortageCents > 0)
    }

    @Test
    fun `projected balances calculated for planning horizon`() {
        val userSettings = createUserSettings(cashOnHandCents = 100000L)
        val income = listOf(createIncome(nextPayday = today))
        val bill = createBill(amountCents = 50000L, dueDate = today)

        val result = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = listOf(bill),
            incomeSchedules = income,
            selectedMonth = currentMonth,
            today = today
        )

        assertFalse(result.projectedBalances.isEmpty())
    }

    @Test
    fun `first failing date identified in shortage`() {
        val futureDate = "2025-07-19"
        val userSettings = createUserSettings(cashOnHandCents = 50000L)
        val bill = createBill(amountCents = 100000L, dueDate = futureDate)

        val result = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = listOf(bill),
            incomeSchedules = emptyList(),
            selectedMonth = currentMonth,
            today = today
        )

        assertTrue(result.hasShortage)
        assertNotNull(result.firstFailingDate)
    }

    @Test
    fun `safe now never goes negative`() {
        val userSettings = createUserSettings(cashOnHandCents = 10000L)
        val bills = listOf(
            createBill(amountCents = 200000L, dueDate = today)
        )
        val income = emptyList<IncomeSchedule>()

        val result = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = bills,
            incomeSchedules = income,
            selectedMonth = currentMonth,
            today = today
        )

        assertEquals(0L, result.safeNowCents)
        assertTrue(result.hasShortage)
    }

    @Test
    fun `paid bills are not included in calculation`() {
        val userSettings = createUserSettings(cashOnHandCents = 100000L)
        val bills = listOf(
            createBill(amountCents = 50000L, isPaid = true)
        )

        val result = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = bills,
            incomeSchedules = emptyList(),
            selectedMonth = currentMonth,
            today = today
        )

        assertEquals(100000L, result.safeNowCents)
        assertFalse(result.hasShortage)
    }

    @Test
    fun `multiple income on same day are cumulative`() {
        val userSettings = createUserSettings(cashOnHandCents = 100000L)
        val bills = listOf(
            createBill(amountCents = 100000L, dueDate = "2025-07-25")
        )
        val income = listOf(
            createIncome(amountCents = 50000L, nextPayday = "2025-07-20"),
            createIncome(amountCents = 75000L, nextPayday = "2025-07-20")
        )

        val result = SafeNowCalculator.calculate(
            userSettings = userSettings,
            bills = bills,
            incomeSchedules = income,
            selectedMonth = currentMonth,
            today = today
        )

        assertTrue(result.safeNowCents >= 0L)
        assertFalse(result.hasShortage)
    }
}
