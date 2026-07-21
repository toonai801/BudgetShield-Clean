package com.toonai.budgetshield.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Month-specific budget categories (Food, Wants).
 * MonthKey format: YYYY-MM
 */
@Entity(
    tableName = "budget_categories",
    indices = [Index(value = ["name", "monthKey"], unique = true)]
)
data class BudgetCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String, // "Food", "Wants"
    val monthKey: String, // YYYY-MM
    val plannedAmountCents: Long,
    val spentAmountCents: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
