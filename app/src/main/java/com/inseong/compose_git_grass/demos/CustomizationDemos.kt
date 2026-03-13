package com.inseong.compose_git_grass.demos

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.inseong.compose_git_grass.components.SectionCard
import com.inseong.gitgrass.GitGrass
import com.inseong.gitgrass.GitGrassDefaults
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale

@Composable
internal fun CustomDateRangeDemo(data: Map<LocalDate, Int>) {
    SectionCard(
        title = "Custom Date Range",
        description = "Display only the last 3 months",
    ) {
        GitGrass(
            contributions = data,
            modifier = Modifier.fillMaxWidth(),
            startDate = LocalDate.now().minusMonths(3),
            endDate = LocalDate.now(),
        )
    }
}

@Composable
internal fun WeekStartDayDemo(data: Map<LocalDate, Int>) {
    SectionCard(
        title = "Week Start Day",
        description = "weekStartDay = SUNDAY",
    ) {
        GitGrass(
            contributions = data,
            modifier = Modifier.fillMaxWidth(),
            weekStartDay = DayOfWeek.SUNDAY,
            weekLabels = GitGrassDefaults.localizedWeekLabels(
                weekStartDay = DayOfWeek.SUNDAY,
            ),
        )
    }
}

@Composable
internal fun KoreanLocalizationDemo(data: Map<LocalDate, Int>) {
    SectionCard(
        title = "Korean Labels",
        description = "Localized month/week labels + custom streak/legend text",
    ) {
        GitGrass(
            contributions = data,
            modifier = Modifier.fillMaxWidth(),
            monthLabels = GitGrassDefaults.localizedMonthLabels(Locale.KOREAN),
            weekLabels = GitGrassDefaults.localizedWeekLabels(locale = Locale.KOREAN),
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
