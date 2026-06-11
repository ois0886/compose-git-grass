package com.inseong.gitgrass

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class RenderDataTest {

    @Test
    fun `buildRenderGrid precomputes count color and labels`() {
        val date = LocalDate.of(2025, 1, 1)
        val grid = listOf(listOf(date))
        val colors = GitGrassColors(
            empty = Color.Gray,
            levels = listOf(Color.Green),
            text = Color.Black,
        )

        val renderGrid = buildRenderGrid(
            grid = grid,
            contributions = mapOf(date to 3),
            colors = colors,
            levelOf = { count -> if (count > 0) 1 else 0 },
            cellContentDescription = { day, count -> "$day has $count" },
            cellClickLabel = { day -> "Open $day" },
            includeClickLabels = true,
        )

        val cell = renderGrid[0][0]
        requireNotNull(cell)
        assertEquals(date, cell.date)
        assertEquals(3, cell.count)
        assertEquals(Color.Green, cell.color)
        assertEquals("$date has 3", cell.contentDescription)
        assertEquals("Open $date", cell.clickLabel)
    }

    @Test
    fun `buildRenderGrid omits click labels when cells are not interactive`() {
        val date = LocalDate.of(2025, 1, 1)
        val renderGrid = buildRenderGrid(
            grid = listOf(listOf(date)),
            contributions = emptyMap(),
            colors = GitGrassDefaults.colors(),
            levelOf = GitGrassDefaults.levelThresholds,
            cellContentDescription = { day, count -> "$day: $count" },
            cellClickLabel = { day -> "Open $day" },
            includeClickLabels = false,
        )

        val cell = renderGrid[0][0]
        requireNotNull(cell)
        assertEquals(0, cell.count)
        assertEquals(GitGrassDefaults.colors().empty, cell.color)
        assertNull(cell.clickLabel)
    }

    @Test
    fun `buildRenderGrid preserves null padding slots`() {
        val renderGrid = buildRenderGrid(
            grid = listOf(listOf(null, LocalDate.of(2025, 1, 2))),
            contributions = emptyMap(),
            colors = GitGrassDefaults.colors(),
            levelOf = GitGrassDefaults.levelThresholds,
            cellContentDescription = { day, count -> "$day: $count" },
            cellClickLabel = { day -> "Open $day" },
            includeClickLabels = false,
        )

        assertNull(renderGrid[0][0])
        requireNotNull(renderGrid[0][1])
    }
}
