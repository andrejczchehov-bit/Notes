package com.example.mynotes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Часть 4 Лабораторной работы №4: Форма, тема и выбор задач.
 * Включает форму ввода с rememberSaveable, переключение темы и список чекбоксов со счетчиком выбора.
 */
@Composable
fun QuickTaskInputScreen(modifier: Modifier = Modifier) {
    // 1. Введенный текст задачи хранится в rememberSaveable
    var taskTitle by rememberSaveable { mutableStateOf("") }
    var addedMessage by rememberSaveable { mutableStateOf("") }

    // 2. Флаг темной/светлой темы через rememberSaveable
    var isDarkTheme by rememberSaveable { mutableStateOf(false) }
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black

    // 3. Список из 5 чекбоксов с названиями задач и отслеживанием выбранных
    val checkboxTasks = listOf("Купить молоко", "Сделать ДЗ", "Прочитать книгу", "Заняться спортом", "Позвонить маме")
    var selectedTasks by rememberSaveable { mutableStateOf(setOf<Int>()) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Переключатель темной/светлой темы
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isDarkTheme) "Тёмная тема" else "Светлая тема",
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { isDarkTheme = it }
                )
            }

            HorizontalDivider(color = if (isDarkTheme) Color.DarkGray else Color.LightGray)

            // 1. Форма быстрого ввода задачи
            Text(
                text = "Быстрое добавление задачи",
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = taskTitle,
                onValueChange = { taskTitle = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Название задачи") },
                singleLine = true
            )

            Button(
                onClick = {
                    if (taskTitle.isNotBlank()) {
                        addedMessage = "Добавлена задача: [$taskTitle]"
                        taskTitle = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Добавить")
            }

            if (addedMessage.isNotBlank()) {
                Text(
                    text = addedMessage,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            HorizontalDivider(color = if (isDarkTheme) Color.DarkGray else Color.LightGray)

            // 3. Список чекбоксов и счетчик выбранных задач
            Text(
                text = "Выбрано задач: ${selectedTasks.size}",
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(checkboxTasks) { index, taskName ->
                    val isChecked = selectedTasks.contains(index)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = taskName,
                            color = textColor
                        )
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                selectedTasks = if (checked) {
                                    selectedTasks + index
                                } else {
                                    selectedTasks - index
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Quick Task Input - Light", showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun QuickTaskInputScreenLightPreview() {
    MaterialTheme {
        QuickTaskInputScreen()
    }
}
