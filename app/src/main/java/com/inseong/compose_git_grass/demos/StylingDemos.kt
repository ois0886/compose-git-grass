package com.inseong.compose_git_grass.demos

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inseong.compose_git_grass.components.SectionCard
import com.inseong.gitgrass.GitGrass
import com.inseong.gitgrass.GitGrassColors
import java.time.LocalDate

@Composable
internal fun CustomCellSizingDemo(data: Map<LocalDate, Int>) {
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
internal fun CustomLevelMappingDemo(data: Map<LocalDate, Int>) {
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

@Suppress("DEPRECATION")
@Composable
internal fun CustomColorsDemo(data: Map<LocalDate, Int>) {
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
