package com.budgeteasy.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extensión para DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

enum class ThemeMode(val value: Int) {
    LIGHT(0),
    DARK(1);

    companion object {
        fun fromValue(value: Int) = entries.find { it.value == value } ?: LIGHT
    }
}

@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val THEME_MODE_KEY = intPreferencesKey("theme_mode")

    // Leer el modo de tema
    val themeMode: Flow<ThemeMode> = context.dataStore.data
        .map { preferences ->
            ThemeMode.fromValue(preferences[THEME_MODE_KEY] ?: ThemeMode.LIGHT.value) // ✅ LIGHT en vez de SYSTEM
        }

    // Guardar el modo de tema
    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.value
        }
    }
}