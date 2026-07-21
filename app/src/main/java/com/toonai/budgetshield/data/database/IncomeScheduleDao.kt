package com.toonai.budgetshield.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.toonai.budgetshield.data.model.IncomeSchedule
import kotlinx.coroutines.flow.Flow

/**
 * DAO for IncomeSchedule (recurring income rules).
 */
@Dao
interface IncomeScheduleDao {

    @Query("SELECT * FROM income_schedules WHERE isActive = 1")
    fun getActiveSchedulesFlow(): Flow<List<IncomeSchedule>>

    @Query("SELECT * FROM income_schedules WHERE isActive = 1")
    suspend fun getActiveSchedules(): List<IncomeSchedule>

    @Query("SELECT * FROM income_schedules WHERE isActive = 1")
    fun getActiveIncomeFlow(): Flow<List<IncomeSchedule>>

    @Query("SELECT * FROM income_schedules WHERE isActive = 1")
    suspend fun getActiveIncome(): List<IncomeSchedule>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(income: IncomeSchedule): Long

    @Update
    suspend fun update(income: IncomeSchedule)

    @Query("DELETE FROM income_schedules WHERE id = :id")
    suspend fun deleteById(id: Long)
}
