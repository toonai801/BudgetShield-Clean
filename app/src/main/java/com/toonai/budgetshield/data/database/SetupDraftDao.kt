package com.toonai.budgetshield.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.toonai.budgetshield.data.model.SetupDraft
import kotlinx.coroutines.flow.Flow

/**
 * DAO for SetupDraft (process death resume support).
 */
@Dao
interface SetupDraftDao {

    @Query("SELECT * FROM setup_draft WHERE id = 1")
    fun getDraftFlow(): Flow<SetupDraft?>

    @Query("SELECT * FROM setup_draft WHERE id = 1")
    suspend fun getDraft(): SetupDraft?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDraft(draft: SetupDraft)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(draft: SetupDraft)

    @Update
    suspend fun update(draft: SetupDraft)

    @Query("DELETE FROM setup_draft WHERE id = 1")
    suspend fun clearDraft()
}
