package com.example.compusconnect

import org.junit.Assert.*
import org.junit.Test

class AuthHelperTest {

    @Test
    fun testAdminLoginSuccess() {
        val session = attemptLogin("admin@compusconnect.com", "admin123")
        assertNotNull(session)
        assertTrue(session!!.isLoggedIn)
        assertEquals(UserRole.ADMIN, session.role)
        assertEquals("admin@compusconnect.com", session.email)
    }

    @Test
    fun testStudentLoginSuccess() {
        val session = attemptLogin("alex@university.edu", "mypassword")
        assertNotNull(session)
        assertTrue(session!!.isLoggedIn)
        assertEquals(UserRole.STUDENT, session.role)
        assertEquals("Alex", session.name)
    }

    @Test
    fun testInvalidLoginBlank() {
        val session1 = attemptLogin("", "password")
        assertNull(session1)

        val session2 = attemptLogin("user@test.com", "")
        assertNull(session2)

        val session3 = attemptLogin("   ", "   ")
        assertNull(session3)
    }
}
