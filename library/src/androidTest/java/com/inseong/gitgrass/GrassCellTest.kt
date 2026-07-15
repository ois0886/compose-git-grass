package com.inseong.gitgrass

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class GrassCellTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun grassCell_click_invokesCallback() {
        var clicked = false
        val date = LocalDate.of(2025, 6, 15)
        val count = 5

        composeTestRule.setContent {
            GrassCell(
                size = GitGrassDefaults.cellSize,
                contentDescriptionText = "$date: $count",
                clickLabelText = "$date details",
                onClick = { clicked = true },
            )
        }

        composeTestRule.onNodeWithContentDescription("$date: $count")
            .performClick()

        assertTrue(clicked)
    }

    @Test
    fun grassCell_longClick_invokesCallback() {
        var longClicked = false
        val date = LocalDate.of(2025, 6, 15)
        val count = 3

        composeTestRule.setContent {
            GrassCell(
                size = GitGrassDefaults.cellSize,
                contentDescriptionText = "$date: $count",
                clickLabelText = "$date details",
                onClick = {},
                onLongClick = { longClicked = true },
            )
        }

        composeTestRule.onNodeWithContentDescription("$date: $count")
            .performTouchInput {
                longClick()
            }

        assertTrue(longClicked)
    }

    @Test
    fun grassCell_noCallback_displaysWithoutCrash() {
        val date = LocalDate.of(2025, 6, 15)
        val count = 0

        composeTestRule.setContent {
            GrassCell(
                size = GitGrassDefaults.cellSize,
                contentDescriptionText = "$date: $count",
                clickLabelText = "$date details",
                onClick = null,
            )
        }

        composeTestRule.onNodeWithContentDescription("$date: $count")
            .assertExists()
    }

    @Test
    fun gitGrass_canvasBackedCells_keepAccessibilitySemantics() {
        val date = LocalDate.of(2025, 6, 15)
        val count = 7

        composeTestRule.setContent {
            GitGrass(
                contributions = mapOf(date to count),
                startDate = date,
                endDate = date,
                showYearLabel = false,
                showWeekLabels = false,
                showMonthLabels = false,
                showLegend = false,
            )
        }

        composeTestRule.onNodeWithContentDescription("$date: $count")
            .assertExists()
    }

    @Test
    fun gitGrass_selectedCell_exposesSelectedSemantics() {
        val date = LocalDate.of(2025, 6, 15)

        composeTestRule.setContent {
            GitGrass(
                contributions = mapOf(date to 4),
                startDate = date,
                endDate = date,
                selection = GitGrassSelection(date = date),
                showYearLabel = false,
                showWeekLabels = false,
                showMonthLabels = false,
                showLegend = false,
            )
        }

        composeTestRule.onNodeWithContentDescription("$date: 4")
            .assertIsSelected()
    }

    @Test
    fun grassCell_longClickOnly_hasNoClickAction() {
        val description = "Long-click only"

        composeTestRule.setContent {
            GrassCell(
                size = GitGrassDefaults.cellSize,
                contentDescriptionText = description,
                clickLabelText = null,
                onClick = null,
                onLongClick = {},
            )
        }

        composeTestRule.onNodeWithContentDescription(description)
            .assertHasNoClickAction()
            .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.OnLongClick))
    }
}
