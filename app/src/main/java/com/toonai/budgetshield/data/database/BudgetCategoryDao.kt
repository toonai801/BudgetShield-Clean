package com.toonai.budgetshield.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.toonai.budgetshield.data.model.BudgetCategory
import kotlinx.coroutines.flow.Flow

/**
 * DAO for BudgetCategory entity.
 */
@Dao
interface BudgetCategoryDao {

    @Query("SELECT * FROM budget_categories WHERE monthKey = :monthKey ORDER BY name ASC")
    fun getBudgetsForMonth(monthKey: String): Flow<List<BudgetCategory>>

    @Query("SELECT * FROM budget_categories WHERE name = :name AND monthKey = :monthKey LIMIT 1")
    fun getBudgetForCategory(name: String, monthKey: String): Flow<BudgetCategory?>

    @Query("SELECT * FROM budget_categories WHERE name = :name AND monthKey = :monthKey LIMIT 1")
    suspend fun getBudgetForCategorySync(name: String, monthKey: String): BudgetCategory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetCategory): Long

    @Update
    suspend fun updateBudget(budget: BudgetCategory)

    @Query("DELETE FROM budget_categories WHERE id = :id")
    suspend fun deleteBudget(id: Long)

    @Query("UPDATE budget_categories SET spentAmountCents = spentAmountCents + :amount WHERE id = :id")
    suspend fun addSpending(id: Long, amount: Long)
}
