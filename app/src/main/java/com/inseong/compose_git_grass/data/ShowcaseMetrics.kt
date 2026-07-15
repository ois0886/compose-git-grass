package com.inseong.compose_git_grass.data

import androidx.compose.runtime.Immutable
import java.time.LocalDate

@Immutable
internal data class ShowcaseMetrics(
    val totalContributions: Long,
    val activeDays: Int,
)

internal fun calculateShowcaseMetrics(
    contributions: Map<LocalDate, Int>,
    startDate: LocalDate,
    endDate: LocalDate,
): ShowcaseMetrics {
    val firstDate = minOf(startDate, endDate)
    val lastDate = maxOf(startDate, endDate)
    var totalContributions = 0L
    var activeDays = 0

    for ((date, rawCount) in contributions) {
        if (date.isBefore(firstDate) || date.isAfter(lastDate)) continue

        val count = rawCount.coerceAtLeast(0)
        totalContributions += count
        if (count > 0) activeDays++
    }

    return ShowcaseMetrics(
        totalContributions = totalContributions,
        activeDays = activeDays,
    )
}
