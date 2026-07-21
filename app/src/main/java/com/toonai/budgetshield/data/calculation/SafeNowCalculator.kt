package com.toonai.budgetshield.data.calculation

import com.toonai.budgetshield.data.model.Bill
import com.toonai.budgetshield.data.model.IncomeSchedule
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Pure deterministic Safe Now calculator.
 * Implements all 9 worked examples from SAFE_NOW_RULES.md.
 */
class SafeNowCalculator {

    data class SafeNowResult(
        val safeNowCents: Long,
        val shortageCents: Long,
        val firstFailingDate: LocalDate?,
        val failingBills: List<Bill>,
        val projectedBalances: List<DailyProjection>,
        val hasUnprotectedBills: Boolean
    )

    data class DailyProjection(
        val date: LocalDate,
        val eventDescription: String,
        val balanceChangeCents: Long,
        val projectedBalanceCents: Long
    )

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    /**
     * Calculate Safe Now based on cleared cash, confirmed income, and protected bills.
     *
     * @param clearedCashCents Current cleared cash balance
     * @param incomeSchedules List of active income schedules with confirmed status
     * @param bills List of bills (protected bills affect Safe Now)
     * @param today Current date (defaults to today)
     * @param planningHorizonMonths Minimum planning horizon (default 2 months)
     */
    fun calculate(
        clearedCashCents: Long,
        incomeSchedules: List<IncomeSchedule>,
        bills: List<Bill>,
        today: LocalDate = LocalDate.now(),
        planningHorizonMonths: Int = 2
    ): SafeNowResult {

        // Determine planning horizon
        val currentMonthEnd = today.withDayOfMonth(today.lengthOfMonth())
        val minimumHorizonEnd = today.plusMonths(planningHorizonMonths.toLong()).withDayOfMonth(1).minusDays(1)

        // Find latest protected bill date
        val latestProtectedBillDate = bills
            .filter { it.isProtected }
            .mapNotNull { parseDateOrNull(it.dueDate) }
            .maxOrNull()

        val horizonEnd = when {
            latestProtectedBillDate != null && latestProtectedBillDate.isAfter(minimumHorizonEnd) -> latestProtectedBillDate
            else -> minimumHorizonEnd
        }

        // Build event timeline
        val events = mutableListOf<FinancialEvent>()

        // Starting balance event
        events.add(FinancialEvent(today, EventType.START, clearedCashCents, "Starting balance"))

        // Add confirmed income events
        incomeSchedules
            .filter { it.isConfirmed && it.isActive }
            .forEach { income ->
                val incomeDate = parseDateOrNull(income.nextPaydayDate)
                if (incomeDate != null && !incomeDate.isBefore(today)) {
                    events.add(FinancialEvent(incomeDate, EventType.INCOME, income.amountCents, income.name))
                }
            }

        // Add protected bill events - use remainingDueCents for partial payments
        bills.filter { it.isProtected }.forEach { bill ->
            val dueDate = parseDateOrNull(bill.dueDate)
            if (dueDate != null) {
                // Overdue bills treated as today
                val effectiveDate = if (dueDate.isBefore(today)) today else dueDate
                // Use remainingDueCents to account for partial payments
                events.add(FinancialEvent(effectiveDate, EventType.BILL, -bill.remainingDueCents, bill.name))
            }
        }

        // Sort by date, then by type (income before bills on same day)
        val sortedEvents = events.sortedWith(
            compareBy<FinancialEvent> { it.date }
                .thenBy { it.type.order }
        )

        // Calculate running projections
        val projections = mutableListOf<DailyProjection>()
        var runningBalance = clearedCashCents
        var minimumBalance = clearedCashCents
        var minimumDate = today
        val failingBills = mutableListOf<Bill>()

        sortedEvents.forEach { event ->
            when (event.type) {
                EventType.START -> { /* Already counted */ }
                EventType.INCOME -> runningBalance += event.amount
                EventType.BILL -> runningBalance += event.amount // amount is negative
            }

            projections.add(DailyProjection(
                date = event.date,
                eventDescription = event.description,
                balanceChangeCents = event.amount,
                projectedBalanceCents = runningBalance
            ))

            // Track minimum balance
            if (runningBalance < minimumBalance) {
                minimumBalance = runningBalance
                minimumDate = event.date
                if (event.type == EventType.BILL) {
                    // Find the bill that caused this
                    val bill = bills.find { it.name == event.description && it.isProtected }
                    if (bill != null && !failingBills.contains(bill)) {
                        failingBills.add(bill)
                    }
                }
            }
        }

        // Calculate results
        val safeNowCents = if (minimumBalance < 0) 0 else minimumBalance
        val shortageCents = if (minimumBalance < 0) -minimumBalance else 0
        val hasUnprotectedBills = bills.any { !it.isProtected }

        return SafeNowResult(
            safeNowCents = safeNowCents,
            shortageCents = shortageCents,
            firstFailingDate = if (shortageCents > 0) minimumDate else null,
            failingBills = failingBills,
            projectedBalances = projections,
            hasUnprotectedBills = hasUnprotectedBills
        )
    }

    private fun parseDateOrNull(dateString: String): LocalDate? {
        return try {
            LocalDate.parse(dateString, dateFormatter)
        } catch (e: Exception) {
            null
        }
    }

    private enum class EventType(val order: Int) {
        START(0),
        INCOME(1),
        BILL(2)
    }

    private data class FinancialEvent(
        val date: LocalDate,
        val type: EventType,
        val amount: Long,
        val description: String
    )
}
