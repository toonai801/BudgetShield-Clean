package com.toonai.budgetshield.data.repository

import com.toonai.budgetshield.data.database.TransactionDao
import com.toonai.budgetshield.data.model.Transaction
import com.toonai.budgetshield.data.model.TransactionCategories
import com.toonai.budgetshield.util.DateParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Repository for transaction operations.
 * Single source of truth for transaction data.
 */
class TransactionRepository(private val transactionDao: TransactionDao) {

    /** All transactions as a reactive stream */
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()

    /** Get recent transactions (last N) */
    suspend fun getRecentTransactions(limit: Int = 5): List<Transaction> {
        return transactionDao.getRecentTransactions(limit)
    }

    /** Get transaction by ID */
    suspend fun getTransactionById(transactionId: Long): Transaction? {
        return transactionDao.getTransactionById(transactionId)
    }

    /** Get transactions for a date range */
    fun getTransactionsForDateRange(startDate: String, endDate: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsForDateRange(startDate, endDate)
    }

    /** Get transactions for current month */
    fun getTransactionsForCurrentMonth(): Flow<List<Transaction>> {
        val monthKey = DateParser.currentMonthKey()
        return transactionDao.getTransactionsForDateRange(
            "${monthKey}-01",
            DateParser.getLastDayOfMonth(monthKey)
        )
    }

    /** Get transactions by category */
    fun getTransactionsByCategory(category: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByCategory(category)
    }

    /** Get net amount for a month */
    fun getNetAmountForMonth(monthKey: String): Flow<Long> {
        return transactionDao.getNetAmountForMonth(monthKey).map { it ?: 0L }
    }

    /** Get total income for a month */
    fun getTotalIncomeForMonth(monthKey: String): Flow<Long> {
        return transactionDao.getTotalIncomeForMonth(monthKey).map { it ?: 0L }
    }

    /** Get total expenses for a month */
    fun getTotalExpensesForMonth(monthKey: String): Flow<Long> {
        return transactionDao.getTotalExpensesForMonth(monthKey).map { it ?: 0L }
    }

    /**
     * Create a new income transaction.
     */
    suspend fun createIncomeTransaction(
        title: String,
        amountCents: Long,
        category: String = TransactionCategories.INCOME,
        relatedIncomeId: Long? = null,
        description: String? = null
    ): Long {
        val transaction = Transaction(
            type = Transaction.TYPE_INCOME,
            title = title,
            description = description,
            amountCents = amountCents,
            category = category,
            icon = "💰",
            relatedIncomeId = relatedIncomeId,
            earnsXp = true,
            xpEarned = 30,
            transactionDate = DateParser.today()
        )
        return transactionDao.insertTransaction(transaction)
    }

    /**
     * Create a bill payment transaction.
     */
    suspend fun createBillPaymentTransaction(
        title: String,
        amountCents: Long,
        relatedBillId: Long,
        isProtected: Boolean = false,
        description: String? = null
    ): Long {
        val transaction = Transaction(
            type = Transaction.TYPE_BILL_PAYMENT,
            title = title,
            description = description,
            amountCents = -amountCents, // Negative for expense
            category = TransactionCategories.BILLS,
            icon = "📄",
            relatedBillId = relatedBillId,
            isProtected = isProtected,
            earnsXp = true,
            xpEarned = if (isProtected) 50 else 25,
            transactionDate = DateParser.today()
        )
        return transactionDao.insertTransaction(transaction)
    }

    /**
     * Create a savings transaction.
     */
    suspend fun createSavingsTransaction(
        title: String,
        amountCents: Long,
        description: String? = null
    ): Long {
        val transaction = Transaction(
            type = Transaction.TYPE_SAVINGS,
            title = title,
            description = description,
            amountCents = -amountCents, // Negative for expense
            category = TransactionCategories.SAVINGS,
            icon = "🏦",
            earnsXp = true,
            xpEarned = 25,
            transactionDate = DateParser.today()
        )
        return transactionDao.insertTransaction(transaction)
    }

    /**
     * Create a spending transaction.
     */
    suspend fun createSpendingTransaction(
        title: String,
        amountCents: Long,
        category: String,
        description: String? = null
    ): Long {
        val icon = when (category) {
            TransactionCategories.FOOD -> "🍔"
            TransactionCategories.WANTS -> "🎮"
            TransactionCategories.TRANSPORT -> "🚌"
            TransactionCategories.UTILITIES -> "⚡"
            else -> "💳"
        }
        val transaction = Transaction(
            type = Transaction.TYPE_SPENDING,
            title = title,
            description = description,
            amountCents = -amountCents,
            category = category,
            icon = icon,
            earnsXp = false,
            xpEarned = 0,
            transactionDate = DateParser.today()
        )
        return transactionDao.insertTransaction(transaction)
    }

    /** Delete a transaction */
    suspend fun deleteTransaction(transactionId: Long) {
        transactionDao.deleteTransactionById(transactionId)
    }

    /** Get transaction count */
    suspend fun getTransactionCount(): Int {
        return transactionDao.getTransactionCount()
    }
}
