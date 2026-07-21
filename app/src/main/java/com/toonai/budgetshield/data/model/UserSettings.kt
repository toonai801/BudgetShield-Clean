package com.toonai.budgetshield.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User settings and first-run completion status.
 * Single-row table with ID = 1.
 */
@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Long = 1,
    val isFirstRunComplete: Boolean = false,
    val currency: String = "USD",
    val timezone: String = "America/Phoenix",
    val notificationsEnabled: Boolean = true,
    val dailyReminderTime: String? = null,
    val billReminderDaysBefore: Int = 3,
    val planningHorizonMonths: Int = 2,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
