package com.toonai.budgetshield.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.toonai.budgetshield.data.model.BudgetCategory
import com.toonai.budgetshield.data.model.BudgetCategoryType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * Room database persistence tests for BudgetCategory.
 * Proves categories survive database close and reopen.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class BudgetCategoryPersistenceTest {

    private lateinit var context: Context
    private var database: BudgetShieldDatabase? = null
    private lateinit var tempDbFile: File

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        tempDbFile = File(context.cacheDir, "test_category_persistence.db")
        if (tempDbFile.exists()) {
            tempDbFile.delete()
        }
    }

    @After
    fun teardown() {
        database?.close()
        database = null
        if (tempDbFile.exists()) {
            tempDbFile.delete()
        }
    }

    private fun createDiskDatabase(): BudgetShieldDatabase {
        return Room.databaseBuilder(
            context,
            BudgetShieldDatabase::class.java,
            tempDbFile.absolutePath
        ).build()
    }

    @Test
    fun `create category with type and read back from room`(): Unit = runBlocking {
        val db = createDiskDatabase()
        database = db

        // Create category with all fields including type
        val category = BudgetCategory(
            name = "Food",
            monthKey = "2025-07",
            plannedAmountCents = 50000L,
            spentAmountCents = 0L,
            categoryType = BudgetCategoryType.FOOD,
            isActive = true,
            icon = "🍔"
        )

        val insertedId = db.budgetCategoryDao().insertCategory(category)
        assertTrue("ID should be positive", insertedId > 0)

        // Read back from Room
        val retrieved = db.budgetCategoryDao().getCategoryById(insertedId)
        assertNotNull("Category should exist", retrieved)
        assertEquals("Food", retrieved?.name)
        assertEquals("2025-07", retrieved?.monthKey)
        assertEquals(50000L, retrieved?.plannedAmountCents)
        assertEquals(BudgetCategoryType.FOOD, retrieved?.categoryType)
        assertEquals("🍔", retrieved?.icon)
    }

    @Test
    fun `edit category and verify updated in room`(): Unit = runBlocking {
        val db = createDiskDatabase()
        database = db

        // Create initial category
        val category = BudgetCategory(
            name = "Food",
            monthKey = "2025-07",
            plannedAmountCents = 50000L,
            spentAmountCents = 0L,
            categoryType = BudgetCategoryType.FOOD
        )

        val id = db.budgetCategoryDao().insertCategory(category)

        // Edit category - change amount and type
        val updated = category.copy(
            id = id,
            plannedAmountCents = 60000L,
            categoryType = BudgetCategoryType.OTHER,
            icon = "🍕"
        )
        db.budgetCategoryDao().updateCategory(updated)

        // Verify updated in Room
        val retrieved = db.budgetCategoryDao().getCategoryById(id)
        assertEquals(60000L, retrieved?.plannedAmountCents)
        assertEquals(BudgetCategoryType.OTHER, retrieved?.categoryType)
        assertEquals("🍕", retrieved?.icon)
    }

    @Test
    fun `category survives database close and reopen`(): Unit = runBlocking {
        // PHASE 1: Create database and insert category
        val db1 = createDiskDatabase()

        val category = BudgetCategory(
            name = "Wants",
            monthKey = "2025-08",
            plannedAmountCents = 30000L,
            spentAmountCents = 5000L,
            categoryType = BudgetCategoryType.WANTS,
            isActive = true,
            icon = "🎮"
        )

        val id = db1.budgetCategoryDao().insertCategory(category)

        // Verify before close
        val beforeClose = db1.budgetCategoryDao().getCategoryById(id)
        assertNotNull("Category should exist before close", beforeClose)
        assertEquals("Wants", beforeClose?.name)
        assertEquals("2025-08", beforeClose?.monthKey)
        assertEquals(30000L, beforeClose?.plannedAmountCents)
        assertEquals(5000L, beforeClose?.spentAmountCents)
        assertEquals(BudgetCategoryType.WANTS, beforeClose?.categoryType)

        // Close database (simulates app process death/restart)
        db1.close()

        // PHASE 2: Reopen database and verify category still exists
        val db2 = createDiskDatabase()
        database = db2

        val afterReopen = db2.budgetCategoryDao().getCategoryById(id)
        assertNotNull("Category should exist after reopen", afterReopen)
        assertEquals("Wants", afterReopen?.name)
        assertEquals("2025-08", afterReopen?.monthKey)
        assertEquals(30000L, afterReopen?.plannedAmountCents)
        assertEquals(5000L, afterReopen?.spentAmountCents)
        assertEquals(BudgetCategoryType.WANTS, afterReopen?.categoryType)
        assertEquals("🎮", afterReopen?.icon)
        assertTrue("Should be active", afterReopen?.isActive ?: false)

        db2.close()
        database = null
    }

    @Test
    fun `multiple categories persist after close and reopen`(): Unit = runBlocking {
        // PHASE 1: Create and populate database
        val db1 = createDiskDatabase()

        val categories = listOf(
            BudgetCategory(
                name = "Food",
                monthKey = "2025-07",
                plannedAmountCents = 50000L,
                categoryType = BudgetCategoryType.FOOD,
                icon = "🍔"
            ),
            BudgetCategory(
                name = "Wants",
                monthKey = "2025-07",
                plannedAmountCents = 30000L,
                categoryType = BudgetCategoryType.WANTS,
                icon = "🎮"
            ),
            BudgetCategory(
                name = "Other",
                monthKey = "2025-07",
                plannedAmountCents = 20000L,
                categoryType = BudgetCategoryType.OTHER,
                icon = "📦"
            )
        )

        val ids = categories.map { db1.budgetCategoryDao().insertCategory(it) }
        assertEquals(3, ids.size)

        // Close database
        db1.close()

        // PHASE 2: Reopen and verify all categories
        val db2 = createDiskDatabase()
        database = db2

        val retrievedCategories = db2.budgetCategoryDao().getCategoriesForMonth("2025-07").first()
        assertEquals(3, retrievedCategories.size)

        // Verify each category by name
        val byName = retrievedCategories.associateBy { it.name }
        assertNotNull("Food should exist", byName["Food"])
        assertEquals(50000L, byName["Food"]?.plannedAmountCents)
        assertEquals(BudgetCategoryType.FOOD, byName["Food"]?.categoryType)

        assertNotNull("Wants should exist", byName["Wants"])
        assertEquals(30000L, byName["Wants"]?.plannedAmountCents)
        assertEquals(BudgetCategoryType.WANTS, byName["Wants"]?.categoryType)

        assertNotNull("Other should exist", byName["Other"])
        assertEquals(20000L, byName["Other"]?.plannedAmountCents)
        assertEquals(BudgetCategoryType.OTHER, byName["Other"]?.categoryType)

        db2.close()
        database = null
    }

    @Test
    fun `category edit persists after process death simulation`(): Unit = runBlocking {
        val db1 = createDiskDatabase()

        // Create category
        val category = BudgetCategory(
            name = "Food",
            monthKey = "2025-09",
            plannedAmountCents = 50000L,
            spentAmountCents = 0L
        )

        val id = db1.budgetCategoryDao().insertCategory(category)

        // Edit category
        val edited = category.copy(
            id = id,
            plannedAmountCents = 75000L,
            spentAmountCents = 25000L,
            categoryType = BudgetCategoryType.FOOD
        )
        db1.budgetCategoryDao().updateCategory(edited)

        // Verify edit before close
        val beforeClose = db1.budgetCategoryDao().getCategoryById(id)
        assertEquals(75000L, beforeClose?.plannedAmountCents)
        assertEquals(25000L, beforeClose?.spentAmountCents)

        // Close and reopen
        db1.close()

        val db2 = createDiskDatabase()
        database = db2

        // Verify edit persisted
        val afterReopen = db2.budgetCategoryDao().getCategoryById(id)
        assertNotNull(afterReopen)
        assertEquals(75000L, afterReopen?.plannedAmountCents)
        assertEquals(25000L, afterReopen?.spentAmountCents)
        assertEquals(BudgetCategoryType.FOOD, afterReopen?.categoryType)

        db2.close()
        database = null
    }

    @Test
    fun `categories scoped by monthKey survive independently`(): Unit = runBlocking {
        val db = createDiskDatabase()
        database = db

        // Create same category for different months
        val julyFood = BudgetCategory(
            name = "Food",
            monthKey = "2025-07",
            plannedAmountCents = 50000L
        )
        val augustFood = BudgetCategory(
            name = "Food",
            monthKey = "2025-08",
            plannedAmountCents = 55000L
        )

        val julyId = db.budgetCategoryDao().insertCategory(julyFood)
        val augustId = db.budgetCategoryDao().insertCategory(augustFood)

        // Verify different IDs
        assertNotEquals(julyId, augustId)

        // Close and reopen
        db.close()

        val db2 = createDiskDatabase()
        database = db2

        // Verify both exist with correct amounts
        val julyRetrieved = db2.budgetCategoryDao().getCategoryById(julyId)
        val augustRetrieved = db2.budgetCategoryDao().getCategoryById(augustId)

        assertEquals(50000L, julyRetrieved?.plannedAmountCents)
        assertEquals("2025-07", julyRetrieved?.monthKey)

        assertEquals(55000L, augustRetrieved?.plannedAmountCents)
        assertEquals("2025-08", augustRetrieved?.monthKey)

        db2.close()
        database = null
    }
}
