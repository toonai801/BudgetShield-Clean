package com.toonai.budgetshield.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.toonai.budgetshield.data.model.SavingsBalance
import kotlinx.coroutines.flow.Flow

/**
 * DAO for SavingsBalance (current savings amount).
 */
@Dao
interface SavingsBalanceDao {

    @Query("SELECT * FROM savings_balance WHERE id = 1")
    fun getBalanceFlow(): Flow<SavingsBalance?>

    @Query("SELECT * FROM savings_balance WHERE id = 1")
    suspend fun getBalance(): SavingsBalance?

    @Query("SELECT * FROM savings_balance WHERE id = 1")
    fun getSavingsFlow(): Flow<SavingsBalance?>

    @Query("SELECT * FROM savings_balance WHERE id = 1")
    suspend fun getSavings(): SavingsBalance?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(savings: SavingsBalance)

    @Update
    suspend fun update(savings: SavingsBalance)

    @Query("SELECT balanceCents FROM savings_balance WHERE id = 1")
    suspend fun getBalanceCents(): Long?
}
