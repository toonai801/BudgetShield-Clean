package com.toonai.budgetshield.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.toonai.budgetshield.data.database.BudgetShieldDatabase
import com.toonai.budgetshield.data.model.SetupDraft
import com.toonai.budgetshield.data.model.XpActivityTypes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SetupActivationRepositoryTest {

    private lateinit var database: BudgetShieldDatabase
    private lateinit var repository: SetupActivationRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            BudgetShieldDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = SetupActivationRepository(database)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `activation atomically creates setup records clears draft and awards setup xp`() = runBlocking {
        database.setupDraftDao().saveDraft(SetupDraft(currentChapter = 6, cashOnHandCents = 25000L))

        val result = repository.activate(validRequest())

        assertTrue(result.activated)
        assertFalse(result.alreadyComplete)
        assertEquals(XpActivityTypes.baseXp(XpActivityTypes.COMPLETE_SETUP), result.xpEarned)

        val settings = database.userSettingsDao().getSettingsSync()
        assertNotNull(settings)
        assertTrue(settings!!.isFirstRunComplete)
        assertEquals(75000L, settings.cashOnHandCents)
        assertEquals(25000L, settings.savingsBalanceCents)
        assertEquals(7, settings.setupChapter)

        assertEquals(1, database.incomeScheduleDao().getAllActiveSchedules().first().size)
        assertEquals(2, database.budgetCategoryDao().getBudgetsForMonth("2026-09").size)
        assertEquals(1, database.billDao().getAllBills().first().size)
        assertEquals(null, database.setupDraftDao().getDraftSync())

        val xp = database.xpEntryDao().getRecentXpEntries(10).single()
        assertEquals(XpActivityTypes.COMPLETE_SETUP, xp.activityType)
        assertEquals(XpActivityTypes.baseXp(XpActivityTypes.COMPLETE_SETUP), xp.amount)
    }

    @Test
    fun `repeat activation is idempotent and does not duplicate setup xp`() = runBlocking {
        val first = repository.activate(validRequest())
        val second = repository.activate(validRequest())

        assertTrue(first.activated)
        assertFalse(second.activated)
        assertTrue(second.alreadyComplete)
        assertEquals(1, database.xpEntryDao().getRecentXpEntries(10).size)
        assertEquals(1, database.incomeScheduleDao().getAllActiveSchedules().first().size)
        assertEquals(1, database.billDao().getAllBills().first().size)
    }

    @Test
    fun `rejected activation appends no setup side effects`() = runBlocking {
        val invalid = validRequest().copy(
            bills = listOf(
                SetupActivationRepository.ActivationBillDraft(
                    name = "Bad Bill",
                    icon = "!",
                    amountCents = 1200L,
                    dueDate = "not-a-date",
                    isProtected = true
                )
            )
        )

        val result = runCatching { repository.activate(invalid) }

        assertTrue(result.isFailure)
        assertEquals(null, database.userSettingsDao().getSettingsSync())
        assertTrue(database.incomeScheduleDao().getAllActiveSchedules().first().isEmpty())
        assertTrue(database.budgetCategoryDao().getBudgetsForMonth("2026-09").isEmpty())
        assertTrue(database.billDao().getAllBills().first().isEmpty())
        assertTrue(database.xpEntryDao().getRecentXpEntries(10).isEmpty())
    }

    private fun validRequest(): SetupActivationRepository.ActivationRequest {
        return SetupActivationRepository.ActivationRequest(
            cashOnHandCents = 75000L,
            savingsBalanceCents = 25000L,
            incomeName = "Job",
            incomeAmountCents = 200000L,
            nextPaydayDate = "2026-09-25",
            frequency = "biweekly",
            paydayAnchorDayOne = null,
            paydayAnchorDayTwo = null,
            isIncomeConfirmed = true,
            foodBudgetCents = 40000L,
            wantsBudgetCents = 15000L,
            bills = listOf(
                SetupActivationRepository.ActivationBillDraft(
                    name = "Rent",
                    icon = "🏠",
                    amountCents = 120000L,
                    dueDate = "2026-10-01",
                    isProtected = true
                )
            ),
            selectedMonth = "2026-09"
        )
    }
}
