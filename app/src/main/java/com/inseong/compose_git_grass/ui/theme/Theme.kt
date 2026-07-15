package com.inseong.compose_git_grass.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GitHubDarkPrimary,
    onPrimary = GitHubDarkBackground,
    background = GitHubDarkBackground,
    onBackground = GitHubDarkText,
    surface = GitHubDarkSurface,
    onSurface = GitHubDarkText,
    surfaceVariant = GitHubDarkSurfaceVariant,
    onSurfaceVariant = GitHubDarkMutedText,
    outline = GitHubDarkBorder,
    outlineVariant = GitHubDarkBorder,
    error = Color(0xFFF85149),
)

private val LightColorScheme = lightColorScheme(
    primary = GitHubLightPrimary,
    onPrimary = Color.White,
    background = GitHubLightBackground,
    onBackground = GitHubLightText,
    surface = GitHubLightSurface,
    onSurface = GitHubLightText,
    surfaceVariant = GitHubLightSurfaceVariant,
    onSurfaceVariant = GitHubLightMutedText,
    outline = GitHubLightBorder,
    outlineVariant = GitHubLightBorder,
    error = Color(0xFFCF222E),
)

@Composable
fun ComposegitgrassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
