package com.toonai.budgetshield.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.toonai.budgetshield.data.model.Bill
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Bill entities.
 * Provides reactive Flow queries for Compose integration.
 */
@Dao
interface BillDao {
    
    /** Get all bills as a reactive stream */
    @Query("SELECT * FROM bills ORDER BY dueDate ASC, name ASC")
    fun getAllBills(): Flow<List<Bill>>
    
    /** Get a single bill by ID */
    @Query("SELECT * FROM bills WHERE id = :billId LIMIT 1")
    suspend fun getBillById(billId: Long): Bill?
    
    /** Get a single bill by ID as Flow */
    @Query("SELECT * FROM bills WHERE id = :billId LIMIT 1")
    fun getBillByIdFlow(billId: Long): Flow<Bill?>
    
    /** Insert a new bill, returns the generated ID */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: Bill): Long

    /** Synchronous blocking version - safe for test environments. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBillBlocking(bill: Bill): Long

    /** Synchronous blocking version - safe for test environments. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBlocking(bill: Bill): Long
    
    /** Update an existing bill */
    @Update
    suspend fun updateBill(bill: Bill)
    
    /** Delete a bill */
    @Delete
    suspend fun deleteBill(bill: Bill)
    
    /** Delete bill by ID */
    @Query("DELETE FROM bills WHERE id = :billId")
    suspend fun deleteBillById(billId: Long)
    
    /** Get total of all unpaid remaining amounts */
    @Query("SELECT COALESCE(SUM(amountCents - paidAmountCents), 0) FROM bills WHERE isPaid = 0")
    fun getTotalUnpaidCents(): Flow<Long>
    
    /** Get total of protected unpaid amounts */
    @Query("SELECT COALESCE(SUM(amountCents - paidAmountCents), 0) FROM bills WHERE isProtected = 1 AND isPaid = 0")
    fun getTotalProtectedCents(): Flow<Long>
    
    /** Get count of protected unpaid bills */
    @Query("SELECT COUNT(*) FROM bills WHERE isProtected = 1 AND isPaid = 0")
    fun getProtectedCount(): Flow<Int>
    
    /** Get count of unprotected unpaid bills */
    @Query("SELECT COUNT(*) FROM bills WHERE isProtected = 0 AND isPaid = 0")
    fun getUnprotectedCount(): Flow<Int>
    
    /** Get count of all bills */
    @Query("SELECT COUNT(*) FROM bills")
    suspend fun getBillCount(): Int

    /**
     * Synchronous blocking version - safe for test environments.
     */
    @Query("SELECT * FROM bills ORDER BY dueDate ASC, name ASC")
    fun getAllBillsBlocking(): List<Bill>
}
