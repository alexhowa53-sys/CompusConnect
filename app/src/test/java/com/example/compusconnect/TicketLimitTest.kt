package com.example.compusconnect

import org.junit.Assert.*
import org.junit.Test

class TicketLimitTest {

    private fun maxAllowedQuantity(alreadyHeld: Int, max: Int = 3): Int {
        return (max - alreadyHeld).coerceAtLeast(0)
    }

    @Test
    fun testMaxAllowedQuantity() {
        assertEquals(3, maxAllowedQuantity(0))
        assertEquals(1, maxAllowedQuantity(2))
        assertEquals(0, maxAllowedQuantity(3))
        assertEquals(0, maxAllowedQuantity(4))
    }

    @Test
    fun testTotalPriceCalculation() {
        val priceStr = "K5000.00"
        val priceNum = priceStr.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
        val quantity = 2
        val total = priceNum * quantity
        assertEquals(10000.0, total, 0.01)
    }

    @Test
    fun testUnreadCountCalculation() {
        val notifications = listOf(
            NotificationItem(id = "1", isRead = false),
            NotificationItem(id = "2", isRead = true),
            NotificationItem(id = "3", isRead = false)
        )
        val unreadCount = notifications.count { !it.isRead }
        assertEquals(2, unreadCount)
    }

    @Test
    fun testEmailFormatValidation() {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        assertTrue(emailRegex.matches("student@university.edu"))
        assertFalse(emailRegex.matches("invalid-email"))
    }
}
