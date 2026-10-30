package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM app_config WHERE key = :key LIMIT 1")
    suspend fun getConfig(key: String): AppConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setConfig(config: AppConfigEntity)

    @Transaction
    @Query("SELECT * FROM workout_sessions ORDER BY startTimeMillis DESC")
    fun getAllWorkoutsWithSets(): Flow<List<WorkoutWithSets>>

    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE isDemo = 0 ORDER BY startTimeMillis DESC")
    fun getRealWorkoutsWithSets(): Flow<List<WorkoutWithSets>>

    @Query("SELECT * FROM workout_sessions ORDER BY startTimeMillis DESC")
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions WHERE isDemo = 0 ORDER BY startTimeMillis DESC")
    fun getRealSessionsOnly(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: String): WorkoutSessionEntity?

    @Query("SELECT * FROM workout_sets WHERE sessionId = :sessionId ORDER BY id ASC")
    fun getSetsForSession(sessionId: String): Flow<List<WorkoutSetEntity>>

    @Query("SELECT * FROM workout_sets WHERE sessionId = :sessionId ORDER BY id ASC")
    suspend fun getSetsForSessionSync(sessionId: String): List<WorkoutSetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<WorkoutSetEntity>)

    @Query("DELETE FROM workout_sets WHERE sessionId = :sessionId")
    suspend fun deleteSetsForSession(sessionId: String)

    @Query("DELETE FROM workout_sessions WHERE isDemo = 1")
    suspend fun purgeDemoSessions()

    @Query("DELETE FROM workout_sets WHERE isDemo = 1")
    suspend fun purgeDemoSets()

    @Query("DELETE FROM personal_records WHERE isDemo = 1")
    suspend fun purgeDemoPersonalRecords()

    @Query("DELETE FROM workout_sessions")
    suspend fun deleteAllSessions()

    @Query("DELETE FROM workout_sets")
    suspend fun deleteAllSets()

    @Transaction
    suspend fun completeWorkoutTransaction(
        session: WorkoutSessionEntity,
        sets: List<WorkoutSetEntity>,
        personalRecords: List<PersonalRecordEntity>
    ) {
        insertSession(session)
        insertSets(sets)
        personalRecords.forEach { pr ->
            insertPersonalRecord(pr)
        }
        clearActiveDraft()
    }

    // Active Draft
    @Query("SELECT * FROM active_workout_draft WHERE id = 'active_draft' LIMIT 1")
    suspend fun getActiveDraft(): ActiveWorkoutDraftEntity?

    @Query("SELECT * FROM active_workout_draft WHERE id = 'active_draft' LIMIT 1")
    fun observeActiveDraft(): Flow<ActiveWorkoutDraftEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveActiveDraft(draft: ActiveWorkoutDraftEntity)

    @Query("DELETE FROM active_workout_draft WHERE id = 'active_draft'")
    suspend fun clearActiveDraft()

    // Routines
    @Query("SELECT * FROM workout_routines ORDER BY isCustom DESC, name ASC")
    fun getAllRoutines(): Flow<List<WorkoutRoutineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: WorkoutRoutineEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRoutinesIfNotExist(routines: List<WorkoutRoutineEntity>)

    @Query("DELETE FROM workout_routines WHERE id = :routineId")
    suspend fun deleteRoutine(routineId: String)

    @Query("DELETE FROM workout_routines WHERE isCustom = 1")
    suspend fun deleteAllCustomRoutines()

    // Personal Records
    @Query("SELECT * FROM personal_records ORDER BY achievedDateMillis DESC")
    fun getAllPersonalRecords(): Flow<List<PersonalRecordEntity>>

    @Query("SELECT * FROM personal_records WHERE isDemo = 0 ORDER BY achievedDateMillis DESC")
    fun getRealPersonalRecords(): Flow<List<PersonalRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalRecord(record: PersonalRecordEntity)

    @Query("SELECT * FROM personal_records WHERE exerciseId = :exerciseId LIMIT 1")
    suspend fun getPersonalRecordForExercise(exerciseId: String): PersonalRecordEntity?

    @Query("DELETE FROM personal_records")
    suspend fun deleteAllPersonalRecords()
}
