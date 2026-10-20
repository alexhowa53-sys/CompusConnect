package com.example.compusconnect

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class AppNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testBottomNavigationSwitching() {
        composeTestRule.setContent {
            MainApp()
        }

        // Wait for splash screen to finish or skip
        composeTestRule.waitForIdle()

        // Verify Home screen is displayed initially
        composeTestRule.onNodeWithText("CampusConnect").assertExists()
        composeTestRule.onNodeWithText("Upcoming events").assertExists()

        // Tap Search tab in bottom bar
        composeTestRule.onNodeWithText("Search").performClick()

        // Verify Search screen headline
        composeTestRule.onNodeWithText("Find your next thing").assertExists()

        // Tap Tickets tab
        composeTestRule.onNodeWithText("Tickets").performClick()
        composeTestRule.onNodeWithText("My Tickets").assertExists()

        // Tap Profile tab
        composeTestRule.onNodeWithText("Profile").performClick()
        composeTestRule.onNodeWithText("You're not logged in").assertExists()
    }
}
