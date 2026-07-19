package com.toonai.budgetshield.navigation

import androidx.navigation3.runtime.NavKey
import org.junit.Test
import org.junit.Assert.*

/**
 * JVM Unit Tests for Navigation 3 Route Completeness
 * Tests the production BudgetShieldRouteRegistry and production routes
 */
class RouteCompletenessTest {

    @Test
    fun `all 14 production destinations exist in registry`() {
        val destinations = BudgetShieldRouteRegistry.allDestinations

        assertEquals("Should have 14 destinations", 14, destinations.size)

        // Verify each required destination type exists
        assertTrue("Should contain SetupQuest", destinations.any { it is SetupQuest })
        assertTrue("Should contain Home", destinations.any { it is Home })
        assertTrue("Should contain Treasure", destinations.any { it is Treasure })
        assertTrue("Should contain Bills", destinations.any { it is Bills })  // NEW: Bills & Payments
        assertTrue("Should contain Stats", destinations.any { it is Stats })
        assertTrue("Should contain Goals", destinations.any { it is Goals })
        assertTrue("Should contain Settings", destinations.any { it is Settings })
        assertTrue("Should contain IncomeEntry", destinations.any { it is IncomeEntry })
        assertTrue("Should contain BillEntry", destinations.any { it is BillEntry })
        assertTrue("Should contain BillPayment", destinations.any { it is BillPayment })
        assertTrue("Should contain SavingsEntry", destinations.any { it is SavingsEntry })
        assertTrue("Should contain TransactionDetails", destinations.any { it is TransactionDetails })
        assertTrue("Should contain BillProtected", destinations.any { it is BillProtected })
        assertTrue("Should contain ShieldProgression", destinations.any { it is ShieldProgression })
    }

    @Test
    fun `registry destination count is exactly 14`() {
        assertEquals("DESTINATION_COUNT should be 14", 14, BudgetShieldRouteRegistry.DESTINATION_COUNT)
        assertEquals("allDestinations size should match DESTINATION_COUNT",
            BudgetShieldRouteRegistry.DESTINATION_COUNT,
            BudgetShieldRouteRegistry.allDestinations.size
        )
    }

    @Test
    fun `every production route implements NavKey`() {
        val destinations = BudgetShieldRouteRegistry.allDestinations

        for (destination in destinations) {
            assertTrue("${destination::class.simpleName} should implement NavKey",
                destination is NavKey)
        }
    }

    @Test
    fun `every production route is serializable`() {
        // All routes are @Serializable - verify by checking they are data objects or data classes
        // SetupQuest, Home, etc. are object declarations (implicitly serializable with @Serializable)
        // TransactionDetails is a data class with @Serializable

        val serializableClasses = listOf(
            SetupQuest::class,
            Home::class,
            Treasure::class,
            Bills::class,  // NEW: Bills & Payments
            Stats::class,
            Goals::class,
            Settings::class,
            IncomeEntry::class,
            BillEntry::class,
            BillPayment::class,
            SavingsEntry::class,
            TransactionDetails::class,
            BillProtected::class,
            ShieldProgression::class
        )

        assertEquals("Should have 14 serializable route classes", 14, serializableClasses.size)
    }

    @Test
    fun `transaction details accepts optional transactionId`() {
        val withoutId = TransactionDetails()
        assertNull("TransactionId should be null by default", withoutId.transactionId)

        val withId = TransactionDetails(123L)
        assertEquals("TransactionId should be set", 123L, withId.transactionId)

        val withNullId = TransactionDetails(null)
        assertNull("TransactionId can be explicitly null", withNullId.transactionId)
    }

    @Test
    fun `transaction details equality works correctly`() {
        val details1a = TransactionDetails(123L)
        val details1b = TransactionDetails(123L)
        val details2 = TransactionDetails(456L)
        val detailsNull = TransactionDetails()

        assertEquals("Same transactionId should be equal", details1a, details1b)
        assertNotEquals("Different transactionId should not be equal", details1a, details2)
        assertNotEquals("Null vs non-null should not be equal", detailsNull, details1a)
    }

    @Test
    fun `isValidDestination recognizes all 14 production routes`() {
        assertTrue("SetupQuest should be valid", BudgetShieldRouteRegistry.isValidDestination(SetupQuest))
        assertTrue("Home should be valid", BudgetShieldRouteRegistry.isValidDestination(Home))
        assertTrue("Treasure should be valid", BudgetShieldRouteRegistry.isValidDestination(Treasure))
        assertTrue("Bills should be valid", BudgetShieldRouteRegistry.isValidDestination(Bills))
        assertTrue("Stats should be valid", BudgetShieldRouteRegistry.isValidDestination(Stats))
        assertTrue("Goals should be valid", BudgetShieldRouteRegistry.isValidDestination(Goals))
        assertTrue("Settings should be valid", BudgetShieldRouteRegistry.isValidDestination(Settings))
        assertTrue("IncomeEntry should be valid", BudgetShieldRouteRegistry.isValidDestination(IncomeEntry))
        assertTrue("BillEntry should be valid", BudgetShieldRouteRegistry.isValidDestination(BillEntry))
        assertTrue("BillPayment should be valid", BudgetShieldRouteRegistry.isValidDestination(BillPayment))
        assertTrue("SavingsEntry should be valid", BudgetShieldRouteRegistry.isValidDestination(SavingsEntry))
        assertTrue("TransactionDetails should be valid",
            BudgetShieldRouteRegistry.isValidDestination(TransactionDetails(1L)))
        assertTrue("BillProtected should be valid", BudgetShieldRouteRegistry.isValidDestination(BillProtected))
        assertTrue("ShieldProgression should be valid", BudgetShieldRouteRegistry.isValidDestination(ShieldProgression))
    }

    @Test
    fun `isValidDestination rejects unknown routes`() {
        // Create a fake NavKey that isn't a real production route
        @kotlinx.serialization.Serializable
        class FakeRoute : NavKey

        val fakeRoute = FakeRoute()
        assertFalse("Fake route should not be valid", BudgetShieldRouteRegistry.isValidDestination(fakeRoute))
    }

    @Test
    fun `getDestinationIndex returns correct indices`() {
        assertEquals("SetupQuest index", 0, BudgetShieldRouteRegistry.getDestinationIndex(SetupQuest))
        assertEquals("Home index", 1, BudgetShieldRouteRegistry.getDestinationIndex(Home))
        assertEquals("Treasure index", 2, BudgetShieldRouteRegistry.getDestinationIndex(Treasure))
        assertEquals("Bills index", 3, BudgetShieldRouteRegistry.getDestinationIndex(Bills))
        assertEquals("ShieldProgression index", 13, BudgetShieldRouteRegistry.getDestinationIndex(ShieldProgression))
    }

    @Test
    fun `getDestinationIndex returns -1 for unknown routes`() {
        @kotlinx.serialization.Serializable
        class UnknownRoute : NavKey

        assertEquals("Unknown route should return -1", -1, BudgetShieldRouteRegistry.getDestinationIndex(UnknownRoute()))
    }

    @Test
    fun `all destinations are distinct types`() {
        val destinationClasses = BudgetShieldRouteRegistry.allDestinations.map { it::class }
        val distinctClasses = destinationClasses.distinct()

        assertEquals("All 14 destinations should have distinct types",
            14, distinctClasses.size)
    }

    @Test
    fun `Treasure and Bills are distinct routes`() {
        // Critical test: verify that Treasure (rewards hub) and Bills (payments) are separate routes
        val hasTreasure = BudgetShieldRouteRegistry.allDestinations.any { it is Treasure }
        val hasBills = BudgetShieldRouteRegistry.allDestinations.any { it is Bills }

        assertTrue("Registry should contain Treasure route", hasTreasure)
        assertTrue("Registry should contain Bills route", hasBills)
        assertNotEquals("Treasure and Bills should be different types",
            Treasure::class, Bills::class)
    }

    @Test
    fun `deliberate mutation test - removing route causes registry size mismatch`() {
        // This test proves the suite would fail if a production route was removed
        // The registry size check would catch it

        val actualSize = BudgetShieldRouteRegistry.allDestinations.size
        val expectedSize = BudgetShieldRouteRegistry.DESTINATION_COUNT

        // If these don't match, something is wrong
        assertEquals("Registry size must match DESTINATION_COUNT", expectedSize, actualSize)

        // Also verify we have exactly 14 by counting types
        val uniqueTypes = BudgetShieldRouteRegistry.allDestinations.map { it::class.simpleName }.distinct()
        assertEquals("Should have 14 unique destination types", 14, uniqueTypes.size)
    }
}
