package com.example.data.model

data class UserProfile(
    val id: Int = 1,
    val name: String = "Alex",
    val goal: String = "Build Muscle",
    val experienceLevel: String = "Beginner",
    val trainingLocation: String = "Gym",
    val equipmentAvailable: String = "Full Equipment",
    val preferredFrequencyDays: Int = 4,
    val weightKg: Float = 72.5f,
    val heightCm: Float = 175f,
    val weightUnit: String = "kg",
    val heightUnit: String = "cm",
    val hasCompletedOnboarding: Boolean = false,
    val isDarkMode: Boolean = false,
    val isDemoDataEnabled: Boolean = true,
    val isAiConsentGranted: Boolean = false,
    val visibleDashboardCards: List<String> = listOf(
        "daily_summary",
        "quick_workout",
        "weight_activity",
        "weekly_progress",
        "recent_tools"
    )
)

data class NutritionEntry(
    val id: Long = 0,
    val dateMillis: Long = System.currentTimeMillis(),
    val mealType: String, // Breakfast, Lunch, Dinner, Snack
    val foodName: String,
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
    val isDemo: Boolean = false
)

data class RecoveryEntry(
    val id: Long = 0,
    val dateMillis: Long = System.currentTimeMillis(),
    val dateIso: String,
    val sleepHours: Float,
    val sleepQuality: Int, // 1 to 5
    val sorenessLevel: Int, // 1 (none) to 5 (severe)
    val readinessScore: Int, // 0 to 100
    val notes: String = "",
    val isDemo: Boolean = false
)

data class HabitItem(
    val id: String,
    val title: String,
    val targetCount: Int = 1,
    val currentCount: Int = 0,
    val streakDays: Int = 0,
    val isCompletedToday: Boolean = false
)

data class WeightMeasurement(
    val id: Long = 0,
    val dateMillis: Long,
    val weightKg: Float,
    val notes: String = "",
    val isDemo: Boolean = false
)

data class EnduranceSession(
    val id: Long = 0,
    val dateMillis: Long,
    val activityType: String, // Running, Cycling, Walking
    val distanceKm: Float,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val isDemo: Boolean = false
) {
    val paceMinutesPerKm: Float
        get() = if (distanceKm > 0f) durationMinutes / distanceKm else 0f
}
