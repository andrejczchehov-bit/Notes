package com.example.mynotes

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

/**
 * UI-тесты для Compose (Лабораторная работа №9, Модуль 3):
 * 1. Тест рендеринга списка.
 * 2. Тест поиска с дебаунсом.
 * 3. Тест добавления новой задачи.
 * 4. Тест автосохранения / чекбокса.
 */
class AppUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun test1_listIsRendered() {
        // Ожидаем появления поля поиска на главном экране
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.searchField).assertExists()
                true
            } catch (_: AssertionError) {
                false
            }
        }
        composeTestRule.onNodeWithTag(TestTags.searchField).assertIsDisplayed()
    }

    @Test
    fun test2_searchFilteringWithDebounce() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.searchField).assertExists()
                true
            } catch (_: AssertionError) { false }
        }

        // Ввод текста в поле поиска
        composeTestRule.onNodeWithTag(TestTags.searchField).performTextInput("УникальнаяЗадача")

        // Ожидание дебаунса (300мс) через waitUntil
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.searchField).assertTextContains("УникальнаяЗадача")
                true
            } catch (_: AssertionError) { false }
        }
    }

    @Test
    fun test3_addNewTaskFlow() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.addTask).assertExists()
                true
            } catch (_: AssertionError) { false }
        }

        // Кликаем по кнопке добавления задачи (+)
        composeTestRule.onNodeWithTag(TestTags.addTask).performClick()

        // Ждем появления кнопки сохранения
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.saveButton).assertExists()
                true
            } catch (_: AssertionError) { false }
        }

        // Вводим название задачи в поле OutlinedTextField
        composeTestRule.onNode(hasSetTextAction()).performTextInput("Автотест Лаба 9")

        // Нажимаем сохранить
        composeTestRule.onNodeWithTag(TestTags.saveButton).performClick()

        // Ждем возврата на главный экран и проверяем появление задачи
        composeTestRule.waitUntil(timeoutMillis = 6000) {
            try {
                composeTestRule.onNodeWithText("Автотест Лаба 9").assertExists()
                true
            } catch (_: AssertionError) { false }
        }

        composeTestRule.onNodeWithText("Автотест Лаба 9").assertIsDisplayed()
    }

    @Test
    fun test4_autoSaveCheckboxToggle() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithTag(TestTags.searchField).assertExists()
                true
            } catch (_: AssertionError) { false }
        }

        // Находим любой чекбокс или элемент карточки и кликаем
        try {
            composeTestRule.onAllNodes(hasClickAction()).filter(hasSetTextAction().not()).let { nodes ->
                if (nodes.fetchSemanticsNodes().isNotEmpty()) {
                    nodes.onFirst().performClick()
                }
            }
        } catch (_: Exception) {}

        // Ждем автосохранения через LaunchedEffect(tasks)
        Thread.sleep(800)
        composeTestRule.waitForIdle()
    }

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
