package com.example.compusconnect

import org.junit.Assert.*
import org.junit.Test

class AuthHelperTest {

    @Test
    fun testValidLoginInput() {
        assertTrue(validateLoginInput("student@university.edu", "password123"))
    }

    @Test
    fun testInvalidEmailFormat() {
        assertFalse(validateLoginInput("notanemail", "password123"))
    }

    @Test
    fun testShortPassword() {
        assertFalse(validateLoginInput("student@university.edu", "123"))
    }

    @Test
    fun testBlankInput() {
        assertFalse(validateLoginInput("", "password123"))
        assertFalse(validateLoginInput("student@university.edu", ""))
    }
}
