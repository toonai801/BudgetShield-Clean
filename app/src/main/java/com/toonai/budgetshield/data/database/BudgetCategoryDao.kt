package com.toonai.budgetshield.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.toonai.budgetshield.data.model.BudgetCategory
import kotlinx.coroutines.flow.Flow

/**
 * DAO for BudgetCategory (monthly Food/Wants budgets).
 */
@Dao
interface BudgetCategoryDao {

    @Query("SELECT * FROM budget_categories WHERE monthKey = :monthKey")
    fun getCategoriesForMonthFlow(monthKey: String): Flow<List<BudgetCategory>>

    @Query("SELECT * FROM budget_categories WHERE monthKey = :monthKey")
    suspend fun getCategoriesForMonth(monthKey: String): List<BudgetCategory>

    @Query("SELECT * FROM budget_categories WHERE monthKey = :monthKey")
    fun getBudgetsForMonthFlow(monthKey: String): Flow<List<BudgetCategory>>

    @Query("SELECT * FROM budget_categories WHERE monthKey = :monthKey")
    suspend fun getBudgetsForMonth(monthKey: String): List<BudgetCategory>

    @Query("SELECT * FROM budget_categories WHERE name = :name AND monthKey = :monthKey LIMIT 1")
    suspend fun getBudgetByNameAndMonth(name: String, monthKey: String): BudgetCategory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: BudgetCategory): Long

    @Update
    suspend fun update(budget: BudgetCategory)

    @Query("UPDATE budget_categories SET spentAmountCents = spentAmountCents + :amount, updatedAt = :timestamp WHERE id = :id")
    suspend fun addSpending(id: Long, amount: Long, timestamp: Long = System.currentTimeMillis())
}
