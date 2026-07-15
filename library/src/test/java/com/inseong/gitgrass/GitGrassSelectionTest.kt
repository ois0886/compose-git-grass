package com.inseong.gitgrass

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GitGrassSelectionTest {

    private val date = LocalDate.of(2025, 6, 15)

    @Test
    fun `resolveSelection returns null for no selection`() {
        assertNull(
            resolveSelection(
                selection = null,
                fallbackOutlineColor = Color.Black,
                cellSize = 12.dp,
            ),
        )
    }

    @Test
    fun `resolveSelection uses text color for unspecified outline`() {
        val result = resolveSelection(
            selection = GitGrassSelection(date = date),
            fallbackOutlineColor = Color.Magenta,
            cellSize = 12.dp,
        )

        requireNotNull(result)
        assertEquals(Color.Magenta, result.outlineColor)
    }

    @Test
    fun `resolveSelection preserves a custom outline color`() {
        val result = resolveSelection(
            selection = GitGrassSelection(date = date, outlineColor = Color.Blue),
            fallbackOutlineColor = Color.Black,
            cellSize = 12.dp,
        )

        requireNotNull(result)
        assertEquals(Color.Blue, result.outlineColor)
    }

    @Test
    fun `resolveSelection clamps negative and oversized widths`() {
        val negative = resolveSelection(
            selection = GitGrassSelection(date = date, outlineWidth = (-2).dp),
            fallbackOutlineColor = Color.Black,
            cellSize = 12.dp,
        )
        val oversized = resolveSelection(
            selection = GitGrassSelection(date = date, outlineWidth = 20.dp),
            fallbackOutlineColor = Color.Black,
            cellSize = 12.dp,
        )

        requireNotNull(negative)
        requireNotNull(oversized)
        assertEquals(0.dp, negative.outlineWidth)
        assertEquals(6.dp, oversized.outlineWidth)
    }
}
