package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        AppConfigEntity::class,
        WorkoutSessionEntity::class,
        WorkoutSetEntity::class,
        ActiveWorkoutDraftEntity::class,
        WorkoutRoutineEntity::class,
        PersonalRecordEntity::class,
        ExtensionStateEntity::class,
        NutritionEntryEntity::class,
        RecoveryEntryEntity::class,
        HabitEntity::class,
        HabitCompletionEntity::class,
        WeightMeasurementEntity::class,
        EnduranceSessionEntity::class,
        UserProfileEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class PulseDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun extensionDao(): ExtensionDao
    abstract fun userDataDao(): UserDataDao

    companion object {
        @Volatile
        private var INSTANCE: PulseDatabase? = null

        fun getInstance(context: Context): PulseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PulseDatabase::class.java,
                    "pulse_health.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
