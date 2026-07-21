package com.toonai.budgetshield.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Budget category entity for month-scoped budget tracking.
 * Supports Food, Wants, and other variable spending categories.
 * MonthKey format: YYYY-MM
 */
@Entity(
    tableName = "budget_categories",
    indices = [
        Index(value = ["name", "monthKey"], unique = true)
    ]
)
data class BudgetCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /** Category name (e.g., "Food", "Wants") */
    val name: String,

    /** Month key in YYYY-MM format */
    val monthKey: String,

    /** Budget amount allocated for this category in cents */
    val plannedAmountCents: Long = 0L,

    /** Amount spent in this category in cents */
    val spentAmountCents: Long = 0L,

    /** Category type for grouping (food, wants, other) */
    val categoryType: String = "",

    /** Whether this category is active/enabled */
    val isActive: Boolean = true,

    /** Icon emoji for visual identification */
    val icon: String = "💰",

    /** When this record was created */
    val createdAt: Long = System.currentTimeMillis(),

    /** When this record was last updated */
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Remaining budget in cents
     */
    val remainingCents: Long
        get() = kotlin.math.max(0L, plannedAmountCents - spentAmountCents)

    /**
     * Budget utilization percentage (0-100+)
     */
    val utilizationPercent: Int
        get() = if (plannedAmountCents > 0) {
            ((spentAmountCents * 100) / plannedAmountCents).toInt()
        } else 0

    /**
     * Whether the budget has been exceeded
     */
    val isOverBudget: Boolean
        get() = spentAmountCents > plannedAmountCents
}

/**
 * Standard budget category types.
 */
object BudgetCategoryType {
    const val FOOD = "food"
    const val WANTS = "wants"
    const val OTHER = "other"
}
