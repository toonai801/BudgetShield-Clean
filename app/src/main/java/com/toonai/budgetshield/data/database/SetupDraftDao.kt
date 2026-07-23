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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDraft(draft: SetupDraft)

    @Query("DELETE FROM setup_drafts WHERE id = 1")
    suspend fun clearDraft()

}
