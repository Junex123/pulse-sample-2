package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExtensionDao {
    @Query("SELECT * FROM extensions")
    fun getAllExtensionStates(): Flow<List<ExtensionStateEntity>>

    @Query("SELECT * FROM extensions WHERE id = :id LIMIT 1")
    suspend fun getExtensionStateById(id: String): ExtensionStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertExtensionState(state: ExtensionStateEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefaultExtensionsIfNotExist(states: List<ExtensionStateEntity>)

    @Query("UPDATE extensions SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun setExtensionEnabled(id: String, isEnabled: Boolean)

    @Query("UPDATE extensions SET isInstalled = :isInstalled, isEnabled = :isEnabled WHERE id = :id")
    suspend fun setExtensionInstalledAndEnabled(id: String, isInstalled: Boolean, isEnabled: Boolean)
}
