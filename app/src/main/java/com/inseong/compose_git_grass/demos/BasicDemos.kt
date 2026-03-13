package com.inseong.compose_git_grass.demos

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.inseong.compose_git_grass.components.SectionCard
import com.inseong.gitgrass.GitGrass
import com.inseong.gitgrass.GitGrassDefaults
import java.time.LocalDate

@Composable
internal fun BasicDemo(data: Map<LocalDate, Int>) {
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
internal fun DarkThemeDemo(data: Map<LocalDate, Int>) {
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
