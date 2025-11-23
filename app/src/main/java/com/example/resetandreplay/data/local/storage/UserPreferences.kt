package com.example.resetandreplay.data.local.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    // Claves para el DataStore
    private val isLoggedInKey = booleanPreferencesKey("is_logged_in")
    private val userEmailKey = stringPreferencesKey("user_email")
    private val userPhotoUriKey = stringPreferencesKey("user_photo_uri")
    private val isAdminKey = booleanPreferencesKey("is_admin") // Nueva clave para el rol de admin

    // Sesión del usuario
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[isLoggedInKey] ?: false }
    suspend fun setLoggedIn(value: Boolean) {
        context.dataStore.edit { it[isLoggedInKey] = value }
    }

    // Email del usuario
    val userEmail: Flow<String?> = context.dataStore.data.map { it[userEmailKey] }
    suspend fun setUserEmail(email: String) {
        context.dataStore.edit { it[userEmailKey] = email }
    }

    // Foto de perfil del usuario
    val userPhotoUri: Flow<String?> = context.dataStore.data.map { it[userPhotoUriKey] }
    suspend fun setUserPhotoUri(uri: String) {
        context.dataStore.edit { it[userPhotoUriKey] = uri }
    }

    // Nuevo campo para gestionar el rol de administrador
    val isAdmin: Flow<Boolean> = context.dataStore.data.map { it[isAdminKey] ?: false }
    suspend fun setAdmin(value: Boolean) {
        context.dataStore.edit { it[isAdminKey] = value }
    }

    // Limpiar preferencias al cerrar sesión
    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
