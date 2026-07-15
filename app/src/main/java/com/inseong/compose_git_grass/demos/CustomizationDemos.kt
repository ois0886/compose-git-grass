package com.inseong.compose_git_grass.demos

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inseong.compose_git_grass.components.DemoBlock
import com.inseong.compose_git_grass.components.SectionCard
import com.inseong.gitgrass.GitGrass
import com.inseong.gitgrass.GitGrassDefaults
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale

@Composable
internal fun LayoutGallery(data: Map<LocalDate, Int>) {
    val endDate = remember { LocalDate.now() }
    val recentStart = remember(endDate) { endDate.minusMonths(3) }

    SectionCard(
        title = "Layout",
        description = "Tune the visible range, cell density, week origin, and supporting labels.",
        badge = "ADAPTIVE",
    ) {
        DemoBlock(
            title = "Focused quarter",
            description = "A compact 90-day view with larger rounded cells.",
        ) {
            GitGrass(
                contributions = data,
                modifier = Modifier.fillMaxWidth(),
                startDate = recentStart,
                endDate = endDate,
                cellSize = 16.dp,
                cellSpacing = 4.dp,
                cellCornerRadius = 5.dp,
                showYearLabel = false,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoBlock(
            title = "Sunday-first, minimal chrome",
            description = "Default labels now automatically follow the configured week start.",
        ) {
            GitGrass(
                contributions = data,
                modifier = Modifier.fillMaxWidth(),
                startDate = recentStart,
                endDate = endDate,
                weekStartDay = DayOfWeek.SUNDAY,
                showYearLabel = false,
                showStreak = false,
                showLegend = false,
            )
        }
    }
}

@Composable
internal fun LocalizationGallery(data: Map<LocalDate, Int>) {
    val endDate = remember { LocalDate.now() }
    val startDate = remember(endDate) { endDate.minusMonths(6) }
    val monthLabels = remember { GitGrassDefaults.localizedMonthLabels(Locale.KOREAN) }
    val weekLabels = remember {
        GitGrassDefaults.localizedWeekLabels(
            weekStartDay = DayOfWeek.MONDAY,
            locale = Locale.KOREAN,
        )
    }

    SectionCard(
        title = "Localization",
        description = "Localize calendar labels, summaries, legends, and accessibility text together.",
        badge = "한국어",
    ) {
        GitGrass(
            contributions = data,
            modifier = Modifier.fillMaxWidth(),
            startDate = startDate,
            endDate = endDate,
            monthLabels = monthLabels,
            weekLabels = weekLabels,
            showYearLabel = false,
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
