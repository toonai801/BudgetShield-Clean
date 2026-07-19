package com.toonai.budgetshield.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Bill entity representing a recurring or one-time bill obligation.
 * Stored in Room database for persistence.
 */
@Entity(tableName = "bills")
@Serializable
data class Bill(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    
    /** User-visible name (e.g., "Rent", "Internet") */
    val name: String,
    
    /** Icon emoji for visual identification */
    val icon: String,
    
    /** Amount due in cents (e.g., $950.00 = 95000) */
    val amountCents: Long,
    
    /** Amount already paid in cents */
    val paidAmountCents: Long = 0L,
    
    /** Due date as ISO-8601 string (YYYY-MM-DD) */
    val dueDate: String,
    
    /** Whether this bill is protected (money set aside) */
    val isProtected: Boolean = false,
    
    /** Whether this bill is fully paid */
    val isPaid: Boolean = false,
    
    /** When this record was created */
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Remaining amount due in cents
     */
    val remainingDueCents: Long
        get() = maxOf(0L, amountCents - paidAmountCents)
    
    /**
     * Formatted amount for display (e.g., "$950.00")
     */
    val formattedAmount: String
        get() = formatCents(amountCents)
    
    /**
     * Formatted remaining due for display
     */
    val formattedRemainingDue: String
        get() = formatCents(remainingDueCents)
    
    /**
     * Progress percentage (0-100) of payment
     */
    val paymentProgress: Int
        get() = if (amountCents > 0) {
            ((paidAmountCents * 100) / amountCents).toInt()
        } else 0
    
    companion object {
        fun formatCents(cents: Long): String {
            val dollars = cents / 100
            val remainder = cents % 100
            return String.format("$%d.%02d", dollars, kotlin.math.abs(remainder))
        }
    }
}
