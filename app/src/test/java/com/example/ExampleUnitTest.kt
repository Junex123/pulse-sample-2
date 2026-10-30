package com.example

import com.example.data.model.DEFAULT_EXERCISES
import com.example.data.model.DEFAULT_FOOD_DATABASE
import com.example.data.model.DEFAULT_ROUTINES
import com.example.data.model.EXTENSION_REGISTRY
import com.example.data.model.WorkoutSet
import com.example.util.DateUtils
import com.example.util.UnitConverter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun defaultExercises_arePopulated() {
        assertTrue("Exercises should be populated", DEFAULT_EXERCISES.isNotEmpty())
        val benchPress = DEFAULT_EXERCISES.find { it.id == "barbell_bench_press" }
        assertNotNull(benchPress)
        assertEquals("Chest", benchPress?.targetMuscle)
    }

    @Test
    fun defaultRoutines_arePopulated() {
        assertTrue(DEFAULT_ROUTINES.isNotEmpty())
        val fullBody = DEFAULT_ROUTINES.find { it.id == "routine_starter_fullbody" }
        assertNotNull(fullBody)
    }

    @Test
    fun extensionRegistry_containsCoreModules() {
        val extensionIds = EXTENSION_REGISTRY.map { it.id }
        assertTrue(extensionIds.contains("ext_nutrition"))
        assertTrue(extensionIds.contains("ext_recovery"))
        assertTrue(extensionIds.contains("ext_habits"))
        assertTrue(extensionIds.contains("ext_analytics"))
        assertTrue(extensionIds.contains("ext_endurance"))
        assertTrue(extensionIds.contains("ext_anatomy"))
    }

    @Test
    fun volumeCalculation_isAccurate() {
        val sets = listOf(
            WorkoutSet(setNumber = 1, exerciseId = "test", exerciseName = "Test", weightKg = 60f, reps = 10, isCompleted = true),
            WorkoutSet(setNumber = 2, exerciseId = "test", exerciseName = "Test", weightKg = 70f, reps = 8, isCompleted = true),
            WorkoutSet(setNumber = 3, exerciseId = "test", exerciseName = "Test", weightKg = 80f, reps = 5, isCompleted = false)
        )
        val completedVolume = sets.filter { it.isCompleted }.fold(0f) { acc, s -> acc + (s.weightKg * s.reps) }
        assertEquals(1160f, completedVolume, 0.01f)
    }

    @Test
    fun epleyOneRepMax_calculation() {
        // 1 rep of 100 kg is exactly 100 kg
        val oneRep = UnitConverter.epleyOneRepMax(100f, 1)
        assertEquals(100f, oneRep, 0.01f)

        // 10 reps of 100 kg: 100 * (1 + 10/30) = 133.33 kg
        val tenReps = UnitConverter.epleyOneRepMax(100f, 10)
        assertEquals(133.33f, tenReps, 0.05f)

        // 0 weight or 0 reps
        assertEquals(0f, UnitConverter.epleyOneRepMax(0f, 10), 0.01f)
        assertEquals(0f, UnitConverter.epleyOneRepMax(100f, 0), 0.01f)
    }

    @Test
    fun unitConversions_accurate() {
        // 70 kg -> 154.32 lbs
        val lbs = UnitConverter.kgToLbs(70f)
        assertEquals(154.32f, lbs, 0.1f)

        // 154.32 lbs -> 70 kg
        val kg = UnitConverter.lbsToKg(lbs)
        assertEquals(70f, kg, 0.1f)

        // 178 cm -> 5 feet 10 inches
        val (ft, inch) = UnitConverter.cmToFeetInches(178f)
        assertEquals(5, ft)
        assertEquals(10, inch)
    }

    @Test
    fun habitStreak_consecutiveDaysCalculation() {
        val today = "2026-10-25"
        val yesterday = "2026-10-24"
        val twoDaysAgo = "2026-10-23"
        val gapDay = "2026-10-20"

        // Empty list -> 0 streak
        assertEquals(0, DateUtils.calculateConsecutiveStreak(emptyList(), today))

        // Only today -> 1 day streak
        assertEquals(1, DateUtils.calculateConsecutiveStreak(listOf(today), today))

        // Multiple entries on same day (duplicate prevention) -> still 1
        assertEquals(1, DateUtils.calculateConsecutiveStreak(listOf(today, today, today), today))

        // 3 consecutive days ending today -> 3
        val threeConsecutive = listOf(today, yesterday, twoDaysAgo)
        assertEquals(3, DateUtils.calculateConsecutiveStreak(threeConsecutive, today))

        // Streak maintained from yesterday when not yet completed today -> 2
        val yesterdayStreak = listOf(yesterday, twoDaysAgo)
        assertEquals(2, DateUtils.calculateConsecutiveStreak(yesterdayStreak, today))

        // Broken streak with gap before twoDaysAgo
        val brokenStreak = listOf(today, yesterday, gapDay)
        assertEquals(2, DateUtils.calculateConsecutiveStreak(brokenStreak, today))
    }

    @Test
    fun foodDatabase_containsStaplesAndIndianFoods() {
        assertTrue(DEFAULT_FOOD_DATABASE.size >= 15)
        val paneer = DEFAULT_FOOD_DATABASE.find { it.name.contains("Paneer", ignoreCase = true) }
        val dal = DEFAULT_FOOD_DATABASE.find { it.name.contains("Dal", ignoreCase = true) }
        val roti = DEFAULT_FOOD_DATABASE.find { it.name.contains("Roti", ignoreCase = true) }
        val whey = DEFAULT_FOOD_DATABASE.find { it.name.contains("Whey", ignoreCase = true) }

        assertNotNull("Paneer should exist in food DB", paneer)
        assertNotNull("Dal should exist in food DB", dal)
        assertNotNull("Roti should exist in food DB", roti)
        assertNotNull("Whey Protein should exist in food DB", whey)
        assertTrue((paneer?.proteinGrams ?: 0) > 10)
    }
}
