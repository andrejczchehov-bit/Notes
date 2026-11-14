package com.example.mynotes

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

/**
 * UI-тесты для Compose (Лабораторная работа №8, Задание 3):
 * а) Тест некликабельности AppButton(enabled = false).
 * б) Тест переключения темы.
 * в) Тест адаптивности макета (BoxWithConstraints).
 */
class AppUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAppButtonDisabledClick() {
        var clicked = false
        composeTestRule.setContent {
            AppTheme {
                AppButton(
                    text = "Test Button",
                    onClick = { clicked = true },
                    enabled = false,
                    modifier = Modifier.testTag("disabled_button")
                )
            }
        }

        composeTestRule.onNodeWithTag("disabled_button").performClick()
        assert(!clicked) { "Кнопка в выключенном состоянии не должна реагировать на клик" }
    }

    @Test
    fun testThemeSwitchingColors() {
        var isDark by mutableStateOf(false)
        composeTestRule.setContent {
            AppTheme(themeMode = if (isDark) ThemeMode.DARK else ThemeMode.LIGHT) {
                Surface(modifier = Modifier.testTag("themed_surface").fillMaxSize()) {
                    Text("Theme Test")
                }
            }
        }

        composeTestRule.onNodeWithTag("themed_surface").assertExists()

        // Переключаем тему
        composeTestRule.runOnUiThread {
            isDark = true
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("themed_surface").assertExists()
    }

    @Test
    fun testAdaptabilityLayout() {
        composeTestRule.setContent {
            AppTheme {
                BoxWithConstraints(modifier = Modifier.testTag("adaptive_container").fillMaxSize()) {
                    val isTablet = this.maxWidth > 600.dp
                    Text(
                        text = if (isTablet) "Tablet Mode" else "Phone Mode",
                        modifier = Modifier.testTag("mode_text")
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag("adaptive_container").assertExists()
        composeTestRule.onNodeWithTag("mode_text").assertExists()
    }
}
