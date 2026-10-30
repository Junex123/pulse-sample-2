package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_config")
data class AppConfigEntity(
    @PrimaryKey val key: String,
    val value: String
)

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val routineId: String?,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationMinutes: Int,
    val notes: String,
    val totalVolumeKg: Float,
    val isCompleted: Boolean,
    val isDemo: Boolean = false
)

@Entity(tableName = "workout_sets")
data class WorkoutSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String,
    val setNumber: Int,
    val exerciseId: String,
    val exerciseName: String,
    val weightKg: Float,
    val reps: Int,
    val rpe: Int,
    val isCompleted: Boolean,
    val isDemo: Boolean = false
)

@Entity(tableName = "active_workout_draft")
data class ActiveWorkoutDraftEntity(
    @PrimaryKey val id: String = "active_draft",
    val title: String,
    val routineId: String?,
    val startTimeMillis: Long,
    val notes: String = "",
    val setsJson: String = "[]"
)

@Entity(tableName = "workout_routines")
data class WorkoutRoutineEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: String,
    val exerciseIdsCsv: String,
    val estimatedMinutes: Int,
    val isCustom: Boolean
)

@Entity(tableName = "personal_records")
data class PersonalRecordEntity(
    @PrimaryKey val exerciseId: String,
    val exerciseName: String,
    val maxWeightKg: Float,
    val maxReps: Int,
    val estimatedOneRepMaxKg: Float = 0f,
    val achievedDateMillis: Long,
    val isDemo: Boolean = false
)

data class WorkoutWithSets(
    @androidx.room.Embedded val session: WorkoutSessionEntity,
    @androidx.room.Relation(
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val sets: List<WorkoutSetEntity>
)

@Entity(tableName = "extensions")
data class ExtensionStateEntity(
    @PrimaryKey val id: String,
    val isInstalled: Boolean,
    val isEnabled: Boolean,
    val installedAtMillis: Long
)

@Entity(tableName = "nutrition_entries")
data class NutritionEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long,
    val mealType: String,
    val foodName: String,
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
    val isDemo: Boolean = false
)

@Entity(tableName = "recovery_entries")
data class RecoveryEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long = System.currentTimeMillis(),
    val dateIso: String,
    val sleepHours: Float,
    val sleepQuality: Int,
    val sorenessLevel: Int,
    val readinessScore: Int,
    val notes: String,
    val isDemo: Boolean = false
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val title: String,
    val targetCount: Int,
    val currentCount: Int,
    val streakDays: Int,
    val isCompletedToday: Boolean
)

@Entity(tableName = "habit_completions", primaryKeys = ["habitId", "dateIso"])
data class HabitCompletionEntity(
    val habitId: String,
    val dateIso: String,
    val completedAtMillis: Long
)

@Entity(tableName = "weight_measurements")
data class WeightMeasurementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long,
    val weightKg: Float,
    val notes: String,
    val isDemo: Boolean = false
)

@Entity(tableName = "endurance_sessions")
data class EnduranceSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long,
    val activityType: String,
    val distanceKm: Float,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val isDemo: Boolean = false
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val goal: String,
    val experienceLevel: String,
    val trainingLocation: String,
    val equipmentAvailable: String,
    val preferredFrequencyDays: Int,
    val weightKg: Float,
    val heightCm: Float,
    val weightUnit: String,
    val heightUnit: String = "cm",
    val hasCompletedOnboarding: Boolean,
    val isDarkMode: Boolean,
    val isDemoDataEnabled: Boolean,
    val isAiConsentGranted: Boolean = false,
    val visibleDashboardCardsCsv: String
)
