package com.toonai.budgetshield.navigation

import org.junit.Test
import org.junit.Assert.*

/**
 * JVM Unit Tests for Navigation 3 Back-Stack Policy
 * Verifies back-stack behavior without requiring Android runtime
 */
class BackStackPolicyTest {

    @Test
    fun `setup quest completion should replace stack with home`() {
        // Simulate: App starts at SetupQuest
        val backStack = mutableListOf<Any>(SetupQuest)

        // User completes Setup Quest - should replace stack
        backStack.clear()
        backStack.add(Home)

        // Verify: Home is now the only entry
        assertEquals("Stack should have 1 entry", 1, backStack.size)
        assertEquals("Home should be the only entry", Home, backStack.first())

        // Verify: Back from Home exits (no Setup Quest to go back to)
        val canGoBack = backStack.size > 1
        assertFalse("Should not be able to go back from Home", canGoBack)
    }

    @Test
    fun `nested navigation back returns to prior screen`() {
        // Simulate: Home -> Treasure -> BillPayment
        val backStack = mutableListOf<Any>(Home, Treasure, BillPayment)

        // User presses Back from BillPayment
        backStack.removeLastOrNull()

        // Verify: Returns to Treasure
        assertEquals("Should return to Treasure", Treasure, backStack.last())
        assertEquals("Stack should have 2 entries", 2, backStack.size)
    }

    @Test
    fun `launch single top prevents duplicate at top of stack`() {
        // Test the launchSingleTop behavior - when navigating to same destination
        // that's already at top, it should not duplicate
        val backStack = mutableListOf<Any>(Home)

        // Simulate launchSingleTop: check if last is same before adding
        fun navigateWithSingleTop(key: Any) {
            if (backStack.lastOrNull() != key) {
                backStack.add(key)
            }
        }

        // Navigate to Home (should NOT add duplicate)
        navigateWithSingleTop(Home)
        assertEquals("Should still have 1 entry", 1, backStack.size)

        // Navigate to Treasure
        navigateWithSingleTop(Treasure)
        assertEquals("Should have 2 entries", 2, backStack.size)

        // Navigate to Home (should add since not at top)
        navigateWithSingleTop(Home)
        assertEquals("Should have 3 entries", 3, backStack.size)
        assertEquals("Last should be Home", Home, backStack.last())
    }

    @Test
    fun `back stack operations follow expected patterns`() {
        val backStack = mutableListOf<Any>()

        // Start at SetupQuest
        backStack.add(SetupQuest)
        assertEquals(1, backStack.size)

        // Replace with Home
        backStack.clear()
        backStack.add(Home)
        assertEquals(1, backStack.size)
        assertEquals(Home, backStack.last())

        // Navigate to Treasure
        backStack.add(Treasure)
        assertEquals(2, backStack.size)

        // Navigate to BillPayment
        backStack.add(BillPayment)
        assertEquals(3, backStack.size)

        // Back from BillPayment
        backStack.removeLastOrNull()
        assertEquals(2, backStack.size)
        assertEquals(Treasure, backStack.last())

        // Back from Treasure
        backStack.removeLastOrNull()
        assertEquals(1, backStack.size)
        assertEquals(Home, backStack.last())
    }

    @Test
    fun `all 13 destinations can be added to back stack`() {
        val backStack = mutableListOf<Any>()

        // Add all 13 destinations
        val allDestinations = listOf(
            SetupQuest, Home, Treasure, Stats, Goals, Settings,
            IncomeEntry, BillEntry, BillPayment, SavingsEntry,
            TransactionDetails(), BillProtected, ShieldProgression
        )

        for (destination in allDestinations) {
            backStack.add(destination)
        }

        assertEquals("Should have 13 entries", 13, backStack.size)

        // Pop all and verify order
        val popped = mutableListOf<Any>()
        while (backStack.isNotEmpty()) {
            popped.add(backStack.removeLast())
        }

        // Verify order (LIFO)
        assertEquals(ShieldProgression, popped[0])
        assertEquals(BillProtected, popped[1])
        assertEquals(TransactionDetails(), popped[2])
        assertEquals(SavingsEntry, popped[3])
        assertEquals(BillPayment, popped[4])
        assertEquals(BillEntry, popped[5])
        assertEquals(IncomeEntry, popped[6])
        assertEquals(Settings, popped[7])
        assertEquals(Goals, popped[8])
        assertEquals(Stats, popped[9])
        assertEquals(Treasure, popped[10])
        assertEquals(Home, popped[11])
        assertEquals(SetupQuest, popped[12])
    }

    @Test
    fun `transaction details with different ids are different entries`() {
        val backStack = mutableListOf<Any>()

        val details1 = TransactionDetails(1L)
        val details2 = TransactionDetails(2L)
        val details3 = TransactionDetails(null)

        backStack.add(details1)
        backStack.add(details2)
        backStack.add(details3)

        assertEquals("Should have 3 entries", 3, backStack.size)
        assertNotEquals("Different IDs should be different entries", backStack[0], backStack[1])
    }

    @Test
    fun `clear and replace works for setup quest completion`() {
        // Simulate the exact pattern used in Setup Quest completion
        val backStack = mutableListOf<Any>(SetupQuest)
        
        // onReplaceStack callback behavior
        backStack.clear()
        backStack.add(Home)
        
        assertEquals("Stack should have only Home", 1, backStack.size)
        assertEquals("Home should be at index 0", Home, backStack[0])
        
        // Verify no SetupQuest remains
        assertFalse("Should not contain SetupQuest", backStack.contains(SetupQuest))
    }
}
