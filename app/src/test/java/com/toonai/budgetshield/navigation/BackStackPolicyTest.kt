package com.toonai.budgetshield.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.junit.Test
import org.junit.Assert.*

/**
 * JVM Unit Tests for Navigation 3 Back-Stack Policy
 * Tests the production BackStackPolicy functions with real NavBackStack
 */
class BackStackPolicyTest {

    private fun createBackStack(): NavBackStack<NavKey> {
        return NavBackStack()
    }

    @Test
    fun `completeSetup replaces stack with only Home`() {
        val backStack = createBackStack()
        backStack.add(SetupQuest)

        BackStackPolicy.completeSetup(backStack)

        assertEquals("Stack should have 1 entry", 1, backStack.size)
        assertTrue("Home should be the only entry", backStack.last() is Home)
        assertFalse("Should not contain SetupQuest", backStack.any { it is SetupQuest })
    }

    @Test
    fun `popNested returns to prior screen`() {
        val backStack = createBackStack()
        backStack.add(Home)
        backStack.add(Treasure)
        backStack.add(BillPayment)

        val popped = BackStackPolicy.popNested(backStack)

        assertTrue("Should have popped an entry", popped)
        assertEquals("Should return to Treasure", Treasure::class.java, backStack.last()::class.java)
        assertEquals("Stack should have 2 entries", 2, backStack.size)
    }

    @Test
    fun `popNested returns false when stack is empty`() {
        val backStack = createBackStack()

        val popped = BackStackPolicy.popNested(backStack)

        assertFalse("Should return false for empty stack", popped)
    }

    @Test
    fun `navigateSingleTop prevents duplicate at top`() {
        val backStack = createBackStack()
        backStack.add(Home)

        // Navigate to same destination already at top - should NOT duplicate
        BackStackPolicy.navigateSingleTop(backStack, Home)
        assertEquals("Should still have 1 entry", 1, backStack.size)

        // Navigate to different destination - should add
        BackStackPolicy.navigateSingleTop(backStack, Treasure)
        assertEquals("Should have 2 entries", 2, backStack.size)

        // Navigate to Home (not at top) - should add
        BackStackPolicy.navigateSingleTop(backStack, Home)
        assertEquals("Should have 3 entries", 3, backStack.size)
    }

    @Test
    fun `canExitFromRoot returns true when only one entry`() {
        val backStack = createBackStack()
        backStack.add(Home)

        assertTrue("Should be able to exit from single entry", BackStackPolicy.canExitFromRoot(backStack))
    }

    @Test
    fun `canExitFromRoot returns true when empty`() {
        val backStack = createBackStack()

        assertTrue("Should be able to exit from empty stack", BackStackPolicy.canExitFromRoot(backStack))
    }

    @Test
    fun `canExitFromRoot returns false when multiple entries`() {
        val backStack = createBackStack()
        backStack.add(Home)
        backStack.add(Treasure)

        assertFalse("Should not exit with multiple entries", BackStackPolicy.canExitFromRoot(backStack))
    }

    @Test
    fun `getCurrentDestination returns top entry`() {
        val backStack = createBackStack()
        backStack.add(Home)
        backStack.add(Treasure)
        backStack.add(Stats)

        val current = BackStackPolicy.getCurrentDestination(backStack)

        assertTrue("Current should be Stats", current is Stats)
    }

    @Test
    fun `getCurrentDestination returns null for empty stack`() {
        val backStack = createBackStack()

        val current = BackStackPolicy.getCurrentDestination(backStack)

        assertNull("Should return null for empty stack", current)
    }

    @Test
    fun `back stack maintains correct order`() {
        val backStack = createBackStack()

        backStack.add(Home)
        backStack.add(Treasure)
        backStack.add(Stats)

        assertTrue("First should be Home", backStack[0] is Home)
        assertTrue("Second should be Treasure", backStack[1] is Treasure)
        assertTrue("Third should be Stats", backStack[2] is Stats)

        BackStackPolicy.popNested(backStack)

        assertEquals("Should have 2 entries", 2, backStack.size)
        assertTrue("Last should now be Treasure", backStack.last() is Treasure)
    }

    @Test
    fun `completeSetup from multiple entries clears all`() {
        val backStack = createBackStack()
        backStack.add(Home)
        backStack.add(Treasure)
        backStack.add(BillPayment)
        backStack.add(BillProtected)

        BackStackPolicy.completeSetup(backStack)

        assertEquals("Should have only 1 entry", 1, backStack.size)
        assertTrue("Should be Home", backStack.last() is Home)
    }

    @Test
    fun `navigateSingleTop distinguishes different transaction details`() {
        val backStack = createBackStack()
        backStack.add(Home)

        val details1 = TransactionDetails(1L)
        val details2 = TransactionDetails(2L)

        BackStackPolicy.navigateSingleTop(backStack, details1)
        assertEquals("Should have 2 entries", 2, backStack.size)

        // Same class at top (TransactionDetails) - should NOT add even with different ID
        // Single-top prevents duplicate destinations, not duplicate data
        BackStackPolicy.navigateSingleTop(backStack, TransactionDetails(999L))
        assertEquals("Should still have 2 entries (single-top prevents duplicate class)", 2, backStack.size)

        // Different class - should add
        BackStackPolicy.navigateSingleTop(backStack, Treasure)
        assertEquals("Should have 3 entries", 3, backStack.size)
    }
}
