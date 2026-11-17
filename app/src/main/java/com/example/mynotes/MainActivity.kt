package com.example.mynotes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
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

        handleIntent(intent)

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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data != null && data.host == "passes.company.example" && data.path?.startsWith("/invite") == true) {
            val token = data.getQueryParameter("token") ?: data.lastPathSegment
            if (isValidToken(token)) {
                Toast.makeText(this, "Приглашение принято", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Приглашение недействительно", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isValidToken(token: String?): Boolean {
        if (token.isNullOrBlank()) return false
        if (token.length !in 8..64) return false
        return token.all { it.isLetterOrDigit() }
    }
}
