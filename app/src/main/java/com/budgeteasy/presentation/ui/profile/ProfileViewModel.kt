package com.budgeteasy.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgeteasy.data.preferences.AppLanguage
import com.budgeteasy.data.preferences.LanguageManager
import com.budgeteasy.data.preferences.ThemeManager
import com.budgeteasy.data.preferences.ThemeMode
import com.budgeteasy.domain.model.User
import com.budgeteasy.domain.usecase.user.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val themeManager: ThemeManager,
    private val languageManager: LanguageManager
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user


    val currentTheme: StateFlow<ThemeMode> = themeManager.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.LIGHT
        )


    val currentLanguage: StateFlow<AppLanguage> = languageManager.appLanguage
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppLanguage.SPANISH
        )

    fun loadUser(userId: Int) {
        viewModelScope.launch {
            try {
                val userData = getUserUseCase(userId)
                _user.value = userData
            } catch (e: Exception) {
                _user.value = null
            }
        }
    }


    suspend fun setThemeMode(mode: ThemeMode) {
        themeManager.setThemeMode(mode)
    }


    suspend fun setLanguage(language: AppLanguage) {
        languageManager.setLanguage(language)
    }
}