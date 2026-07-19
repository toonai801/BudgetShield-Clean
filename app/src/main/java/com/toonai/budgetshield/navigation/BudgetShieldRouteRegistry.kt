package com.toonai.budgetshield.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Production Route Registry - Single Source of Truth
 * Contains all 14 approved production destinations.
 * Used by both the production app and tests.
 */
object BudgetShieldRouteRegistry {

    /**
     * All 14 production destinations in approved order.
     * This is the authoritative list. If a route is missing from here,
     * it is not part of the production app.
     */
    val allDestinations: List<NavKey> = listOf(
        SetupQuest,
        Home,
        Treasure,
        Bills,
        Stats,
        Goals,
        Settings,
        IncomeEntry,
        BillEntry,
        BillPayment,
        SavingsEntry,
        TransactionDetails(), // Default instance for registry listing
        BillProtected,
        ShieldProgression
    )

    /**
     * Count of production destinations.
     */
    const val DESTINATION_COUNT: Int = 14

    /**
     * Check if a given key is a valid production destination.
     */
    fun isValidDestination(key: NavKey): Boolean {
        return when (key) {
            is SetupQuest,
            is Home,
            is Treasure,
            is Bills,
            is Stats,
            is Goals,
            is Settings,
            is IncomeEntry,
            is BillEntry,
            is BillPayment,
            is SavingsEntry,
            is TransactionDetails,
            is BillProtected,
            is ShieldProgression -> true
            else -> false
        }
    }

    /**
     * Get the index of a destination in the registry (for ordering/reference).
     * Returns -1 if not found.
     */
    fun getDestinationIndex(key: NavKey): Int {
        return allDestinations.indexOfFirst { it::class == key::class }
    }
}
