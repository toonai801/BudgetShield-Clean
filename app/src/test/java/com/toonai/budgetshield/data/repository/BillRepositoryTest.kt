package com.toonai.budgetshield.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.toonai.budgetshield.data.database.BudgetShieldDatabase
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
 * Repository integration tests using real Room database.
 * Tests BillRepository.payBill() and BillRepository.createBill() directly.
 * All invalid payment cases prove no mutation by reading before and after.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class BillRepositoryTest {

    private lateinit var database: BudgetShieldDatabase
    private lateinit var repository: BillRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            BudgetShieldDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = BillRepository(database.billDao())
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `createBill inserts and can be read back`() = runBlocking {
        val id = repository.createBill(
            name = "Test Bill",
            icon = "📄",
            amountCents = 10000L,
            dueDate = "2026-08-01",
            isProtected = false
        )
        assertTrue("ID should be positive", id > 0)

        val retrieved = repository.getBillById(id)
        assertNotNull("Bill should be found", retrieved)
        assertEquals("Test Bill", retrieved?.name)
        assertEquals(10000L, retrieved?.amountCents)
        assertEquals("2026-08-01", retrieved?.dueDate)
        assertFalse("Should not be protected", retrieved?.isProtected ?: true)
    }

    @Test
    fun `partial payment returns true`() = runBlocking {
        val billId = repository.createBill(
            name = "Rent",
            icon = "🏠",
            amountCents = 95000L, // $950
            dueDate = "2026-08-01",
            isProtected = true
        )

        val result = repository.payBill(billId, 30000L) // Pay $300

        assertTrue("Partial payment should succeed", result)
    }

    @Test
    fun `partial payment updates paidAmountCents and remainingDueCents correctly`() = runBlocking {
        val billId = repository.createBill(
            name = "Utilities",
            icon = "⚡",
            amountCents = 20000L, // $200
            dueDate = "2026-08-05",
            isProtected = false
        )

        repository.payBill(billId, 7500L) // Pay $75

        val bill = repository.getBillById(billId)!!
        assertEquals(7500L, bill.paidAmountCents)
        assertEquals(12500L, bill.remainingDueCents) // $200 - $75 = $125
        assertFalse("Should not be fully paid", bill.isPaid)
    }

    @Test
    fun `partial payment reduces totalUnpaidCents`() = runBlocking {
        val billId = repository.createBill(
            name = "Internet",
            icon = "🌐",
            amountCents = 6000L, // $60
            dueDate = "2026-08-10",
            isProtected = false
        )

        val initialTotal = repository.totalUnpaidCents.first()
        assertEquals(6000L, initialTotal)

        repository.payBill(billId, 2000L) // Pay $20

        val updatedTotal = repository.totalUnpaidCents.first()
        assertEquals(4000L, updatedTotal) // $60 - $20 = $40 remaining
    }

    @Test
    fun `partial payment reduces totalProtectedCents for a protected bill`() = runBlocking {
        val billId = repository.createBill(
            name = "Insurance",
            icon = "🛡️",
            amountCents = 15000L, // $150
            dueDate = "2026-08-15",
            isProtected = true
        )

        val initialProtected = repository.totalProtectedCents.first()
        assertEquals(15000L, initialProtected)

        repository.payBill(billId, 5000L) // Pay $50

        val updatedProtected = repository.totalProtectedCents.first()
        assertEquals(10000L, updatedProtected) // $150 - $50 = $100 remaining
    }

    @Test
    fun `full payment returns true`() = runBlocking {
        val billId = repository.createBill(
            name = "Subscription",
            icon = "📺",
            amountCents = 1500L, // $15
            dueDate = "2026-08-01",
            isProtected = false
        )

        val result = repository.payBill(billId, 1500L) // Pay full $15

        assertTrue("Full payment should succeed", result)
    }

    @Test
    fun `full payment sets isPaid true and remainingDueCents to zero`() = runBlocking {
        val billId = repository.createBill(
            name = "Gym",
            icon = "💪",
            amountCents = 5000L, // $50
            dueDate = "2026-08-20",
            isProtected = true
        )

        repository.payBill(billId, 5000L) // Pay full $50

        val bill = repository.getBillById(billId)!!
        assertEquals(5000L, bill.paidAmountCents)
        assertEquals(0L, bill.remainingDueCents)
        assertTrue("Should be marked as paid", bill.isPaid)
    }

    @Test
    fun `full payment removes the bill from applicable unpaid totals`() = runBlocking {
        val billId = repository.createBill(
            name = "Phone",
            icon = "📱",
            amountCents = 8000L, // $80
            dueDate = "2026-08-25",
            isProtected = true
        )

        val initialUnpaid = repository.totalUnpaidCents.first()
        val initialProtected = repository.totalProtectedCents.first()
        assertEquals(8000L, initialUnpaid)
        assertEquals(8000L, initialProtected)

        repository.payBill(billId, 8000L) // Pay full

        val finalUnpaid = repository.totalUnpaidCents.first()
        val finalProtected = repository.totalProtectedCents.first()
        assertEquals(0L, finalUnpaid)
        assertEquals(0L, finalProtected)
    }

    @Test
    fun `zero payment returns false and leaves the stored bill unchanged`() = runBlocking {
        val billId = repository.createBill(
            name = "Zero Test",
            icon = "🧪",
            amountCents = 10000L,
            dueDate = "2026-08-01",
            isProtected = false
        )

        // Read before
        val before = repository.getBillById(billId)!!
        assertEquals(0L, before.paidAmountCents)
        assertFalse(before.isPaid)

        // Attempt zero payment
        val result = repository.payBill(billId, 0L)

        // Read after
        val after = repository.getBillById(billId)!!

        assertFalse("Zero payment should fail", result)
        assertEquals("Bill should be unchanged", before.paidAmountCents, after.paidAmountCents)
        assertEquals("isPaid should be unchanged", before.isPaid, after.isPaid)
        assertEquals("amountCents should be unchanged", before.amountCents, after.amountCents)
    }

    @Test
    fun `negative payment returns false and leaves the stored bill unchanged`() = runBlocking {
        val billId = repository.createBill(
            name = "Negative Test",
            icon = "🧪",
            amountCents = 5000L,
            dueDate = "2026-08-01",
            isProtected = false
        )

        // Make partial payment first
        repository.payBill(billId, 1000L)

        // Read before negative attempt
        val before = repository.getBillById(billId)!!
        assertEquals(1000L, before.paidAmountCents)
        assertFalse(before.isPaid)

        // Attempt negative payment
        val result = repository.payBill(billId, -100L)

        // Read after
        val after = repository.getBillById(billId)!!

        assertFalse("Negative payment should fail", result)
        assertEquals("Bill should be unchanged", before.paidAmountCents, after.paidAmountCents)
        assertEquals("isPaid should be unchanged", before.isPaid, after.isPaid)
    }

    @Test
    fun `excessive payment returns false and leaves the stored bill unchanged`() = runBlocking {
        val billId = repository.createBill(
            name = "Excessive Test",
            icon = "🧪",
            amountCents = 5000L, // $50
            dueDate = "2026-08-01",
            isProtected = false
        )

        // Make partial payment first
        repository.payBill(billId, 2000L) // Pay $20

        // Read before excessive attempt
        val before = repository.getBillById(billId)!!
        assertEquals(2000L, before.paidAmountCents)
        assertEquals(3000L, before.remainingDueCents) // $30 remaining

        // Attempt payment exceeding remaining
        val result = repository.payBill(billId, 5000L) // Try to pay $50 when only $30 remains

        // Read after
        val after = repository.getBillById(billId)!!

        assertFalse("Excessive payment should fail", result)
        assertEquals("paidAmountCents should be unchanged", before.paidAmountCents, after.paidAmountCents)
        assertEquals("isPaid should be unchanged", before.isPaid, after.isPaid)
    }

    @Test
    fun `missing bill ID returns false and does not mutate other bills`() = runBlocking {
        // Create two bills
        val billId1 = repository.createBill(
            name = "Bill One",
            icon = "1️⃣",
            amountCents = 10000L,
            dueDate = "2026-08-01",
            isProtected = false
        )
        val billId2 = repository.createBill(
            name = "Bill Two",
            icon = "2️⃣",
            amountCents = 20000L,
            dueDate = "2026-08-02",
            isProtected = false
        )

        // Read both before
        val before1 = repository.getBillById(billId1)!!
        val before2 = repository.getBillById(billId2)!!

        // Attempt payment to non-existent bill ID
        val nonExistentId = 999999L
        val result = repository.payBill(nonExistentId, 5000L)

        // Read both after
        val after1 = repository.getBillById(billId1)!!
        val after2 = repository.getBillById(billId2)!!

        assertFalse("Payment to missing bill should fail", result)
        assertNull("Non-existent bill should not exist", repository.getBillById(nonExistentId))

        // Verify bill1 unchanged
        assertEquals("Bill 1 unchanged", before1.paidAmountCents, after1.paidAmountCents)
        assertEquals("Bill 1 unchanged", before1.isPaid, after1.isPaid)

        // Verify bill2 unchanged
        assertEquals("Bill 2 unchanged", before2.paidAmountCents, after2.paidAmountCents)
        assertEquals("Bill 2 unchanged", before2.isPaid, after2.isPaid)
    }

    @Test
    fun `paying an already fully paid bill returns false and leaves it unchanged`() = runBlocking {
        val billId = repository.createBill(
            name = "Already Paid",
            icon = "✅",
            amountCents = 10000L, // $100
            dueDate = "2026-08-01",
            isProtected = false
        )

        // Pay in full
        repository.payBill(billId, 10000L)

        // Verify fully paid
        val afterFirstPayment = repository.getBillById(billId)!!
        assertTrue(afterFirstPayment.isPaid)
        assertEquals(10000L, afterFirstPayment.paidAmountCents)

        // Attempt another payment
        val result = repository.payBill(billId, 1000L)

        // Read after second attempt
        val afterSecondAttempt = repository.getBillById(billId)!!

        assertFalse("Payment to already-paid bill should fail", result)
        assertEquals("paidAmountCents should be unchanged", 10000L, afterSecondAttempt.paidAmountCents)
        assertTrue("Should still be paid", afterSecondAttempt.isPaid)
        assertEquals("remainingDueCents should be zero", 0L, afterSecondAttempt.remainingDueCents)
    }
}
