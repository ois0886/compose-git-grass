package com.inseong.gitgrass

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
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

/** Bold year label displayed above the graph (e.g. "2024 - 2025"). */
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

/**
 * Horizontal row of month abbreviations that scrolls in sync with the grid.
 *
 * Uses the same [scrollState] as [GrassGridContent] with scrolling disabled,
 * so it follows the grid's scroll position exactly.
 */
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
        if (weekLabelWidth.value > 0f) {
            Spacer(modifier = Modifier.width(weekLabelWidth))
        }

        Row(modifier = Modifier.horizontalScroll(scrollState, enabled = false)) {
            val columnWidth = cellSize + cellSpacing
            var lastEndX = -1

            for ((weekIndex, monthNumber) in monthPositions) {
                if (weekIndex <= lastEndX) continue

                val label = monthLabels.getOrElse(monthNumber) { "" }
                if (label.isEmpty()) continue

                val offsetColumns = if (lastEndX < 0) weekIndex else weekIndex - lastEndX - 1
                if (offsetColumns > 0) {
                    Spacer(modifier = Modifier.width(columnWidth * offsetColumns))
                }

                BasicText(
                    text = label,
                    style = TextStyle(fontSize = fontSize, color = textColor),
                )

                lastEndX = weekIndex
            }
        }
    }
}

/**
 * Vertical column of weekday labels (e.g. Mon, Wed, Fri).
 *
 * Only even-indexed rows (0, 2, 4, 6) display labels to avoid visual clutter,
 * matching GitHub's convention of showing Mon/Wed/Fri.
 * Gracefully handles label lists shorter than 7 by leaving missing slots empty.
 */
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
        for (index in 0 until 7) {
            val label = weekLabels.getOrElse(index) { "" }
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

/** Scrollable grid of contribution cells organized by week columns. */
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
                            onClick = onCellClick?.let { callback -> { callback(day, count) } },
                        )
                    } else {
                        Spacer(modifier = Modifier.size(cellSize))
                    }
                }
            }
        }
    }
}

/** Single rounded-rectangle cell representing one day. */
@Composable
internal fun GrassCell(
    color: Color,
    size: Dp,
    cornerRadius: Dp,
    onClick: (() -> Unit)?,
) {
    val shape = RoundedCornerShape(cornerRadius)
    val baseModifier = Modifier
        .size(size)
        .clip(shape)
        .background(color, shape)

    Box(
        modifier = if (onClick != null) {
            baseModifier.clickable(onClick = onClick)
        } else {
            baseModifier
        },
    )
}

/** Displays max and current streak counts side by side. */
@Composable
internal fun StreakSummary(
    streakInfo: GitGrassStreakInfo,
    maxLabel: String,
    currentLabel: String,
    fontSize: TextUnit,
    spacing: Dp,
    textColor: Color,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
        BasicText(
            text = "$maxLabel: ${streakInfo.maxStreak}",
            style = TextStyle(fontSize = fontSize, color = textColor),
        )
        BasicText(
            text = "$currentLabel: ${streakInfo.currentStreak}",
            style = TextStyle(fontSize = fontSize, color = textColor),
        )
    }
}

/**
 * Maps a level index to its corresponding color.
 *
 * Level 0 (or negative) returns [GitGrassColors.empty].
 * Positive levels are mapped to [GitGrassColors.levels], clamped to the list bounds.
 */
internal fun levelToColor(level: Int, colors: GitGrassColors): Color {
    if (level <= 0 || colors.levels.isEmpty()) return colors.empty
    return colors.levels[(level - 1).coerceIn(0, colors.levels.lastIndex)]
}
