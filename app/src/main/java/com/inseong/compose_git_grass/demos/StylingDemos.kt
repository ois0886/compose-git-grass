package com.inseong.compose_git_grass.demos

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.inseong.compose_git_grass.components.DemoBlock
import com.inseong.compose_git_grass.components.SectionCard
import com.inseong.gitgrass.GitGrass
import com.inseong.gitgrass.GitGrassColors
import com.inseong.gitgrass.GitGrassDefaults
import java.time.LocalDate

@Composable
internal fun ThemeGallery(data: Map<LocalDate, Int>) {
    val endDate = remember { LocalDate.now() }
    val startDate = remember(endDate) { endDate.minusMonths(5) }
    val blueColors = remember {
        GitGrassColors(
            empty = Color(0xFFEFF6FF),
            levels = listOf(
                Color(0xFFBFDBFE),
                Color(0xFF60A5FA),
                Color(0xFF2563EB),
                Color(0xFF1E3A8A),
            ),
            text = Color(0xFF1E3A8A),
        )
    }

    SectionCard(
        title = "Themes",
        description = "Use the GitHub presets or provide any number of custom color levels.",
        badge = "COLOR",
    ) {
        DemoBlock(
            title = "GitHub dark",
            description = "The built-in dark palette on its native canvas.",
        ) {
            Surface(
                color = Color(0xFF0D1117),
                shape = RoundedCornerShape(12.dp),
            ) {
                GitGrass(
                    contributions = data,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    startDate = startDate,
                    endDate = endDate,
                    colors = GitGrassDefaults.darkColors(),
                    showYearLabel = false,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoBlock(
            title = "Blue scale",
            description = "A custom four-level palette without Material dependencies.",
        ) {
            GitGrass(
                contributions = data,
                modifier = Modifier.fillMaxWidth(),
                startDate = startDate,
                endDate = endDate,
                colors = blueColors,
                showYearLabel = false,
            )
        }
    }
}

@Composable
internal fun LevelsGallery(data: Map<LocalDate, Int>) {
    val endDate = remember { LocalDate.now() }
    val startDate = remember(endDate) { endDate.minusMonths(4) }

    SectionCard(
        title = "Levels",
        description = "Map domain values to visual intensity with a small pure function.",
        badge = "MAPPING",
    ) {
        DemoBlock(
            title = "Binary activity",
            description = "Every active day uses the strongest level.",
        ) {
            GitGrass(
                contributions = data,
                modifier = Modifier.fillMaxWidth(),
                startDate = startDate,
                endDate = endDate,
                showYearLabel = false,
                levelOf = { count -> if (count > 0) 4 else 0 },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoBlock(
            title = "Wide thresholds",
            description = "A slower ramp for higher-volume activity data.",
        ) {
            GitGrass(
                contributions = data,
                modifier = Modifier.fillMaxWidth(),
                startDate = startDate,
                endDate = endDate,
                showYearLabel = false,
                levelOf = { count ->
                    when {
                        count <= 0 -> 0
                        count < 5 -> 1
                        count < 10 -> 2
                        count < 15 -> 3
                        else -> 4
                    }
                },
            )
        }
    }
}
