package com.toonai.budgetshield.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.toonai.budgetshield.data.model.SetupDraft
import kotlinx.coroutines.flow.Flow

/**
 * DAO for setup draft persistence.
 * Allows process-death resume of incomplete setup.
 */
@Dao
interface SetupDraftDao {

    @Query("SELECT * FROM setup_drafts WHERE id = 1")
    fun getDraft(): Flow<SetupDraft?>

    @Query("SELECT * FROM setup_drafts WHERE id = 1")
    suspend fun getDraftSync(): SetupDraft?

    /**
     * Synchronous blocking version - safe for test environments.
     * Room allows this if called from a background thread.
     */
    @Query("SELECT * FROM setup_drafts WHERE id = 1")
    fun getDraftBlocking(): SetupDraft?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDraft(draft: SetupDraft)

    /**
     * Synchronous blocking version - safe for test environments.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveDraftBlocking(draft: SetupDraft)

    @Query("DELETE FROM setup_drafts WHERE id = 1")
    suspend fun clearDraft()

    /**
     * Synchronous blocking version - safe for test environments.
     */
    @Query("DELETE FROM setup_drafts WHERE id = 1")
    fun clearDraftBlocking()
}
