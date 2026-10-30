package com.example.util

import kotlin.math.roundToInt

object UnitConverter {

    const val LBS_PER_KG = 2.20462262f
    const val INCHES_PER_CM = 0.393701f

    fun kgToLbs(kg: Float): Float {
        return (kg * LBS_PER_KG * 10f).roundToInt() / 10f
    }

    fun lbsToKg(lbs: Float): Float {
        return (lbs / LBS_PER_KG * 10f).roundToInt() / 10f
    }

    fun formatWeight(weightKg: Float, unit: String): String {
        return if (unit.equals("lbs", ignoreCase = true)) {
            String.format("%.1f lbs", kgToLbs(weightKg))
        } else {
            String.format("%.1f kg", weightKg)
        }
    }

    fun formatHeight(heightCm: Float, unit: String): String {
        return if (unit.equals("in", ignoreCase = true) || unit.equals("inches", ignoreCase = true)) {
            val totalInches = (heightCm * INCHES_PER_CM).roundToInt()
            val feet = totalInches / 12
            val inches = totalInches % 12
            "$feet' $inches\""
        } else {
            "${heightCm.roundToInt()} cm"
        }
    }

    fun cmToFeetInches(cm: Float): Pair<Int, Int> {
        val totalInches = (cm * INCHES_PER_CM).roundToInt()
        val feet = totalInches / 12
        val inches = totalInches % 12
        return Pair(feet, inches)
    }

    /**
     * Epley formula for estimated 1-Rep Maximum:
     * 1RM = Weight * (1 + Reps / 30)
     */
    fun calculateEpley1RM(weightKg: Float, reps: Int): Float {
        if (weightKg <= 0f || reps <= 0) return 0f
        if (reps == 1) return weightKg
        val oneRm = weightKg * (1f + reps / 30f)
        return (oneRm * 10f).roundToInt() / 10f
    }

    fun epleyOneRepMax(weightKg: Float, reps: Int): Float = calculateEpley1RM(weightKg, reps)

    fun calculateSetVolume(weightKg: Float, reps: Int): Float {
        if (weightKg <= 0f || reps <= 0) return 0f
        return weightKg * reps
    }
}
