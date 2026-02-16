package com.inseong.gitgrass

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class LevelToColorTest {

    private val colors = GitGrassColors(
        empty = Color.Gray,
        levels = listOf(Color.Green, Color.Blue, Color.Red),
        text = Color.Black,
        border = Color.White,
    )

    @Test
    fun `level 0 returns empty color`() {
        assertEquals(Color.Gray, levelToColor(0, colors))
    }

    @Test
    fun `negative level returns empty color`() {
        assertEquals(Color.Gray, levelToColor(-1, colors))
    }

    @Test
    fun `level 1 returns first level color`() {
        assertEquals(Color.Green, levelToColor(1, colors))
    }

    @Test
    fun `level 2 returns second level color`() {
        assertEquals(Color.Blue, levelToColor(2, colors))
    }

    @Test
    fun `level 3 returns third level color`() {
        assertEquals(Color.Red, levelToColor(3, colors))
    }

    @Test
    fun `level exceeding list size clamps to last color`() {
        assertEquals(Color.Red, levelToColor(4, colors))
        assertEquals(Color.Red, levelToColor(100, colors))
    }

    @Test
    fun `empty levels list always returns empty color`() {
        val noLevels = colors.copy(levels = emptyList())

        assertEquals(Color.Gray, levelToColor(0, noLevels))
        assertEquals(Color.Gray, levelToColor(1, noLevels))
        assertEquals(Color.Gray, levelToColor(5, noLevels))
    }

    @Test
    fun `single level color works correctly`() {
        val singleLevel = colors.copy(levels = listOf(Color.Cyan))

        assertEquals(Color.Gray, levelToColor(0, singleLevel))
        assertEquals(Color.Cyan, levelToColor(1, singleLevel))
        assertEquals(Color.Cyan, levelToColor(5, singleLevel))
    }
}
