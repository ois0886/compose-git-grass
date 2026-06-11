package com.inseong.compose_git_grass.demos

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.inseong.compose_git_grass.components.SectionCard
import com.inseong.gitgrass.GitGrass
import com.inseong.gitgrass.GitGrassDefaults
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale

@Composable
internal fun CustomDateRangeDemo(data: Map<LocalDate, Int>) {
    val dateRange = remember {
        val end = LocalDate.now()
        end.minusMonths(3) to end
    }

    SectionCard(
        title = "Custom Date Range",
        description = "Display only the last 3 months",
    ) {
        GitGrass(
            contributions = data,
            modifier = Modifier.fillMaxWidth(),
            startDate = dateRange.first,
            endDate = dateRange.second,
        )
    }
}

@Composable
internal fun WeekStartDayDemo(data: Map<LocalDate, Int>) {
    val sundayWeekLabels = remember {
        GitGrassDefaults.localizedWeekLabels(
            weekStartDay = DayOfWeek.SUNDAY,
        )
    }

    SectionCard(
        title = "Week Start Day",
        description = "weekStartDay = SUNDAY",
    ) {
        GitGrass(
            contributions = data,
            modifier = Modifier.fillMaxWidth(),
            weekStartDay = DayOfWeek.SUNDAY,
            weekLabels = sundayWeekLabels,
        )
    }
}

@Composable
internal fun KoreanLocalizationDemo(data: Map<LocalDate, Int>) {
    val monthLabels = remember { GitGrassDefaults.localizedMonthLabels(Locale.KOREAN) }
    val weekLabels = remember { GitGrassDefaults.localizedWeekLabels(locale = Locale.KOREAN) }

    SectionCard(
        title = "Korean Labels",
        description = "Localized month/week labels + custom streak/legend text",
    ) {
        GitGrass(
            contributions = data,
            modifier = Modifier.fillMaxWidth(),
            monthLabels = monthLabels,
            weekLabels = weekLabels,
            showStreak = true,
            streakMaxLabel = "최대 연속",
            streakCurrentLabel = "현재 연속",
            lessLabel = "적음",
            moreLabel = "많음",
            cellContentDescription = { date, count -> "$date: ${count}건" },
            cellClickLabel = { date -> "$date 상세보기" },
        )
    }
}
