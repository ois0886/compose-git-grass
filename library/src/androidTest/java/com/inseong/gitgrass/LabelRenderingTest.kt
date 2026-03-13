package com.inseong.gitgrass

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class LabelRenderingTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun monthRow_rendersVisibleMonthLabels() {
        val days = generateDayList(
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 3, 31),
        )
        val grid = buildGrid(days)
        val monthPositions = createMonthLabels(grid)

        composeTestRule.setContent {
            MonthRow(
                weekCount = grid.size,
                monthPositions = monthPositions,
                monthLabels = GitGrassDefaults.monthLabels,
                cellSize = GitGrassDefaults.cellSize,
                cellSpacing = GitGrassDefaults.cellSpacing,
                fontSize = GitGrassDefaults.labelFontSize,
                textColor = GitGrassDefaults.colors().text,
                scrollState = ScrollState(0),
                weekLabelWidth = GitGrassDefaults.weekLabelWidth,
            )
        }

        composeTestRule.onNodeWithText("Jan").assertIsDisplayed()
    }

    @Test
    fun weekLabelColumn_rendersAlternateLabels() {
        composeTestRule.setContent {
            WeekLabelColumn(
                weekLabels = GitGrassDefaults.weekLabels,
                cellSize = GitGrassDefaults.cellSize,
                cellSpacing = GitGrassDefaults.cellSpacing,
                fontSize = GitGrassDefaults.labelFontSize,
                textColor = GitGrassDefaults.colors().text,
            )
        }

        // Even indices (0, 2, 4, 6) show labels
        composeTestRule.onNodeWithText("Mon").assertIsDisplayed()
        composeTestRule.onNodeWithText("Wed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fri").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sun").assertIsDisplayed()
    }
}
