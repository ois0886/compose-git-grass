package com.inseong.compose_git_grass.demos

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inseong.compose_git_grass.components.SectionCard
import com.inseong.gitgrass.GitGrass
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
internal fun ToggleVisibilityDemo(data: Map<LocalDate, Int>) {
    val dateRange = remember {
        val end = LocalDate.now()
        end.minusMonths(3) to end
    }

    SectionCard(
        title = "Toggle Visibility",
        description = "All labels/streak/legend hidden vs shown",
    ) {
        Text(
            text = "Everything hidden",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        GitGrass(
            contributions = data,
            modifier = Modifier.fillMaxWidth(),
            startDate = dateRange.first,
            endDate = dateRange.second,
            showYearLabel = false,
            showWeekLabels = false,
            showMonthLabels = false,
            showStreak = false,
            showLegend = false,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Everything shown",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        GitGrass(
            contributions = data,
            modifier = Modifier.fillMaxWidth(),
            startDate = dateRange.first,
            endDate = dateRange.second,
            showYearLabel = true,
            showWeekLabels = true,
            showMonthLabels = true,
            showStreak = true,
            showLegend = true,
        )
    }
}

@Composable
internal fun CellClickDemo(
    data: Map<LocalDate, Int>,
    snackbarHostState: SnackbarHostState,
) {
    val scope = rememberCoroutineScope()
    val dateRange = remember {
        val end = LocalDate.now()
        end.minusMonths(3) to end
    }

    SectionCard(
        title = "Cell Click & Long-Click",
        description = "Tap or long-press a cell to see a Snackbar",
    ) {
        GitGrass(
            contributions = data,
            modifier = Modifier.fillMaxWidth(),
            startDate = dateRange.first,
            endDate = dateRange.second,
            showStreak = true,
            onCellClick = { date, count ->
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar("Clicked: $date ($count contributions)")
                }
            },
            onCellLongClick = { date, count ->
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar("Long-pressed: $date ($count contributions)")
                }
            },
        )
    }
}
