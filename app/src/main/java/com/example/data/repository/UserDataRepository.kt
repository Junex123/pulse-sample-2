package com.example.data.repository

import com.example.data.db.AppConfigEntity
import com.example.data.db.EnduranceSessionEntity
import com.example.data.db.HabitCompletionEntity
import com.example.data.db.HabitEntity
import com.example.data.db.NutritionEntryEntity
import com.example.data.db.RecoveryEntryEntity
import com.example.data.db.UserDataDao
import com.example.data.db.UserProfileEntity
import com.example.data.db.WeightMeasurementEntity
import com.example.data.model.EnduranceSession
import com.example.data.model.HabitItem
import com.example.data.model.NutritionEntry
import com.example.data.model.RecoveryEntry
import com.example.data.model.UserProfile
import com.example.data.model.WeightMeasurement
import com.example.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class UserDataRepository(private val userDataDao: UserDataDao) {

    val userProfile: Flow<UserProfile> = userDataDao.getUserProfile().map { entity ->
        if (entity == null) {
            UserProfile()
        } else {
            UserProfile(
                id = entity.id,
                name = entity.name,
                goal = entity.goal,
                experienceLevel = entity.experienceLevel,
                trainingLocation = entity.trainingLocation,
                equipmentAvailable = entity.equipmentAvailable,
                preferredFrequencyDays = entity.preferredFrequencyDays,
                weightKg = entity.weightKg,
                heightCm = entity.heightCm,
                weightUnit = entity.weightUnit,
                heightUnit = entity.heightUnit,
                hasCompletedOnboarding = entity.hasCompletedOnboarding,
                isDarkMode = entity.isDarkMode,
                isDemoDataEnabled = entity.isDemoDataEnabled,
                isAiConsentGranted = entity.isAiConsentGranted,
                visibleDashboardCards = entity.visibleDashboardCardsCsv.split(",").filter { it.isNotBlank() }
            )
        }
    }

    suspend fun getUserProfileSync(): UserProfile? {
        val entity = userDataDao.getUserProfileSync() ?: return null
        return UserProfile(
            id = entity.id,
            name = entity.name,
            goal = entity.goal,
            experienceLevel = entity.experienceLevel,
            trainingLocation = entity.trainingLocation,
            equipmentAvailable = entity.equipmentAvailable,
            preferredFrequencyDays = entity.preferredFrequencyDays,
            weightKg = entity.weightKg,
            heightCm = entity.heightCm,
            weightUnit = entity.weightUnit,
            heightUnit = entity.heightUnit,
            hasCompletedOnboarding = entity.hasCompletedOnboarding,
            isDarkMode = entity.isDarkMode,
            isDemoDataEnabled = entity.isDemoDataEnabled,
            isAiConsentGranted = entity.isAiConsentGranted,
            visibleDashboardCards = entity.visibleDashboardCardsCsv.split(",").filter { it.isNotBlank() }
        )
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        val entity = UserProfileEntity(
            id = profile.id,
            name = profile.name,
            goal = profile.goal,
            experienceLevel = profile.experienceLevel,
            trainingLocation = profile.trainingLocation,
            equipmentAvailable = profile.equipmentAvailable,
            preferredFrequencyDays = profile.preferredFrequencyDays,
            weightKg = profile.weightKg,
            heightCm = profile.heightCm,
            weightUnit = profile.weightUnit,
            heightUnit = profile.heightUnit,
            hasCompletedOnboarding = profile.hasCompletedOnboarding,
            isDarkMode = profile.isDarkMode,
            isDemoDataEnabled = profile.isDemoDataEnabled,
            isAiConsentGranted = profile.isAiConsentGranted,
            visibleDashboardCardsCsv = profile.visibleDashboardCards.joinToString(",")
        )
        userDataDao.upsertUserProfile(entity)
    }

    // Nutrition
    val nutritionEntries: Flow<List<NutritionEntry>> = userDataDao.getAllNutritionEntries().map { list ->
        list.map {
            NutritionEntry(
                id = it.id,
                dateMillis = it.dateMillis,
                mealType = it.mealType,
                foodName = it.foodName,
                calories = it.calories,
                proteinGrams = it.proteinGrams,
                carbsGrams = it.carbsGrams,
                fatGrams = it.fatGrams,
                isDemo = it.isDemo
            )
        }
    }

    val realNutritionEntries: Flow<List<NutritionEntry>> = userDataDao.getRealNutritionEntries().map { list ->
        list.map {
            NutritionEntry(
                id = it.id,
                dateMillis = it.dateMillis,
                mealType = it.mealType,
                foodName = it.foodName,
                calories = it.calories,
                proteinGrams = it.proteinGrams,
                carbsGrams = it.carbsGrams,
                fatGrams = it.fatGrams,
                isDemo = it.isDemo
            )
        }
    }

    suspend fun logNutrition(entry: NutritionEntry) {
        userDataDao.insertNutritionEntry(
            NutritionEntryEntity(
                dateMillis = entry.dateMillis,
                mealType = entry.mealType,
                foodName = entry.foodName,
                calories = entry.calories,
                proteinGrams = entry.proteinGrams,
                carbsGrams = entry.carbsGrams,
                fatGrams = entry.fatGrams,
                isDemo = entry.isDemo
            )
        )
    }

    suspend fun deleteNutrition(id: Long) {
        userDataDao.deleteNutritionEntry(id)
    }

    // Recovery
    val recoveryEntries: Flow<List<RecoveryEntry>> = userDataDao.getAllRecoveryEntries().map { list ->
        list.map {
            RecoveryEntry(
                id = it.id,
                dateMillis = it.dateMillis,
                dateIso = it.dateIso,
                sleepHours = it.sleepHours,
                sleepQuality = it.sleepQuality,
                sorenessLevel = it.sorenessLevel,
                readinessScore = it.readinessScore,
                notes = it.notes,
                isDemo = it.isDemo
            )
        }
    }

    val realRecoveryEntries: Flow<List<RecoveryEntry>> = userDataDao.getRealRecoveryEntries().map { list ->
        list.map {
            RecoveryEntry(
                id = it.id,
                dateMillis = it.dateMillis,
                dateIso = it.dateIso,
                sleepHours = it.sleepHours,
                sleepQuality = it.sleepQuality,
                sorenessLevel = it.sorenessLevel,
                readinessScore = it.readinessScore,
                notes = it.notes,
                isDemo = it.isDemo
            )
        }
    }

    suspend fun logRecovery(entry: RecoveryEntry) {
        userDataDao.insertRecoveryEntry(
            RecoveryEntryEntity(
                dateMillis = entry.dateMillis,
                dateIso = entry.dateIso,
                sleepHours = entry.sleepHours,
                sleepQuality = entry.sleepQuality,
                sorenessLevel = entry.sorenessLevel,
                readinessScore = entry.readinessScore,
                notes = entry.notes,
                isDemo = entry.isDemo
            )
        )
    }

    // Habits & Date-aware Streaks
    val habits: Flow<List<HabitItem>> = userDataDao.getAllHabits()
        .combine(userDataDao.getAllHabitCompletions()) { habitEntities, completions ->
            val todayIso = DateUtils.currentIsoDate()
            val completionsByHabit = completions.groupBy { it.habitId }

            habitEntities.map { entity ->
                val habitCompletions = completionsByHabit[entity.id] ?: emptyList()
                val completedDates = habitCompletions.map { it.dateIso }
                val isCompletedToday = completedDates.contains(todayIso)
                val streak = DateUtils.calculateConsecutiveStreak(completedDates, todayIso)

                HabitItem(
                    id = entity.id,
                    title = entity.title,
                    targetCount = entity.targetCount,
                    currentCount = if (isCompletedToday) entity.targetCount else 0,
                    streakDays = streak,
                    isCompletedToday = isCompletedToday
                )
            }
        }

    suspend fun toggleHabit(habitId: String) {
        val todayIso = DateUtils.currentIsoDate()
        val existing = userDataDao.getHabitCompletionForDate(habitId, todayIso)
        if (existing != null) {
            // Already completed today: uncheck
            userDataDao.deleteHabitCompletion(habitId, todayIso)
        } else {
            // Mark completed today
            userDataDao.insertHabitCompletion(
                HabitCompletionEntity(
                    habitId = habitId,
                    dateIso = todayIso,
                    completedAtMillis = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun addHabit(title: String, targetCount: Int = 1) {
        val id = "habit_" + System.currentTimeMillis()
        userDataDao.upsertHabit(
            HabitEntity(
                id = id,
                title = title,
                targetCount = targetCount,
                currentCount = 0,
                streakDays = 0,
                isCompletedToday = false
            )
        )
    }

    // Weight Measurements
    val weightMeasurements: Flow<List<WeightMeasurement>> = userDataDao.getAllWeightMeasurements().map { list ->
        list.map {
            WeightMeasurement(
                id = it.id,
                dateMillis = it.dateMillis,
                weightKg = it.weightKg,
                notes = it.notes,
                isDemo = it.isDemo
            )
        }
    }

    val realWeightMeasurements: Flow<List<WeightMeasurement>> = userDataDao.getRealWeightMeasurements().map { list ->
        list.map {
            WeightMeasurement(
                id = it.id,
                dateMillis = it.dateMillis,
                weightKg = it.weightKg,
                notes = it.notes,
                isDemo = it.isDemo
            )
        }
    }

    suspend fun logWeight(weightKg: Float, notes: String = "") {
        userDataDao.insertWeightMeasurement(
            WeightMeasurementEntity(
                dateMillis = System.currentTimeMillis(),
                weightKg = weightKg,
                notes = notes,
                isDemo = false
            )
        )
    }

    // Endurance Sessions
    val enduranceSessions: Flow<List<EnduranceSession>> = userDataDao.getAllEnduranceSessions().map { list ->
        list.map {
            EnduranceSession(
                id = it.id,
                dateMillis = it.dateMillis,
                activityType = it.activityType,
                distanceKm = it.distanceKm,
                durationMinutes = it.durationMinutes,
                caloriesBurned = it.caloriesBurned,
                isDemo = it.isDemo
            )
        }
    }

    val realEnduranceSessions: Flow<List<EnduranceSession>> = userDataDao.getRealEnduranceSessions().map { list ->
        list.map {
            EnduranceSession(
                id = it.id,
                dateMillis = it.dateMillis,
                activityType = it.activityType,
                distanceKm = it.distanceKm,
                durationMinutes = it.durationMinutes,
                caloriesBurned = it.caloriesBurned,
                isDemo = it.isDemo
            )
        }
    }

    suspend fun logEndurance(session: EnduranceSession) {
        userDataDao.insertEnduranceSession(
            EnduranceSessionEntity(
                dateMillis = session.dateMillis,
                activityType = session.activityType,
                distanceKm = session.distanceKm,
                durationMinutes = session.durationMinutes,
                caloriesBurned = session.caloriesBurned,
                isDemo = false
            )
        )
    }

    // Purge demo data
    suspend fun purgeDemoData() {
        userDataDao.purgeDemoNutrition()
        userDataDao.purgeDemoRecovery()
        userDataDao.purgeDemoWeight()
        userDataDao.purgeDemoEndurance()
    }

    // Atomic Profile Updates
    suspend fun updateTheme(isDark: Boolean) {
        userDataDao.updateTheme(isDark)
    }

    suspend fun updateDemoDataEnabled(enabled: Boolean) {
        userDataDao.updateDemoDataEnabled(enabled)
    }

    suspend fun updateAiConsent(granted: Boolean) {
        userDataDao.updateAiConsent(granted)
    }

    suspend fun updateWeight(weightKg: Float) {
        userDataDao.updateWeight(weightKg)
    }

    suspend fun updateDashboardCards(cards: List<String>) {
        userDataDao.updateDashboardCards(cards.joinToString(","))
    }

    // Privacy Data Reset
    suspend fun deleteAllUserData() {
        userDataDao.wipeAllUserDataTransaction()
        saveUserProfile(UserProfile(hasCompletedOnboarding = false))
    }

    // Safe Single-Seed Initialization
    suspend fun initializeDefaultDataIfEmpty() {
        val seedVersion = userDataDao.getConfig("seed_version")
        if (seedVersion != null && seedVersion.value == "1") {
            // Already seeded previously. Never duplicate sample data!
            return
        }

        // Seed default habits
        val defaultHabits = listOf(
            HabitEntity(
                id = "habit_water",
                title = "Drink 2.5L Water",
                targetCount = 5,
                currentCount = 0,
                streakDays = 0,
                isCompletedToday = false
            ),
            HabitEntity(
                id = "habit_protein",
                title = "Hit Daily Protein Goal",
                targetCount = 1,
                currentCount = 0,
                streakDays = 0,
                isCompletedToday = false
            ),
            HabitEntity(
                id = "habit_mobility",
                title = "10-min Post-Workout Mobility",
                targetCount = 1,
                currentCount = 0,
                streakDays = 0,
                isCompletedToday = false
            ),
            HabitEntity(
                id = "habit_sleep",
                title = "8 Hours Sleep Target",
                targetCount = 1,
                currentCount = 0,
                streakDays = 0,
                isCompletedToday = false
            )
        )
        userDataDao.insertDefaultHabitsIfNotExist(defaultHabits)

        // Seed initial sample completion records (for streak demo)
        val todayIso = DateUtils.currentIsoDate()
        val yesterdayIso = DateUtils.previousDayIso(todayIso)
        val dayBeforeYesterdayIso = DateUtils.previousDayIso(yesterdayIso)

        if (yesterdayIso.isNotBlank()) {
            userDataDao.insertHabitCompletion(
                HabitCompletionEntity("habit_protein", yesterdayIso, System.currentTimeMillis() - 86400000L)
            )
            userDataDao.insertHabitCompletion(
                HabitCompletionEntity("habit_protein", dayBeforeYesterdayIso, System.currentTimeMillis() - (86400000L * 2))
            )
        }

        // Seed initial sample weight
        userDataDao.insertWeightMeasurement(
            WeightMeasurementEntity(
                dateMillis = System.currentTimeMillis() - (86400000L * 3),
                weightKg = 73.0f,
                notes = "Sample initial log",
                isDemo = true
            )
        )
        userDataDao.insertWeightMeasurement(
            WeightMeasurementEntity(
                dateMillis = System.currentTimeMillis(),
                weightKg = 72.5f,
                notes = "Morning weighed, fasted",
                isDemo = true
            )
        )

        // Seed initial sample nutrition log
        userDataDao.insertNutritionEntry(
            NutritionEntryEntity(
                dateMillis = System.currentTimeMillis() - (3600000L * 4),
                mealType = "Breakfast",
                foodName = "Oatmeal with Whey & Berries (Sample)",
                calories = 460,
                proteinGrams = 38,
                carbsGrams = 58,
                fatGrams = 9,
                isDemo = true
            )
        )
        userDataDao.insertNutritionEntry(
            NutritionEntryEntity(
                dateMillis = System.currentTimeMillis() - (3600000L * 1),
                mealType = "Lunch",
                foodName = "Grilled Chicken Breast with Brown Rice (Sample)",
                calories = 620,
                proteinGrams = 52,
                carbsGrams = 68,
                fatGrams = 12,
                isDemo = true
            )
        )

        // Seed initial recovery log
        userDataDao.insertRecoveryEntry(
            RecoveryEntryEntity(
                dateMillis = System.currentTimeMillis(),
                dateIso = todayIso,
                sleepHours = 7.8f,
                sleepQuality = 4,
                sorenessLevel = 2,
                readinessScore = 84,
                notes = "Felt well-rested. Minor soreness in upper chest.",
                isDemo = true
            )
        )

        // Seed initial endurance log
        userDataDao.insertEnduranceSession(
            EnduranceSessionEntity(
                dateMillis = System.currentTimeMillis() - 86400000L,
                activityType = "Running",
                distanceKm = 5.2f,
                durationMinutes = 28,
                caloriesBurned = 360,
                isDemo = true
            )
        )

        // Record seed completion flag in app_config
        userDataDao.setConfig(AppConfigEntity("seed_version", "1"))
    }
}
