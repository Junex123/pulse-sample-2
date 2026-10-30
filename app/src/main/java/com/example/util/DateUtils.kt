package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    private val isoDateFormat: SimpleDateFormat
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getDefault()
        }

    fun currentIsoDate(): String {
        return isoDateFormat.format(Date())
    }

    fun toIsoDate(epochMillis: Long): String {
        return isoDateFormat.format(Date(epochMillis))
    }

    fun formatEpochDate(epochMillis: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(epochMillis))
    }

    fun formatEpochDateTime(epochMillis: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
        return sdf.format(Date(epochMillis))
    }

    fun previousDayIso(dateIso: String): String {
        return try {
            val cal = Calendar.getInstance()
            cal.time = isoDateFormat.parse(dateIso) ?: Date()
            cal.add(Calendar.DAY_OF_YEAR, -1)
            isoDateFormat.format(cal.time)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Calculates the consecutive streak of completed dates leading up to today (or yesterday).
     * Prevents multiple completions on the same day from inflating streak.
     */
    fun calculateConsecutiveStreak(completedDates: Collection<String>, todayIso: String = currentIsoDate()): Int {
        if (completedDates.isEmpty()) return 0

        val dateSet = completedDates.toSet()
        var streak = 0
        var checkDate = todayIso

        // If not completed today, check if yesterday was completed
        if (!dateSet.contains(checkDate)) {
            val yesterday = previousDayIso(todayIso)
            if (!dateSet.contains(yesterday)) {
                return 0
            }
            checkDate = yesterday
        }

        // Count backwards consecutive days
        while (dateSet.contains(checkDate) && checkDate.isNotBlank()) {
            streak++
            checkDate = previousDayIso(checkDate)
        }

        return streak
    }
}
