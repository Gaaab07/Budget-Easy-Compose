package com.budgeteasy.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgeteasy.data.preferences.AppLanguage
import com.budgeteasy.data.preferences.LanguageManager
import com.budgeteasy.data.preferences.ThemeManager
import com.budgeteasy.data.preferences.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeManager: ThemeManager,
    private val languageManager: LanguageManager
) : ViewModel() {

    val currentTheme: StateFlow<ThemeMode> = themeManager.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeMode.LIGHT
    )

    val currentLanguage: StateFlow<AppLanguage> = languageManager.appLanguage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AppLanguage.SPANISH
    )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themeManager.setThemeMode(mode)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            languageManager.setLanguage(language)
        }
    }
}