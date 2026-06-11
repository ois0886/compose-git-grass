package com.inseong.gitgrass

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

/**
 * Default values for [GitGrass] composable parameters.
 *
 * Provides GitHub-style light/dark color schemes, English labels, standard sizing,
 * and sensible date range defaults (past 1 year to today).
 */
object GitGrassDefaults {

    /** Number of days in a week. Used for grid row count and label sizing. */
    internal const val DAYS_PER_WEEK = 7

    @Suppress("DEPRECATION")
    private val lightColorScheme = GitGrassColors(
        empty = Color(0xFFEBEDF0),
        levels = listOf(
            Color(0xFF9BE9A8),
            Color(0xFF40C463),
            Color(0xFF30A14E),
            Color(0xFF216E39),
        ),
        text = Color(0xFF24292F),
        border = Color(0xFFD0D7DE),
    )

    @Suppress("DEPRECATION")
    private val darkColorScheme = GitGrassColors(
        empty = Color(0xFF161B22),
        levels = listOf(
            Color(0xFF0E4429),
            Color(0xFF006D32),
            Color(0xFF26A641),
            Color(0xFF39D353),
        ),
        text = Color(0xFFC9D1D9),
        border = Color(0xFF30363D),
    )

    /** Default width for the week label column. */
    val weekLabelWidth: Dp = 28.dp

    // ── Layout spacing constants ────────────────────────────────────
    internal val yearLabelBottomSpacing: Dp = 4.dp
    internal val monthRowBottomSpacing: Dp = 2.dp
    internal val streakTopSpacing: Dp = 8.dp
    internal val legendTopSpacing: Dp = 8.dp
    internal val streakItemSpacing: Dp = 16.dp

    /** GitHub light theme colors. */
    fun colors(): GitGrassColors = lightColorScheme

    /** GitHub dark theme colors. */
    fun darkColors(): GitGrassColors = darkColorScheme

    /**
     * English month labels. Index 0 is empty so that `monthLabels[date.monthValue]`
     * maps directly without offset.
     */
    val monthLabels: List<String> = listOf(
        "", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
    )

    /** English weekday labels, Monday-first order. */
    val weekLabels: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    /**
     * Returns localized month labels using the device's default locale.
     *
     * Index 0 is empty so that `monthLabels[date.monthValue]` maps directly.
     *
     * @param locale Locale to use for month name formatting. Defaults to [Locale.getDefault].
     */
    fun localizedMonthLabels(locale: Locale = Locale.getDefault()): List<String> {
        return listOf("") + Month.entries.map { month ->
            month.getDisplayName(TextStyle.SHORT, locale)
        }
    }

    /**
     * Returns localized weekday labels starting from [weekStartDay].
     *
     * @param weekStartDay First day of the week. Defaults to [DayOfWeek.MONDAY].
     * @param locale Locale to use for weekday name formatting. Defaults to [Locale.getDefault].
     */
    fun localizedWeekLabels(
        weekStartDay: DayOfWeek = DayOfWeek.MONDAY,
        locale: Locale = Locale.getDefault(),
    ): List<String> {
        return weekDaysOrdered(weekStartDay).map { day ->
            day.getDisplayName(TextStyle.SHORT, locale)
        }
    }

    val cellSize: Dp = 12.dp
    val cellSpacing: Dp = 3.dp
    val cellCornerRadius: Dp = 2.dp

    val labelFontSize: TextUnit = 10.sp
    val yearLabelFontSize: TextUnit = 13.sp

    /**
     * Default mapping from contribution count to color level index.
     *
     * Returns 0 for no contributions (rendered with [GitGrassColors.empty]),
     * and 1–4 for increasing activity (mapped to [GitGrassColors.levels]).
     */
    val levelThresholds: (Int) -> Int = { count ->
        when {
            count <= 0 -> 0
            count <= 3 -> 1
            count <= 6 -> 2
            count <= 9 -> 3
            else -> 4
        }
    }

    /**
     * Default start date: 1 year ago from today, adjusted back to the nearest [weekStartDay]
     * so the grid starts on a clean week boundary.
     *
     * @param weekStartDay First day of the week. Defaults to [DayOfWeek.MONDAY].
     */
    fun startDate(weekStartDay: DayOfWeek = DayOfWeek.MONDAY): LocalDate {
        return LocalDate.now()
            .minusYears(1)
            .with(TemporalAdjusters.previousOrSame(weekStartDay))
    }

    /** Default end date: today. */
    fun endDate(): LocalDate = LocalDate.now()
}
