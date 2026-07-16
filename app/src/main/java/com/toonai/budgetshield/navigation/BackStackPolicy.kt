package com.toonai.budgetshield.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * Production Back-Stack Policy Functions
 * These are the authoritative rules for navigation behavior.
 * Used by MainActivity and tested directly in unit tests.
 */
object BackStackPolicy {

    /**
     * Completes the Setup Quest by replacing the entire back stack with Home.
     * After this, pressing Back from Home will exit the app (no Setup Quest to return to).
     *
     * @param backStack The NavBackStack to modify
     */
    fun completeSetup(backStack: NavBackStack<NavKey>) {
        backStack.clear()
        backStack.add(Home)
    }

    /**
     * Navigates to a destination using single-top behavior.
     * If the destination is already at the top of the stack, it is not duplicated.
     *
     * @param backStack The NavBackStack to modify
     * @param route The destination to navigate to
     */
    fun navigateSingleTop(backStack: NavBackStack<NavKey>, route: NavKey) {
        val currentTop = backStack.lastOrNull()
        if (currentTop?.javaClass != route.javaClass) {
            backStack.add(route)
        }
    }

    /**
     * Pops the back stack, returning to the previous destination.
     * Used for nested navigation (e.g., Home -> Treasure -> BillPayment, then Back).
     *
     * @param backStack The NavBackStack to modify
     * @return true if there was an entry to pop, false if stack was empty
     */
    fun popNested(backStack: NavBackStack<NavKey>): Boolean {
        return backStack.removeLastOrNull() != null
    }

    /**
     * Checks if the app can exit from the current root state.
     * This is true when the back stack has only one entry (Home) or is empty.
     *
     * @param backStack The NavBackStack to check
     * @return true if pressing Back should finish the activity
     */
    fun canExitFromRoot(backStack: NavBackStack<NavKey>): Boolean {
        return backStack.size <= 1
    }

    /**
     * Gets the current top destination, or null if stack is empty.
     */
    fun getCurrentDestination(backStack: NavBackStack<NavKey>): NavKey? {
        return backStack.lastOrNull()
    }

    /**
     * Checks if the back stack contains a specific destination type.
     */
    inline fun <reified T : NavKey> containsDestination(backStack: NavBackStack<NavKey>): Boolean {
        return backStack.any { it is T }
    }

    /**
     * Gets the size of the back stack.
     */
    fun getBackStackSize(backStack: NavBackStack<NavKey>): Int {
        return backStack.size
    }
}
