package com.toonai.budgetshield.util

import com.toonai.budgetshield.data.calculator.DatedAmount
import com.toonai.budgetshield.data.calculator.SafeNowResult

/**
 * Compatibility wrapper for SafeNowCalculator.
 * Delegates to the implementation in data.calculator package.
 */
object SafeNowCalculator {
    /**
     * Dated amount for income or bills.
     * Re-exported for backward compatibility.
     */
    data class DatedAmount(
        val name: String,
        val amountCents: Long,
        val date: String
    )

    /**
     * Calculate Safe Now using the new implementation.
     */
    fun calculate(
        cashOnHandCents: Long,
        confirmedIncome: List<DatedAmount>,
        protectedBills: List<DatedAmount>
    ): SafeNowResult {
        val convertedIncome = confirmedIncome.map {
            com.toonai.budgetshield.data.calculator.DatedAmount(it.name, it.amountCents, it.date)
        }
        val convertedBills = protectedBills.map {
            com.toonai.budgetshield.data.calculator.DatedAmount(it.name, it.amountCents, it.date)
        }

        return com.toonai.budgetshield.data.calculator.SafeNowCalculator.calculate(
            cashOnHandCents = cashOnHandCents,
            confirmedIncome = convertedIncome,
            protectedBills = convertedBills
        )
    }
}