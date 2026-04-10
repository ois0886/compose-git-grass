package com.inseong.gitgrass

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import org.junit.Assert.assertEquals
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
                color = GitGrassDefaults.colors().levels[0],
                size = GitGrassDefaults.cellSize,
                cornerRadius = GitGrassDefaults.cellCornerRadius,
                date = date,
                count = count,
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
                color = GitGrassDefaults.colors().levels[0],
                size = GitGrassDefaults.cellSize,
                cornerRadius = GitGrassDefaults.cellCornerRadius,
                date = date,
                count = count,
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
                color = GitGrassDefaults.colors().empty,
                size = GitGrassDefaults.cellSize,
                cornerRadius = GitGrassDefaults.cellCornerRadius,
                date = date,
                count = count,
                contentDescriptionText = "$date: $count",
                clickLabelText = "$date details",
                onClick = null,
            )
        }

        composeTestRule.onNodeWithContentDescription("$date: $count")
            .assertExists()
    }
}
