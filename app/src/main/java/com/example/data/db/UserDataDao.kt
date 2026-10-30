package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDataDao {
    // App Config / Seed status
    @Query("SELECT * FROM app_config WHERE key = :key LIMIT 1")
    suspend fun getConfig(key: String): AppConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setConfig(config: AppConfigEntity)

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileSync(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserProfile(profile: UserProfileEntity)

    // Nutrition
    @Query("SELECT * FROM nutrition_entries ORDER BY dateMillis DESC")
    fun getAllNutritionEntries(): Flow<List<NutritionEntryEntity>>

    @Query("SELECT * FROM nutrition_entries WHERE isDemo = 0 ORDER BY dateMillis DESC")
    fun getRealNutritionEntries(): Flow<List<NutritionEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutritionEntry(entry: NutritionEntryEntity)

    @Query("DELETE FROM nutrition_entries WHERE id = :id")
    suspend fun deleteNutritionEntry(id: Long)

    @Query("DELETE FROM nutrition_entries WHERE isDemo = 1")
    suspend fun purgeDemoNutrition()

    // Recovery
    @Query("SELECT * FROM recovery_entries ORDER BY dateMillis DESC")
    fun getAllRecoveryEntries(): Flow<List<RecoveryEntryEntity>>

    @Query("SELECT * FROM recovery_entries WHERE isDemo = 0 ORDER BY dateMillis DESC")
    fun getRealRecoveryEntries(): Flow<List<RecoveryEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecoveryEntry(entry: RecoveryEntryEntity)

    @Query("DELETE FROM recovery_entries WHERE isDemo = 1")
    suspend fun purgeDemoRecovery()

    // Habits
    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHabit(habit: HabitEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefaultHabitsIfNotExist(habits: List<HabitEntity>)

    @Query("UPDATE habits SET currentCount = :count, isCompletedToday = :isCompleted WHERE id = :id")
    suspend fun updateHabitProgress(id: String, count: Int, isCompleted: Boolean)

    // Habit Date-based Completions
    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY dateIso DESC")
    fun getCompletionsForHabit(habitId: String): Flow<List<HabitCompletionEntity>>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY dateIso DESC")
    suspend fun getCompletionsForHabitSync(habitId: String): List<HabitCompletionEntity>

    @Query("SELECT * FROM habit_completions")
    fun getAllHabitCompletions(): Flow<List<HabitCompletionEntity>>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND dateIso = :dateIso LIMIT 1")
    suspend fun getHabitCompletionForDate(habitId: String, dateIso: String): HabitCompletionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitCompletion(completion: HabitCompletionEntity)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND dateIso = :dateIso")
    suspend fun deleteHabitCompletion(habitId: String, dateIso: String)

    // Weight Measurements
    @Query("SELECT * FROM weight_measurements ORDER BY dateMillis DESC")
    fun getAllWeightMeasurements(): Flow<List<WeightMeasurementEntity>>

    @Query("SELECT * FROM weight_measurements WHERE isDemo = 0 ORDER BY dateMillis DESC")
    fun getRealWeightMeasurements(): Flow<List<WeightMeasurementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightMeasurement(measurement: WeightMeasurementEntity)

    @Query("DELETE FROM weight_measurements WHERE isDemo = 1")
    suspend fun purgeDemoWeight()

    // Endurance Sessions
    @Query("SELECT * FROM endurance_sessions ORDER BY dateMillis DESC")
    fun getAllEnduranceSessions(): Flow<List<EnduranceSessionEntity>>

    @Query("SELECT * FROM endurance_sessions WHERE isDemo = 0 ORDER BY dateMillis DESC")
    fun getRealEnduranceSessions(): Flow<List<EnduranceSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnduranceSession(session: EnduranceSessionEntity)

    @Query("DELETE FROM endurance_sessions WHERE isDemo = 1")
    suspend fun purgeDemoEndurance()

    @Query("UPDATE user_profile SET isDarkMode = :isDark WHERE id = 1")
    suspend fun updateTheme(isDark: Boolean)

    @Query("UPDATE user_profile SET isDemoDataEnabled = :enabled WHERE id = 1")
    suspend fun updateDemoDataEnabled(enabled: Boolean)

    @Query("UPDATE user_profile SET isAiConsentGranted = :granted WHERE id = 1")
    suspend fun updateAiConsent(granted: Boolean)

    @Query("UPDATE user_profile SET weightKg = :weightKg WHERE id = 1")
    suspend fun updateWeight(weightKg: Float)

    @Query("UPDATE user_profile SET visibleDashboardCardsCsv = :cardsCsv WHERE id = 1")
    suspend fun updateDashboardCards(cardsCsv: String)

    // Total Data Deletion for Privacy / User Reset
    @Query("DELETE FROM nutrition_entries")
    suspend fun deleteAllNutrition()

    @Query("DELETE FROM recovery_entries")
    suspend fun deleteAllRecovery()

    @Query("DELETE FROM habit_completions")
    suspend fun deleteAllHabitCompletions()

    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits()

    @Query("DELETE FROM weight_measurements")
    suspend fun deleteAllWeight()

    @Query("DELETE FROM endurance_sessions")
    suspend fun deleteAllEndurance()

    @Query("DELETE FROM app_config")
    suspend fun deleteAllConfig()

    @androidx.room.Transaction
    suspend fun wipeAllUserDataTransaction() {
        deleteAllNutrition()
        deleteAllRecovery()
        deleteAllHabitCompletions()
        deleteAllHabits()
        deleteAllWeight()
        deleteAllEndurance()
        deleteAllConfig()
    }
}
