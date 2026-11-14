package com.example.mynotes

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

/**
 * ViewModel для управления глобальными настройками приложения (тема, режим темы, принудительный темный контент, уведомления, фильтрация).
 */
class SettingsViewModel : ViewModel() {
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _forceDarkContent = MutableStateFlow(false)
    val forceDarkContent: StateFlow<Boolean> = _forceDarkContent.asStateFlow()

    private val _showCompletedTasks = MutableStateFlow(true)
    val showCompletedTasks: StateFlow<Boolean> = _showCompletedTasks.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(true)
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    fun toggleTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
        _themeMode.value = if (enabled) ThemeMode.DARK else ThemeMode.LIGHT
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        _isDarkTheme.value = (mode == ThemeMode.DARK)
    }

    fun setForceDarkContent(force: Boolean) {
        _forceDarkContent.value = force
    }

    fun toggleShowCompleted(enabled: Boolean) {
        _showCompletedTasks.value = enabled
    }

    fun toggleSound(enabled: Boolean) {
        _soundEnabled.value = enabled
    }

    fun toggleVibration(enabled: Boolean) {
        _vibrationEnabled.value = enabled
    }
}
