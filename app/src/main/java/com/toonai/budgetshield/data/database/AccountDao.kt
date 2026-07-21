package com.toonai.budgetshield.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.toonai.budgetshield.data.model.Account
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Account (cash source).
 */
@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts WHERE isDefault = 1 LIMIT 1")
    fun getDefaultAccountFlow(): Flow<Account?>

    @Query("SELECT * FROM accounts WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultAccount(): Account?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: Account): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAndReturnId(account: Account): Long

    @Update
    suspend fun update(account: Account)

    @Query("SELECT openingBalanceCents FROM accounts WHERE isDefault = 1 LIMIT 1")
    suspend fun getCashOnHandCents(): Long?
}
