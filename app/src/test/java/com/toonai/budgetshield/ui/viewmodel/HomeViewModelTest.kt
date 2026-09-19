package com.toonai.budgetshield.ui.viewmodel

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.toonai.budgetshield.data.database.BudgetShieldDatabase
import com.toonai.budgetshield.data.model.Transaction
import com.toonai.budgetshield.data.model.UserSettings
import com.toonai.budgetshield.data.model.UserStreak
import com.toonai.budgetshield.data.repository.BillRepository
import com.toonai.budgetshield.data.repository.IncomeRepository
import com.toonai.budgetshield.data.repository.SavingsGoalRepository
import com.toonai.budgetshield.data.repository.TransactionRepository
import com.toonai.budgetshield.data.repository.UserSettingsRepository
import com.toonai.budgetshield.ui.screens.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var database: BudgetShieldDatabase
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            BudgetShieldDatabase::class.java
        ).allowMainThreadQueries().build()
        viewModel = HomeViewModel(
            userSettingsRepository = UserSettingsRepository(database.userSettingsDao()),
            billRepository = BillRepository(database),
            incomeRepository = IncomeRepository(database.incomeScheduleDao()),
            transactionRepository = TransactionRepository(database.transactionDao()),
            savingsGoalRepository = SavingsGoalRepository(database)
        )
    }

    @After
    fun teardown() {
        database.close()
        Dispatchers.resetMain()
    }

    @Test
    fun `home loads current streak and recent transactions from persisted ledgers`() = runTest(dispatcher) {
        database.userSettingsDao().insertSettings(
            UserSettings(
                id = 1L,
                cashOnHandCents = 100000L,
                savingsBalanceCents = 25000L,
                selectedMonth = "2026-09"
            )
        )
        database.userStreakDao().insertOrUpdateStreak(
            UserStreak(
                currentStreak = 4,
                bestStreak = 7,
                lastActivityDate = "2026-09-19",
                isActiveToday = true,
                totalActiveDays = 9
            )
        )
        database.transactionDao().insertTransaction(
            Transaction(
                type = Transaction.TYPE_SPENDING,
                title = "Groceries",
                amountCents = -4321L,
                category = "Food",
                icon = "🍔",
                transactionDate = "2026-09-18",
                createdAt = 1000L
            )
        )
        database.transactionDao().insertTransaction(
            Transaction(
                type = Transaction.TYPE_INCOME,
                title = "Paycheck",
                amountCents = 250000L,
                category = "Income",
                icon = "💰",
                transactionDate = "2026-09-19",
                createdAt = 2000L
            )
        )
        assertEquals(4, SavingsGoalRepository(database).getCurrentStreak())

        val state = viewModel.loadHomeDataState()
        assertNull(state.error, state.error)
        assertEquals(4, state.currentStreak)
        assertEquals(2, state.recentTransactions.size)
        assertEquals("Paycheck", state.recentTransactions[0].name)
        assertEquals("+$2500.00", state.recentTransactions[0].amountDisplay)
        assertEquals(TransactionType.INCOME, state.recentTransactions[0].type)
        assertEquals("Groceries", state.recentTransactions[1].name)
        assertEquals("-$43.21", state.recentTransactions[1].amountDisplay)
        assertEquals(TransactionType.SPENDING, state.recentTransactions[1].type)
    }
}
