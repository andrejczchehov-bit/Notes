package com.example.mynotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
