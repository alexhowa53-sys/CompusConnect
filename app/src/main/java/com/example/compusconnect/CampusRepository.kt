package com.example.compusconnect

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class CampusRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun signIn(email: String, password: String): Result<UserSession> {
        return try {
            val trimmedEmail = email.trim()
            val result = auth.signInWithEmailAndPassword(trimmedEmail, password).await()
            val user = result.user ?: return Result.failure(Exception("Authentication failed."))
            
            // Read role securely from Firestore users/{uid}, auto-provisioning if missing
            val userDoc = try {
                val doc = db.collection("users").document(user.uid).get().await()
                if (!doc.exists()) {
                    val defaultData = mapOf(
                        "name" to (user.displayName ?: trimmedEmail.substringBefore("@")),
                        "email" to trimmedEmail,
                        "role" to "student"
                    )
                    db.collection("users").document(user.uid).set(defaultData).await()
                    db.collection("users").document(user.uid).get().await()
                } else {
                    doc
                }
            } catch (e: Exception) {
                null
            }

            val roleStr = userDoc?.getString("role") ?: "student"
            val role = if (roleStr.equals("admin", ignoreCase = true)) UserRole.ADMIN else UserRole.STUDENT
            val name = userDoc?.getString("name") ?: user.displayName ?: trimmedEmail.substringBefore("@")

            Result.success(
                UserSession(
                    isLoggedIn = true,
                    uid = user.uid,
                    name = name,
                    email = trimmedEmail,
                    role = role
                )
            )
        } catch (e: Exception) {
            Log.e("CampusRepo", "Sign in failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun fetchUserRole(uid: String): UserRole {
        return try {
            val doc = db.collection("users").document(uid).get().await()
            val roleStr = doc.getString("role") ?: "student"
            if (roleStr.equals("admin", ignoreCase = true)) UserRole.ADMIN else UserRole.STUDENT
        } catch (e: Exception) {
            UserRole.STUDENT
        }
    }

    suspend fun signUp(email: String, password: String, name: String): Result<UserSession> {
        return try {
            val trimmedEmail = email.trim()
            val trimmedName = name.trim()
            val result = auth.createUserWithEmailAndPassword(trimmedEmail, password).await()
            val user = result.user ?: return Result.failure(Exception("Sign up failed."))

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(trimmedName)
                .build()
            user.updateProfile(profileUpdates).await()

            // Create Firestore user document with role = "student"
            val userData = mapOf(
                "name" to trimmedName,
                "email" to trimmedEmail,
                "role" to "student"
            )
            try {
                db.collection("users").document(user.uid).set(userData).await()
            } catch (e: Exception) {
                Log.e("CampusRepo", "Failed to write user doc to Firestore", e)
            }

            Result.success(
                UserSession(
                    isLoggedIn = true,
                    uid = user.uid,
                    name = trimmedName,
                    email = trimmedEmail,
                    role = UserRole.STUDENT
                )
            )
        } catch (e: Exception) {
            Log.e("CampusRepo", "Sign up failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun fetchEvents(): List<Event> {
        return try {
            val snapshot = db.collection("events").get().await()
            if (snapshot.isEmpty) {
                emptyList()
            } else {
                snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    Event(
                        id = doc.id,
                        title = data["title"] as? String ?: "Untitled Event",
                        day = data["day"] as? String ?: "14",
                        month = data["month"] as? String ?: "OCT",
                        time = data["time"] as? String ?: "7:00 PM",
                        location = data["location"] as? String ?: "Campus",
                        description = data["description"] as? String ?: "",
                        category = try { EventCategory.valueOf(data["category"] as? String ?: "MUSIC") } catch (e: Exception) { EventCategory.MUSIC },
                        isPaid = data["isPaid"] as? Boolean ?: false,
                        price = data["price"] as? String ?: "",
                        posterUrl = data["posterUrl"] as? String ?: "",
                        ownerUid = data["ownerUid"] as? String ?: "",
                        ticketsSold = data["ticketsSold"] as? Long ?: 0L
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("CampusRepo", "Fetch events failed", e)
            emptyList()
        }
    }

    suspend fun uploadPosterImage(imageUri: Uri): Result<String> {
        return try {
            val posterRef = storage.reference.child("event_posters/${System.currentTimeMillis()}.jpg")
            posterRef.putFile(imageUri).await()
            val downloadUrl = posterRef.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e("CampusRepo", "Upload poster image failed", e)
            Result.failure(e)
        }
    }

    suspend fun addEvent(event: Event, ownerUid: String): Boolean {
        return try {
            val eventMap = mapOf(
                "title" to event.title,
                "day" to event.day,
                "month" to event.month,
                "time" to event.time,
                "location" to event.location,
                "description" to event.description,
                "category" to event.category.name,
                "isPaid" to event.isPaid,
                "price" to event.price,
                "posterUrl" to event.posterUrl,
                "ownerUid" to ownerUid,
                "ticketsSold" to 0L
            )
            db.collection("events").document(event.id).set(eventMap).await()
            createBroadcastNotification("New event: ${event.title}", "Check out the new event on campus.", "new_event", event.id, ownerUid)
            true
        } catch (e: Exception) {
            Log.e("CampusRepo", "Add event failed", e)
            false
        }
    }

    suspend fun updateEvent(event: Event): Boolean {
        return try {
            val eventMap = mapOf(
                "title" to event.title,
                "day" to event.day,
                "month" to event.month,
                "time" to event.time,
                "location" to event.location,
                "description" to event.description,
                "category" to event.category.name,
                "isPaid" to event.isPaid,
                "price" to event.price,
                "posterUrl" to event.posterUrl
            )
            db.collection("events").document(event.id).update(eventMap).await()
            createBroadcastNotification("Event updated", "${event.title} details have been updated.", "event_updated", event.id, event.ownerUid)
            true
        } catch (e: Exception) {
            Log.e("CampusRepo", "Update event failed", e)
            false
        }
    }

    suspend fun deleteEvent(eventId: String): Boolean {
        return try {
            db.collection("events").document(eventId).delete().await()
            createBroadcastNotification("Event cancelled", "An upcoming event has been cancelled.", "event_cancelled", eventId, "")
            true
        } catch (e: Exception) {
            Log.e("CampusRepo", "Delete event failed", e)
            false
        }
    }

    suspend fun saveEvent(uid: String, event: Event): Boolean {
        return try {
            val eventMap = mapOf(
                "id" to event.id,
                "title" to event.title,
                "day" to event.day,
                "month" to event.month,
                "time" to event.time,
                "location" to event.location,
                "description" to event.description,
                "category" to event.category.name,
                "isPaid" to event.isPaid,
                "price" to event.price,
                "posterUrl" to event.posterUrl,
                "savedAt" to System.currentTimeMillis()
            )
            db.collection("users").document(uid).collection("saved").document(event.id).set(eventMap).await()
            true
        } catch (e: Exception) {
            Log.e("CampusRepo", "Save event failed", e)
            false
        }
    }

    suspend fun unsaveEvent(uid: String, eventId: String): Boolean {
        return try {
            db.collection("users").document(uid).collection("saved").document(eventId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("CampusRepo", "Unsave event failed", e)
            false
        }
    }

    suspend fun fetchUserSavedEventIds(uid: String): Set<String> {
        return try {
            val snapshot = db.collection("users").document(uid).collection("saved").get().await()
            snapshot.documents.map { it.id }.toSet()
        } catch (e: Exception) {
            Log.e("CampusRepo", "Fetch user saved event IDs failed", e)
            emptySet()
        }
    }

    suspend fun fetchUserTicketCount(uid: String, eventId: String): Int {
        return try {
            val doc = db.collection("users").document(uid).collection("ticketCounts").document(eventId).get().await()
            (doc.getLong("count") ?: 0L).toInt()
        } catch (e: Exception) {
            0
        }
    }

    suspend fun purchaseTickets(
        uid: String,
        event: Event,
        quantity: Int,
        buyerName: String,
        buyerEmail: String
    ): Result<List<String>> {
        return try {
            val refCodes = mutableListOf<String>()
            val ticketCountsRef = db.collection("users").document(uid).collection("ticketCounts").document(event.id)
            val eventRef = db.collection("events").document(event.id)

            db.runTransaction { transaction ->
                val countSnapshot = transaction.get(ticketCountsRef)
                val currentCount = if (countSnapshot.exists()) (countSnapshot.getLong("count") ?: 0L) else 0L
                if (currentCount + quantity > MAX_TICKETS_PER_EVENT) {
                    throw Exception("You have reached the limit of $MAX_TICKETS_PER_EVENT tickets for this event.")
                }

                val eventSnapshot = transaction.get(eventRef)
                val currentSold = if (eventSnapshot.exists()) (eventSnapshot.getLong("ticketsSold") ?: 0L) else 0L
                
                // Use merge so missing ticketsSold field in event document never fails
                transaction.set(eventRef, mapOf("ticketsSold" to currentSold + quantity), SetOptions.merge())
                transaction.set(ticketCountsRef, mapOf("count" to currentCount + quantity))

                for (i in 0 until quantity) {
                    val refCode = "CC-TKT-${(100000..999999).random()}"
                    refCodes.add(refCode)
                    val ticketId = db.collection("users").document(uid).collection("tickets").document().id
                    val ticketMap = mapOf(
                        "id" to ticketId,
                        "eventId" to event.id,
                        "eventTitle" to event.title,
                        "price" to event.price,
                        "referenceCode" to refCode,
                        "buyerName" to buyerName,
                        "buyerEmail" to buyerEmail,
                        "purchasedAt" to System.currentTimeMillis()
                    )
                    val ticketRef = db.collection("users").document(uid).collection("tickets").document(ticketId)
                    transaction.set(ticketRef, ticketMap)
                }

                // Personal notification
                val notifId = db.collection("users").document(uid).collection("notifications").document().id
                val notifMap = mapOf(
                    "id" to notifId,
                    "title" to "Ticket confirmed: ${event.title}",
                    "body" to "You have successfully purchased $quantity ticket(s) for ${event.title}.",
                    "type" to "ticket_confirmed",
                    "eventId" to event.id,
                    "createdAt" to System.currentTimeMillis()
                )
                val notifRef = db.collection("users").document(uid).collection("notifications").document(notifId)
                transaction.set(notifRef, notifMap)
            }.await()

            Result.success(refCodes)
        } catch (e: Exception) {
            Log.e("CampusRepo", "Purchase tickets failed", e)
            Result.failure(e)
        }
    }

    suspend fun fetchUserTicketEventIds(uid: String): Set<String> {
        return try {
            val snapshot = db.collection("users").document(uid).collection("tickets").get().await()
            snapshot.documents.mapNotNull { it.getString("eventId") }.toSet()
        } catch (e: Exception) {
            Log.e("CampusRepo", "Fetch user ticket event IDs failed", e)
            emptySet()
        }
    }

    suspend fun createBroadcastNotification(title: String, body: String, type: String, eventId: String?, adminUid: String): Boolean {
        return try {
            val notifId = db.collection("notifications").document().id
            val notifMap = mapOf(
                "id" to notifId,
                "title" to title,
                "body" to body,
                "type" to type,
                "eventId" to eventId,
                "createdAt" to System.currentTimeMillis(),
                "createdBy" to adminUid
            )
            db.collection("notifications").document(notifId).set(notifMap).await()
            true
        } catch (e: Exception) {
            Log.e("CampusRepo", "Create broadcast notification failed", e)
            false
        }
    }

    suspend fun markNotificationsAsSeen(uid: String) {
        try {
            db.collection("users").document(uid).update("notificationsLastSeenAt", System.currentTimeMillis()).await()
        } catch (e: Exception) {
            try {
                db.collection("users").document(uid).set(mapOf("notificationsLastSeenAt" to System.currentTimeMillis()), SetOptions.merge()).await()
            } catch (inner: Exception) {
                Log.e("CampusRepo", "Mark notifications as seen failed", inner)
            }
        }
    }

    suspend fun fetchNotifications(uid: String): List<NotificationItem> {
        return try {
            val lastSeenDoc = db.collection("users").document(uid).get().await()
            val lastSeenAt = lastSeenDoc.getLong("notificationsLastSeenAt") ?: 0L

            val personalSnap = db.collection("users").document(uid).collection("notifications").get().await()
            val broadcastSnap = db.collection("notifications").get().await()

            val personalList = personalSnap.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                NotificationItem(
                    id = doc.id,
                    title = data["title"] as? String ?: "",
                    body = data["body"] as? String ?: "",
                    type = data["type"] as? String ?: "ticket_confirmed",
                    eventId = data["eventId"] as? String,
                    createdAt = data["createdAt"] as? Long ?: System.currentTimeMillis(),
                    isRead = (data["createdAt"] as? Long ?: 0L) <= lastSeenAt
                )
            }

            val broadcastList = broadcastSnap.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                NotificationItem(
                    id = doc.id,
                    title = data["title"] as? String ?: "",
                    body = data["body"] as? String ?: "",
                    type = data["type"] as? String ?: "new_event",
                    eventId = data["eventId"] as? String,
                    createdAt = data["createdAt"] as? Long ?: System.currentTimeMillis(),
                    isRead = (data["createdAt"] as? Long ?: 0L) <= lastSeenAt
                )
            }

            (personalList + broadcastList).sortedByDescending { it.createdAt }
        } catch (e: Exception) {
            Log.e("CampusRepo", "Fetch notifications failed", e)
            emptyList()
        }
    }
}
