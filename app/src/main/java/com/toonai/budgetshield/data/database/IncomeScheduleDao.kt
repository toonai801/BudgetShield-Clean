package com.toonai.budgetshield.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.toonai.budgetshield.data.model.IncomeSchedule
import kotlinx.coroutines.flow.Flow

/**
 * DAO for IncomeSchedule entity.
 */
@Dao
interface IncomeScheduleDao {

    @Query("SELECT * FROM income_schedules WHERE isActive = 1 ORDER BY nextPaydayDate ASC LIMIT 1")
    fun getActiveSchedule(): Flow<IncomeSchedule?>

    @Query("SELECT * FROM income_schedules WHERE isActive = 1 ORDER BY nextPaydayDate ASC")
    fun getAllActiveSchedules(): Flow<List<IncomeSchedule>>

    @Query("SELECT * FROM income_schedules WHERE id = :id LIMIT 1")
    suspend fun getScheduleById(id: Long): IncomeSchedule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: IncomeSchedule): Long

    @Update
    suspend fun updateSchedule(schedule: IncomeSchedule)

    @Query("DELETE FROM income_schedules WHERE id = :id")
    suspend fun deleteSchedule(id: Long)

    @Query("UPDATE income_schedules SET isActive = 0 WHERE id = :id")
    suspend fun deactivateSchedule(id: Long)
}
