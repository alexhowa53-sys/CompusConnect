package com.example.compusconnect

data class NotificationItem(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "new_event", // "new_event", "event_updated", "event_cancelled", "ticket_confirmed"
    val eventId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
