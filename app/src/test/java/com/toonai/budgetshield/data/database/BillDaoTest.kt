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

/**
 * Room integration tests for BillDao.
 * Uses Robolectric to run without a connected device.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28]) // Use SDK 28 for Robolectric compatibility
class BillDaoTest {

    private lateinit var database: BudgetShieldDatabase
    private lateinit var billDao: BillDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            BudgetShieldDatabase::class.java
        ).allowMainThreadQueries().build()
        billDao = database.billDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `insert and retrieve bill`() = runBlocking {
        val bill = Bill(
            name = "Test Bill",
            icon = "📄",
            amountCents = 10000L, // $100.00
            dueDate = "2026-08-01"
        )

        val id = billDao.insertBill(bill)
        assertTrue("ID should be positive", id > 0)

        val retrieved = billDao.getBillById(id)
        assertNotNull("Bill should be found", retrieved)
        assertEquals("Test Bill", retrieved?.name)
        assertEquals(10000L, retrieved?.amountCents)
    }

    @Test
    fun `insert bill with flow`() = runBlocking {
        val bill = Bill(
            name = "Flow Test",
            icon = "⚡",
            amountCents = 5000L,
            dueDate = "2026-08-15"
        )

        val id = billDao.insertBill(bill)
        val flowBill = billDao.getBillByIdFlow(id).first()

        assertNotNull("Flow should emit bill", flowBill)
        assertEquals("Flow Test", flowBill?.name)
    }

    @Test
    fun `update bill`() = runBlocking {
        val bill = Bill(
            name = "Original",
            icon = "📄",
            amountCents = 10000L,
            dueDate = "2026-08-01"
        )

        val id = billDao.insertBill(bill)
        val inserted = billDao.getBillById(id)!!

        val updated = inserted.copy(
            name = "Updated",
            amountCents = 20000L
        )

        billDao.updateBill(updated)

        val retrieved = billDao.getBillById(id)
        assertEquals("Updated", retrieved?.name)
        assertEquals(20000L, retrieved?.amountCents)
    }

    @Test
    fun `delete bill by id`() = runBlocking {
        val bill = Bill(
            name = "To Delete",
            icon = "🗑️",
            amountCents = 5000L,
            dueDate = "2026-08-01"
        )

        val id = billDao.insertBill(bill)
        assertNotNull("Bill should exist", billDao.getBillById(id))

        billDao.deleteBillById(id)
        assertNull("Bill should be deleted", billDao.getBillById(id))
    }

    @Test
    fun `get all bills sorted by due date`() = runBlocking {
        val bill1 = Bill(
            name = "Later Bill",
            icon = "📄",
            amountCents = 10000L,
            dueDate = "2026-08-15"
        )
        val bill2 = Bill(
            name = "Earlier Bill",
            icon = "⚡",
            amountCents = 5000L,
            dueDate = "2026-08-01"
        )

        billDao.insertBill(bill1)
        billDao.insertBill(bill2)

        val bills = billDao.getAllBills().first()
        assertEquals(2, bills.size)
        assertEquals("Earlier Bill", bills[0].name) // Should be first (earlier date)
        assertEquals("Later Bill", bills[1].name)
    }

    @Test
    fun `total unpaid cents calculation`() = runBlocking {
        // Bill 1: $100, not paid
        val bill1 = Bill(
            name = "Bill 1",
            icon = "📄",
            amountCents = 10000L,
            dueDate = "2026-08-01",
            isPaid = false,
            paidAmountCents = 0L
        )

        // Bill 2: $50, partially paid ($20)
        val bill2 = Bill(
            name = "Bill 2",
            icon = "⚡",
            amountCents = 5000L,
            dueDate = "2026-08-15",
            isPaid = false,
            paidAmountCents = 2000L
        )

        // Bill 3: $30, fully paid
        val bill3 = Bill(
            name = "Bill 3",
            icon = "🛡️",
            amountCents = 3000L,
            dueDate = "2026-08-20",
            isPaid = true,
            paidAmountCents = 3000L
        )

        billDao.insertBill(bill1)
        bill2.let { billDao.insertBill(it) }
        billDao.insertBill(bill3)

        val totalUnpaid = billDao.getTotalUnpaidCents().first()
        // Bill 1: $100 remaining + Bill 2: $30 remaining = $130 = 13000 cents
        assertEquals(13000L, totalUnpaid)
    }

    @Test
    fun `protected unpaid cents calculation`() = runBlocking {
        // Protected bill: $100, not paid
        val protectedBill = Bill(
            name = "Protected",
            icon = "🛡️",
            amountCents = 10000L,
            dueDate = "2026-08-01",
            isProtected = true,
            isPaid = false,
            paidAmountCents = 0L
        )

        // Unprotected bill: $50, not paid
        val unprotectedBill = Bill(
            name = "Unprotected",
            icon = "⚠️",
            amountCents = 5000L,
            dueDate = "2026-08-15",
            isProtected = false,
            isPaid = false,
            paidAmountCents = 0L
        )

        // Protected but paid bill: $30
        val protectedPaidBill = Bill(
            name = "Protected Paid",
            icon = "✓",
            amountCents = 3000L,
            dueDate = "2026-08-20",
            isProtected = true,
            isPaid = true,
            paidAmountCents = 3000L
        )

        billDao.insertBill(protectedBill)
        billDao.insertBill(unprotectedBill)
        billDao.insertBill(protectedPaidBill)

        val protectedCents = billDao.getTotalProtectedCents().first()
        // Only the unpaid protected bill: $100 = 10000 cents
        assertEquals(10000L, protectedCents)
    }

    @Test
    fun `protected and unprotected counts`() = runBlocking {
        val protected1 = Bill(
            name = "Protected 1",
            icon = "🛡️",
            amountCents = 10000L,
            dueDate = "2026-08-01",
            isProtected = true,
            isPaid = false
        )
        val protected2 = Bill(
            name = "Protected 2",
            icon = "🛡️",
            amountCents = 5000L,
            dueDate = "2026-08-10",
            isProtected = true,
            isPaid = true // Paid, should not count
        )
        val unprotected = Bill(
            name = "Unprotected",
            icon = "⚠️",
            amountCents = 3000L,
            dueDate = "2026-08-15",
            isProtected = false,
            isPaid = false
        )

        billDao.insertBill(protected1)
        billDao.insertBill(protected2)
        billDao.insertBill(unprotected)

        val protectedCount = billDao.getProtectedCount().first()
        val unprotectedCount = billDao.getUnprotectedCount().first()

        assertEquals(1, protectedCount) // Only unpaid protected
        assertEquals(1, unprotectedCount)
    }

    @Test
    fun `partial payment reduces totals`() = runBlocking {
        // Create a bill
        val bill = Bill(
            name = "Partial Pay Test",
            icon = "📄",
            amountCents = 10000L, // $100
            dueDate = "2026-08-01",
            isPaid = false,
            paidAmountCents = 0L
        )

        val id = billDao.insertBill(bill)

        // Check initial total
        val initialTotal = billDao.getTotalUnpaidCents().first()
        assertEquals(10000L, initialTotal)

        // Make partial payment of $30
        val retrieved = billDao.getBillById(id)!!
        val updated = retrieved.copy(
            paidAmountCents = 3000L,
            isPaid = false // Still not fully paid
        )
        billDao.updateBill(updated)

        // Check updated total
        val updatedTotal = billDao.getTotalUnpaidCents().first()
        assertEquals(7000L, updatedTotal) // $100 - $30 = $70 remaining
    }

    @Test
    fun `full payment removes from unpaid totals`() = runBlocking {
        val bill = Bill(
            name = "Full Pay Test",
            icon = "📄",
            amountCents = 5000L, // $50
            dueDate = "2026-08-01",
            isPaid = false,
            paidAmountCents = 0L
        )

        val id = billDao.insertBill(bill)

        // Initial total should include this bill
        val initialTotal = billDao.getTotalUnpaidCents().first()
        assertEquals(5000L, initialTotal)

        // Mark as fully paid
        val retrieved = billDao.getBillById(id)!!
        val updated = retrieved.copy(
            paidAmountCents = 5000L,
            isPaid = true
        )
        billDao.updateBill(updated)

        // Total should now be zero
        val updatedTotal = billDao.getTotalUnpaidCents().first()
        assertEquals(0L, updatedTotal)
    }

    @Test
    fun `zero totals with no bills`() = runBlocking {
        val totalUnpaid = billDao.getTotalUnpaidCents().first()
        val protectedCents = billDao.getTotalProtectedCents().first()
        val protectedCount = billDao.getProtectedCount().first()
        val unprotectedCount = billDao.getUnprotectedCount().first()

        assertEquals(0L, totalUnpaid)
        assertEquals(0L, protectedCents)
        assertEquals(0, protectedCount)
        assertEquals(0, unprotectedCount)
    }

    @Test
    fun `bill count`() = runBlocking {
        assertEquals(0, billDao.getBillCount())

        val bill1 = Bill(
            name = "Bill 1",
            icon = "📄",
            amountCents = 10000L,
            dueDate = "2026-08-01"
        )
        val bill2 = Bill(
            name = "Bill 2",
            icon = "⚡",
            amountCents = 5000L,
            dueDate = "2026-08-15"
        )

        billDao.insertBill(bill1)
        assertEquals(1, billDao.getBillCount())

        billDao.insertBill(bill2)
        assertEquals(2, billDao.getBillCount())
    }
}
