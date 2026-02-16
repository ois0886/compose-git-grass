package com.inseong.gitgrass

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import java.time.LocalDate

@Composable
internal fun YearLabel(
    text: String,
    fontSize: TextUnit,
    textColor: Color,
) {
    BasicText(
        text = text,
        style = TextStyle(
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = textColor,
        ),
    )
}

@Composable
internal fun MonthRow(
    monthPositions: List<Pair<Int, Int>>,
    monthLabels: List<String>,
    cellSize: Dp,
    cellSpacing: Dp,
    fontSize: TextUnit,
    textColor: Color,
    scrollState: ScrollState,
    weekLabelWidth: Dp,
) {
    Row {
        Spacer(modifier = Modifier.width(weekLabelWidth))

        Row(modifier = Modifier.horizontalScroll(scrollState, enabled = false)) {
            val columnWidth = cellSize + cellSpacing
            var lastEndX = -1

            for ((weekIndex, monthNumber) in monthPositions) {
                val x = weekIndex
                if (x <= lastEndX) continue

                val label = monthLabels.getOrElse(monthNumber) { "" }
                if (label.isEmpty()) continue

                val offsetColumns = if (lastEndX < 0) x else x - lastEndX - 1
                if (offsetColumns > 0) {
                    Spacer(modifier = Modifier.width(columnWidth * offsetColumns))
                }

                BasicText(
                    text = label,
                    style = TextStyle(fontSize = fontSize, color = textColor),
                )

                lastEndX = x
            }
        }
    }
}

@Composable
internal fun WeekLabelColumn(
    weekLabels: List<String>,
    cellSize: Dp,
    cellSpacing: Dp,
    fontSize: TextUnit,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(cellSpacing),
    ) {
        for ((index, label) in weekLabels.withIndex()) {
            val displayLabel = if (index % 2 == 0) label else ""
            Box(modifier = Modifier.height(cellSize)) {
                BasicText(
                    text = displayLabel,
                    style = TextStyle(fontSize = fontSize, color = textColor),
                )
            }
        }
    }
}

@Composable
internal fun GrassGridContent(
    grid: List<List<LocalDate?>>,
    contributions: Map<LocalDate, Int>,
    colors: GitGrassColors,
    cellSize: Dp,
    cellSpacing: Dp,
    cellCornerRadius: Dp,
    levelOf: (Int) -> Int,
    scrollState: ScrollState,
    onCellClick: ((LocalDate, Int) -> Unit)?,
) {
    Row(
        modifier = Modifier.horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(cellSpacing),
    ) {
        for (week in grid) {
            Column(verticalArrangement = Arrangement.spacedBy(cellSpacing)) {
                for (day in week) {
                    if (day != null) {
                        val count = contributions[day] ?: 0
                        val level = levelOf(count)
                        val color = levelToColor(level, colors)
                        GrassCell(
                            color = color,
                            size = cellSize,
                            cornerRadius = cellCornerRadius,
                            onClick = if (onCellClick != null) {
                                { onCellClick(day, count) }
                            } else {
                                null
                            },
                        )
                    } else {
                        Spacer(modifier = Modifier.size(cellSize))
                    }
                }
            }
        }
    }
}

@Composable
internal fun GrassCell(
    color: Color,
    size: Dp,
    cornerRadius: Dp,
    onClick: (() -> Unit)?,
) {
    val shape = RoundedCornerShape(cornerRadius)
    var modifier = Modifier
        .size(size)
        .clip(shape)
        .background(color, shape)

    if (onClick != null) {
        modifier = modifier.clickable(onClick = onClick)
    }

    Box(modifier = modifier)
}

@Composable
internal fun StreakSummary(
    streakInfo: GitGrassStreakInfo,
    maxLabel: String,
    currentLabel: String,
    fontSize: TextUnit,
    textColor: Color,
) {
    Row {
        BasicText(
            text = "$maxLabel: ${streakInfo.maxStreak}",
            style = TextStyle(fontSize = fontSize, color = textColor),
        )
        Spacer(modifier = Modifier.width(cellSpacingDefault))
        BasicText(
            text = "$currentLabel: ${streakInfo.currentStreak}",
            style = TextStyle(fontSize = fontSize, color = textColor),
        )
    }
}

private val cellSpacingDefault = GitGrassDefaults.cellSpacing

internal fun levelToColor(level: Int, colors: GitGrassColors): Color = when (level) {
    1 -> colors.level1
    2 -> colors.level2
    3 -> colors.level3
    4 -> colors.level4
    else -> colors.empty
}
