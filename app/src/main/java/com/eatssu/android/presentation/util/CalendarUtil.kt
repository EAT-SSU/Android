package com.eatssu.android.presentation.util

import java.time.DayOfWeek
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


object CalendarUtil {
    lateinit var selectedDate: LocalDate

    fun monthYearFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM")
        return date.format(formatter)
    }

    fun daysInWeekArray(selectedDate: LocalDate): ArrayList<LocalDate> {
        val days = ArrayList<LocalDate>()
        var current = sundayForDate(selectedDate)
        val endDate = current!!.plusWeeks(1)
        while (current!!.isBefore(endDate)) {
            days.add(current)
            current = current.plusDays(1)
        }
        return days
    }

    private fun sundayForDate(current: LocalDate): LocalDate? {
        var current = current
        val oneWeekAgo = current.minusWeeks(1)
        while (current.isAfter(oneWeekAgo)) {
            if (current.dayOfWeek == DayOfWeek.SUNDAY) return current
            current = current.minusDays(1)
        }
        return null
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
