package com.example.data.model

data class WorkoutRoutine(
    val id: String,
    val name: String,
    val description: String,
    val category: String, // "Full Body", "Upper Body", "Lower Body", "Push", "Pull"
    val exerciseIds: List<String>,
    val estimatedMinutes: Int = 45,
    val isCustom: Boolean = false
)

data class WorkoutSet(
    val id: Long = 0,
    val setNumber: Int,
    val exerciseId: String,
    val exerciseName: String,
    val weightKg: Float,
    val reps: Int,
    val rpe: Int = 8, // Rate of Perceived Exertion (1-10)
    val isCompleted: Boolean = false,
    val isDemo: Boolean = false
)

data class WorkoutSession(
    val id: String,
    val title: String,
    val routineId: String?,
    val startTimeMillis: Long,
    val endTimeMillis: Long = 0,
    val durationMinutes: Int = 0,
    val notes: String = "",
    val sets: List<WorkoutSet> = emptyList(),
    val totalVolumeKg: Float = 0f,
    val isCompleted: Boolean = false,
    val isDemo: Boolean = false
)

data class PersonalRecord(
    val exerciseId: String,
    val exerciseName: String,
    val maxWeightKg: Float,
    val maxReps: Int,
    val estimatedOneRepMaxKg: Float = 0f,
    val achievedDateMillis: Long,
    val isDemo: Boolean = false
)

val DEFAULT_ROUTINES = listOf(
    WorkoutRoutine(
        id = "routine_starter_fullbody",
        name = "Full Body Starter",
        description = "Ideal 3x weekly starter foundation focusing on compound movement patterns.",
        category = "Full Body",
        exerciseIds = listOf("goblet_squat", "dumbbell_bench_press", "dumbbell_row", "plank"),
        estimatedMinutes = 35
    ),
    WorkoutRoutine(
        id = "routine_upper_power",
        name = "Upper Body Strength",
        description = "Comprehensive chest, back, and shoulder power routine.",
        category = "Upper Body",
        exerciseIds = listOf("barbell_bench_press", "pull_up", "overhead_press", "dumbbell_row", "bicep_curl"),
        estimatedMinutes = 50
    ),
    WorkoutRoutine(
        id = "routine_lower_power",
        name = "Lower Body Foundation",
        description = "Leg drive focusing on quads, glutes, hamstrings, and core bracing.",
        category = "Lower Body",
        exerciseIds = listOf("barbell_squat", "romanian_deadlift", "goblet_squat", "plank"),
        estimatedMinutes = 45
    ),
    WorkoutRoutine(
        id = "routine_push_hypertrophy",
        name = "Push Hypertrophy",
        description = "Chest, front/lateral deltoids, and tricep hypertrophy volume.",
        category = "Push",
        exerciseIds = listOf("dumbbell_bench_press", "incline_dumbbell_press", "lateral_raise", "tricep_rope_pushdown"),
        estimatedMinutes = 45
    )
)
