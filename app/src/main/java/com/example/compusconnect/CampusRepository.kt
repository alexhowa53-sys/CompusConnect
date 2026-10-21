package com.example.compusconnect

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
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
            
            // Read role securely from Firestore users/{uid}
            val userDoc = db.collection("users").document(user.uid).get().await()
            val roleStr = userDoc.getString("role") ?: "student"
            val role = if (roleStr.equals("admin", ignoreCase = true)) UserRole.ADMIN else UserRole.STUDENT
            val name = userDoc.getString("name") ?: user.displayName ?: trimmedEmail.substringBefore("@")

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
            db.collection("users").document(user.uid).set(userData).await()

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
                        title = data["title"] as? String ?: "",
                        day = data["day"] as? String ?: "14",
                        month = data["month"] as? String ?: "OCT",
                        time = data["time"] as? String ?: "7:00 PM",
                        location = data["location"] as? String ?: "",
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
            true
        } catch (e: Exception) {
            Log.e("CampusRepo", "Update event failed", e)
            false
        }
    }

    suspend fun deleteEvent(eventId: String): Boolean {
        return try {
            db.collection("events").document(eventId).delete().await()
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

    suspend fun purchaseTicket(uid: String, event: Event): Result<String> {
        return try {
            val refCode = "CC-TKT-${(100000..999999).random()}"
            val ticketId = db.collection("users").document(uid).collection("tickets").document().id
            val ticketMap = mapOf(
                "id" to ticketId,
                "eventId" to event.id,
                "eventTitle" to event.title,
                "price" to event.price,
                "referenceCode" to refCode,
                "purchasedAt" to System.currentTimeMillis()
            )

            db.runTransaction { transaction ->
                val eventRef = db.collection("events").document(event.id)
                val snapshot = transaction.get(eventRef)
                val currentSold = snapshot.getLong("ticketsSold") ?: 0L
                transaction.update(eventRef, "ticketsSold", currentSold + 1)

                val ticketRef = db.collection("users").document(uid).collection("tickets").document(ticketId)
                transaction.set(ticketRef, ticketMap)
            }.await()

            Result.success(refCode)
        } catch (e: Exception) {
            Log.e("CampusRepo", "Purchase ticket failed", e)
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
}
