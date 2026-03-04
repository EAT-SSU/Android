package com.eatssu.android.presentation.util

import java.time.DayOfWeek
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


object CalendarUtil {
    var selectedDate: LocalDate = LocalDate.now()

    fun monthYearFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM")
        return date.format(formatter)
    }

    fun daysInWeekArray(selectedDate: LocalDate): ArrayList<LocalDate> {
        val days = ArrayList<LocalDate>(7)
        var current = sundayForDate(selectedDate)
        repeat(7) {
            days.add(current)
            current = current.plusDays(1)
        }
        return days
    }

    private fun sundayForDate(currentDate: LocalDate): LocalDate {
        var current = currentDate
        repeat(7) {
            if (current.dayOfWeek == DayOfWeek.SUNDAY) return current
            current = current.minusDays(1)
        }

        return currentDate
    }

    fun convertMillisToDateString(
        millis: Long,
        zoneId: ZoneId = ZoneId.systemDefault(),
    ): String {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.getDefault())
        return Instant.ofEpochMilli(millis)
            .atZone(zoneId)
            .toLocalDate()
            .format(formatter)
    }

    fun getNextDayDate(clock: Clock = Clock.systemDefaultZone()): String {
        val nextDay = LocalDate.now(clock).plusDays(1)
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.getDefault())
        return nextDay.format(formatter)
    }
}
