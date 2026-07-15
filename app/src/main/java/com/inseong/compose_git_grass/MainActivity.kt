package com.inseong.compose_git_grass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inseong.compose_git_grass.data.generateSampleData
import com.inseong.compose_git_grass.demos.FeaturedContributionDemo
import com.inseong.compose_git_grass.demos.LayoutGallery
import com.inseong.compose_git_grass.demos.LevelsGallery
import com.inseong.compose_git_grass.demos.LocalizationGallery
import com.inseong.compose_git_grass.demos.ThemeGallery
import com.inseong.compose_git_grass.ui.theme.ComposegitgrassTheme

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
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        TopAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = "compose-git-grass",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = "Jetpack Compose contribution graph",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background,
                            ),
                        )
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

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, top = 18.dp, end = 18.dp, bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            ShowcaseIntro()
        }
        item {
            FeaturedContributionDemo(sampleData, snackbarHostState)
        }
        item {
            GalleryIntro()
        }
        item {
            LayoutGallery(sampleData)
        }
        item {
            LocalizationGallery(sampleData)
        }
        item {
            ThemeGallery(sampleData)
        }
        item {
            LevelsGallery(sampleData)
        }
    }
}

@Composable
private fun ShowcaseIntro() {
    Column {
        Text(
            text = "Daily activity, clearly composed.",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "A lightweight, Foundation-only contribution graph with controlled selection, localization, and accessible interactions.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun GalleryIntro() {
    Column {
        Text(text = "API gallery", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "The core customization paths, grouped by intent.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
