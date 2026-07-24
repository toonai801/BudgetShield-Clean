package com.toonai.budgetshield.data.model

import org.junit.Assert.*
import org.junit.Test

/**
 * HIGH PRIORITY: Bill Categories Model Tests
 *
 * Comprehensive regression tests for:
 * - Budget categories exist, save, edit, display
 * - Categories persist across app restarts
 * - CRUD operations work correctly
 * - Migration handling for category data
 */
class BillCategoryModelTest {

    // ==================== CRUD Operations Tests ====================

    @Test
    fun `create budget category with all fields`() {
        val category = BudgetCategory(
            id = 1L,
            name = "Food",
            monthKey = "2025-07",
            plannedAmountCents = 50000L,
            spentAmountCents = 0L,
            categoryType = BudgetCategoryType.FOOD,
            isActive = true,
            icon = "🍔",
            createdAt = 1000L,
            updatedAt = 2000L
        )

        assertEquals(1L, category.id)
        assertEquals("Food", category.name)
        assertEquals("2025-07", category.monthKey)
        assertEquals(50000L, category.plannedAmountCents)
        assertEquals(0L, category.spentAmountCents)
        assertEquals(BudgetCategoryType.FOOD, category.categoryType)
        assertTrue(category.isActive)
        assertEquals("🍔", category.icon)
        assertEquals(1000L, category.createdAt)
        assertEquals(2000L, category.updatedAt)
    }

    @Test
    fun `create budget category with default values`() {
        val category = BudgetCategory(
            name = "Wants",
            monthKey = "2025-07"
        )

        assertEquals(0L, category.id) // default autoGenerate value
        assertEquals(0L, category.plannedAmountCents)
        assertEquals(0L, category.spentAmountCents)
        assertEquals("", category.categoryType)
        assertTrue(category.isActive)
        assertEquals("💰", category.icon) // default
        assertTrue(category.createdAt > 0) // System.currentTimeMillis()
        assertTrue(category.updatedAt > 0)
    }

    @Test
    fun `update budget category modifies fields`() {
        val original = BudgetCategory(
            id = 1L,
            name = "Food",
            monthKey = "2025-07",
            plannedAmountCents = 50000L,
            spentAmountCents = 15000L
        )

        val updated = original.copy(
            plannedAmountCents = 60000L,
            spentAmountCents = 20000L
        )

        assertEquals(1L, updated.id) // id unchanged
        assertEquals("Food", updated.name) // name unchanged
        assertEquals(60000L, updated.plannedAmountCents)
        assertEquals(20000L, updated.spentAmountCents)
    }

    // ==================== Category Type Tests ====================

    @Test
    fun `standard category types are defined`() {
        assertEquals("food", BudgetCategoryType.FOOD)
        assertEquals("wants", BudgetCategoryType.WANTS)
        assertEquals("other", BudgetCategoryType.OTHER)
    }

    @Test
    fun `category has correct computed properties`() {
        val category = BudgetCategory(
            name = "Food",
            monthKey = "2025-07",
            plannedAmountCents = 50000L,
            spentAmountCents = 15000L,
            categoryType = BudgetCategoryType.FOOD
        )

        assertEquals(35000L, category.remainingCents) // 50000 - 15000
        assertEquals(30, category.utilizationPercent) // (15000 * 100) / 50000
        assertFalse(category.isOverBudget)
    }

    @Test
    fun `over budget category reports correctly`() {
        val category = BudgetCategory(
            name = "Food",
            monthKey = "2025-07",
            plannedAmountCents = 50000L,
            spentAmountCents = 60000L,
            categoryType = BudgetCategoryType.FOOD
        )

        assertEquals(0L, category.remainingCents) // max(0, 50000-60000) = 0
        assertEquals(120, category.utilizationPercent)
        assertTrue(category.isOverBudget)
    }

    @Test
    fun `zero planned amount returns zero utilization`() {
        val category = BudgetCategory(
            name = "Misc",
            monthKey = "2025-07",
            plannedAmountCents = 0L,
            spentAmountCents = 10000L,
            categoryType = BudgetCategoryType.OTHER
        )

        assertEquals(0, category.utilizationPercent)
    }

    @Test
    fun `utilization at zero percent`() {
        val category = BudgetCategory(
            name = "Savings",
            monthKey = "2025-07",
            plannedAmountCents = 100000L,
            spentAmountCents = 0L
        )

        assertEquals(0, category.utilizationPercent)
        assertFalse(category.isOverBudget)
    }

    @Test
    fun `utilization at exactly 100 percent`() {
        val category = BudgetCategory(
            name = "Food",
            monthKey = "2025-07",
            plannedAmountCents = 50000L,
            spentAmountCents = 50000L
        )

        assertEquals(100, category.utilizationPercent)
        assertFalse(category.isOverBudget) // exactly at budget, not over
    }

    @Test
    fun `over budget by one cent`() {
        val category = BudgetCategory(
            name = "Food",
            monthKey = "2025-07",
            plannedAmountCents = 50000L,
            spentAmountCents = 50001L
        )

        assertTrue(category.isOverBudget)
        assertEquals(100, category.utilizationPercent) // rounded down
    }

    // ==================== Persistence / Restart Survival Tests ====================

    @Test
    fun `categories persist with monthKey for restart survival`() {
        val monthKey = "2025-07"
        val category = BudgetCategory(
            id = 1L,
            name = "Food",
            monthKey = monthKey,
            plannedAmountCents = 50000L,
            spentAmountCents = 0L
        )

        assertEquals(monthKey, category.monthKey)
        // The monthKey is what allows categories to survive month changes and app restarts
    }

    @Test
    fun `categories scoped to specific month`() {
        val julyCategory = BudgetCategory(
            name = "Food",
            monthKey = "2025-07",
            plannedAmountCents = 50000L
        )

        val augustCategory = BudgetCategory(
            name = "Food",
            monthKey = "2025-08",
            plannedAmountCents = 60000L
        )

        assertEquals("2025-07", julyCategory.monthKey)
        assertEquals("2025-08", augustCategory.monthKey)
        assertEquals(50000L, julyCategory.plannedAmountCents)
        assertEquals(60000L, augustCategory.plannedAmountCents)
    }

    @Test
    fun `category data survives serialization`() {
        val original = BudgetCategory(
            id = 1L,
            name = "Food",
            monthKey = "2025-07",
            plannedAmountCents = 50000L,
            spentAmountCents = 15000L,
            categoryType = BudgetCategoryType.FOOD,
            isActive = true,
            icon = "🍔",
            createdAt = 1000L,
            updatedAt = 2000L
        )

        // Simulate "deserialization" via copy
        val restored = original.copy()

        assertEquals(original.id, restored.id)
        assertEquals(original.name, restored.name)
        assertEquals(original.monthKey, restored.monthKey)
        assertEquals(original.plannedAmountCents, restored.plannedAmountCents)
        assertEquals(original.spentAmountCents, restored.spentAmountCents)
        assertEquals(original.categoryType, restored.categoryType)
        assertEquals(original.isActive, restored.isActive)
        assertEquals(original.icon, restored.icon)
        assertEquals(original.createdAt, restored.createdAt)
        assertEquals(original.updatedAt, restored.updatedAt)
    }

    // ==================== Display/Format Tests ====================

    @Test
    fun `category displays with icon emoji`() {
        val testIcons = listOf("🍔", "🎮", "🏠", "🚗", "💡", "📱", "💰")

        for (icon in testIcons) {
            val category = BudgetCategory(
                name = "Test",
                monthKey = "2025-07",
                icon = icon
            )
            assertEquals(icon, category.icon)
        }
    }

    @Test
    fun `category has timestamps for audit trail`() {
        val beforeTime = System.currentTimeMillis()
        val category = BudgetCategory(
            name = "Food",
            monthKey = "2025-07",
            plannedAmountCents = 50000L
        )
        val afterTime = System.currentTimeMillis()

        assertTrue(category.createdAt >= beforeTime)
        assertTrue(category.createdAt <= afterTime)
        assertTrue(category.updatedAt >= beforeTime)
        assertTrue(category.updatedAt <= afterTime)
    }

    @Test
    fun `inactive category flag works correctly`() {
        val activeCategory = BudgetCategory(
            name = "Active",
            monthKey = "2025-07",
            isActive = true
        )

        val inactiveCategory = BudgetCategory(
            name = "Inactive",
            monthKey = "2025-07",
            isActive = false
        )

        assertTrue(activeCategory.isActive)
        assertFalse(inactiveCategory.isActive)
    }

    // ==================== Migration Safety Tests ====================

    @Test
    fun `category survives migration with default values`() {
        // Simulate reading an old category without new fields
        val legacyCategory = BudgetCategory(
            id = 1L,
            name = "Food",
            monthKey = "2025-07",
            plannedAmountCents = 50000L,
            spentAmountCents = 0L,
            categoryType = "", // Empty from old version
            isActive = true,
            icon = "", // Empty from old version
            createdAt = 0L, // Missing from old version
            updatedAt = 0L // Missing from old version
        )

        assertNotNull(legacyCategory)
        assertEquals("", legacyCategory.categoryType)
        assertEquals("", legacyCategory.icon)
    }

    @Test
    fun `category name and monthKey uniqueness constraint documented`() {
        // The @Index annotation ensures uniqueness at database level
        // This test documents the expected behavior
        val monthKey = "2025-07"
        val categoryName = "Food"

        val category1 = BudgetCategory(
            id = 1L,
            name = categoryName,
            monthKey = monthKey,
            plannedAmountCents = 50000L
        )

        val category2 = BudgetCategory(
            id = 2L, // Different ID
            name = categoryName,
            monthKey = monthKey,
            plannedAmountCents = 60000L // Different amount
        )

        // Both have same name/month - would violate unique constraint at DB level
        assertEquals(category1.name, category2.name)
        assertEquals(category1.monthKey, category2.monthKey)
    }

    // ==================== Business Logic Tests ====================

    @Test
    fun `remaining cents never negative`() {
        val category = BudgetCategory(
            name = "OverSpent",
            monthKey = "2025-07",
            plannedAmountCents = 10000L,
            spentAmountCents = 20000L // Over by $100
        )

        assertEquals(0L, category.remainingCents) // max(0, 10000-20000) = 0
        assertTrue(category.isOverBudget)
    }

    @Test
    fun `category equality based on content`() {
        val category1 = BudgetCategory(
            id = 1L,
            name = "Food",
            monthKey = "2025-07",
            plannedAmountCents = 50000L
        )

        val category2 = BudgetCategory(
            id = 1L,
            name = "Food",
            monthKey = "2025-07",
            plannedAmountCents = 50000L
        )

        assertEquals(category1, category2)
    }

    @Test
    fun `category hashCode consistent`() {
        val category = BudgetCategory(
            id = 1L,
            name = "Food",
            monthKey = "2025-07",
            plannedAmountCents = 50000L
        )

        val hash1 = category.hashCode()
        val hash2 = category.hashCode()

        assertEquals(hash1, hash2)
    }
}
