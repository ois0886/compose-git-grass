package com.inseong.compose_git_grass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inseong.compose_git_grass.data.generateSampleData
import com.inseong.compose_git_grass.data.generateSparseData
import com.inseong.compose_git_grass.demos.BasicDemo
import com.inseong.compose_git_grass.demos.CellClickDemo
import com.inseong.compose_git_grass.demos.CustomCellSizingDemo
import com.inseong.compose_git_grass.demos.CustomColorsDemo
import com.inseong.compose_git_grass.demos.CustomDateRangeDemo
import com.inseong.compose_git_grass.demos.CustomLevelMappingDemo
import com.inseong.compose_git_grass.demos.DarkThemeDemo
import com.inseong.compose_git_grass.demos.KoreanLocalizationDemo
import com.inseong.compose_git_grass.demos.ToggleVisibilityDemo
import com.inseong.compose_git_grass.demos.WeekStartDayDemo
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
