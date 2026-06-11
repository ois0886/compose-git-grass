package com.inseong.gitgrass

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import java.time.LocalDate

/** Precomputed cell values used by the Compose rendering layer. */
@Immutable
internal data class GrassCellRenderData(
    val date: LocalDate,
    val count: Int,
    val color: Color,
    val contentDescription: String,
    val clickLabel: String?,
)

internal fun buildRenderGrid(
    grid: Grid,
    contributions: ContributionData,
    colors: GitGrassColors,
    levelOf: (Int) -> Int,
    cellContentDescription: (LocalDate, Int) -> String,
    cellClickLabel: (LocalDate) -> String,
    includeClickLabels: Boolean,
): RenderGrid {
    if (grid.isEmpty()) return emptyList()

    val renderGrid = ArrayList<List<GrassCellRenderData?>>(grid.size)
    for (week in grid) {
        val renderWeek = ArrayList<GrassCellRenderData?>(week.size)
        for (day in week) {
            renderWeek.add(
                if (day == null) {
                    null
                } else {
                    val count = contributions[day] ?: 0
                    val level = levelOf(count)
                    GrassCellRenderData(
                        date = day,
                        count = count,
                        color = levelToColor(level, colors),
                        contentDescription = cellContentDescription(day, count),
                        clickLabel = if (includeClickLabels) cellClickLabel(day) else null,
                    )
                },
            )
        }
        renderGrid.add(renderWeek)
    }
    return renderGrid
}
