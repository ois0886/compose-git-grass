package com.inseong.compose_git_grass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inseong.compose_git_grass.ui.theme.ComposegitgrassTheme
import com.inseong.gitgrass.GitGrass
import com.inseong.gitgrass.GitGrassColors
import com.inseong.gitgrass.GitGrassDefaults
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposegitgrassTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(title = { Text("compose-git-grass") })
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                ) { innerPadding ->
                    GitGrassShowcase(
                        modifier = Modifier.padding(innerPadding),
                        snackbarHostState = snackbarHostState,
                    )
                }
            }
        }
    }
}

@Composable
fun GitGrassShowcase(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
) {
    val sampleData = remember { generateSampleData() }
    val sparseData = remember { generateSparseData() }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item { BasicDemo(sampleData) }
        item { DarkThemeDemo(sampleData) }
        item { CustomDateRangeDemo(sparseData) }
        item { WeekStartDayDemo(sampleData) }
        item { KoreanLocalizationDemo(sampleData) }
        item { CustomCellSizingDemo(sampleData) }
        item { ToggleVisibilityDemo(sampleData) }
        item { CustomLevelMappingDemo(sampleData) }
        item { CustomColorsDemo(sampleData) }
        item { CellClickDemo(sampleData, snackbarHostState) }
    }
}

// region Demo Sections

@Composable
private fun BasicDemo(data: Map<LocalDate, Int>) {
    SectionCard(
        title = "Basic",
        description = "Default settings - just pass contributions data",
    ) {
        GitGrass(
            contributions = data,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun DarkThemeDemo(data: Map<LocalDate, Int>) {
    SectionCard(
        title = "Dark Theme",
        description = "GitGrassDefaults.darkColors() on dark background",
    ) {
        Surface(
            color = Color(0xFF0D1117),
            shape = RoundedCornerShape(8.dp),
        ) {
            GitGrass(
                contributions = data,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = GitGrassDefaults.darkColors(),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Auto theme (follows system)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(4.dp))

        val autoColors = if (isSystemInDarkTheme()) {
            GitGrassDefaults.darkColors()
        } else {
            GitGrassDefaults.colors()
        }
        GitGrass(
            contributions = data,
            modifier = Modifier.fillMaxWidth(),
            colors = autoColors,
        )
    }
}

@Composable
private fun CustomDateRangeDemo(data: Map<LocalDate, Int>) {
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
private fun WeekStartDayDemo(data: Map<LocalDate, Int>) {
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
private fun KoreanLocalizationDemo(data: Map<LocalDate, Int>) {
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
        )
    }
}

@Composable
private fun CustomCellSizingDemo(data: Map<LocalDate, Int>) {
    SectionCard(
        title = "Custom Cell Sizing",
        description = "Large rounded cells vs tiny dense cells",
    ) {
        Text(
            text = "Large (20dp, radius 10dp)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        GitGrass(
            contributions = data,
            modifier = Modifier.fillMaxWidth(),
            startDate = LocalDate.now().minusMonths(3),
            endDate = LocalDate.now(),
            cellSize = 20.dp,
            cellSpacing = 4.dp,
            cellCornerRadius = 10.dp,
            labelFontSize = 14.sp,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tiny (8dp, radius 0dp)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        GitGrass(
            contributions = data,
            modifier = Modifier.fillMaxWidth(),
            cellSize = 8.dp,
            cellSpacing = 1.dp,
            cellCornerRadius = 0.dp,
            labelFontSize = 8.sp,
        )
    }
}

@Composable
private fun ToggleVisibilityDemo(data: Map<LocalDate, Int>) {
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
            startDate = LocalDate.now().minusMonths(3),
            endDate = LocalDate.now(),
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
            startDate = LocalDate.now().minusMonths(3),
            endDate = LocalDate.now(),
            showYearLabel = true,
            showWeekLabels = true,
            showMonthLabels = true,
            showStreak = true,
            showLegend = true,
        )
    }
}

@Composable
private fun CustomLevelMappingDemo(data: Map<LocalDate, Int>) {
    SectionCard(
        title = "Custom Level Mapping",
        description = "Binary: any contribution = max level",
    ) {
        GitGrass(
            contributions = data,
            modifier = Modifier.fillMaxWidth(),
            startDate = LocalDate.now().minusMonths(3),
            endDate = LocalDate.now(),
            levelOf = { count -> if (count > 0) 4 else 0 },
        )
    }
}

@Composable
private fun CustomColorsDemo(data: Map<LocalDate, Int>) {
    SectionCard(
        title = "Custom Colors",
        description = "Blue ocean & warm sunset color palettes",
    ) {
        Text(
            text = "Blue Ocean",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        GitGrass(
            contributions = data,
            modifier = Modifier.fillMaxWidth(),
            startDate = LocalDate.now().minusMonths(6),
            endDate = LocalDate.now(),
            colors = GitGrassColors(
                empty = Color(0xFFE8EAF6),
                levels = listOf(
                    Color(0xFFBBDEFB),
                    Color(0xFF64B5F6),
                    Color(0xFF1E88E5),
                    Color(0xFF0D47A1),
                ),
                text = Color(0xFF1A237E),
                border = Color(0xFFC5CAE9),
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Warm Sunset",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        GitGrass(
            contributions = data,
            modifier = Modifier.fillMaxWidth(),
            startDate = LocalDate.now().minusMonths(6),
            endDate = LocalDate.now(),
            colors = GitGrassColors(
                empty = Color(0xFFFFF3E0),
                levels = listOf(
                    Color(0xFFFFCC80),
                    Color(0xFFFF9800),
                    Color(0xFFF57C00),
                    Color(0xFFE65100),
                ),
                text = Color(0xFFBF360C),
                border = Color(0xFFFFE0B2),
            ),
        )
    }
}

@Composable
private fun CellClickDemo(
    data: Map<LocalDate, Int>,
    snackbarHostState: SnackbarHostState,
) {
    val scope = rememberCoroutineScope()

    SectionCard(
        title = "Cell Click & Long-Click",
        description = "Tap or long-press a cell to see a Snackbar",
    ) {
        GitGrass(
            contributions = data,
            modifier = Modifier.fillMaxWidth(),
            startDate = LocalDate.now().minusMonths(3),
            endDate = LocalDate.now(),
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

// endregion

// region Components

@Composable
private fun SectionCard(
    title: String,
    description: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

// endregion

// region Data Generators

private fun generateSampleData(): Map<LocalDate, Int> {
    val random = Random(seed = 42)
    val start = GitGrassDefaults.startDate()
    val end = GitGrassDefaults.endDate()
    val map = mutableMapOf<LocalDate, Int>()
    var current = start
    while (!current.isAfter(end)) {
        if (random.nextFloat() > 0.3f) {
            map[current] = random.nextInt(1, 15)
        }
        current = current.plusDays(1)
    }
    return map
}

private fun generateSparseData(): Map<LocalDate, Int> {
    val random = Random(seed = 123)
    val start = LocalDate.now().minusMonths(6)
    val end = LocalDate.now()
    val map = mutableMapOf<LocalDate, Int>()
    var current = start
    while (!current.isAfter(end)) {
        if (random.nextFloat() > 0.8f) {
            map[current] = random.nextInt(1, 6)
        }
        current = current.plusDays(1)
    }
    return map
}

// endregion
