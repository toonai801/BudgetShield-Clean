package com.toonai.budgetshield.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User settings and first-run completion status.
 * Single-row table with id = 1.
 */
@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Long = 1L,
    
    /** Whether the user has completed the setup quest */
    val isFirstRunComplete: Boolean = false,
    
    /** Currency code (USD, EUR, etc.) */
    val currency: String = "USD",
    
    /** IANA timezone ID */
    val timezone: String = "America/Phoenix",
    
    /** Current cleared cash on hand in cents */
    val cashOnHandCents: Long = 0L,
    
    /** Current savings balance in cents */
    val savingsBalanceCents: Long = 0L,
    
    /** Selected/displayed month (YYYY-MM format) */
    val selectedMonth: String = "",
    
    /** Current setup chapter (0 = not started, 1-6 = chapter, 7 = complete) */
    val setupChapter: Int = 0,
    
    /** Timestamp when created */
    val createdAt: Long = System.currentTimeMillis(),
    
    /** Timestamp when last updated */
    val updatedAt: Long = System.currentTimeMillis()
)
