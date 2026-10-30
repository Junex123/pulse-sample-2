package com.example.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add isDemo to workout_sessions
        db.execSQL("ALTER TABLE workout_sessions ADD COLUMN isDemo INTEGER NOT NULL DEFAULT 0")

        // Add isDemo to workout_sets
        db.execSQL("ALTER TABLE workout_sets ADD COLUMN isDemo INTEGER NOT NULL DEFAULT 0")

        // Add estimatedOneRepMaxKg to personal_records
        db.execSQL("ALTER TABLE personal_records ADD COLUMN estimatedOneRepMaxKg REAL NOT NULL DEFAULT 0")

        // Add isDemo to nutrition_entries
        db.execSQL("ALTER TABLE nutrition_entries ADD COLUMN isDemo INTEGER NOT NULL DEFAULT 0")

        // Add dateMillis and isDemo to recovery_entries
        db.execSQL("ALTER TABLE recovery_entries ADD COLUMN dateMillis INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE recovery_entries ADD COLUMN isDemo INTEGER NOT NULL DEFAULT 0")

        // Add isDemo to weight_measurements
        db.execSQL("ALTER TABLE weight_measurements ADD COLUMN isDemo INTEGER NOT NULL DEFAULT 0")

        // Add isDemo to endurance_sessions
        db.execSQL("ALTER TABLE endurance_sessions ADD COLUMN isDemo INTEGER NOT NULL DEFAULT 0")

        // Add heightUnit and isAiConsentGranted to user_profile
        db.execSQL("ALTER TABLE user_profile ADD COLUMN heightUnit TEXT NOT NULL DEFAULT 'cm'")
        db.execSQL("ALTER TABLE user_profile ADD COLUMN isAiConsentGranted INTEGER NOT NULL DEFAULT 0")

        // Create app_config table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS app_config (
                `key` TEXT NOT NULL PRIMARY KEY,
                `value` TEXT NOT NULL
            )
            """.trimIndent()
        )

        // Create active_workout_draft table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS active_workout_draft (
                `id` TEXT NOT NULL PRIMARY KEY,
                `title` TEXT NOT NULL,
                `routineId` TEXT,
                `startTimeMillis` INTEGER NOT NULL,
                `notes` TEXT NOT NULL,
                `setsJson` TEXT NOT NULL
            )
            """.trimIndent()
        )

        // Create habit_completions table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS habit_completions (
                `habitId` TEXT NOT NULL,
                `dateIso` TEXT NOT NULL,
                `completedAtMillis` INTEGER NOT NULL,
                PRIMARY KEY(`habitId`, `dateIso`)
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add isDemo to personal_records
        db.execSQL("ALTER TABLE personal_records ADD COLUMN isDemo INTEGER NOT NULL DEFAULT 0")
    }
}
