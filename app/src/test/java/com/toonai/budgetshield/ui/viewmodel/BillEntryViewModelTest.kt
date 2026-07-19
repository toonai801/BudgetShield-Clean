package com.toonai.budgetshield.ui.viewmodel

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.toonai.budgetshield.data.database.BudgetShieldDatabase
import com.toonai.budgetshield.data.repository.BillRepository
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
 * BillEntryViewModel tests using real production ViewModel and Room-backed BillRepository.
 * Tests success path, validation failures, and real persistence failure.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class BillEntryViewModelTest {

    private lateinit var database: BudgetShieldDatabase
    private lateinit var repository: BillRepository
    private lateinit var viewModel: BillEntryViewModel

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            BudgetShieldDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = BillRepository(database.billDao())
        viewModel = BillEntryViewModel(repository)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `valid input returns Result success with a positive inserted ID and the record exists`() = runBlocking {
        val result = viewModel.createBill(
            name = "Valid Bill",
            icon = "📄",
            amountCents = 5000L,
            dueDate = "2026-08-01",
            isProtected = false
        )

        // Verify Result.success
        assertTrue("Should be success", result.isSuccess)

        // Extract ID
        val billId = result.getOrNull()
        assertNotNull("ID should not be null", billId)
        assertTrue("ID should be positive", billId!! > 0)

        // Verify record exists in repository
        val bill = repository.getBillById(billId)
        assertNotNull("Bill should exist in repository", bill)
        assertEquals("Valid Bill", bill?.name)
        assertEquals(5000L, bill?.amountCents)
    }

    @Test
    fun `blank name returns Result failure and inserts nothing`() = runBlocking {
        val initialCount = repository.allBills.first().size

        val result = viewModel.createBill(
            name = "", // Blank name
            icon = "📄",
            amountCents = 5000L,
            dueDate = "2026-08-01",
            isProtected = false
        )

        // Verify Result.failure
        assertTrue("Should be failure", result.isFailure)

        // Verify no bill was inserted
        val exception = result.exceptionOrNull()
        assertNotNull("Should have exception", exception)
        assertTrue("Should be IllegalArgumentException", exception is IllegalArgumentException)
        assertEquals("Bill name is required", exception?.message)

        // Verify no new bills in repository
        val bills = repository.allBills.first()
        assertEquals("No bills should be inserted", initialCount, bills.size)
    }

    @Test
    fun `zero amount returns Result failure and inserts nothing`() = runBlocking {
        val initialCount = repository.allBills.first().size

        val result = viewModel.createBill(
            name = "Zero Amount Bill",
            icon = "📄",
            amountCents = 0L, // Zero amount
            dueDate = "2026-08-01",
            isProtected = false
        )

        // Verify Result.failure
        assertTrue("Should be failure", result.isFailure)

        // Verify error message
        val exception = result.exceptionOrNull()
        assertNotNull("Should have exception", exception)
        assertTrue("Should be IllegalArgumentException", exception is IllegalArgumentException)
        assertEquals("Amount must be greater than \$0.00", exception?.message)

        // Verify no bill was inserted
        val bills = repository.allBills.first()
        assertEquals("No bills should be inserted", initialCount, bills.size)
    }

    @Test
    fun `negative amount returns Result failure and inserts nothing`() = runBlocking {
        val initialCount = repository.allBills.first().size

        val result = viewModel.createBill(
            name = "Negative Amount Bill",
            icon = "📄",
            amountCents = -100L, // Negative amount
            dueDate = "2026-08-01",
            isProtected = false
        )

        // Verify Result.failure
        assertTrue("Should be failure", result.isFailure)

        val exception = result.exceptionOrNull()
        assertNotNull("Should have exception", exception)
        assertTrue("Should be IllegalArgumentException", exception is IllegalArgumentException)

        // Verify no bill was inserted
        val bills = repository.allBills.first()
        assertEquals("No bills should be inserted", initialCount, bills.size)
    }

    @Test
    fun `blank due date returns Result failure and inserts nothing`() = runBlocking {
        val initialCount = repository.allBills.first().size

        val result = viewModel.createBill(
            name = "Blank Date Bill",
            icon = "📄",
            amountCents = 5000L,
            dueDate = "", // Blank date
            isProtected = false
        )

        // Verify Result.failure
        assertTrue("Should be failure", result.isFailure)

        // Verify error message
        val exception = result.exceptionOrNull()
        assertNotNull("Should have exception", exception)
        assertTrue("Should be IllegalArgumentException", exception is IllegalArgumentException)
        assertEquals("Due date is required", exception?.message)

        // Verify no bill was inserted
        val bills = repository.allBills.first()
        assertEquals("No bills should be inserted", initialCount, bills.size)
    }

    @Test
    fun `real persistence failure returns Result failure instead of success`() = runBlocking {
        // Close the database to force persistence failure
        database.close()

        // Try to create a bill with closed database
        val result = viewModel.createBill(
            name = "Will Fail",
            icon = "📄",
            amountCents = 5000L,
            dueDate = "2026-08-01",
            isProtected = false
        )

        // Verify Result.failure (not success or crash)
        assertTrue("Should be failure due to closed database", result.isFailure)

        // Verify we got an exception (not a success with fake ID)
        val exception = result.exceptionOrNull()
        assertNotNull("Should have exception", exception)
    }
}
