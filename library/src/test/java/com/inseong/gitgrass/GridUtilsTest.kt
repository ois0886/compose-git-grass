package com.inseong.gitgrass

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class GridUtilsTest {

    // ── generateDayList ─────────────────────────────────────────────────

    @Test
    fun `generateDayList returns inclusive range`() {
        val start = LocalDate.of(2025, 1, 1)
        val end = LocalDate.of(2025, 1, 5)
        val days = generateDayList(start, end)

        assertEquals(5, days.size)
        assertEquals(start, days.first())
        assertEquals(end, days.last())
    }

    @Test
    fun `generateDayList single day`() {
        val date = LocalDate.of(2025, 6, 15)
        val days = generateDayList(date, date)

        assertEquals(1, days.size)
        assertEquals(date, days[0])
    }

    @Test
    fun `generateDayList returns empty when start is after end`() {
        val start = LocalDate.of(2025, 3, 10)
        val end = LocalDate.of(2025, 3, 1)
        val days = generateDayList(start, end)

        assertTrue(days.isEmpty())
    }

    // ── buildGrid ───────────────────────────────────────────────────────

    @Test
    fun `buildGrid returns empty for empty days`() {
        assertTrue(buildGrid(emptyList()).isEmpty())
    }

    @Test
    fun `buildGrid each week has exactly 7 slots`() {
        val days = generateDayList(
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 3, 31),
        )
        val grid = buildGrid(days)

        for (week in grid) {
            assertEquals(7, week.size)
        }
    }

    @Test
    fun `buildGrid first day is placed at correct weekday index`() {
        // 2025-01-01 is a Wednesday (dayOfWeek.value = 3, index = 2)
        val days = generateDayList(
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 1, 7),
        )
        val grid = buildGrid(days)

        val firstWeek = grid[0]
        assertEquals(null, firstWeek[0]) // Monday
        assertEquals(null, firstWeek[1]) // Tuesday
        assertEquals(LocalDate.of(2025, 1, 1), firstWeek[2]) // Wednesday
    }

    @Test
    fun `buildGrid starting on Monday creates no null padding in first week`() {
        val monday = LocalDate.of(2025, 1, 6)
        assertEquals(DayOfWeek.MONDAY, monday.dayOfWeek)

        val days = generateDayList(monday, LocalDate.of(2025, 1, 12))
        val grid = buildGrid(days)

        assertEquals(1, grid.size)
        for (day in grid[0]) {
            assertTrue(day != null)
        }
    }

    @Test
    fun `buildGrid full week creates single column`() {
        val days = generateDayList(
            LocalDate.of(2025, 1, 6),
            LocalDate.of(2025, 1, 12),
        )
        val grid = buildGrid(days)

        assertEquals(1, grid.size)
        assertEquals(7, grid[0].filterNotNull().size)
    }

    // ── createMonthLabels ───────────────────────────────────────────────

    @Test
    fun `createMonthLabels detects month transitions`() {
        val days = generateDayList(
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 3, 31),
        )
        val grid = buildGrid(days)
        val labels = createMonthLabels(grid)

        val months = labels.map { it.second }
        assertEquals(listOf(1, 2, 3), months)
    }

    @Test
    fun `createMonthLabels empty grid returns empty`() {
        assertTrue(createMonthLabels(emptyList()).isEmpty())
    }

    @Test
    fun `createMonthLabels single month has one entry`() {
        val days = generateDayList(
            LocalDate.of(2025, 5, 1),
            LocalDate.of(2025, 5, 31),
        )
        val grid = buildGrid(days)
        val labels = createMonthLabels(grid)

        assertEquals(1, labels.size)
        assertEquals(5, labels[0].second)
    }

    // ── formatYearLabel ─────────────────────────────────────────────────

    @Test
    fun `formatYearLabel same year`() {
        assertEquals(
            "2025",
            formatYearLabel(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31)),
        )
    }

    @Test
    fun `formatYearLabel different years`() {
        assertEquals(
            "2024 - 2025",
            formatYearLabel(LocalDate.of(2024, 6, 1), LocalDate.of(2025, 5, 31)),
        )
    }

    // ── calculateStreak ─────────────────────────────────────────────────

    @Test
    fun `calculateStreak empty contributions returns zeros`() {
        val days = generateDayList(
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 1, 10),
        )
        val streak = calculateStreak(emptyMap(), days, LocalDate.of(2025, 1, 10))

        assertEquals(0, streak.maxStreak)
        assertEquals(0, streak.currentStreak)
    }

    @Test
    fun `calculateStreak empty days returns zeros`() {
        val streak = calculateStreak(
            mapOf(LocalDate.of(2025, 1, 1) to 5),
            emptyList(),
            LocalDate.of(2025, 1, 1),
        )

        assertEquals(0, streak.maxStreak)
        assertEquals(0, streak.currentStreak)
    }

    @Test
    fun `calculateStreak max streak computed correctly`() {
        val days = generateDayList(
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 1, 10),
        )
        // Streak of 3 (Jan 2-4), then gap, then streak of 5 (Jan 6-10)
        val contributions = mapOf(
            LocalDate.of(2025, 1, 2) to 1,
            LocalDate.of(2025, 1, 3) to 2,
            LocalDate.of(2025, 1, 4) to 1,
            LocalDate.of(2025, 1, 6) to 3,
            LocalDate.of(2025, 1, 7) to 1,
            LocalDate.of(2025, 1, 8) to 2,
            LocalDate.of(2025, 1, 9) to 1,
            LocalDate.of(2025, 1, 10) to 4,
        )

        val streak = calculateStreak(contributions, days, LocalDate.of(2025, 1, 10))

        assertEquals(5, streak.maxStreak)
        assertEquals(5, streak.currentStreak)
    }

    @Test
    fun `calculateStreak current streak stops at gap`() {
        val days = generateDayList(
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 1, 5),
        )
        val contributions = mapOf(
            LocalDate.of(2025, 1, 1) to 1,
            LocalDate.of(2025, 1, 2) to 1,
            LocalDate.of(2025, 1, 4) to 1,
            LocalDate.of(2025, 1, 5) to 1,
        )

        val streak = calculateStreak(contributions, days, LocalDate.of(2025, 1, 5))

        assertEquals(2, streak.maxStreak)
        assertEquals(2, streak.currentStreak)
    }

    @Test
    fun `calculateStreak ignores days after today`() {
        val days = generateDayList(
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 1, 10),
        )
        val contributions = days.associateWith { 1 }

        val streak = calculateStreak(contributions, days, LocalDate.of(2025, 1, 5))

        assertEquals(10, streak.maxStreak)
        assertEquals(5, streak.currentStreak)
    }

    @Test
    fun `calculateStreak all days contributing`() {
        val days = generateDayList(
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 1, 7),
        )
        val contributions = days.associateWith { 3 }

        val streak = calculateStreak(contributions, days, LocalDate.of(2025, 1, 7))

        assertEquals(7, streak.maxStreak)
        assertEquals(7, streak.currentStreak)
    }

    @Test
    fun `calculateStreak negative contribution counts are treated as no contribution`() {
        val days = generateDayList(
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 1, 5),
        )
        // Jan 1=positive, Jan 2=negative (should break streak), Jan 3-5=positive
        val contributions = mapOf(
            LocalDate.of(2025, 1, 1) to 3,
            LocalDate.of(2025, 1, 2) to -1,
            LocalDate.of(2025, 1, 3) to 2,
            LocalDate.of(2025, 1, 4) to 1,
            LocalDate.of(2025, 1, 5) to 4,
        )

        val streak = calculateStreak(contributions, days, LocalDate.of(2025, 1, 5))

        assertEquals(3, streak.maxStreak)
        assertEquals(3, streak.currentStreak)
    }
}
