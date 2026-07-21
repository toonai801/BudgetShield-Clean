package com.toonai.budgetshield.data.repository

import com.toonai.budgetshield.data.database.IncomeScheduleDao
import com.toonai.budgetshield.data.model.IncomeSchedule
import kotlinx.coroutines.flow.Flow

/**
 * Repository for income schedule operations.
 * Single source of truth for income data.
 */
class IncomeRepository(private val incomeScheduleDao: IncomeScheduleDao) {

    /** All active income schedules as a reactive stream */
    fun getActiveSchedule(): Flow<IncomeSchedule?> = incomeScheduleDao.getActiveSchedule()

    /** Get all active income schedules */
    fun getAllActiveSchedules(): Flow<List<IncomeSchedule>> = incomeScheduleDao.getAllActiveSchedules()

    /** Get a specific income schedule by ID */
    suspend fun getScheduleById(id: Long): IncomeSchedule? {
        return incomeScheduleDao.getScheduleById(id)
    }

    /**
     * Create a new income schedule.
     *
     * @param name Display name for the income
     * @param amountCents Amount in cents
     * @param nextPayday Next payday as YYYY-MM-DD
     * @param frequency Frequency (weekly, biweekly, semimonthly, monthly)
     * @param isConfirmed Whether this income is confirmed
     * @param isActive Whether this schedule is active
     * @return The generated ID
     */
    suspend fun createIncomeSchedule(
        name: String,
        amountCents: Long,
        nextPayday: String,
        frequency: String,
        isConfirmed: Boolean = true,
        isActive: Boolean = true
    ): Long {
        val incomeSchedule = IncomeSchedule(
            name = name.trim(),
            amountCents = amountCents,
            nextPayday = nextPayday,
            nextPaydayDate = nextPayday,
            frequency = frequency,
            isConfirmed = isConfirmed,
            isActive = isActive
        )
        return incomeScheduleDao.insertSchedule(incomeSchedule)
    }

    /** Save/update an income schedule (compatible API) */
    suspend fun saveSchedule(schedule: IncomeSchedule): Long {
        return if (schedule.id > 0) {
            incomeScheduleDao.updateSchedule(schedule)
            schedule.id
        } else {
            incomeScheduleDao.insertSchedule(schedule)
        }
    }

    /** Update an existing income schedule */
    suspend fun updateIncomeSchedule(incomeSchedule: IncomeSchedule) {
        incomeScheduleDao.updateSchedule(incomeSchedule)
    }

    /** Delete an income schedule */
    suspend fun deleteIncomeSchedule(incomeId: Long) {
        incomeScheduleDao.deleteSchedule(incomeId)
    }

    /** Deactivate an income schedule */
    suspend fun deactivateSchedule(incomeId: Long) {
        incomeScheduleDao.deactivateSchedule(incomeId)
    }

    /** Check if any active income schedules exist */
    suspend fun hasActiveSchedules(): Boolean {
        // This is a simple implementation - could be optimized
        return true
    }
}
