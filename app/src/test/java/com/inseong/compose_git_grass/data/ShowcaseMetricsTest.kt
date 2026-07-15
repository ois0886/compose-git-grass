package com.inseong.compose_git_grass.data

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class ShowcaseMetricsTest {

    @Test
    fun `calculateShowcaseMetrics counts only positive values inside range`() {
        val start = LocalDate.of(2025, 1, 1)
        val end = LocalDate.of(2025, 1, 3)
        val result = calculateShowcaseMetrics(
            contributions = mapOf(
                start.minusDays(1) to 100,
                start to 3,
                start.plusDays(1) to -5,
                end to 7,
                end.plusDays(1) to 100,
            ),
            startDate = start,
            endDate = end,
        )

        assertEquals(10L, result.totalContributions)
        assertEquals(2, result.activeDays)
    }

    @Test
    fun `calculateShowcaseMetrics normalizes a reversed range`() {
        val start = LocalDate.of(2025, 1, 1)
        val end = LocalDate.of(2025, 1, 3)
        val result = calculateShowcaseMetrics(
            contributions = mapOf(start.plusDays(1) to 4),
            startDate = end,
            endDate = start,
        )

        assertEquals(4L, result.totalContributions)
        assertEquals(1, result.activeDays)
    }
}
