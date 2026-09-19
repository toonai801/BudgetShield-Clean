package com.toonai.budgetshield.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.toonai.budgetshield.data.database.BudgetShieldDatabase
import com.toonai.budgetshield.data.model.BudgetCategory
import com.toonai.budgetshield.data.model.BudgetCategoryType
import com.toonai.budgetshield.data.model.Transaction
import com.toonai.budgetshield.data.model.UserSettings
import com.toonai.budgetshield.data.model.XpActivityTypes
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FinancialMutationRepositoryTest {

    private lateinit var database: BudgetShieldDatabase
    private lateinit var savingsRepository: SavingsGoalRepository
    private lateinit var budgetRepository: BudgetRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            BudgetShieldDatabase::class.java
        ).allowMainThreadQueries().build()
        savingsRepository = SavingsGoalRepository(database)
        budgetRepository = BudgetRepository(database)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `savings contribution atomically updates balances goal transaction xp and streak`() = runBlocking {
        database.userSettingsDao().insertSettings(
            UserSettings(id = 1L, cashOnHandCents = 50000L, savingsBalanceCents = 10000L)
        )
        val goalId = savingsRepository.createGoal(
            name = "Emergency",
            targetAmountCents = 20000L
        )

        val result = savingsRepository.recordSavingsContribution(
            amountCents = 12500L,
            note = "Emergency deposit",
            goalId = goalId
        )

        assertNotNull(result)
        assertEquals(XpActivityTypes.baseXp(XpActivityTypes.ADD_SAVINGS), result!!.xpEarned)

        val settings = database.userSettingsDao().getSettingsSync()
        assertEquals(37500L, settings?.cashOnHandCents)
        assertEquals(22500L, settings?.savingsBalanceCents)

        val goal = database.savingsGoalDao().getGoalById(goalId)
        assertEquals(12500L, goal?.currentAmountCents)

        val transaction = database.transactionDao().getRecentTransactions(10).single()
        assertEquals(Transaction.TYPE_SAVINGS, transaction.type)
        assertEquals(-12500L, transaction.amountCents)
        assertEquals(result.transactionId, transaction.id)

        val xp = database.xpEntryDao().getRecentXpEntries(10).single()
        assertEquals(XpActivityTypes.ADD_SAVINGS, xp.activityType)
        assertEquals(transaction.id, xp.relatedId)

        val streak = database.userStreakDao().getUserStreakSync()
        assertEquals(1, streak?.currentStreak)
    }

    @Test
    fun `rejected savings contribution appends no side effects`() = runBlocking {
        database.userSettingsDao().insertSettings(
            UserSettings(id = 1L, cashOnHandCents = 1000L, savingsBalanceCents = 0L)
        )
        val goalId = savingsRepository.createGoal(
            name = "Emergency",
            targetAmountCents = 20000L
        )

        val result = savingsRepository.recordSavingsContribution(
            amountCents = 2500L,
            note = "Too much",
            goalId = goalId
        )

        assertNull(result)
        assertEquals(1000L, database.userSettingsDao().getSettingsSync()?.cashOnHandCents)
        assertEquals(0L, database.savingsGoalDao().getGoalById(goalId)?.currentAmountCents)
        assertTrue(database.transactionDao().getRecentTransactions(10).isEmpty())
        assertTrue(database.xpEntryDao().getRecentXpEntries(10).isEmpty())
        assertNull(database.userStreakDao().getUserStreakSync())
    }

    @Test
    fun `spending atomically updates budget cash transaction and on-track xp`() = runBlocking {
        database.userSettingsDao().insertSettings(
            UserSettings(id = 1L, cashOnHandCents = 40000L)
        )
        val categoryId = database.budgetCategoryDao().insertBudget(
            BudgetCategory(
                name = "Food",
                monthKey = "2026-09",
                plannedAmountCents = 20000L,
                spentAmountCents = 5000L,
                categoryType = BudgetCategoryType.FOOD
            )
        )

        val result = budgetRepository.recordSpending(
            categoryId = categoryId,
            amountCents = 7500L,
            note = "Groceries"
        )

        assertNotNull(result)
        assertEquals(XpActivityTypes.baseXp(XpActivityTypes.BUDGET_ON_TRACK), result!!.xpEarned)
        assertEquals(32500L, database.userSettingsDao().getSettingsSync()?.cashOnHandCents)
        assertEquals(12500L, database.budgetCategoryDao().getCategoryById(categoryId)?.spentAmountCents)

        val transaction = database.transactionDao().getRecentTransactions(10).single()
        assertEquals(Transaction.TYPE_SPENDING, transaction.type)
        assertEquals(-7500L, transaction.amountCents)
        assertEquals("Food", transaction.category)
        assertEquals(result.transactionId, transaction.id)

        val xp = database.xpEntryDao().getRecentXpEntries(10).single()
        assertEquals(XpActivityTypes.BUDGET_ON_TRACK, xp.activityType)
        assertEquals(transaction.id, xp.relatedId)
    }

    @Test
    fun `rejected spending appends no side effects`() = runBlocking {
        database.userSettingsDao().insertSettings(
            UserSettings(id = 1L, cashOnHandCents = 1000L)
        )
        val categoryId = database.budgetCategoryDao().insertBudget(
            BudgetCategory(
                name = "Wants",
                monthKey = "2026-09",
                plannedAmountCents = 10000L,
                spentAmountCents = 2000L,
                categoryType = BudgetCategoryType.WANTS
            )
        )

        val result = budgetRepository.recordSpending(
            categoryId = categoryId,
            amountCents = 1500L,
            note = "Too much"
        )

        assertNull(result)
        assertEquals(1000L, database.userSettingsDao().getSettingsSync()?.cashOnHandCents)
        assertEquals(2000L, database.budgetCategoryDao().getCategoryById(categoryId)?.spentAmountCents)
        assertTrue(database.transactionDao().getRecentTransactions(10).isEmpty())
        assertTrue(database.xpEntryDao().getRecentXpEntries(10).isEmpty())
    }
}
