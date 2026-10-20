package com.example.compusconnect

import org.junit.Assert.*
import org.junit.Test

class EventFilterTest {

    private val testEvents = listOf(
        Event(
            id = "1",
            title = "Homecoming Concert",
            day = "14",
            month = "OCT",
            time = "7:00 PM",
            location = "Great Hall",
            description = "Live music and food trucks.",
            category = EventCategory.MUSIC,
            isPaid = true,
            price = "$5.00"
        ),
        Event(
            id = "2",
            title = "Tech Careers Fair",
            day = "16",
            month = "OCT",
            time = "10:00 AM",
            location = "Student Center",
            description = "Connect with top employers.",
            category = EventCategory.CAREER,
            isPaid = false,
            price = ""
        ),
        Event(
            id = "3",
            title = "Inter-Hall Football",
            day = "18",
            month = "OCT",
            time = "4:30 PM",
            location = "Sports Complex",
            description = "Championship match.",
            category = EventCategory.SPORTS,
            isPaid = true,
            price = "$2.00"
        )
    )

    @Test
    fun testCategoryFiltering() {
        val musicEvents = testEvents.filter { it.category == EventCategory.MUSIC }
        assertEquals(1, musicEvents.size)
        assertEquals("Homecoming Concert", musicEvents[0].title)

        val sportsEvents = testEvents.filter { it.category == EventCategory.SPORTS }
        assertEquals(1, sportsEvents.size)
        assertEquals("Inter-Hall Football", sportsEvents[0].title)
    }

    @Test
    fun testPricingFiltering() {
        val freeEvents = testEvents.filter { !it.isPaid }
        assertEquals(1, freeEvents.size)
        assertEquals("Tech Careers Fair", freeEvents[0].title)

        val paidEvents = testEvents.filter { it.isPaid }
        assertEquals(2, paidEvents.size)
    }

    @Test
    fun testSearchQueryMatching() {
        val query = "Tech"
        val results = testEvents.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.location.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
        assertEquals(1, results.size)
        assertEquals("Tech Careers Fair", results[0].title)
    }
}
