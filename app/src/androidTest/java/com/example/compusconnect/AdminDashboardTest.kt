package com.example.compusconnect

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class AdminDashboardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAdminDashboardAndCreationFlow() {
        composeTestRule.setContent {
            MainApp()
        }

        composeTestRule.waitForIdle()

        // Navigate to Profile tab
        composeTestRule.onNodeWithText("Profile").performClick()

        // Click Log in
        composeTestRule.onNodeWithText("Log in").performClick()

        // Type test admin email and password in LoginAuthScreen
        composeTestRule.onNodeWithText("Campus Email").performTextInput("admin@compusconnect.com")
        composeTestRule.onNodeWithText("Password").performTextInput("admin123")

        // Click Log in button
        composeTestRule.onNodeWithText("Log in to CampusConnect").performClick()

        // Verify Admin Dashboard is displayed ("Good morning, Maya")
        composeTestRule.onNodeWithText("Good morning, Maya").assertExists()
        composeTestRule.onNodeWithText("STUDENT RADIO · ADMIN").assertExists()

        // Click "+ Create a new event"
        composeTestRule.onNodeWithText("Create a new event").performClick()

        // Verify Create Event screen title
        composeTestRule.onNodeWithText("Create Event").assertExists()
    }
}
