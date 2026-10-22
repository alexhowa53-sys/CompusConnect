package com.example.compusconnect

import androidx.compose.ui.graphics.Color

const val MAX_TICKETS_PER_EVENT = 3

enum class EventCategory(val label: String) {
    MUSIC("MUSIC"),
    SPORTS("SPORTS"),
    CAREER("CAREER"),
    COMMUNITY("COMMUNITY");

    val color: Color
        get() = when (this) {
            MUSIC -> CampusAmber
            SPORTS -> CampusNavy
            CAREER, COMMUNITY -> CampusSageText
        }
}

data class Event(
    val id: String,
    val title: String,
    val day: String,
    val month: String,
    val time: String,
    val location: String,
    val description: String,
    val category: EventCategory,
    val isPaid: Boolean,
    val price: String,
    val isSaved: Boolean = false,
    val isPurchased: Boolean = false,
    val posterUrl: String = "poster_1",
    val ownerUid: String = "",
    val ticketsSold: Long = 0L
)

val sampleEvents = listOf(
    Event(
        id = "1",
        title = "Welcoming Function",
        day = "14",
        month = "OCT",
        time = "7:00 PM",
        location = "Great Hall",
        description = "Join us for the biggest night of the semester with live music and food trucks.",
        category = EventCategory.MUSIC,
        isPaid = true,
        price = "K5000.00",
        isSaved = true,
        isPurchased = true
    ),
    Event(
        id = "2",
        title = "Tech Careers Fair",
        day = "16",
        month = "OCT",
        time = "10:00 AM",
        location = "Computer Lab1",
        description = "Connect with top tech employers and alumni recruiters.",
        category = EventCategory.CAREER,
        isPaid = false,
        price = "",
        isSaved = true,
        isPurchased = false
    ),
    Event(
        id = "3",
        title = "UNIMA Football Finals",
        day = "18",
        month = "OCT",
        time = "4:30 PM",
        location = "Sports Complex",
        description = "Come support your hall in the championship match!",
        category = EventCategory.SPORTS,
        isPaid = true,
        price = "K2000.00",
        isSaved = false,
        isPurchased = true
    ),
    Event(
        id = "4",
        title = "Poetry & Spoken Word Night",
        day = "21",
        month = "OCT",
        time = "6:00 PM",
        location = "Room A",
        description = "An evening of powerful performances and acoustic interludes.",
        category = EventCategory.COMMUNITY,
        isPaid = false,
        price = "",
        isSaved = false,
        isPurchased = false
    )
)
