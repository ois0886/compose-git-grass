package com.inseong.gitgrass

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

object GitGrassDefaults {

    fun colors(): GitGrassColors = GitGrassColors(
        empty = Color(0xFFEBEDF0),
        level1 = Color(0xFF9BE9A8),
        level2 = Color(0xFF40C463),
        level3 = Color(0xFF30A14E),
        level4 = Color(0xFF216E39),
        text = Color(0xFF24292F),
        border = Color(0xFFD0D7DE),
    )

    fun darkColors(): GitGrassColors = GitGrassColors(
        empty = Color(0xFF161B22),
        level1 = Color(0xFF0E4429),
        level2 = Color(0xFF006D32),
        level3 = Color(0xFF26A641),
        level4 = Color(0xFF39D353),
        text = Color(0xFFC9D1D9),
        border = Color(0xFF30363D),
    )

    /** Index 0 is empty so that `monthLabels[date.monthValue]` works directly. */
    val monthLabels: List<String> = listOf(
        "", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
    )

    val weekLabels: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val cellSize: Dp = 12.dp
    val cellSpacing: Dp = 3.dp
    val cellCornerRadius: Dp = 2.dp

    val labelFontSize: TextUnit = 10.sp
    val yearLabelFontSize: TextUnit = 13.sp

    val levelThresholds: (Int) -> Int = { count ->
        when {
            count <= 0 -> 0
            count <= 3 -> 1
            count <= 6 -> 2
            count <= 9 -> 3
            else -> 4
        }
    }

    fun startDate(): LocalDate {
        val now = LocalDate.now()
        return now.minusYears(1)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }

    fun endDate(): LocalDate = LocalDate.now()
}
