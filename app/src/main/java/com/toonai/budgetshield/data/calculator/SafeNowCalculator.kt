package com.toonai.budgetshield.data.calculator

import com.toonai.budgetshield.data.model.Bill
import com.toonai.budgetshield.data.model.IncomeFrequency
import com.toonai.budgetshield.data.model.IncomeSchedule
import com.toonai.budgetshield.data.model.UserSettings
import java.time.LocalDate
import java.time.YearMonth

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
    val planningHorizonEnd: String,
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
        val todayDate = parseIsoDate(today, "today")
        parseMonth(selectedMonth, "selected month")
        validateInputs(userSettings, bills, incomeSchedules)

        // Get protected unpaid bills only
        val protectedBills = bills.filter {
            it.isProtected && !it.isPaid && it.remainingDueCents > 0L
        }

        // Only active and confirmed income can increase Safe Now.
        val confirmedIncome = incomeSchedules.filter { it.isConfirmed && it.isActive }

        // Calculate planning horizon
        val horizonEnd = calculatePlanningHorizon(
            today = todayDate,
            planningHorizonMonths = userSettings.planningHorizonMonths,
            protectedBills = protectedBills
        )

        // Build event list
        val events = buildEvents(
            today = todayDate,
            horizonEnd = horizonEnd,
            startingCash = userSettings.cashOnHandCents,
            protectedBills = protectedBills,
            confirmedIncome = confirmedIncome
        )

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
                planningHorizonEnd = horizonEnd.toString(),
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
                planningHorizonEnd = horizonEnd.toString(),
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
    private fun calculatePlanningHorizon(
        today: LocalDate,
        planningHorizonMonths: Int,
        protectedBills: List<Bill>
    ): LocalDate {
        val configuredEnd = YearMonth.from(today)
            .plusMonths((planningHorizonMonths - 1).toLong())
            .atEndOfMonth()
        val latestBillDate = protectedBills
            .maxOfOrNull { LocalDate.parse(it.dueDate) }

        return if (latestBillDate != null && latestBillDate > configuredEnd) {
            latestBillDate
        } else {
            configuredEnd
        }
    }

    /**
     * Build list of financial events (date, change, description).
     */
    private fun buildEvents(
        today: LocalDate,
        horizonEnd: LocalDate,
        startingCash: Long,
        protectedBills: List<Bill>,
        confirmedIncome: List<IncomeSchedule>
    ): List<FinancialEvent> {
        val events = mutableListOf<FinancialEvent>()

        // Starting balance event
        events.add(FinancialEvent(today, startingCash, "starting_cash", "Starting Cash"))

        // Add overdue bills as events on today
        protectedBills.filter { LocalDate.parse(it.dueDate) < today }.forEach { bill ->
            events.add(FinancialEvent(
                today,
                Math.negateExact(bill.remainingDueCents),
                "overdue_bill",
                bill.name
            ))
        }

        // Add confirmed income events
        confirmedIncome.forEach { income ->
            generateIncomeOccurrences(income, today, horizonEnd).forEach { occurrenceDate ->
                events.add(FinancialEvent(
                    occurrenceDate,
                    income.amountCents,
                    "income",
                    income.name
                ))
            }
        }

        // Add future bill events
        protectedBills.filter { LocalDate.parse(it.dueDate) >= today }.forEach { bill ->
            events.add(FinancialEvent(
                LocalDate.parse(bill.dueDate),
                Math.negateExact(bill.remainingDueCents),
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
        horizonEnd: LocalDate
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
                runningBalance = Math.addExact(runningBalance, event.amountCents)
            }

            balances[date.toString()] = runningBalance
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

    private fun validateInputs(
        userSettings: UserSettings,
        bills: List<Bill>,
        incomeSchedules: List<IncomeSchedule>
    ) {
        require(userSettings.cashOnHandCents >= 0L) { "Cash on hand cannot be negative" }
        require(userSettings.planningHorizonMonths >= 2) {
            "Planning horizon must cover at least the current and next calendar month"
        }

        bills.forEach { bill ->
            require(bill.amountCents > 0L) { "Bill amount must be positive" }
            require(bill.paidAmountCents in 0L..bill.amountCents) {
                "Bill paid amount must be between zero and the bill amount"
            }
            parseIsoDate(bill.dueDate, "bill due date")
        }

        incomeSchedules.forEach { income ->
            require(income.amountCents > 0L) { "Income amount must be positive" }
            parseIsoDate(income.nextPayday, "income payday")
            require(income.frequency in SUPPORTED_FREQUENCIES) {
                "Unsupported income frequency: ${income.frequency}"
            }
            IncomeRecurrencePolicy.validateAnchors(
                frequency = income.frequency,
                anchorDayOne = income.paydayAnchorDayOne,
                anchorDayTwo = income.paydayAnchorDayTwo
            )
        }
    }

    private fun generateIncomeOccurrences(
        income: IncomeSchedule,
        today: LocalDate,
        horizonEnd: LocalDate
    ): List<LocalDate> {
        val firstPayday = LocalDate.parse(income.nextPayday)
        if (income.frequency == IncomeFrequency.ONE_TIME) {
            return if (firstPayday in today..horizonEnd) listOf(firstPayday) else emptyList()
        }

        if (IncomeRecurrencePolicy.isSemimonthly(income.frequency)) {
            return generateSemimonthlyOccurrences(income, firstPayday, today, horizonEnd)
        }

        val occurrences = mutableListOf<LocalDate>()
        val monthlyAnchorDay = firstPayday.dayOfMonth
        var occurrence = firstPayday
        while (occurrence < today) {
            occurrence = nextOccurrence(occurrence, income.frequency, monthlyAnchorDay)
        }
        while (occurrence <= horizonEnd) {
            occurrences.add(occurrence)
            occurrence = nextOccurrence(occurrence, income.frequency, monthlyAnchorDay)
        }
        return occurrences
    }

    private fun generateSemimonthlyOccurrences(
        income: IncomeSchedule,
        firstPayday: LocalDate,
        today: LocalDate,
        horizonEnd: LocalDate
    ): List<LocalDate> {
        val anchorOne = requireNotNull(income.paydayAnchorDayOne)
        val anchorTwo = requireNotNull(income.paydayAnchorDayTwo)
        val firstMonth = YearMonth.from(firstPayday)
        val lastMonth = YearMonth.from(horizonEnd)
        val lowerBound = maxOf(firstPayday, today)
        val occurrences = mutableListOf<LocalDate>()
        var month = firstMonth

        while (month <= lastMonth) {
            val monthDates = listOf(
                IncomeRecurrencePolicy.resolveDay(month, anchorOne),
                IncomeRecurrencePolicy.resolveDay(month, anchorTwo)
            ).sorted()
            occurrences += monthDates.filter { it in lowerBound..horizonEnd }
            month = month.plusMonths(1)
        }
        return occurrences
    }

    private fun nextOccurrence(
        current: LocalDate,
        frequency: String,
        monthlyAnchorDay: Int
    ): LocalDate = when (frequency) {
        IncomeFrequency.WEEKLY -> current.plusWeeks(1)
        IncomeFrequency.BIWEEKLY -> current.plusWeeks(2)
        IncomeFrequency.MONTHLY -> {
            val nextMonth = YearMonth.from(current).plusMonths(1)
            nextMonth.atDay(minOf(monthlyAnchorDay, nextMonth.lengthOfMonth()))
        }
        else -> error("Unsupported recurring frequency: $frequency")
    }

    private fun parseIsoDate(value: String, label: String): LocalDate = try {
        LocalDate.parse(value)
    } catch (error: Exception) {
        throw IllegalArgumentException("Invalid $label: $value", error)
    }

    private fun parseMonth(value: String, label: String): YearMonth = try {
        YearMonth.parse(value)
    } catch (error: Exception) {
        throw IllegalArgumentException("Invalid $label: $value", error)
    }

    /**
     * Internal representation of a financial event.
     */
    private data class FinancialEvent(
        val date: LocalDate,
        val amountCents: Long,
        val type: String,
        val description: String
    )

    private val SUPPORTED_FREQUENCIES = setOf(
        IncomeFrequency.WEEKLY,
        IncomeFrequency.BIWEEKLY,
        IncomeFrequency.SEMIMONTHLY,
        IncomeFrequency.MONTHLY,
        IncomeFrequency.ONE_TIME,
        IncomeFrequency.TWICE_MONTHLY
    )
}
