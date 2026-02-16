package com.inseong.compose_git_grass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.inseong.compose_git_grass.ui.theme.ComposegitgrassTheme
import com.inseong.gitgrass.GitGrass
import com.inseong.gitgrass.GitGrassDefaults
import java.time.LocalDate
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposegitgrassTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center,
                    ) {
                        GitGrassDemo()
                    }
                }
            }
        }
    }
}

@Composable
fun GitGrassDemo(modifier: Modifier = Modifier) {
    val contributions = remember { generateSampleData() }

    GitGrass(
        contributions = contributions,
        modifier = modifier.padding(horizontal = 16.dp),
        showStreak = true,
    )
}

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

@Preview(showBackground = true)
@Composable
fun GitGrassDemoPreview() {
    ComposegitgrassTheme {
        GitGrassDemo()
    }
}
