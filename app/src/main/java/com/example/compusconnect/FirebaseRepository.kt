package com.example.compusconnect

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Authentication
    suspend fun signIn(email: String, pass: String): UserSession? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            val user = result.user ?: return null
            val role = if (email.equals("admin@compusconnect.com", ignoreCase = true)) UserRole.ADMIN else UserRole.STUDENT
            UserSession(
                isLoggedIn = true,
                name = user.displayName ?: email.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = email,
                role = role
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun signUp(email: String, pass: String): UserSession? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user ?: return null
            UserSession(
                isLoggedIn = true,
                name = user.displayName ?: email.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = email,
                role = UserRole.STUDENT
            )
        } catch (e: Exception) {
            null
        }
    }

    // Firestore Events
    suspend fun fetchEvents(): List<Event> {
        return try {
            val snapshot = db.collection("events").get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Event::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun publishEvent(event: Event): Boolean {
        return try {
            db.collection("events").document(event.id).set(event).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteEvent(eventId: String): Boolean {
        return try {
            db.collection("events").document(eventId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Firebase Storage for local image uploads
    suspend fun uploadImage(uri: Uri): String? {
        return try {
            val ref = storage.reference.child("posters/${UUID.randomUUID()}.jpg")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }
}
