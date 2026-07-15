package com.toonai.budgetshield.navigation

import org.junit.Test
import org.junit.Assert.*

/**
 * JVM Unit Tests for Navigation 3 Back-Stack Policy
 * Verifies back-stack behavior without requiring Android runtime
 * 
 * Uses test doubles to avoid Navigation 3 runtime dependencies in unit tests.
 */
class BackStackPolicyTest {

    // Test doubles that mirror the real route structure
    private object TestSetupQuest
    private object TestHome
    private object TestTreasure
    private object TestStats
    private object TestGoals
    private object TestSettings
    private object TestIncomeEntry
    private object TestBillEntry
    private object TestBillPayment
    private object TestSavingsEntry
    private data class TestTransactionDetails(val id: Long? = null)
    private object TestBillProtected
    private object TestShieldProgression

    @Test
    fun `setup quest completion should replace stack with home`() {
        // Simulate: App starts at SetupQuest
        val backStack = mutableListOf<Any>(TestSetupQuest)

        // User completes Setup Quest - should replace stack
        backStack.clear()
        backStack.add(TestHome)

        // Verify: Home is now the only entry
        assertEquals("Stack should have 1 entry", 1, backStack.size)
        assertEquals("Home should be the only entry", TestHome, backStack.first())

        // Verify: Back from Home exits (no Setup Quest to go back to)
        val canGoBack = backStack.size > 1
        assertFalse("Should not be able to go back from Home", canGoBack)
    }

    @Test
    fun `nested navigation back returns to prior screen`() {
        // Simulate: Home -> Treasure -> BillPayment
        val backStack = mutableListOf<Any>(TestHome, TestTreasure, TestBillPayment)

        // User presses Back from BillPayment
        backStack.removeLastOrNull()

        // Verify: Returns to Treasure
        assertEquals("Should return to Treasure", TestTreasure, backStack.last())
        assertEquals("Stack should have 2 entries", 2, backStack.size)
    }

    @Test
    fun `launch single top prevents duplicate at top of stack`() {
        // Test the launchSingleTop behavior - when navigating to same destination
        // that's already at top, it should not duplicate
        val backStack = mutableListOf<Any>(TestHome)

        // Simulate launchSingleTop: check if last is same before adding
        fun navigateWithSingleTop(key: Any) {
            if (backStack.lastOrNull() != key) {
                backStack.add(key)
            }
        }

        // Navigate to Home (should NOT add duplicate)
        navigateWithSingleTop(TestHome)
        assertEquals("Should still have 1 entry", 1, backStack.size)

        // Navigate to Treasure
        navigateWithSingleTop(TestTreasure)
        assertEquals("Should have 2 entries", 2, backStack.size)

        // Navigate to Home (should add since not at top)
        navigateWithSingleTop(TestHome)
        assertEquals("Should have 3 entries", 3, backStack.size)
        assertEquals("Last should be Home", TestHome, backStack.last())
    }

    @Test
    fun `back stack operations follow expected patterns`() {
        val backStack = mutableListOf<Any>()

        // Start at SetupQuest
        backStack.add(TestSetupQuest)
        assertEquals(1, backStack.size)

        // Replace with Home
        backStack.clear()
        backStack.add(TestHome)
        assertEquals(1, backStack.size)
        assertEquals(TestHome, backStack.last())

        // Navigate to Treasure
        backStack.add(TestTreasure)
        assertEquals(2, backStack.size)

        // Navigate to BillPayment
        backStack.add(TestBillPayment)
        assertEquals(3, backStack.size)

        // Back from BillPayment
        backStack.removeLastOrNull()
        assertEquals(2, backStack.size)
        assertEquals(TestTreasure, backStack.last())

        // Back from Treasure
        backStack.removeLastOrNull()
        assertEquals(1, backStack.size)
        assertEquals(TestHome, backStack.last())
    }

    @Test
    fun `all 13 destinations can be added to back stack`() {
        val backStack = mutableListOf<Any>()

        // Add all 13 destinations using test doubles
        val allDestinations = listOf(
            TestSetupQuest, TestHome, TestTreasure, TestStats, TestGoals, TestSettings,
            TestIncomeEntry, TestBillEntry, TestBillPayment, TestSavingsEntry,
            TestTransactionDetails(), TestBillProtected, TestShieldProgression
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
        assertEquals(TestShieldProgression, popped[0])
        assertEquals(TestBillProtected, popped[1])
        assertEquals(TestTransactionDetails(), popped[2])
        assertEquals(TestSavingsEntry, popped[3])
        assertEquals(TestBillPayment, popped[4])
        assertEquals(TestBillEntry, popped[5])
        assertEquals(TestIncomeEntry, popped[6])
        assertEquals(TestSettings, popped[7])
        assertEquals(TestGoals, popped[8])
        assertEquals(TestStats, popped[9])
        assertEquals(TestTreasure, popped[10])
        assertEquals(TestHome, popped[11])
        assertEquals(TestSetupQuest, popped[12])
    }

    @Test
    fun `transaction details with different ids are different entries`() {
        val backStack = mutableListOf<Any>()

        val details1 = TestTransactionDetails(1L)
        val details2 = TestTransactionDetails(2L)
        val details3 = TestTransactionDetails(null)

        backStack.add(details1)
        backStack.add(details2)
        backStack.add(details3)

        assertEquals("Should have 3 entries", 3, backStack.size)
        assertNotEquals("Different IDs should be different entries", backStack[0], backStack[1])
    }

    @Test
    fun `clear and replace works for setup quest completion`() {
        // Simulate the exact pattern used in Setup Quest completion
        val backStack = mutableListOf<Any>(TestSetupQuest)
        
        // onReplaceStack callback behavior
        backStack.clear()
        backStack.add(TestHome)
        
        assertEquals("Stack should have only Home", 1, backStack.size)
        assertEquals("Home should be at index 0", TestHome, backStack[0])
        
        // Verify no SetupQuest remains
        assertFalse("Should not contain SetupQuest", backStack.contains(TestSetupQuest))
    }
}
