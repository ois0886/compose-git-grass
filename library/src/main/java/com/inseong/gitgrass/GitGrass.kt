package com.inseong.gitgrass

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Immutable
data class GitGrassStreakInfo(
    val maxStreak: Int,
    val currentStreak: Int,
)

@Composable
fun GitGrass(
    contributions: Map<LocalDate, Int>,
    modifier: Modifier = Modifier,
    startDate: LocalDate = GitGrassDefaults.startDate(),
    endDate: LocalDate = GitGrassDefaults.endDate(),
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
    streakMaxLabel: String = "Max streak",
    streakCurrentLabel: String = "Current streak",
    levelOf: (count: Int) -> Int = GitGrassDefaults.levelThresholds,
    onCellClick: ((date: LocalDate, count: Int) -> Unit)? = null,
) {
    val days = remember(startDate, endDate) { generateDayList(startDate, endDate) }
    val grid = remember(days) { buildGrid(days) }
    val monthPositions = remember(grid) { createMonthLabels(grid) }
    val yearLabel = remember(startDate, endDate) { formatYearLabel(startDate, endDate) }
    val streakInfo = remember(contributions, days) { calculateStreak(contributions, days) }

    val scrollState: ScrollState = rememberScrollState()

    LaunchedEffect(grid) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    val weekLabelWidth = if (showWeekLabels) 28.dp else 0.dp

    Column(modifier = modifier) {
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
                contributions = contributions,
                colors = colors,
                cellSize = cellSize,
                cellSpacing = cellSpacing,
                cellCornerRadius = cellCornerRadius,
                levelOf = levelOf,
                scrollState = scrollState,
                onCellClick = onCellClick,
            )
        }

        if (showStreak) {
            Spacer(modifier = Modifier.height(8.dp))
            StreakSummary(
                streakInfo = streakInfo,
                maxLabel = streakMaxLabel,
                currentLabel = streakCurrentLabel,
                fontSize = labelFontSize,
                textColor = colors.text,
            )
        }
    }
}
