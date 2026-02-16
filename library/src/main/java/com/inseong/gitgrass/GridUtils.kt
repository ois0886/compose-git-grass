package com.inseong.gitgrass

import java.time.DayOfWeek
import java.time.LocalDate

internal fun generateDayList(start: LocalDate, end: LocalDate): List<LocalDate> {
    val days = mutableListOf<LocalDate>()
    var current = start
    while (!current.isAfter(end)) {
        days.add(current)
        current = current.plusDays(1)
    }
    return days
}

/**
 * Builds a grid of weeks (columns) where each week has 7 rows (Mon=0 … Sun=6).
 * The first and last weeks may contain `null` for days outside the range.
 */
internal fun buildGrid(days: List<LocalDate>): List<List<LocalDate?>> {
    if (days.isEmpty()) return emptyList()

    val weeks = mutableListOf<MutableList<LocalDate?>>()
    var currentWeek = MutableList<LocalDate?>(7) { null }

    for (day in days) {
        val dayIndex = day.dayOfWeek.value - 1 // Monday=0, Sunday=6
        if (dayIndex == 0 && currentWeek.any { it != null }) {
            weeks.add(currentWeek)
            currentWeek = MutableList(7) { null }
        }
        currentWeek[dayIndex] = day
    }

    if (currentWeek.any { it != null }) {
        weeks.add(currentWeek)
    }

    return weeks
}

/**
 * Returns a list of (weekIndex, monthNumber) pairs indicating where each month label should appear.
 * A month label is placed at the first week that contains a day in that month.
 */
internal fun createMonthLabels(grid: List<List<LocalDate?>>): List<Pair<Int, Int>> {
    val result = mutableListOf<Pair<Int, Int>>()
    var lastMonth = -1

    for ((weekIndex, week) in grid.withIndex()) {
        val firstDay = week.filterNotNull().firstOrNull() ?: continue
        val month = firstDay.monthValue
        if (month != lastMonth) {
            result.add(weekIndex to month)
            lastMonth = month
        }
    }

    return result
}

internal fun formatYearLabel(start: LocalDate, end: LocalDate): String {
    return if (start.year == end.year) {
        "${start.year}"
    } else {
        "${start.year} - ${end.year}"
    }
}

internal fun calculateStreak(
    contributions: Map<LocalDate, Int>,
    days: List<LocalDate>,
): GitGrassStreakInfo {
    var maxStreak = 0
    var currentStreak = 0
    var tempStreak = 0

    for (day in days) {
        val count = contributions[day] ?: 0
        if (count > 0) {
            tempStreak++
            if (tempStreak > maxStreak) {
                maxStreak = tempStreak
            }
        } else {
            tempStreak = 0
        }
    }

    // Current streak: count consecutive contribution days ending at today or the last day
    val today = LocalDate.now()
    val reversedDays = days.filter { !it.isAfter(today) }.sortedDescending()
    for (day in reversedDays) {
        val count = contributions[day] ?: 0
        if (count > 0) {
            currentStreak++
        } else {
            break
        }
    }

    return GitGrassStreakInfo(
        maxStreak = maxStreak,
        currentStreak = currentStreak,
    )
}
