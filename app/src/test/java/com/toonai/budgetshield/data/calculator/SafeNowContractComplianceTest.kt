package com.toonai.budgetshield.data.calculator

import com.toonai.budgetshield.data.model.Bill
import com.toonai.budgetshield.data.model.IncomeFrequency
import com.toonai.budgetshield.data.model.IncomeSchedule
import com.toonai.budgetshield.data.model.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class SafeNowContractComplianceTest {

    private fun settings(
        cash: Long = 10_000L,
        horizonMonths: Int = 2,
        selectedMonth: String = "2024-01"
    ) = UserSettings(
        cashOnHandCents = cash,
        planningHorizonMonths = horizonMonths,
        selectedMonth = selectedMonth
    )

    private fun bill(
        amount: Long,
        dueDate: String,
        paid: Long = 0L
    ) = Bill(
        name = "Protected bill",
        icon = "bill",
        amountCents = amount,
        paidAmountCents = paid,
        dueDate = dueDate,
        isProtected = true
    )

    private fun income(
        amount: Long,
        nextPayday: String,
        frequency: String,
        confirmed: Boolean = true,
        active: Boolean = true
    ) = IncomeSchedule(
        name = "Income",
        amountCents = amount,
        nextPayday = nextPayday,
        frequency = frequency,
        isConfirmed = confirmed,
        isActive = active
    )

    @Test
    fun `confirmed inactive income is excluded`() {
        val result = SafeNowCalculator.calculate(
            userSettings = settings(),
            bills = listOf(bill(15_000L, "2024-01-20")),
            incomeSchedules = listOf(
                income(10_000L, "2024-01-20", IncomeFrequency.ONE_TIME, active = false)
            ),
            selectedMonth = "2024-01",
            today = "2024-01-15"
        )

        assertEquals(0L, result.safeNowCents)
        assertTrue(result.hasShortage)
        assertEquals(5_000L, result.shortageCents)
    }

    @Test
    fun `configured horizon uses the final real day of the next month`() {
        val result = SafeNowCalculator.calculate(
            userSettings = settings(horizonMonths = 2),
            bills = emptyList(),
            incomeSchedules = emptyList(),
            selectedMonth = "2024-01",
            today = "2024-01-15"
        )

        assertEquals("2024-02-29", result.planningHorizonEnd)
    }

    @Test
    fun `known protected bill extends configured horizon`() {
        val result = SafeNowCalculator.calculate(
            userSettings = settings(cash = 20_000L),
            bills = listOf(bill(5_000L, "2024-04-15")),
            incomeSchedules = emptyList(),
            selectedMonth = "2024-01",
            today = "2024-01-15"
        )

        assertEquals("2024-04-15", result.planningHorizonEnd)
        assertEquals(15_000L, result.safeNowCents)
    }

    @Test
    fun `weekly income recurs through the horizon`() {
        val result = SafeNowCalculator.calculate(
            userSettings = settings(),
            bills = listOf(bill(18_000L, "2024-01-28")),
            incomeSchedules = listOf(
                income(5_000L, "2024-01-20", IncomeFrequency.WEEKLY)
            ),
            selectedMonth = "2024-01",
            today = "2024-01-15"
        )

        assertEquals(2_000L, result.safeNowCents)
        assertEquals(15_000L, result.projectedBalances["2024-01-20"])
        assertEquals(20_000L, result.projectedBalances["2024-01-27"])
    }

    @Test
    fun `monthly income preserves anchor and clamps to month end`() {
        val result = SafeNowCalculator.calculate(
            userSettings = settings(),
            bills = listOf(bill(25_000L, "2024-02-29")),
            incomeSchedules = listOf(
                income(10_000L, "2024-01-31", IncomeFrequency.MONTHLY)
            ),
            selectedMonth = "2024-01",
            today = "2024-01-15"
        )

        assertEquals(5_000L, result.safeNowCents)
        assertEquals(5_000L, result.projectedBalances["2024-02-29"])
    }

    @Test
    fun `one time income is not repeated`() {
        val result = SafeNowCalculator.calculate(
            userSettings = settings(),
            bills = listOf(bill(16_000L, "2024-02-20")),
            incomeSchedules = listOf(
                income(5_000L, "2024-01-20", IncomeFrequency.ONE_TIME)
            ),
            selectedMonth = "2024-01",
            today = "2024-01-15"
        )

        assertTrue(result.hasShortage)
        assertEquals(1_000L, result.shortageCents)
    }

    @Test
    fun `invalid persisted date blocks calculation`() {
        assertThrows(IllegalArgumentException::class.java) {
            SafeNowCalculator.calculate(
                userSettings = settings(),
                bills = listOf(bill(5_000L, "2024-02-31")),
                incomeSchedules = emptyList(),
                selectedMonth = "2024-01",
                today = "2024-01-15"
            )
        }
    }

    @Test
    fun `arithmetic overflow blocks calculation`() {
        assertThrows(ArithmeticException::class.java) {
            SafeNowCalculator.calculate(
                userSettings = settings(cash = Long.MAX_VALUE),
                bills = emptyList(),
                incomeSchedules = listOf(
                    income(1L, "2024-01-15", IncomeFrequency.ONE_TIME)
                ),
                selectedMonth = "2024-01",
                today = "2024-01-15"
            )
        }
    }
}
