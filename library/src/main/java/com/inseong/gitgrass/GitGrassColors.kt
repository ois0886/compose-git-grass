package com.inseong.gitgrass

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Color scheme for the [GitGrass] contribution graph.
 *
 * @param empty Color for days with zero contributions.
 * @param levels Ordered list of colors for contribution levels (lowest to highest).
 *   The size determines how many distinct levels the graph displays.
 *   For example, GitHub uses 4 levels by default.
 * @param text Color used for all label text (year, month, week, streak).
 * @param border Deprecated. Not used in rendering. Will be removed in 2.0.0.
 */
@Immutable
data class GitGrassColors(
    val empty: Color,
    val levels: List<Color>,
    val text: Color,
    @Deprecated(
        message = "Not used in rendering. Will be removed in 2.0.0.",
        level = DeprecationLevel.WARNING,
    )
    val border: Color = Color.Transparent,
)
