package com.inseong.gitgrass

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
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

/** One month-label slot rendered inside the same lazy item as its week column. */
@Composable
internal fun MonthLabelSlot(
    text: String,
    slotWidth: Dp,
    fontSize: TextUnit,
    textColor: Color,
) {
    val placeholder = text.ifEmpty { "\u00A0" }
    val textModifier = if (text.isEmpty()) Modifier.clearAndSetSemantics { } else Modifier

    Box(
        modifier = Modifier
            .width(slotWidth)
            .wrapContentSize(unbounded = true, align = Alignment.CenterStart),
    ) {
        BasicText(
            text = placeholder,
            modifier = textModifier,
            softWrap = false,
            style = TextStyle(fontSize = fontSize, color = textColor),
        )
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
    renderGrid: RenderGrid,
    monthPositions: MonthPositions,
    monthLabels: List<String>,
    showMonthLabels: Boolean,
    cellSize: Dp,
    cellSpacing: Dp,
    cellCornerRadius: Dp,
    labelFontSize: TextUnit,
    textColor: Color,
    scrollState: LazyListState,
    selection: GitGrassSelection?,
    onCellClick: ((LocalDate, Int) -> Unit)?,
    onCellLongClick: ((LocalDate, Int) -> Unit)?,
) {
    val labelMap = remember(monthPositions) {
        monthPositions.associate { (weekIndex, month) -> weekIndex to month }
    }

    LazyRow(
        state = scrollState,
        contentPadding = PaddingValues(end = cellSize + cellSpacing),
        horizontalArrangement = Arrangement.spacedBy(cellSpacing),
    ) {
        items(
            count = renderGrid.size,
            key = { weekIndex -> weekIndex },
            contentType = { "grass-week" },
        ) { weekIndex ->
            Column {
                if (showMonthLabels) {
                    val monthNumber = labelMap[weekIndex]
                    val monthLabel = monthNumber?.let { month ->
                        monthLabels.getOrElse(month) { "" }
                    }.orEmpty()
                    MonthLabelSlot(
                        text = monthLabel,
                        slotWidth = cellSize,
                        fontSize = labelFontSize,
                        textColor = textColor,
                    )
                    Spacer(modifier = Modifier.height(GitGrassDefaults.monthRowBottomSpacing))
                }

                GrassWeekColumn(
                    week = renderGrid[weekIndex],
                    cellSize = cellSize,
                    cellSpacing = cellSpacing,
                    cellCornerRadius = cellCornerRadius,
                    selection = selection,
                    onCellClick = onCellClick,
                    onCellLongClick = onCellLongClick,
                )
            }
        }
    }
}

/** A single week column of [DAYS_PER_WEEK] day cells. */
@Composable
internal fun GrassWeekColumn(
    week: List<GrassCellRenderData?>,
    cellSize: Dp,
    cellSpacing: Dp,
    cellCornerRadius: Dp,
    selection: GitGrassSelection?,
    onCellClick: ((LocalDate, Int) -> Unit)?,
    onCellLongClick: ((LocalDate, Int) -> Unit)?,
) {
    val weekHeight = (cellSize * DAYS_PER_WEEK) + (cellSpacing * (DAYS_PER_WEEK - 1))

    Box(modifier = Modifier.width(cellSize).height(weekHeight)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val cellSizePx = cellSize.toPx()
            val cellSpacingPx = cellSpacing.toPx()
            val cornerRadiusPx = cellCornerRadius.toPx()

            for (index in 0 until DAYS_PER_WEEK) {
                val cell = week.getOrNull(index)
                if (cell != null) {
                    val top = index.toFloat() * (cellSizePx + cellSpacingPx)
                    drawRoundRect(
                        color = cell.color,
                        topLeft = Offset(0f, top),
                        size = Size(cellSizePx, cellSizePx),
                        cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                    )

                    if (selection?.date == cell.date) {
                        val strokeWidthPx = selection.outlineWidth.toPx()
                            .coerceIn(0f, cellSizePx.coerceAtLeast(0f))
                        if (strokeWidthPx > 0f) {
                            val inset = strokeWidthPx / 2f
                            val selectedSize = (cellSizePx - strokeWidthPx).coerceAtLeast(0f)
                            val selectedRadius = (cornerRadiusPx - inset).coerceAtLeast(0f)
                            drawRoundRect(
                                color = selection.outlineColor,
                                topLeft = Offset(inset, top + inset),
                                size = Size(selectedSize, selectedSize),
                                cornerRadius = CornerRadius(selectedRadius, selectedRadius),
                                style = Stroke(width = strokeWidthPx),
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.matchParentSize(),
            verticalArrangement = Arrangement.spacedBy(cellSpacing),
        ) {
            for (index in 0 until DAYS_PER_WEEK) {
                val cell = week.getOrNull(index)
                if (cell != null) {
                    GrassCell(
                        size = cellSize,
                        contentDescriptionText = cell.contentDescription,
                        clickLabelText = cell.clickLabel,
                        isSelected = selection?.date == cell.date,
                        onClick = onCellClick?.let { callback -> { callback(cell.date, cell.count) } },
                        onLongClick = onCellLongClick?.let { callback -> { callback(cell.date, cell.count) } },
                    )
                } else {
                    Spacer(modifier = Modifier.size(cellSize))
                }
            }
        }
    }
}

/**
 * Transparent per-day hit target layered above the Canvas-rendered cell.
 *
 * Includes accessibility semantics with content description
 * for screen reader support.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun GrassCell(
    size: Dp,
    contentDescriptionText: String,
    clickLabelText: String?,
    isSelected: Boolean = false,
    onClick: (() -> Unit)?,
    onLongClick: (() -> Unit)? = null,
) {
    val hasActions = onClick != null || onLongClick != null
    val baseModifier = Modifier
        .size(size)
        .semantics {
            contentDescription = contentDescriptionText
            if (isSelected) {
                selected = true
            }
            if (hasActions) {
                role = Role.Button
            }
        }

    val interactionModifier = when {
        onClick != null && onLongClick != null -> {
            baseModifier.combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                onClickLabel = clickLabelText,
            )
        }

        onClick != null -> {
            baseModifier.clickable(
                role = Role.Button,
                onClickLabel = clickLabelText,
                onClick = onClick,
            )
        }

        onLongClick != null -> {
            baseModifier
                .pointerInput(onLongClick) {
                    detectTapGestures(onLongPress = { onLongClick() })
                }
                .semantics {
                    onLongClick {
                        onLongClick()
                        true
                    }
                }
        }

        else -> baseModifier
    }

    Box(modifier = interactionModifier)
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
    shape: Shape,
    fontSize: TextUnit,
    lessLabel: String,
    moreLabel: String,
) {
    val textStyle = TextStyle(fontSize = fontSize, color = colors.text)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(cellSpacing),
    ) {
        BasicText(text = lessLabel, style = textStyle)

        Box(
            modifier = Modifier
                .size(cellSize)
                .background(colors.empty, shape)
                .semantics { contentDescription = "Level 0" },
        )

        for ((index, levelColor) in colors.levels.withIndex()) {
            Box(
                modifier = Modifier
                    .size(cellSize)
                    .background(levelColor, shape)
                    .semantics { contentDescription = "Level ${index + 1}" },
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
