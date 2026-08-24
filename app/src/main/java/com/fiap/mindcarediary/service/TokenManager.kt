package com.fiap.mindcarediary.service

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "auth_preferences"
)

class TokenManager(
    private val context: Context
) {

    companion object {
        private val JWT_TOKEN =
            stringPreferencesKey("jwt_token")
    }

    val token: Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[JWT_TOKEN]
        }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[JWT_TOKEN] = token
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(JWT_TOKEN)
        }
    }
}