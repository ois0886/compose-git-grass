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
 * @param border Color used for cell borders when enabled.
 */
@Immutable
data class GitGrassColors(
    val empty: Color,
    val levels: List<Color>,
    val text: Color,
    val border: Color,
)
