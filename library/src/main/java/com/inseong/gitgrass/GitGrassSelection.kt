package com.inseong.gitgrass

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import java.time.LocalDate

/**
 * Controlled selection shown on top of a [GitGrass] contribution cell.
 *
 * [GitGrass] never changes this value internally. Update the selection from the
 * `onCellClick` callback or other application state and pass the new value back.
 *
 * @param date Date to highlight. Dates outside the displayed range are ignored.
 * @param outlineColor Inset outline color. [Color.Unspecified] uses [GitGrassColors.text].
 * @param outlineWidth Inset outline width. Values are safely clamped to the selected cell size.
 */
@Immutable
data class GitGrassSelection(
    val date: LocalDate,
    val outlineColor: Color = Color.Unspecified,
    val outlineWidth: Dp = GitGrassDefaults.selectionOutlineWidth,
)

internal fun resolveSelection(
    selection: GitGrassSelection?,
    fallbackOutlineColor: Color,
    cellSize: Dp,
): GitGrassSelection? {
    selection ?: return null

    val maxOutlineWidth = (cellSize / 2).coerceAtLeast(Dp.Hairline)
    return selection.copy(
        outlineColor = if (selection.outlineColor == Color.Unspecified) {
            fallbackOutlineColor
        } else {
            selection.outlineColor
        },
        outlineWidth = selection.outlineWidth.coerceIn(Dp.Hairline, maxOutlineWidth),
    )
}
