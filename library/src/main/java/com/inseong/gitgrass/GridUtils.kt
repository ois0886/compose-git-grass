package com.inseong.gitgrass

import com.inseong.gitgrass.GitGrassDefaults.DAYS_PER_WEEK
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Normalizes a date range by swapping if [start] is after [end].
 *
 * @return Pair of (normalizedStart, normalizedEnd) where start <= end.
 */
internal fun normalizeDateRange(
    start: LocalDate,
    end: LocalDate,
): Pair<LocalDate, LocalDate> {
    return if (start.isAfter(end)) end to start else start to end
}

/**
 * Clamps all negative contribution counts to 0.
 *
 * Returns the original map unchanged if no negative values exist.
 */
internal fun normalizeContributions(
    contributions: ContributionData,
): ContributionData {
    return contributions.mapValues { (_, v) -> v.coerceAtLeast(0) }
}

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
 * Builds a grid of weeks (columns) where each week has [DAYS_PER_WEEK] rows.
 *
 * The first and last weeks may contain `null` entries for days outside the
 * [start, end] range, ensuring the grid always has complete 7-day columns.
 *
 * @param days Sorted list of dates to arrange into weeks.
 * @param weekStartDay The first day of the week (default: Monday).
 * @return List of weeks, each week being a list of [DAYS_PER_WEEK] nullable [LocalDate]s.
 */
internal fun buildGrid(
    days: List<LocalDate>,
    weekStartDay: DayOfWeek = DayOfWeek.MONDAY,
): Grid {
    if (days.isEmpty()) return emptyList()

    val weeks = mutableListOf<MutableList<LocalDate?>>()
    var currentWeek = MutableList<LocalDate?>(DAYS_PER_WEEK) { null }

    for (day in days) {
        val dayIndex = dayIndexInWeek(day.dayOfWeek, weekStartDay)
        if (dayIndex == 0 && currentWeek.any { it != null }) {
            weeks.add(currentWeek)
            currentWeek = MutableList(DAYS_PER_WEEK) { null }
        }
        currentWeek[dayIndex] = day
    }

    if (currentWeek.any { it != null }) {
        weeks.add(currentWeek)
    }

    return weeks
}

/**
 * Calculates the index of [dayOfWeek] within a week starting on [weekStartDay].
 *
 * For example, if weekStartDay is MONDAY, then Monday=0, Tuesday=1, ..., Sunday=6.
 * If weekStartDay is SUNDAY, then Sunday=0, Monday=1, ..., Saturday=6.
 */
internal fun dayIndexInWeek(dayOfWeek: DayOfWeek, weekStartDay: DayOfWeek): Int {
    return (dayOfWeek.value - weekStartDay.value + DAYS_PER_WEEK) % DAYS_PER_WEEK
}

/**
 * Returns an ordered list of [DayOfWeek] starting from [weekStartDay].
 */
internal fun weekDaysOrdered(weekStartDay: DayOfWeek): List<DayOfWeek> {
    return (0 until DAYS_PER_WEEK).map { offset ->
        DayOfWeek.of((weekStartDay.value - 1 + offset) % DAYS_PER_WEEK + 1)
    }
}

/**
 * Determines where each month label should be placed above the grid.
 *
 * Scans the grid left-to-right and emits a position entry whenever the month
 * changes. Each entry is a pair of (weekIndex, monthNumber) where monthNumber
 * follows [java.time.LocalDate.getMonthValue] (1=Jan, 12=Dec).
 */
internal fun createMonthLabels(grid: Grid): MonthPositions {
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
 * Calculates max and current contribution streaks in a single forward pass.
 *
 * A streak is a sequence of consecutive days with at least one contribution.
 * The current streak counts the consecutive contributing days ending at [today]
 * (or the last day in [days] that is not after [today]).
 *
 * @param contributions Map of dates to contribution counts.
 * @param days Sorted list of dates to examine.
 * @param today Reference date for current streak calculation.
 */
internal fun calculateStreak(
    contributions: ContributionData,
    days: List<LocalDate>,
    today: LocalDate = LocalDate.now(),
): GitGrassStreakInfo {
    if (days.isEmpty()) return GitGrassStreakInfo(maxStreak = 0, currentStreak = 0)

    var maxStreak = 0
    var runLength = 0
    var currentRunLength = 0

    for (day in days) {
        val hasContribution = (contributions[day] ?: 0) > 0

        // ── Max streak: track across all days ────────────────────────
        if (hasContribution) {
            runLength++
            if (runLength > maxStreak) maxStreak = runLength
        } else {
            runLength = 0
        }

        // ── Current streak: track only for days up to today ─────────
        if (!day.isAfter(today)) {
            if (hasContribution) {
                currentRunLength++
            } else {
                currentRunLength = 0
            }
        }
    }

    return GitGrassStreakInfo(maxStreak = maxStreak, currentStreak = currentRunLength)
}
