package com.toonai.budgetshield.navigation

import com.toonai.budgetshield.ui.components.MainDestination
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for BudgetShieldNavShell navigation behavior
 * Verifies MainDestination enum and route-to-destination mapping
 */
class BudgetShieldNavShellTest {

    @Test
    fun `MainDestination enum has exactly five destinations`() {
        val destinations = MainDestination.values()
        assertEquals("Should have 5 main destinations", 5, destinations.size)
    }

    @Test
    fun `MainDestination enum values are in correct order`() {
        val expected = arrayOf(
            MainDestination.HOME,
            MainDestination.TREASURE,
            MainDestination.STATS,
            MainDestination.GOALS,
            MainDestination.SETTINGS
        )
        assertArrayEquals("MainDestination values should be in correct order",
            expected, MainDestination.values())
    }

    @Test
    fun `MainDestination has correct labels`() {
        assertEquals("Home", MainDestination.HOME.label)
        assertEquals("Treasure", MainDestination.TREASURE.label)
        assertEquals("Stats", MainDestination.STATS.label)
        assertEquals("Goals", MainDestination.GOALS.label)
        assertEquals("Settings", MainDestination.SETTINGS.label)
    }

    @Test
    fun `MainDestination HOME maps to Home route`() {
        assertEquals(Home::class, MainDestination.HOME.routeClass)
    }

    @Test
    fun `MainDestination TREASURE maps to Treasure route`() {
        assertEquals(Treasure::class, MainDestination.TREASURE.routeClass)
    }

    @Test
    fun `MainDestination STATS maps to Stats route`() {
        assertEquals(Stats::class, MainDestination.STATS.routeClass)
    }

    @Test
    fun `MainDestination GOALS maps to Goals route`() {
        assertEquals(Goals::class, MainDestination.GOALS.routeClass)
    }

    @Test
    fun `MainDestination SETTINGS maps to Settings route`() {
        assertEquals(Settings::class, MainDestination.SETTINGS.routeClass)
    }

    @Test
    fun `getMainDestinationForKey returns HOME for Home route`() {
        val result = getMainDestinationForKey(Home)
        assertEquals(MainDestination.HOME, result)
    }

    @Test
    fun `getMainDestinationForKey returns TREASURE for Treasure route`() {
        val result = getMainDestinationForKey(Treasure)
        assertEquals(MainDestination.TREASURE, result)
    }

    @Test
    fun `getMainDestinationForKey returns STATS for Stats route`() {
        val result = getMainDestinationForKey(Stats)
        assertEquals(MainDestination.STATS, result)
    }

    @Test
    fun `getMainDestinationForKey returns GOALS for Goals route`() {
        val result = getMainDestinationForKey(Goals)
        assertEquals(MainDestination.GOALS, result)
    }

    @Test
    fun `getMainDestinationForKey returns SETTINGS for Settings route`() {
        val result = getMainDestinationForKey(Settings)
        assertEquals(MainDestination.SETTINGS, result)
    }

    @Test
    fun `getMainDestinationForKey returns HOME for Home-owned routes`() {
        // Bills is owned by Home
        assertEquals(MainDestination.HOME, getMainDestinationForKey(Bills))

        // IncomeEntry is owned by Home
        assertEquals(MainDestination.HOME, getMainDestinationForKey(IncomeEntry))

        // BillEntry is owned by Home
        assertEquals(MainDestination.HOME, getMainDestinationForKey(BillEntry))

        // TransactionDetails is owned by Home
        assertEquals(MainDestination.HOME, getMainDestinationForKey(TransactionDetails()))
    }

    @Test
    fun `getMainDestinationForKey returns null for SetupQuest`() {
        // SetupQuest should have no footer
        val result = getMainDestinationForKey(SetupQuest)
        assertNull("SetupQuest should not map to any main destination", result)
    }

    @Test
    fun `all production routes are mapped to a MainDestination or null`() {
        val allRoutes = BudgetShieldRouteRegistry.allDestinations

        for (route in allRoutes) {
            val destination = getMainDestinationForKey(route)

            // Every route should either map to a destination or explicitly be null (SetupQuest)
            when (route) {
                is SetupQuest -> assertNull("SetupQuest should have no destination", destination)
                else -> assertNotNull("$route should map to a MainDestination", destination)
            }
        }
    }

    @Test
    fun `all 14 routes have correct ownership mapping`() {
        // Home-owned routes highlight HOME
        val homeOwnedRoutes = listOf(
            Home, Bills, IncomeEntry, BillEntry, BillPayment,
            TransactionDetails(), BillProtected, ShieldProgression
        )
        for (route in homeOwnedRoutes) {
            assertEquals("$route should be owned by HOME", MainDestination.HOME, getMainDestinationForKey(route))
        }

        // Self-owned routes
        assertEquals(MainDestination.TREASURE, getMainDestinationForKey(Treasure))
        assertEquals(MainDestination.STATS, getMainDestinationForKey(Stats))
        assertEquals(MainDestination.GOALS, getMainDestinationForKey(Goals))
        assertEquals(MainDestination.SETTINGS, getMainDestinationForKey(Settings))

        // SavingsEntry is Home-owned
        assertEquals(MainDestination.HOME, getMainDestinationForKey(SavingsEntry))
    }

    @Test
    fun `MainDestination values match footer order`() {
        // The footer should display in this exact order
        val expectedOrder = listOf("Home", "Treasure", "Stats", "Goals", "Settings")
        val actualOrder = MainDestination.values().map { it.label }
        assertEquals("Footer destinations should be in correct order", expectedOrder, actualOrder)
    }
}
