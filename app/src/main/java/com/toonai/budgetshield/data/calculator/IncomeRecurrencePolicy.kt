package com.toonai.budgetshield.data.calculator

import com.toonai.budgetshield.data.model.IncomeFrequency
import java.time.YearMonth

/** Shared validation and date-resolution rules for recurring income schedules. */
object IncomeRecurrencePolicy {

    private val supportedFrequencies = setOf(
        IncomeFrequency.WEEKLY,
        IncomeFrequency.BIWEEKLY,
        IncomeFrequency.SEMIMONTHLY,
        IncomeFrequency.TWICE_MONTHLY,
        IncomeFrequency.MONTHLY,
        IncomeFrequency.ONE_TIME
    )

    fun validateFrequency(frequency: String) {
        require(frequency in supportedFrequencies) { "Unsupported income frequency: $frequency" }
    }

    fun validateAnchors(
        frequency: String,
        anchorDayOne: Int?,
        anchorDayTwo: Int?
    ) {
        validateFrequency(frequency)
        if (!isSemimonthly(frequency)) return

        require(anchorDayOne != null && anchorDayTwo != null) {
            "Twice-monthly income requires two payday days"
        }
        require(anchorDayOne in 1..31 && anchorDayTwo in 1..31) {
            "Payday days must be between 1 and 31"
        }
        require(anchorDayOne != anchorDayTwo) {
            "Twice-monthly payday days must be different"
        }

        // A non-leap year covers every possible month length. Reject the pair up
        // front if short-month clamping could ever turn it into one calendar date.
        val collision = (1..12).any { month ->
            val yearMonth = YearMonth.of(2023, month)
            resolveDay(yearMonth, anchorDayOne) == resolveDay(yearMonth, anchorDayTwo)
        }
        require(!collision) {
            "Those payday days become the same date in a short month; choose two distinct days"
        }
    }

    fun resolveDay(yearMonth: YearMonth, anchorDay: Int) =
        yearMonth.atDay(minOf(anchorDay, yearMonth.lengthOfMonth()))

    fun isSemimonthly(frequency: String): Boolean =
        frequency == IncomeFrequency.SEMIMONTHLY || frequency == IncomeFrequency.TWICE_MONTHLY
}
