package com.inseong.gitgrass

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
    fun gitGrass_rendersVisibleMonthLabelsInsideGrid() {
        composeTestRule.setContent {
            GitGrass(
                contributions = emptyMap(),
                startDate = LocalDate.of(2025, 1, 1),
                endDate = LocalDate.of(2025, 1, 15),
                showYearLabel = false,
                showWeekLabels = false,
                showLegend = false,
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

    @Test
    fun gitGrass_defaultWeekLabels_followSundayWeekStart() {
        val date = LocalDate.of(2025, 1, 5)

        composeTestRule.setContent {
            GitGrass(
                contributions = emptyMap(),
                startDate = date,
                endDate = date.plusDays(6),
                weekStartDay = java.time.DayOfWeek.SUNDAY,
                showYearLabel = false,
                showMonthLabels = false,
                showLegend = false,
            )
        }

        composeTestRule.onNodeWithText("Sun").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mon").assertDoesNotExist()
    }
}
