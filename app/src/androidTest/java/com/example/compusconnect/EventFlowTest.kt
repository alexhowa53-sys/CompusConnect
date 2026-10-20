package com.example.compusconnect

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class EventFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testEventDetailsAndBookingFlow() {
        composeTestRule.setContent {
            MainApp()
        }

        composeTestRule.waitForIdle()

        // Click on an event card on the Home screen
        composeTestRule.onNodeWithText("Welcoming Function").performClick()

        // Verify Event Details screen displays title and "About this event"
        composeTestRule.onNodeWithText("Welcoming Function").assertExists()
        composeTestRule.onNodeWithText("About this event").assertExists()

        // Click "Buy Ticket" button
        composeTestRule.onNodeWithText("Buy Ticket").performClick()

        // Verify Ticket Confirmation screen ("You're on the list!")
        composeTestRule.onNodeWithText("You're on the list!").assertExists()
    }
}
