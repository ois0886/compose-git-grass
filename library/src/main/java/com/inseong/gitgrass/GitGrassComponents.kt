package com.inseong.gitgrass

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.inseong.gitgrass.GitGrassDefaults.DAYS_PER_WEEK
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
 * Uses the same column layout as [GrassGridContent] — each week is a fixed-width
 * [cellSize] slot with [cellSpacing] gaps — ensuring pixel-perfect alignment.
 * The [scrollState] is shared with the grid (scrolling disabled here) so it
 * follows the grid's scroll position exactly.
 */
@Composable
internal fun MonthRow(
    weekCount: Int,
    monthPositions: MonthPositions,
    monthLabels: List<String>,
    cellSize: Dp,
    cellSpacing: Dp,
    fontSize: TextUnit,
    textColor: Color,
    scrollState: ScrollState,
    weekLabelWidth: Dp,
) {
    val labelMap = remember(monthPositions) {
        monthPositions.associate { (weekIndex, month) -> weekIndex to month }
    }

    Row {
        if (weekLabelWidth.value > 0f) {
            Spacer(modifier = Modifier.width(weekLabelWidth))
        }

        Row(
            modifier = Modifier.horizontalScroll(scrollState, enabled = false),
            horizontalArrangement = Arrangement.spacedBy(cellSpacing),
        ) {
            for (weekIndex in 0 until weekCount) {
                Box(modifier = Modifier.width(cellSize).wrapContentSize(unbounded = true, align = Alignment.CenterStart)) {
                    val monthNumber = labelMap[weekIndex]
                    if (monthNumber != null) {
                        val label = monthLabels.getOrElse(monthNumber) { "" }
                        if (label.isNotEmpty()) {
                            BasicText(
                                text = label,
                                softWrap = false,
                                style = TextStyle(fontSize = fontSize, color = textColor),
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Vertical column of weekday labels (e.g. Mon, Wed, Fri).
 *
 * Only even-indexed rows (0, 2, 4, 6) display labels to avoid visual clutter,
 * matching GitHub's convention of showing alternate day labels.
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
        for (index in 0 until DAYS_PER_WEEK) {
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
    grid: Grid,
    contributions: ContributionData,
    colors: GitGrassColors,
    cellSize: Dp,
    cellSpacing: Dp,
    cellCornerRadius: Dp,
    levelOf: (Int) -> Int,
    scrollState: ScrollState,
    cellContentDescription: (LocalDate, Int) -> String,
    cellClickLabel: (LocalDate) -> String,
    onCellClick: ((LocalDate, Int) -> Unit)?,
    onCellLongClick: ((LocalDate, Int) -> Unit)?,
) {
    Row(
        modifier = Modifier.horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(cellSpacing),
    ) {
        for (week in grid) {
            GrassWeekColumn(
                week = week,
                contributions = contributions,
                colors = colors,
                cellSize = cellSize,
                cellSpacing = cellSpacing,
                cellCornerRadius = cellCornerRadius,
                levelOf = levelOf,
                cellContentDescription = cellContentDescription,
                cellClickLabel = cellClickLabel,
                onCellClick = onCellClick,
                onCellLongClick = onCellLongClick,
            )
        }
    }
}

/** A single week column of [DAYS_PER_WEEK] day cells. */
@Composable
internal fun GrassWeekColumn(
    week: List<LocalDate?>,
    contributions: ContributionData,
    colors: GitGrassColors,
    cellSize: Dp,
    cellSpacing: Dp,
    cellCornerRadius: Dp,
    levelOf: (Int) -> Int,
    cellContentDescription: (LocalDate, Int) -> String,
    cellClickLabel: (LocalDate) -> String,
    onCellClick: ((LocalDate, Int) -> Unit)?,
    onCellLongClick: ((LocalDate, Int) -> Unit)?,
) {
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
                    date = day,
                    count = count,
                    contentDescriptionText = cellContentDescription(day, count),
                    clickLabelText = cellClickLabel(day),
                    onClick = onCellClick?.let { callback -> { callback(day, count) } },
                    onLongClick = onCellLongClick?.let { callback -> { callback(day, count) } },
                )
            } else {
                Spacer(modifier = Modifier.size(cellSize))
            }
        }
    }
}

/**
 * Single rounded-rectangle cell representing one day.
 *
 * Includes accessibility semantics with content description
 * for screen reader support.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun GrassCell(
    color: Color,
    size: Dp,
    cornerRadius: Dp,
    date: LocalDate,
    count: Int,
    contentDescriptionText: String,
    clickLabelText: String,
    onClick: (() -> Unit)?,
    onLongClick: (() -> Unit)? = null,
) {
    val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }

    val baseModifier = Modifier
        .size(size)
        .clip(shape)
        .background(color, shape)
        .semantics {
            contentDescription = contentDescriptionText
            if (onClick != null) {
                role = Role.Button
            }
        }

    Box(
        modifier = if (onClick != null || onLongClick != null) {
            baseModifier.combinedClickable(
                onClick = onClick ?: {},
                onLongClick = onLongClick,
                onClickLabel = clickLabelText,
            )
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
    val maxText = "$maxLabel: ${streakInfo.maxStreak}"
    val currentText = "$currentLabel: ${streakInfo.currentStreak}"

    Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
        BasicText(
            text = maxText,
            style = TextStyle(fontSize = fontSize, color = textColor),
        )
        BasicText(
            text = currentText,
            style = TextStyle(fontSize = fontSize, color = textColor),
        )
    }
}

/**
 * Color legend showing "Less ↔ More" with gradient cells.
 *
 * Displays: [lessLabel] [empty] [level1] [level2] ... [levelN] [moreLabel]
 */
@Composable
internal fun ColorLegend(
    colors: GitGrassColors,
    cellSize: Dp,
    cellSpacing: Dp,
    cornerRadius: Dp,
    fontSize: TextUnit,
    lessLabel: String,
    moreLabel: String,
) {
    val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    val textStyle = TextStyle(fontSize = fontSize, color = colors.text)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(cellSpacing),
    ) {
        BasicText(text = lessLabel, style = textStyle)

        Box(
            modifier = Modifier
                .size(cellSize)
                .clip(shape)
                .background(colors.empty, shape),
        )

        for (levelColor in colors.levels) {
            Box(
                modifier = Modifier
                    .size(cellSize)
                    .clip(shape)
                    .background(levelColor, shape),
            )
        }

        BasicText(text = moreLabel, style = textStyle)
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
