package com.inseong.gitgrass

import java.time.LocalDate

/**
 * Generates a sequential list of dates from [start] to [end] (inclusive).
 *
 * Returns an empty list if [start] is after [end].
 */
internal fun generateDayList(start: LocalDate, end: LocalDate): List<LocalDate> {
    if (start.isAfter(end)) return emptyList()

    val days = mutableListOf<LocalDate>()
    var current = start
    while (!current.isAfter(end)) {
        days.add(current)
        current = current.plusDays(1)
    }
    return days
}

/**
 * Builds a grid of weeks (columns) where each week has 7 rows (Mon=0, Sun=6).
 *
 * The first and last weeks may contain `null` entries for days outside the
 * [start, end] range, ensuring the grid always has complete 7-day columns.
 *
 * @return List of weeks, each week being a list of 7 nullable [LocalDate]s.
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
 * Determines where each month label should be placed above the grid.
 *
 * Scans the grid left-to-right and emits a position entry whenever the month
 * changes. Each entry is a pair of (weekIndex, monthNumber) where monthNumber
 * follows [java.time.LocalDate.getMonthValue] (1=Jan, 12=Dec).
 */
internal fun createMonthLabels(grid: List<List<LocalDate?>>): List<Pair<Int, Int>> {
    val result = mutableListOf<Pair<Int, Int>>()
    var lastMonth = -1

    for ((weekIndex, week) in grid.withIndex()) {
        val firstDay = week.firstNotNullOfOrNull { it } ?: continue
        val month = firstDay.monthValue
        if (month != lastMonth) {
            result.add(weekIndex to month)
            lastMonth = month
        }
    }

    return result
}

/**
 * Formats a year label string for the given date range.
 *
 * Returns `"2025"` when both dates fall in the same year,
 * or `"2024 - 2025"` when they span multiple years.
 */
internal fun formatYearLabel(start: LocalDate, end: LocalDate): String {
    return if (start.year == end.year) {
        "${start.year}"
    } else {
        "${start.year} - ${end.year}"
    }
}

/**
 * Calculates max and current contribution streaks.
 *
 * A streak is a sequence of consecutive days with at least one contribution.
 * The current streak counts backward from [today] (or the last day in [days]
 * that is not after [today]).
 *
 * @param contributions Map of dates to contribution counts.
 * @param days Sorted list of dates to examine.
 * @param today Reference date for current streak calculation.
 */
internal fun calculateStreak(
    contributions: Map<LocalDate, Int>,
    days: List<LocalDate>,
    today: LocalDate = LocalDate.now(),
): GitGrassStreakInfo {
    if (days.isEmpty()) return GitGrassStreakInfo(maxStreak = 0, currentStreak = 0)

    // Max streak: longest consecutive run of contributing days
    var maxStreak = 0
    var runLength = 0
    for (day in days) {
        if ((contributions[day] ?: 0) > 0) {
            runLength++
            if (runLength > maxStreak) maxStreak = runLength
        } else {
            runLength = 0
        }
    }

    // Current streak: count backward from today
    var currentStreak = 0
    for (i in days.indices.reversed()) {
        val day = days[i]
        if (day.isAfter(today)) continue
        if ((contributions[day] ?: 0) > 0) {
            currentStreak++
        } else {
            break
        }
    }

    return GitGrassStreakInfo(maxStreak = maxStreak, currentStreak = currentStreak)
}
