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
            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

            CompositionLocalProvider(LocalSettingsViewModel provides settingsViewModel) {
                AppTheme(darkTheme = isDarkTheme) {
                    AppNavGraph(
                        viewModel = taskViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}
