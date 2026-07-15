package com.inseong.compose_git_grass.demos

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inseong.compose_git_grass.components.SectionCard
import com.inseong.compose_git_grass.data.calculateShowcaseMetrics
import com.inseong.gitgrass.GitGrass
import com.inseong.gitgrass.GitGrassDefaults
import com.inseong.gitgrass.GitGrassSelection
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
internal fun FeaturedContributionDemo(
    data: Map<LocalDate, Int>,
    snackbarHostState: SnackbarHostState,
) {
    val endDate = remember { GitGrassDefaults.endDate() }
    val startDate = remember(endDate) { endDate.minusYears(1) }
    val metrics = remember(data, startDate, endDate) {
        calculateShowcaseMetrics(data, startDate, endDate)
    }
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", Locale.ENGLISH)
    }
    var selectedDate by remember(endDate) { mutableStateOf(endDate) }
    val selectedCount = data[selectedDate]?.coerceAtLeast(0) ?: 0
    val isDarkTheme = isSystemInDarkTheme()
    val graphColors = if (isDarkTheme) {
        GitGrassDefaults.darkColors()
    } else {
        GitGrassDefaults.colors()
    }
    val selectedLevel = GitGrassDefaults.levelThresholds(selectedCount)
    val selectedColor = if (selectedLevel <= 0 || graphColors.levels.isEmpty()) {
        graphColors.empty
    } else {
        graphColors.levels[(selectedLevel - 1).coerceIn(0, graphColors.levels.lastIndex)]
    }
    val scope = rememberCoroutineScope()

    SectionCard(
        title = "Contribution activity",
        description = "Tap any day to inspect it. Long-press a cell to try the secondary action.",
        badge = "INTERACTIVE",
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            MetricPill(
                value = metrics.totalContributions.toString(),
                label = "contributions",
                modifier = Modifier.weight(1f),
            )
            MetricPill(
                value = metrics.activeDays.toString(),
                label = "active days",
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        GitGrass(
            contributions = data,
            modifier = Modifier.fillMaxWidth(),
            startDate = startDate,
            endDate = endDate,
            colors = graphColors,
            showYearLabel = false,
            showStreak = false,
            selection = GitGrassSelection(
                date = selectedDate,
                outlineColor = MaterialTheme.colorScheme.onSurface,
            ),
            onCellClick = { date, _ -> selectedDate = date },
            onCellLongClick = { date, count ->
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(
                        message = "$date has $count contributions",
                    )
                }
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(12.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(selectedColor, RoundedCornerShape(3.dp)),
                )
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text(
                        text = selectedDate.format(dateFormatter),
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = "$selectedCount contributions · level $selectedLevel",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricPill(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
