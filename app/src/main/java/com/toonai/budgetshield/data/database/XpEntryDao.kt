package com.toonai.budgetshield.data.database

import androidx.room.*
import com.toonai.budgetshield.data.model.XpEntry
import kotlinx.coroutines.flow.Flow

/**
 * DAO for XP entry operations.
 */
@Dao
interface XpEntryDao {

    @Query("SELECT * FROM xp_entries ORDER BY createdAt DESC")
    fun getAllXpEntries(): Flow<List<XpEntry>>

    @Query("SELECT * FROM xp_entries WHERE entryDate >= :startDate AND entryDate <= :endDate ORDER BY createdAt DESC")
    fun getXpEntriesForDateRange(startDate: String, endDate: String): Flow<List<XpEntry>>

    @Query("SELECT * FROM xp_entries WHERE entryDate = :date ORDER BY createdAt DESC")
    fun getXpEntriesForDate(date: String): Flow<List<XpEntry>>

    @Query("SELECT SUM(amount) FROM xp_entries")
    fun getTotalXp(): Flow<Int?>

    @Query("SELECT SUM(amount) FROM xp_entries WHERE entryDate >= :startDate AND entryDate <= :endDate")
    suspend fun getXpForDateRange(startDate: String, endDate: String): Int?

    @Query("SELECT SUM(amount) FROM xp_entries WHERE entryDate = :date")
    suspend fun getXpForDate(date: String): Int?

    @Query("SELECT SUM(amount) FROM xp_entries WHERE entryDate LIKE :monthKey || '%'")
    fun getXpForMonth(monthKey: String): Flow<Int?>

    @Query("SELECT * FROM xp_entries ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecentXpEntries(limit: Int): List<XpEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertXpEntry(entry: XpEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBlocking(entry: XpEntry): Long

    @Query("DELETE FROM xp_entries WHERE id = :entryId")
    suspend fun deleteXpEntry(entryId: Long)

    @Query("SELECT COUNT(*) FROM xp_entries WHERE activityType = :activityType AND entryDate = :date")
    suspend fun getActivityCountForDate(activityType: String, date: String): Int

    @Query("SELECT * FROM xp_entries WHERE activityType = :activityType ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastActivityOfType(activityType: String): XpEntry?
}
