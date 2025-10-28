package com.budgeteasy.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

private val Context.languageDataStore: DataStore<Preferences> by preferencesDataStore(name = "language_preferences")

enum class AppLanguage(val code: String, val displayName: String) {
    SPANISH("es", "Español"),
    ENGLISH("en", "English");

    companion object {
        fun fromCode(code: String) = entries.find { it.code == code } ?: SPANISH
    }

    fun toLocale(): Locale = Locale(code)
}

@Singleton
class LanguageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val LANGUAGE_KEY = stringPreferencesKey("app_language")

    // Leer idioma guardado
    val appLanguage: Flow<AppLanguage> = context.languageDataStore.data
        .map { preferences ->
            AppLanguage.fromCode(preferences[LANGUAGE_KEY] ?: AppLanguage.SPANISH.code)
        }

    // Guardar idioma
    suspend fun setLanguage(language: AppLanguage) {
        context.languageDataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language.code
        }
    }

    // Aplicar idioma al contexto
    fun applyLanguage(context: Context, language: AppLanguage): Context {
        val locale = language.toLocale()
        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}