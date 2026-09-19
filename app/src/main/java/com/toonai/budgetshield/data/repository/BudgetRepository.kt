package com.toonai.budgetshield.data.repository

import androidx.room.withTransaction
import com.toonai.budgetshield.data.database.BudgetCategoryDao
import com.toonai.budgetshield.data.database.BudgetShieldDatabase
import com.toonai.budgetshield.data.model.BudgetCategory
import com.toonai.budgetshield.data.model.BudgetCategoryType
import com.toonai.budgetshield.data.model.Transaction
import com.toonai.budgetshield.data.model.TransactionCategories
import com.toonai.budgetshield.data.model.XpActivityTypes
import com.toonai.budgetshield.data.model.XpEntry
import com.toonai.budgetshield.util.DateParser
import kotlinx.coroutines.flow.Flow

/**
 * Repository for budget category operations.
 * Single source of truth for budget data.
 */
class BudgetRepository(private val database: BudgetShieldDatabase) {
    private val budgetCategoryDao: BudgetCategoryDao = database.budgetCategoryDao()

    /**
     * Get budget for a specific category and month (compatible API).
     *
     * @param name Name of the category (e.g., "Food", "Wants")
     * @param monthKey Month in YYYY-MM format
     * @return Flow of budget category
     */
    fun getBudgetForCategory(name: String, monthKey: String): Flow<BudgetCategory?> {
        return budgetCategoryDao.getBudgetForCategory(name, monthKey)
    }

    /**
     * Save a budget category (compatible API).
     *
     * @param name Name of the category (e.g., "Food", "Wants")
     * @param monthKey Month in YYYY-MM format
     * @param amountCents Budget amount in cents
     */
    suspend fun saveBudget(name: String, monthKey: String, amountCents: Long) {
        val categoryType = when (name) {
            "Food" -> BudgetCategoryType.FOOD
            "Wants" -> BudgetCategoryType.WANTS
            else -> BudgetCategoryType.OTHER
        }

        val existing = budgetCategoryDao.getBudgetForCategorySync(name, monthKey)

        if (existing != null) {
            val updated = existing.copy(
                plannedAmountCents = amountCents,
                updatedAt = System.currentTimeMillis()
            )
            budgetCategoryDao.updateBudget(updated)
        } else {
            val category = BudgetCategory(
                name = name,
                monthKey = monthKey,
                plannedAmountCents = amountCents,
                categoryType = categoryType
            )
            budgetCategoryDao.insertBudget(category)
        }
    }

    /**
     * Get budget categories for a specific month.
     *
     * @param monthKey Month in YYYY-MM format
     * @return Flow of categories for that month
     */
    fun getBudgetsForMonth(monthKey: String): Flow<List<BudgetCategory>> {
        return budgetCategoryDao.getCategoriesForMonth(monthKey)
    }

    /** Get a specific category by ID */
    suspend fun getCategoryById(categoryId: Long): BudgetCategory? {
        // Not directly available in the DAO, would need to add
        return null
    }

    /**
     * Create or update a budget category.
     *
     * @param name Category name
     * @param categoryType Category type (food, wants, other)
     * @param budgetAmountCents Budget amount in cents
     * @param monthKey Month in YYYY-MM format
     * @param icon Icon emoji
     * @return The category ID
     */
    suspend fun createBudgetCategory(
        name: String,
        categoryType: String,
        budgetAmountCents: Long,
        monthKey: String,
        icon: String = "💰"
    ): Long {
        val existing = budgetCategoryDao.getBudgetForCategorySync(name, monthKey)

        return if (existing != null) {
            val updated = existing.copy(
                plannedAmountCents = budgetAmountCents,
                updatedAt = System.currentTimeMillis()
            )
            budgetCategoryDao.updateBudget(updated)
            existing.id
        } else {
            val category = BudgetCategory(
                name = name.trim(),
                monthKey = monthKey,
                plannedAmountCents = budgetAmountCents,
                categoryType = categoryType,
                icon = icon
            )
            budgetCategoryDao.insertBudget(category)
        }
    }

    /** Update a budget category */
    suspend fun updateBudgetCategory(category: BudgetCategory) {
        budgetCategoryDao.updateBudget(category)
    }

    /** Delete a budget category */
    suspend fun deleteCategory(categoryId: Long) {
        budgetCategoryDao.deleteBudget(categoryId)
    }

    /** Record spending in a category */
    suspend fun addSpending(categoryId: Long, amountCents: Long) {
        budgetCategoryDao.addSpending(categoryId, amountCents)
    }

    data class SpendingResult(
        val transactionId: Long,
        val xpEarned: Int
    )

    /**
     * Atomically records category spending.
     *
     * Spending updates the category usage, reduces cleared cash, appends the
     * immutable transaction ledger row, and awards budget-on-track XP only if
     * the category remains at or under plan after the spend.
     */
    suspend fun recordSpending(
        categoryId: Long,
        amountCents: Long,
        note: String? = null
    ): SpendingResult? {
        if (amountCents <= 0) return null

        return database.withTransaction {
            val category = budgetCategoryDao.getCategoryById(categoryId) ?: return@withTransaction null
            val settingsDao = database.userSettingsDao()
            val settings = settingsDao.getSettingsSync() ?: return@withTransaction null
            if (settings.cashOnHandCents < amountCents) return@withTransaction null

            val newSpentAmount = category.spentAmountCents + amountCents
            budgetCategoryDao.updateBudget(
                category.copy(
                    spentAmountCents = newSpentAmount,
                    updatedAt = System.currentTimeMillis()
                )
            )
            settingsDao.updateSettings(
                settings.copy(
                    cashOnHandCents = settings.cashOnHandCents - amountCents,
                    updatedAt = System.currentTimeMillis()
                )
            )

            val transactionCategory = when (category.categoryType) {
                BudgetCategoryType.FOOD -> TransactionCategories.FOOD
                BudgetCategoryType.WANTS -> TransactionCategories.WANTS
                else -> category.name
            }
            val icon = when (transactionCategory) {
                TransactionCategories.FOOD -> "🍔"
                TransactionCategories.WANTS -> "🎮"
                else -> category.icon
            }
            val stayedOnTrack = category.plannedAmountCents > 0 && newSpentAmount <= category.plannedAmountCents
            val xpAmount = if (stayedOnTrack) XpActivityTypes.baseXp(XpActivityTypes.BUDGET_ON_TRACK) else 0
            val today = DateParser.today()
            val transactionId = database.transactionDao().insertTransaction(
                Transaction(
                    type = Transaction.TYPE_SPENDING,
                    title = note?.takeIf { it.isNotBlank() } ?: category.name,
                    description = note,
                    amountCents = -amountCents,
                    category = transactionCategory,
                    icon = icon,
                    earnsXp = stayedOnTrack,
                    xpEarned = xpAmount,
                    transactionDate = today
                )
            )

            if (stayedOnTrack) {
                database.xpEntryDao().insertXpEntry(
                    XpEntry(
                        amount = xpAmount,
                        activityType = XpActivityTypes.BUDGET_ON_TRACK,
                        description = "${category.name} stayed on track",
                        relatedId = transactionId,
                        entryDate = today
                    )
                )
            }

            SpendingResult(transactionId = transactionId, xpEarned = xpAmount)
        }
    }

    /**
     * Initialize default categories for a month if they don't exist.
     *
     * @param monthKey Month in YYYY-MM format
     */
    suspend fun initializeDefaultCategoriesForMonth(monthKey: String) {
        // Create Food category if it doesn't exist
        val foodExists = budgetCategoryDao.getBudgetForCategorySync("Food", monthKey) != null
        if (!foodExists) {
            createBudgetCategory(
                name = "Food",
                categoryType = BudgetCategoryType.FOOD,
                budgetAmountCents = 0,
                monthKey = monthKey,
                icon = "🍽️"
            )
        }

        // Create Wants category if it doesn't exist
        val wantsExists = budgetCategoryDao.getBudgetForCategorySync("Wants", monthKey) != null
        if (!wantsExists) {
            createBudgetCategory(
                name = "Wants",
                categoryType = BudgetCategoryType.WANTS,
                budgetAmountCents = 0,
                monthKey = monthKey,
                icon = "🎁"
            )
        }
    }
}
