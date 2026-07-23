package com.toonai.budgetshield.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Transaction entity representing any financial movement.
 * Includes income, bill payments, savings transfers, and spending.
 */
@Entity(tableName = "transactions")
@Serializable
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /** Transaction type (income, bill_payment, savings, spending) */
    val type: String,

    /** Display title/name */
    val title: String,

    /** Optional description/note */
    val description: String? = null,

    /** Amount in cents (positive for income, negative for expenses) */
    val amountCents: Long,

    /** Category for grouping (Food, Housing, Wants, Bills, etc.) */
    val category: String,

    /** Icon emoji */
    val icon: String = "💰",

    /** Related bill ID if this is a bill payment */
    val relatedBillId: Long? = null,

    /** Related income ID if this is income */
    val relatedIncomeId: Long? = null,

    /** Whether this transaction contributes to XP */
    val earnsXp: Boolean = true,

    /** XP earned from this transaction */
    val xpEarned: Int = 0,

    /** Whether transaction is protected (shielded) */
    val isProtected: Boolean = false,

    /** Transaction date as ISO-8601 string (YYYY-MM-DD) */
    val transactionDate: String,

    /** Timestamp when created */
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Formatted amount for display (e.g., "-$95.00" or "+$2,400.00")
     */
    val formattedAmount: String
        get() = formatCentsSigned(amountCents)

    /**
     * Whether this is an income transaction
     */
    val isIncome: Boolean
        get() = amountCents > 0

    /**
     * Whether this is an expense transaction
     */
    val isExpense: Boolean
        get() = amountCents < 0

    companion object {
        const val TYPE_INCOME = "income"
        const val TYPE_BILL_PAYMENT = "bill_payment"
        const val TYPE_SAVINGS = "savings"
        const val TYPE_SPENDING = "spending"
        const val TYPE_REFUND = "refund"

        fun formatCentsSigned(cents: Long): String {
            val dollars = cents / 100
            val remainder = kotlin.math.abs(cents % 100)
            val sign = if (cents < 0) "-" else "+"
            return String.format("$%s%d.%02d", sign, kotlin.math.abs(dollars), remainder)
        }
    }
}

/**
 * Transaction type categories for grouping
 */
object TransactionCategories {
    const val INCOME = "Income"
    const val HOUSING = "Housing"
    const val FOOD = "Food"
    const val WANTS = "Wants"
    const val BILLS = "Bills"
    const val SAVINGS = "Savings"
    const val TRANSPORT = "Transport"
    const val UTILITIES = "Utilities"
    const val OTHER = "Other"
}
