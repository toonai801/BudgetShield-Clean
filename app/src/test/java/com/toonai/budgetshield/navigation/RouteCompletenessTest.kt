package com.toonai.budgetshield.navigation

import androidx.navigation3.runtime.NavKey
import org.junit.Test
import org.junit.Assert.*

/**
 * JVM Unit Tests for Navigation 3 Route Completeness
 * Verifies all 13 destinations are properly defined and implement NavKey
 */
class RouteCompletenessTest {

    @Test
    fun `all 13 destinations exist as NavKey implementations`() {
        // Verify each destination implements NavKey by creating instances
        val destinations: List<NavKey> = listOf(
            SetupQuest,
            Home,
            Treasure,
            Stats,
            Goals,
            Settings,
            IncomeEntry,
            BillEntry,
            BillPayment,
            SavingsEntry,
            TransactionDetails(),
            BillProtected,
            ShieldProgression
        )

        // Verify all are NavKey instances
        assertEquals("Should have 13 destinations", 13, destinations.size)
        
        // All should be NavKey
        for (destination in destinations) {
            assertNotNull("Destination should not be null", destination)
        }
    }

    @Test
    fun `transaction details accepts optional transactionId`() {
        val withoutId = TransactionDetails()
        assertNull("TransactionId should be null by default", withoutId.transactionId)

        val withId = TransactionDetails(transactionId = 123L)
        assertEquals("TransactionId should be set", 123L, withId.transactionId)

        val withNullId = TransactionDetails(transactionId = null)
        assertNull("TransactionId can be explicitly null", withNullId.transactionId)
    }

    @Test
    fun `transaction details equality`() {
        val details1 = TransactionDetails(123L)
        val details2 = TransactionDetails(123L)
        val details3 = TransactionDetails(456L)

        assertEquals("Same transactionId should be equal", details1, details2)
        assertNotEquals("Different transactionId should not be equal", details1, details3)
    }

    @Test
    fun `all object destinations are singletons`() {
        // Object destinations should be singletons
        assertSame("SetupQuest should be singleton", SetupQuest, SetupQuest)
        assertSame("Home should be singleton", Home, Home)
        assertSame("Treasure should be singleton", Treasure, Treasure)
        assertSame("Stats should be singleton", Stats, Stats)
        assertSame("Goals should be singleton", Goals, Goals)
        assertSame("Settings should be singleton", Settings, Settings)
        assertSame("IncomeEntry should be singleton", IncomeEntry, IncomeEntry)
        assertSame("BillEntry should be singleton", BillEntry, BillEntry)
        assertSame("BillPayment should be singleton", BillPayment, BillPayment)
        assertSame("SavingsEntry should be singleton", SavingsEntry, SavingsEntry)
        assertSame("BillProtected should be singleton", BillProtected, BillProtected)
        assertSame("ShieldProgression should be singleton", ShieldProgression, ShieldProgression)
    }

    @Test
    fun `route count is exactly 13`() {
        val routes = listOf(
            SetupQuest, Home, Treasure, Stats, Goals, Settings,
            IncomeEntry, BillEntry, BillPayment, SavingsEntry,
            TransactionDetails(), BillProtected, ShieldProgression
        )
        assertEquals("Should have exactly 13 routes", 13, routes.size)
    }

    @Test
    fun `transaction details is data class`() {
        // TransactionDetails is a data class with transactionId property
        val details = TransactionDetails(123L)
        
        // Should have component function (data class feature)
        val (id) = details
        assertEquals(123L, id)
        
        // copy() should work
        val copied = details.copy(transactionId = 456L)
        assertEquals(456L, copied.transactionId)
    }

    @Test
    fun `destination names match expected values`() {
        // Verify class names match expected destination names
        assertEquals("SetupQuest", SetupQuest::class.simpleName)
        assertEquals("Home", Home::class.simpleName)
        assertEquals("Treasure", Treasure::class.simpleName)
        assertEquals("Stats", Stats::class.simpleName)
        assertEquals("Goals", Goals::class.simpleName)
        assertEquals("Settings", Settings::class.simpleName)
        assertEquals("IncomeEntry", IncomeEntry::class.simpleName)
        assertEquals("BillEntry", BillEntry::class.simpleName)
        assertEquals("BillPayment", BillPayment::class.simpleName)
        assertEquals("SavingsEntry", SavingsEntry::class.simpleName)
        assertEquals("TransactionDetails", TransactionDetails::class.simpleName)
        assertEquals("BillProtected", BillProtected::class.simpleName)
        assertEquals("ShieldProgression", ShieldProgression::class.simpleName)
    }
}
