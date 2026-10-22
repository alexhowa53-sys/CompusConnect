package com.example.compusconnect

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "campus_prefs")

object CampusPreferences {
    private val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me")
    private val SAVED_EMAIL_KEY = stringPreferencesKey("saved_email")

    fun getRememberMe(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { prefs -> prefs[REMEMBER_ME_KEY] ?: false }
    }

    fun getSavedEmail(context: Context): Flow<String> {
        return context.dataStore.data.map { prefs -> prefs[SAVED_EMAIL_KEY] ?: "" }
    }

    suspend fun saveLoginPreferences(context: Context, rememberMe: Boolean, email: String) {
        context.dataStore.edit { prefs ->
            prefs[REMEMBER_ME_KEY] = rememberMe
            if (rememberMe) {
                prefs[SAVED_EMAIL_KEY] = email
            } else {
                prefs[SAVED_EMAIL_KEY] = ""
            }
        }
    }
}
