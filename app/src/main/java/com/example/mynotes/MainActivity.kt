package com.example.mynotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.color.DynamicColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Применяем динамические цвета Material 3 для корректной окраски XML-диалогов (DatePicker/TimePicker)
        DynamicColors.applyToActivityIfAvailable(this)

        setContent {
            val taskViewModel: TaskViewModel = viewModel()
            val settingsViewModel: SettingsViewModel = viewModel()
            val themeMode by settingsViewModel.themeMode.collectAsState()
            val forceDark by settingsViewModel.forceDarkContent.collectAsState()

            CompositionLocalProvider(LocalSettingsViewModel provides settingsViewModel) {
                AppTheme(themeMode = themeMode, forceDarkContent = forceDark) {
                    AppNavGraph(
                        viewModel = taskViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}
