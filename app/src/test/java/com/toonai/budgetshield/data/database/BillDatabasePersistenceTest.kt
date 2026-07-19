package com.toonai.budgetshield.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.toonai.budgetshield.data.model.Bill
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
 * Room database persistence tests using real disk-backed database.
 * Proves bills survive database close and reopen.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class BillDatabasePersistenceTest {

    private lateinit var context: Context
    private var database: BudgetShieldDatabase? = null
    private lateinit var tempDbFile: File

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        tempDbFile = File(context.cacheDir, "test_persistence.db")
        // Clean up any existing file
        if (tempDbFile.exists()) {
            tempDbFile.delete()
        }
    }

    @After
    fun teardown() {
        database?.close()
        database = null
        // Clean up
        if (tempDbFile.exists()) {
            tempDbFile.delete()
        }
    }

    /**
     * Create a real disk-backed database (not in-memory) to test persistence.
     */
    private fun createDiskDatabase(): BudgetShieldDatabase {
        return Room.databaseBuilder(
            context,
            BudgetShieldDatabase::class.java,
            tempDbFile.absolutePath
        ).build()
    }

    @Test
    fun billPersistsAfterDatabaseCloseAndReopen(): Unit = runBlocking {
        // PHASE 1: Create database and insert bill
        val db1 = createDiskDatabase()

        val bill = Bill(
            name = "Rent",
            icon = "🏠",
            amountCents = 95000L, // $950.00
            dueDate = "2026-08-01",
            isProtected = true
        )

        val insertedId = db1.billDao().insertBill(bill)
        assertTrue("ID should be positive", insertedId > 0)

        // Verify bill exists before close
        val beforeClose = db1.billDao().getBillById(insertedId)
        assertNotNull("Bill should exist before close", beforeClose)
        assertEquals("Rent", beforeClose?.name)
        assertEquals(95000L, beforeClose?.amountCents)

        // Close the database (simulates app process death/restart)
        db1.close()

        // PHASE 2: Reopen database and verify bill still exists
        val db2 = createDiskDatabase()

        val afterReopen = db2.billDao().getBillById(insertedId)
        assertNotNull("Bill should exist after reopen", afterReopen)
        assertEquals("Rent", afterReopen?.name)
        assertEquals(95000L, afterReopen?.amountCents)
        assertEquals("🏠", afterReopen?.icon)
        assertEquals("2026-08-01", afterReopen?.dueDate)
        assertTrue("Should be protected", afterReopen?.isProtected ?: false)

        db2.close()
        database = null
    }

    @Test
    fun multipleBillsPersistAfterCloseAndReopen(): Unit = runBlocking {
        // PHASE 1: Create and populate database
        val db1 = createDiskDatabase()

        val bills = listOf(
            Bill(name = "Rent", icon = "🏠", amountCents = 95000L, dueDate = "2026-08-01", isProtected = true),
            Bill(name = "Electric", icon = "⚡", amountCents = 8500L, dueDate = "2026-08-05", isProtected = true),
            Bill(name = "Internet", icon = "🌐", amountCents = 6000L, dueDate = "2026-08-10", isProtected = false)
        )

        val insertedIds = bills.map { db1.billDao().insertBill(it) }
        assertEquals(3, insertedIds.size)
        assertTrue("All IDs should be positive", insertedIds.all { it > 0 })

        // Close database
        db1.close()

        // PHASE 2: Reopen and verify all bills
        val db2 = createDiskDatabase()

        val retrievedBills = db2.billDao().getAllBills().first()
        assertEquals(3, retrievedBills.size)

        // Verify each bill by name
        val byName = retrievedBills.associateBy { it.name }
        assertNotNull("Rent should exist", byName["Rent"])
        assertEquals(95000L, byName["Rent"]?.amountCents)
        assertNotNull("Electric should exist", byName["Electric"])
        assertEquals(8500L, byName["Electric"]?.amountCents)
        assertNotNull("Internet should exist", byName["Internet"])
        assertEquals(6000L, byName["Internet"]?.amountCents)

        db2.close()
        database = null
    }

    @Test
    fun paymentPersistsAfterCloseAndReopen(): Unit = runBlocking {
        // PHASE 1: Create bill and make partial payment
        val db1 = createDiskDatabase()

        val bill = Bill(
            name = "Car Payment",
            icon = "🚗",
            amountCents = 40000L, // $400
            dueDate = "2026-08-15",
            isProtected = true,
            paidAmountCents = 0L,
            isPaid = false
        )

        val billId = db1.billDao().insertBill(bill)

        // Make partial payment of $150
        val beforePayment = db1.billDao().getBillById(billId)!!
        val afterPayment = beforePayment.copy(
            paidAmountCents = 15000L, // $150
            isPaid = false // Not fully paid yet
        )
        db1.billDao().updateBill(afterPayment)

        // Verify payment before close
        val beforeClose = db1.billDao().getBillById(billId)!!
        assertEquals(15000L, beforeClose.paidAmountCents)
        assertEquals(25000L, beforeClose.remainingDueCents) // $400 - $150 = $250
        assertFalse("Should not be fully paid", beforeClose.isPaid)

        db1.close()

        // PHASE 2: Reopen and verify payment persisted
        val db2 = createDiskDatabase()

        val afterReopen = db2.billDao().getBillById(billId)!!
        assertEquals(15000L, afterReopen.paidAmountCents)
        assertEquals(25000L, afterReopen.remainingDueCents)
        assertFalse("Should still not be fully paid", afterReopen.isPaid)

        db2.close()
        database = null
    }

    @Test
    fun fullPaymentPersistsAfterCloseAndReopen(): Unit = runBlocking {
        val db1 = createDiskDatabase()

        val bill = Bill(
            name = "Subscription",
            icon = "📺",
            amountCents = 1500L, // $15
            dueDate = "2026-08-01",
            paidAmountCents = 0L,
            isPaid = false
        )

        val billId = db1.billDao().insertBill(bill)

        // Make full payment
        val beforePayment = db1.billDao().getBillById(billId)!!
        val fullyPaid = beforePayment.copy(
            paidAmountCents = 1500L, // Full amount
            isPaid = true
        )
        db1.billDao().updateBill(fullyPaid)

        db1.close()

        // Reopen and verify full payment persisted
        val db2 = createDiskDatabase()

        val afterReopen = db2.billDao().getBillById(billId)!!
        assertEquals(1500L, afterReopen.paidAmountCents)
        assertEquals(0L, afterReopen.remainingDueCents)
        assertTrue("Should be marked as paid", afterReopen.isPaid)

        db2.close()
        database = null
    }
}
