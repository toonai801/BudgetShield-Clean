package com.toonai.budgetshield.data.database

import androidx.room.*
import com.toonai.budgetshield.data.model.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * DAO for transaction operations.
 */
@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC, createdAt DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE transactionDate >= :startDate AND transactionDate <= :endDate ORDER BY transactionDate DESC, createdAt DESC")
    fun getTransactionsForDateRange(startDate: String, endDate: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY transactionDate DESC")
    fun getTransactionsByCategory(category: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY transactionDate DESC")
    fun getTransactionsByType(type: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :transactionId LIMIT 1")
    suspend fun getTransactionById(transactionId: Long): Transaction?

    @Query("SELECT * FROM transactions WHERE relatedBillId = :billId ORDER BY transactionDate DESC")
    fun getTransactionsForBill(billId: Long): Flow<List<Transaction>>

    @Query("SELECT SUM(amountCents) FROM transactions WHERE transactionDate LIKE :monthKey || '%'")
    fun getNetAmountForMonth(monthKey: String): Flow<Long?>

    @Query("SELECT SUM(amountCents) FROM transactions WHERE type = 'income' AND transactionDate LIKE :monthKey || '%'")
    fun getTotalIncomeForMonth(monthKey: String): Flow<Long?>

    @Query("SELECT SUM(ABS(amountCents)) FROM transactions WHERE amountCents < 0 AND transactionDate LIKE :monthKey || '%'")
    fun getTotalExpensesForMonth(monthKey: String): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBlocking(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteTransactionById(transactionId: Long)

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getTransactionCount(): Int

    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC LIMIT :limit")
    suspend fun getRecentTransactions(limit: Int): List<Transaction>

    @Query("SELECT SUM(xpEarned) FROM transactions WHERE earnsXp = 1")
    fun getTotalXpFromTransactions(): Flow<Int?>
}
