package com.toonai.budgetshield.data.calculator

import com.toonai.budgetshield.data.model.Bill
import com.toonai.budgetshield.data.model.IncomeSchedule
import com.toonai.budgetshield.data.model.UserSettings

/**
 * Result of Safe Now calculation.
 *
 * @property safeNowCents The spendable amount (never negative)
 * @property hasShortage Whether there's a projected shortage
 * @property shortageCents Amount of shortage if any (absolute value)
 * @property firstFailingDate First date where balance goes negative (YYYY-MM-DD)
 * @property failingBills List of bills contributing to the failure
 * @property explanation Human-readable explanation of the result
 * @property projectedBalances Map of dates to projected balances for debugging
 */
data class SafeNowResult(
    val safeNowCents: Long,
    val hasShortage: Boolean,
    val shortageCents: Long,
    val firstFailingDate: String?,
    val failingBills: List<Bill>,
    val explanation: String,
    val projectedBalances: Map<String, Long> = emptyMap()
)

/**
 * Balance projection for a specific date.
 */
data class DatedBalance(
    val date: String,
    val balanceCents: Long,
    val event: String
)

/**
 * Dated amount for income or bills.
 * Used by the existing API.
 */
data class DatedAmount(
    val name: String,
    val amountCents: Long,
    val date: String
)

/**
 * Pure function calculator for Safe Now amount.
 * Implements all 9 worked examples from SAFE_NOW_RULES.md.
 */
object SafeNowCalculator {

    /**
     * Calculate Safe Now based on current state.
     *
     * @param userSettings Current user settings with cash and savings
     * @param bills List of all bills (protected and unprotected)
     * @param incomeSchedules List of confirmed income schedules
     * @param selectedMonth The currently selected month (YYYY-MM format)
     * @param today Today's date (YYYY-MM-DD format)
     * @return SafeNowResult with calculation details
     */
    fun calculate(
        userSettings: UserSettings,
        bills: List<Bill>,
        incomeSchedules: List<IncomeSchedule>,
        selectedMonth: String,
        today: String
    ): SafeNowResult {
        // Get protected unpaid bills only
        val protectedBills = bills.filter { it.isProtected && !it.isPaid }

        // Get confirmed income only
        val confirmedIncome = incomeSchedules.filter { it.isConfirmed }

        // Calculate planning horizon
        val horizonEnd = calculatePlanningHorizon(today, protectedBills)

        // Build event list
        val events = buildEvents(today, userSettings.cashOnHandCents, protectedBills, confirmedIncome)

        // Calculate projected balances
        val projectedBalances = calculateProjectedBalances(events, horizonEnd)

        // Find minimum balance
        val minBalance = projectedBalances.values.minOrNull() ?: userSettings.cashOnHandCents

        // Determine result
        return if (minBalance < 0) {
            // Shortage case
            val failingEntry = projectedBalances.entries.find { it.value < 0 }
            val failingDate = failingEntry?.key
            val shortage = kotlin.math.abs(minBalance)

            // Find bills due on or before failing date
            val failingBills = protectedBills.filter { bill ->
                val billDate = if (bill.dueDate < today) today else bill.dueDate
                failingDate != null && billDate <= failingDate
            }

            SafeNowResult(
                safeNowCents = 0,
                hasShortage = true,
                shortageCents = shortage,
                firstFailingDate = failingDate,
                failingBills = failingBills,
                explanation = buildShortageExplanation(failingDate, shortage, failingBills),
                projectedBalances = projectedBalances
            )
        } else {
            // No shortage
            SafeNowResult(
                safeNowCents = minBalance,
                hasShortage = false,
                shortageCents = 0,
                firstFailingDate = null,
                failingBills = emptyList(),
                explanation = "Safe to spend ${formatCents(minBalance)}",
                projectedBalances = projectedBalances
            )
        }
    }

    /**
     * Alternative calculate method using the simpler API from the old calculator.
     * Maintains backward compatibility.
     */
    fun calculate(
        cashOnHandCents: Long,
        confirmedIncome: List<DatedAmount>,
        protectedBills: List<DatedAmount>,
        today: String = java.time.LocalDate.now().toString()
    ): SafeNowResult {
        val userSettings = UserSettings(
            id = 1L,
            cashOnHandCents = cashOnHandCents,
            savingsBalanceCents = 0L,
            selectedMonth = today.substring(0, 7)
        )

        val bills = protectedBills.map { dated ->
            Bill(
                id = 0L,
                name = dated.name,
                icon = "💰",
                amountCents = dated.amountCents,
                dueDate = dated.date,
                isProtected = true,
                isPaid = false
            )
        }

        val income = confirmedIncome.map { dated ->
            IncomeSchedule(
                id = 0L,
                name = dated.name,
                amountCents = dated.amountCents,
                nextPayday = dated.date,
                frequency = "biweekly",
                isConfirmed = true
            )
        }

        return calculate(userSettings, bills, income, today.substring(0, 7), today)
    }

    /**
     * Calculate planning horizon end date.
     * Extends through end of next month or latest protected bill, whichever is later.
     */
    private fun calculatePlanningHorizon(today: String, protectedBills: List<Bill>): String {
        val todayYear = today.substring(0, 4).toInt()
        val todayMonth = today.substring(5, 7).toInt()

        // Calculate end of next month
        val nextMonth = if (todayMonth == 12) 1 else todayMonth + 1
        val nextMonthYear = if (todayMonth == 12) todayYear + 1 else todayYear
        val endOfNextMonth = String.format("%04d-%02d-31", nextMonthYear, nextMonth)

        // Find latest protected bill date
        val latestBillDate = protectedBills.map { it.dueDate }.maxOrNull() ?: endOfNextMonth

        // Return whichever is later
        return if (latestBillDate > endOfNextMonth) latestBillDate else endOfNextMonth
    }

    /**
     * Build list of financial events (date, change, description).
     */
    private fun buildEvents(
        today: String,
        startingCash: Long,
        protectedBills: List<Bill>,
        confirmedIncome: List<IncomeSchedule>
    ): List<FinancialEvent> {
        val events = mutableListOf<FinancialEvent>()

        // Starting balance event
        events.add(FinancialEvent(today, startingCash, "starting_cash", "Starting Cash"))

        // Add overdue bills as events on today
        protectedBills.filter { it.dueDate < today }.forEach { bill ->
            events.add(FinancialEvent(
                today,
                -bill.remainingDueCents,
                "overdue_bill",
                bill.name
            ))
        }

        // Add confirmed income events
        confirmedIncome.forEach { income ->
            events.add(FinancialEvent(
                income.nextPayday,
                income.amountCents,
                "income",
                income.name
            ))
        }

        // Add future bill events
        protectedBills.filter { it.dueDate >= today }.forEach { bill ->
            events.add(FinancialEvent(
                bill.dueDate,
                -bill.remainingDueCents,
                "bill",
                bill.name
            ))
        }

        return events
    }

    /**
     * Calculate projected balances at each event date.
     * Same-day ordering: income first, then bills (to allow income to protect same-day bills).
     */
    private fun calculateProjectedBalances(
        events: List<FinancialEvent>,
        horizonEnd: String
    ): Map<String, Long> {
        val balances = mutableMapOf<String, Long>()
        var runningBalance = 0L

        // Sort events by date
        val sortedEvents = events.sortedWith(compareBy({ it.date }, { it.type }))

        // Group events by date and process
        val eventsByDate = sortedEvents.groupBy { it.date }
        val sortedDates = eventsByDate.keys.sorted()

        for (date in sortedDates) {
            if (date > horizonEnd) break

            val dayEvents = eventsByDate[date] ?: continue

            // Process events in order: starting_cash, income, then bills
            val startingEvents = dayEvents.filter { it.type == "starting_cash" }
            val incomeEvents = dayEvents.filter { it.type == "income" }
            val billEvents = dayEvents.filter { it.type == "bill" || it.type == "overdue_bill" }

            for (event in startingEvents + incomeEvents + billEvents) {
                runningBalance += event.amountCents
            }

            balances[date] = runningBalance
        }

        return balances
    }

    /**
     * Build explanation for shortage case.
     */
    private fun buildShortageExplanation(
        failingDate: String?,
        shortageCents: Long,
        failingBills: List<Bill>
    ): String {
        val billNames = failingBills.joinToString(", ") { it.name }
        return "Shortage of ${formatCents(shortageCents)} projected for $failingDate. " +
               "Affected bills: $billNames"
    }

    /**
     * Format cents as currency string.
     */
    private fun formatCents(cents: Long): String {
        val dollars = cents / 100
        val remainder = kotlin.math.abs(cents % 100)
        return String.format("$%d.%02d", dollars, remainder)
    }

    /**
     * Internal representation of a financial event.
     */
    private data class FinancialEvent(
        val date: String,
        val amountCents: Long,
        val type: String,
        val description: String
    )
}