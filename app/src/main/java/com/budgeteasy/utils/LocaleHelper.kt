package com.budgeteasy.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.budgeteasy.data.preferences.AppLanguage
import com.budgeteasy.presentation.ui.settings.SettingsViewModel

@Composable
fun getCurrentLanguage(): AppLanguage {
    val viewModel: SettingsViewModel = hiltViewModel()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    return currentLanguage
}

// Función helper para traducir textos comunes
fun translate(spanish: String, english: String, language: AppLanguage): String {
    return if (language == AppLanguage.SPANISH) spanish else english
}