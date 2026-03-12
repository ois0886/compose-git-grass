package com.inseong.gitgrass

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale

class GitGrassDefaultsTest {

    // ── startDate ─────────────────────────────────────────────────────

    @Test
    fun `startDate returns date about 1 year ago`() {
        val start = GitGrassDefaults.startDate()
        val today = LocalDate.now()
        val oneYearAgo = today.minusYears(1)

        // Should be within 7 days of one year ago (adjusted to Monday)
        assertFalse(start.isAfter(oneYearAgo))
        assertTrue(start.isAfter(oneYearAgo.minusDays(7)))
    }

    @Test
    fun `startDate default is Monday-aligned`() {
        val start = GitGrassDefaults.startDate()
        assertEquals(DayOfWeek.MONDAY, start.dayOfWeek)
    }

    @Test
    fun `startDate with Sunday weekStartDay is Sunday-aligned`() {
        val start = GitGrassDefaults.startDate(DayOfWeek.SUNDAY)
        assertEquals(DayOfWeek.SUNDAY, start.dayOfWeek)
    }

    // ── endDate ───────────────────────────────────────────────────────

    @Test
    fun `endDate returns today`() {
        assertEquals(LocalDate.now(), GitGrassDefaults.endDate())
    }

    // ── levelThresholds ───────────────────────────────────────────────

    @Test
    fun `levelThresholds zero returns 0`() {
        assertEquals(0, GitGrassDefaults.levelThresholds(0))
    }

    @Test
    fun `levelThresholds negative returns 0`() {
        assertEquals(0, GitGrassDefaults.levelThresholds(-5))
    }

    @Test
    fun `levelThresholds 1 to 3 returns level 1`() {
        assertEquals(1, GitGrassDefaults.levelThresholds(1))
        assertEquals(1, GitGrassDefaults.levelThresholds(3))
    }

    @Test
    fun `levelThresholds 4 to 6 returns level 2`() {
        assertEquals(2, GitGrassDefaults.levelThresholds(4))
        assertEquals(2, GitGrassDefaults.levelThresholds(6))
    }

    @Test
    fun `levelThresholds 7 to 9 returns level 3`() {
        assertEquals(3, GitGrassDefaults.levelThresholds(7))
        assertEquals(3, GitGrassDefaults.levelThresholds(9))
    }

    @Test
    fun `levelThresholds 10 or more returns level 4`() {
        assertEquals(4, GitGrassDefaults.levelThresholds(10))
        assertEquals(4, GitGrassDefaults.levelThresholds(100))
    }

    // ── localizedMonthLabels ──────────────────────────────────────────

    @Test
    fun `localizedMonthLabels has 13 elements with empty first`() {
        val labels = GitGrassDefaults.localizedMonthLabels(Locale.ENGLISH)

        assertEquals(13, labels.size)
        assertEquals("", labels[0])
    }

    @Test
    fun `localizedMonthLabels English matches expected`() {
        val labels = GitGrassDefaults.localizedMonthLabels(Locale.ENGLISH)

        assertEquals("Jan", labels[1])
        assertEquals("Dec", labels[12])
    }

    @Test
    fun `localizedMonthLabels Korean produces non-empty labels`() {
        val labels = GitGrassDefaults.localizedMonthLabels(Locale.KOREAN)

        for (i in 1..12) {
            assertTrue("Month $i should not be empty", labels[i].isNotEmpty())
        }
    }

    // ── localizedWeekLabels ───────────────────────────────────────────

    @Test
    fun `localizedWeekLabels has 7 elements`() {
        val labels = GitGrassDefaults.localizedWeekLabels(locale = Locale.ENGLISH)
        assertEquals(7, labels.size)
    }

    @Test
    fun `localizedWeekLabels Monday-start first is Mon`() {
        val labels = GitGrassDefaults.localizedWeekLabels(
            weekStartDay = DayOfWeek.MONDAY,
            locale = Locale.ENGLISH,
        )
        assertEquals("Mon", labels[0])
        assertEquals("Sun", labels[6])
    }

    @Test
    fun `localizedWeekLabels Sunday-start first is Sun`() {
        val labels = GitGrassDefaults.localizedWeekLabels(
            weekStartDay = DayOfWeek.SUNDAY,
            locale = Locale.ENGLISH,
        )
        assertEquals("Sun", labels[0])
        assertEquals("Sat", labels[6])
    }

    // ── colors ────────────────────────────────────────────────────────

    @Test
    fun `colors light theme has 4 levels`() {
        val colors = GitGrassDefaults.colors()
        assertEquals(4, colors.levels.size)
    }

    @Test
    fun `darkColors has 4 levels`() {
        val colors = GitGrassDefaults.darkColors()
        assertEquals(4, colors.levels.size)
    }

    // ── static labels ─────────────────────────────────────────────────

    @Test
    fun `monthLabels has 13 elements`() {
        assertEquals(13, GitGrassDefaults.monthLabels.size)
        assertEquals("", GitGrassDefaults.monthLabels[0])
        assertEquals("Jan", GitGrassDefaults.monthLabels[1])
    }

    @Test
    fun `weekLabels has 7 elements Monday-first`() {
        assertEquals(7, GitGrassDefaults.weekLabels.size)
        assertEquals("Mon", GitGrassDefaults.weekLabels[0])
        assertEquals("Sun", GitGrassDefaults.weekLabels[6])
    }
}
