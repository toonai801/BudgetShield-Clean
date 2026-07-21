package com.toonai.budgetshield.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Current savings balance.
 * Single-row table with ID = 1.
 */
@Entity(tableName = "savings_balance")
data class SavingsBalance(
    @PrimaryKey
    val id: Long = 1,
    val balanceCents: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
