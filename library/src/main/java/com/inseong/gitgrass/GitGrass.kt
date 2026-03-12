package com.inseong.gitgrass

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Streak information calculated from contribution data.
 *
 * @param maxStreak Longest consecutive run of contributing days in the date range.
 * @param currentStreak Number of consecutive contributing days ending today (or the most recent day).
 */
@Immutable
data class GitGrassStreakInfo(
    val maxStreak: Int,
    val currentStreak: Int,
)

/**
 * A GitHub-style contribution graph (grass) composable.
 *
 * Renders a horizontally scrollable grid of colored cells representing daily contributions.
 * The grid is organized as week columns with optional month labels,
 * weekday labels, year label, streak summary, and color legend.
 *
 * The graph automatically scrolls to the most recent date on first composition.
 *
 * @param contributions Map of dates to contribution counts. Missing dates are treated as 0.
 *   Negative values are automatically clamped to 0.
 * @param modifier Modifier applied to the root layout.
 * @param startDate First date shown in the graph (defaults to ~1 year ago, week-aligned).
 *   If [startDate] is after [endDate], they are swapped automatically.
 * @param endDate Last date shown in the graph (defaults to today).
 * @param weekStartDay First day of the week for grid layout (defaults to [DayOfWeek.MONDAY]).
 * @param colors Color scheme for cells and labels. See [GitGrassDefaults.colors] and [GitGrassDefaults.darkColors].
 * @param monthLabels Month name strings indexed by [LocalDate.getMonthValue] (index 0 unused).
 * @param weekLabels Weekday name strings in order starting from [weekStartDay] (size 7).
 * @param cellSize Width and height of each day cell.
 * @param cellSpacing Gap between cells and between week columns.
 * @param cellCornerRadius Corner radius for each cell's rounded rectangle.
 * @param labelFontSize Font size for month, week, and streak labels.
 * @param showYearLabel Whether to display the year label above the graph.
 * @param showWeekLabels Whether to display weekday labels on the left.
 * @param showMonthLabels Whether to display month labels above the grid.
 * @param showStreak Whether to display max/current streak below the graph.
 * @param showLegend Whether to display the color legend below the graph.
 * @param streakMaxLabel Text prefix for max streak display.
 * @param streakCurrentLabel Text prefix for current streak display.
 * @param lessLabel Text for "Less" in the color legend.
 * @param moreLabel Text for "More" in the color legend.
 * @param levelOf Maps a contribution count to a color level index (0 = empty, 1+ = [GitGrassColors.levels]).
 * @param onCellClick Optional callback invoked when a cell is tapped, receiving the date and count.
 * @param onCellLongClick Optional callback invoked when a cell is long-pressed, receiving the date and count.
 */
@Composable
fun GitGrass(
    contributions: Map<LocalDate, Int>,
    modifier: Modifier = Modifier,
    startDate: LocalDate = GitGrassDefaults.startDate(),
    endDate: LocalDate = GitGrassDefaults.endDate(),
    weekStartDay: DayOfWeek = DayOfWeek.MONDAY,
    colors: GitGrassColors = GitGrassDefaults.colors(),
    monthLabels: List<String> = GitGrassDefaults.monthLabels,
    weekLabels: List<String> = GitGrassDefaults.weekLabels,
    cellSize: Dp = GitGrassDefaults.cellSize,
    cellSpacing: Dp = GitGrassDefaults.cellSpacing,
    cellCornerRadius: Dp = GitGrassDefaults.cellCornerRadius,
    labelFontSize: TextUnit = GitGrassDefaults.labelFontSize,
    showYearLabel: Boolean = true,
    showWeekLabels: Boolean = true,
    showMonthLabels: Boolean = true,
    showStreak: Boolean = false,
    showLegend: Boolean = true,
    streakMaxLabel: String = "Max streak",
    streakCurrentLabel: String = "Current streak",
    lessLabel: String = "Less",
    moreLabel: String = "More",
    levelOf: (count: Int) -> Int = GitGrassDefaults.levelThresholds,
    onCellClick: ((date: LocalDate, count: Int) -> Unit)? = null,
    onCellLongClick: ((date: LocalDate, count: Int) -> Unit)? = null,
) {
    val (safeStart, safeEnd) = remember(startDate, endDate) {
        normalizeDateRange(startDate, endDate)
    }
    val safeContributions = remember(contributions) {
        normalizeContributions(contributions)
    }

    val days = remember(safeStart, safeEnd) { generateDayList(safeStart, safeEnd) }
    val grid = remember(days, weekStartDay) { buildGrid(days, weekStartDay) }
    val monthPositions = remember(grid) { createMonthLabels(grid) }
    val yearLabel = remember(safeStart, safeEnd) { formatYearLabel(safeStart, safeEnd) }
    val streakInfo = remember(safeContributions, days) {
        calculateStreak(safeContributions, days, safeEnd)
    }

    val scrollState: ScrollState = rememberScrollState()

    LaunchedEffect(grid) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    val weekLabelWidth = if (showWeekLabels) 28.dp else 0.dp

    Column(
        modifier = modifier.semantics {
            contentDescription = "Contribution graph"
        },
    ) {
        if (showYearLabel) {
            YearLabel(
                text = yearLabel,
                fontSize = GitGrassDefaults.yearLabelFontSize,
                textColor = colors.text,
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (showMonthLabels) {
            MonthRow(
                weekCount = grid.size,
                monthPositions = monthPositions,
                monthLabels = monthLabels,
                cellSize = cellSize,
                cellSpacing = cellSpacing,
                fontSize = labelFontSize,
                textColor = colors.text,
                scrollState = scrollState,
                weekLabelWidth = weekLabelWidth,
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        Row {
            if (showWeekLabels) {
                WeekLabelColumn(
                    weekLabels = weekLabels,
                    cellSize = cellSize,
                    cellSpacing = cellSpacing,
                    fontSize = labelFontSize,
                    textColor = colors.text,
                    modifier = Modifier.width(weekLabelWidth),
                )
            }

            GrassGridContent(
                grid = grid,
                contributions = safeContributions,
                colors = colors,
                cellSize = cellSize,
                cellSpacing = cellSpacing,
                cellCornerRadius = cellCornerRadius,
                levelOf = levelOf,
                scrollState = scrollState,
                onCellClick = onCellClick,
                onCellLongClick = onCellLongClick,
            )
        }

        if (showStreak) {
            Spacer(modifier = Modifier.height(8.dp))
            StreakSummary(
                streakInfo = streakInfo,
                maxLabel = streakMaxLabel,
                currentLabel = streakCurrentLabel,
                fontSize = labelFontSize,
                spacing = 16.dp,
                textColor = colors.text,
            )
        }

        if (showLegend) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.width(
                    weekLabelWidth + (cellSize + cellSpacing) * grid.size.coerceAtLeast(1),
                ),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ColorLegend(
                    colors = colors,
                    cellSize = cellSize,
                    cellSpacing = cellSpacing,
                    cornerRadius = cellCornerRadius,
                    fontSize = labelFontSize,
                    lessLabel = lessLabel,
                    moreLabel = moreLabel,
                )
            }
        }
    }
}
