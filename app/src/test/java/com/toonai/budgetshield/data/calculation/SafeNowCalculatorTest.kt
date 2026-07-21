package com.toonai.budgetshield.data.calculation

import com.toonai.budgetshield.data.model.Bill
import com.toonai.budgetshield.data.model.IncomeSchedule
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/**
 * Unit tests for SafeNowCalculator covering all 9 worked examples.
 */
class SafeNowCalculatorTest {

    private val calculator = SafeNowCalculator()

    // Example 1: Bill Due Before Next Payday
    @Test
    fun example1_billDueBeforePayday() {
        val today = LocalDate.of(2025, 7, 14)
        val clearedCash = 100000L // $1,000.00

        val income = listOf(
            IncomeSchedule(
                id = 1,
                name = "Payday",
                amountCents = 150000, // $1,500.00
                frequency = "biweekly",
                nextPaydayDate = "2025-07-25",
                isConfirmed = true
            )
        )

        val bills = listOf(
            Bill(
                id = 1,
                name = "Rent",
                icon = "🏠",
                amountCents = 80000, // $800.00
                dueDate = "2025-07-20",
                isProtected = true,
                isPaid = false
            )
        )

        val result = calculator.calculate(clearedCash, income, bills, today)

        assertEquals(20000L, result.safeNowCents) // $200.00
        assertEquals(0L, result.shortageCents)
        assertEquals(null, result.firstFailingDate)
    }

    // Example 2: Bill and Confirmed Income on Same Day
    @Test
    fun example2_billAndIncomeSameDay() {
        val today = LocalDate.of(2025, 7, 14)
        val clearedCash = 100000L

        val income = listOf(
            IncomeSchedule(
                id = 1,
                name = "Side Income",
                amountCents = 50000, // $500.00
                frequency = "one_time",
                nextPaydayDate = "2025-07-20",
                isConfirmed = true
            )
        )

        val bills = listOf(
            Bill(
                id = 1,
                name = "Bill",
                icon = "📋",
                amountCents = 30000, // $300.00
                dueDate = "2025-07-20",
                isProtected = true,
                isPaid = false
            )
        )

        val result = calculator.calculate(clearedCash, income, bills, today)

        // Today's balance ($1000) is the minimum
        assertEquals(100000L, result.safeNowCents) // $1,000.00
        assertEquals(0L, result.shortageCents)
    }

    // Example 3: Income Arriving One Day After a Bill
    @Test
    fun example3_incomeAfterBill() {
        val today = LocalDate.of(2025, 7, 14)
        val clearedCash = 100000L

        val income = listOf(
            IncomeSchedule(
                id = 1,
                name = "Payday",
                amountCents = 150000,
                frequency = "biweekly",
                nextPaydayDate = "2025-07-21", // One day after bill
                isConfirmed = true
            )
        )

        val bills = listOf(
            Bill(
                id = 1,
                name = "Bill",
                icon = "📋",
                amountCents = 80000,
                dueDate = "2025-07-20",
                isProtected = true,
                isPaid = false
            )
        )

        val result = calculator.calculate(clearedCash, income, bills, today)

        assertEquals(20000L, result.safeNowCents) // $200.00
        assertEquals(0L, result.shortageCents)
    }

    // Example 4: Two Paychecks and Multiple Bills Across Two Months
    @Test
    fun example4_multiplePaychecksAndBills() {
        val today = LocalDate.of(2025, 7, 14)
        val clearedCash = 100000L

        val income = listOf(
            IncomeSchedule(
                id = 1,
                name = "Payday 1",
                amountCents = 150000,
                frequency = "biweekly",
                nextPaydayDate = "2025-07-20",
                isConfirmed = true
            ),
            IncomeSchedule(
                id = 2,
                name = "Payday 2",
                amountCents = 150000,
                frequency = "biweekly",
                nextPaydayDate = "2025-08-05",
                isConfirmed = true
            )
        )

        val bills = listOf(
            Bill(id = 1, name = "Rent", icon = "🏠", amountCents = 80000, dueDate = "2025-07-25", isProtected = true, isPaid = false),
            Bill(id = 2, name = "Utilities", icon = "💡", amountCents = 15000, dueDate = "2025-07-28", isProtected = true, isPaid = false),
            Bill(id = 3, name = "Insurance", icon = "🛡️", amountCents = 20000, dueDate = "2025-08-10", isProtected = true, isPaid = false),
            Bill(id = 4, name = "Internet", icon = "🌐", amountCents = 8000, dueDate = "2025-08-15", isProtected = true, isPaid = false)
        )

        val result = calculator.calculate(clearedCash, income, bills, today)

        assertEquals(100000L, result.safeNowCents) // $1,000.00 - starting is minimum
        assertEquals(0L, result.shortageCents)
    }

    // Example 5: Overdue Bill
    @Test
    fun example5_overdueBill() {
        val today = LocalDate.of(2025, 7, 14)
        val clearedCash = 100000L

        val income = listOf(
            IncomeSchedule(
                id = 1,
                name = "Payday",
                amountCents = 50000,
                frequency = "biweekly",
                nextPaydayDate = "2025-07-20",
                isConfirmed = true
            )
        )

        val bills = listOf(
            Bill(
                id = 1,
                name = "Overdue Bill",
                icon = "⚠️",
                amountCents = 20000, // $200.00
                dueDate = "2025-07-10", // 4 days ago
                isProtected = true,
                isPaid = false
            )
        )

        val result = calculator.calculate(clearedCash, income, bills, today)

        // Overdue bill treated as due today
        assertEquals(80000L, result.safeNowCents) // $800.00
        assertEquals(0L, result.shortageCents)
    }

    // Example 6: Partially Paid Bill
    @Test
    fun example6_partiallyPaidBill() {
        val today = LocalDate.of(2025, 7, 14)
        val clearedCash = 100000L

        val income = listOf(
            IncomeSchedule(
                id = 1,
                name = "Payday",
                amountCents = 80000,
                frequency = "biweekly",
                nextPaydayDate = "2025-07-25",
                isConfirmed = true
            )
        )

        // Bill was $500, $200 paid, $300 remaining
        val bills = listOf(
            Bill(
                id = 1,
                name = "Partial Bill",
                icon = "📋",
                amountCents = 50000, // Original $500
                paidAmountCents = 20000, // $200 paid
                dueDate = "2025-07-20",
                isProtected = true,
                isPaid = false
            )
        )

        val result = calculator.calculate(clearedCash, income, bills, today)

        assertEquals(70000L, result.safeNowCents) // $700.00
        assertEquals(0L, result.shortageCents)
    }

    // Example 7: Unconfirmed Side Income
    @Test
    fun example7_unconfirmedIncome() {
        val today = LocalDate.of(2025, 7, 14)
        val clearedCash = 100000L

        val income = listOf(
            IncomeSchedule(
                id = 1,
                name = "Unconfirmed Side Income",
                amountCents = 50000,
                frequency = "one_time",
                nextPaydayDate = "2025-07-19",
                isConfirmed = false // NOT confirmed
            ),
            IncomeSchedule(
                id = 2,
                name = "Confirmed Payday",
                amountCents = 100000,
                frequency = "biweekly",
                nextPaydayDate = "2025-07-25",
                isConfirmed = true
            )
        )

        val bills = listOf(
            Bill(
                id = 1,
                name = "Bill",
                icon = "📋",
                amountCents = 80000,
                dueDate = "2025-07-20",
                isProtected = true,
                isPaid = false
            )
        )

        val result = calculator.calculate(clearedCash, income, bills, today)

        // Unconfirmed income on July 19 is NOT used
        assertEquals(20000L, result.safeNowCents) // $200.00
        assertEquals(0L, result.shortageCents)
    }

    // Example 8: Unprotected Bill
    @Test
    fun example8_unprotectedBill() {
        val today = LocalDate.of(2025, 7, 14)
        val clearedCash = 100000L

        val income = listOf(
            IncomeSchedule(
                id = 1,
                name = "Payday",
                amountCents = 100000,
                frequency = "biweekly",
                nextPaydayDate = "2025-07-25",
                isConfirmed = true
            )
        )

        val bills = listOf(
            Bill(
                id = 1,
                name = "Protected Rent",
                icon = "🏠",
                amountCents = 80000,
                dueDate = "2025-07-20",
                isProtected = true,
                isPaid = false
            ),
            Bill(
                id = 2,
                name = "Unprotected Streaming",
                icon = "📺",
                amountCents = 1500, // $15.00
                dueDate = "2025-07-22",
                isProtected = false, // NOT protected
                isPaid = false
            )
        )

        val result = calculator.calculate(clearedCash, income, bills, today)

        assertEquals(20000L, result.safeNowCents) // $200.00 (unprotected bill excluded)
        assertEquals(0L, result.shortageCents)
        assertEquals(true, result.hasUnprotectedBills)
    }

    // Example 9: Spending Transaction Causes Underfunding
    @Test
    fun example9_spendingCausesUnderfunding() {
        val today = LocalDate.of(2025, 7, 14)
        // After spending $800, only $200 remaining
        val clearedCash = 20000L // $200.00

        val income = listOf(
            IncomeSchedule(
                id = 1,
                name = "Payday",
                amountCents = 50000,
                frequency = "biweekly",
                nextPaydayDate = "2025-07-18",
                isConfirmed = true
            )
        )

        val bills = listOf(
            Bill(
                id = 1,
                name = "Insurance",
                icon = "🛡️",
                amountCents = 90000, // $900.00
                dueDate = "2025-07-20",
                isProtected = true,
                isPaid = false
            )
        )

        val result = calculator.calculate(clearedCash, income, bills, today)

        assertEquals(0L, result.safeNowCents)
        assertEquals(20000L, result.shortageCents) // $200.00 shortage
        assertEquals(LocalDate.of(2025, 7, 20), result.firstFailingDate)
    }

    // Edge case: Zero cleared cash
    @Test
    fun edgeCase_zeroClearedCash() {
        val today = LocalDate.of(2025, 7, 14)
        val clearedCash = 0L

        val income = emptyList<IncomeSchedule>()
        val bills = emptyList<Bill>()

        val result = calculator.calculate(clearedCash, income, bills, today)

        assertEquals(0L, result.safeNowCents)
        assertEquals(0L, result.shortageCents)
    }

    // Edge case: Invalid date parsing
    @Test
    fun edgeCase_invalidDates() {
        val today = LocalDate.of(2025, 7, 14)
        val clearedCash = 100000L

        val income = listOf(
            IncomeSchedule(
                id = 1,
                name = "Invalid Income",
                amountCents = 50000,
                frequency = "one_time",
                nextPaydayDate = "invalid-date",
                isConfirmed = true
            )
        )

        val bills = listOf(
            Bill(
                id = 1,
                name = "Invalid Bill",
                icon = "❓",
                amountCents = 30000,
                dueDate = "invalid-date",
                isProtected = true,
                isPaid = false
            )
        )

        val result = calculator.calculate(clearedCash, income, bills, today)

        // Should handle invalid dates gracefully
        assertEquals(100000L, result.safeNowCents)
        assertEquals(0L, result.shortageCents)
    }

    // Edge case: Large money amounts
    @Test
    fun edgeCase_largeAmounts() {
        val today = LocalDate.of(2025, 7, 14)
        val clearedCash = 10_000_000L // $100,000.00

        val income = listOf(
            IncomeSchedule(
                id = 1,
                name = "Large Income",
                amountCents = 50_000_000L, // $500,000.00
                frequency = "monthly",
                nextPaydayDate = "2025-07-20",
                isConfirmed = true
            )
        )

        val bills = listOf(
            Bill(
                id = 1,
                name = "Large Bill",
                icon = "🏢",
                amountCents = 5_000_000L, // $50,000.00
                dueDate = "2025-07-25",
                isProtected = true,
                isPaid = false
            )
        )

        val result = calculator.calculate(clearedCash, income, bills, today)

        assertEquals(10_000_000L, result.safeNowCents) // Starting is minimum
        assertEquals(0L, result.shortageCents)
    }
}
