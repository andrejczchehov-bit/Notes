package com.example.mynotes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Итоговый экран списка задач (TaskListScreen) для лабораторной работы.
 * Включает:
 * - Заголовок «Мои задачи» и подзаголовок «N задач на сегодня»
 * - Кнопку «Обновить» на всю ширину (fillMaxWidth) с индикатором загрузки при isLoading
 * - Список LazyColumn, состоящий из переиспользуемых TaskCard
 * - State Hoisting с подключением к TaskViewModel
 */

// 1. Stateless (чистый UI с поднятым состоянием)
@Composable
fun TaskContent(
    tasks: List<Task>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onTaskClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Заголовок
            Text(
                text = "Мои задачи",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            // Подзаголовок: N задач на сегодня
            Text(
                text = "${tasks.size} задач на сегодня",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Кнопка «Обновить» на всю ширину (fillMaxWidth)
            Button(
                onClick = onRefresh,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Обновить")
            }

            // Список задач через LazyColumn или текст «Дела не запланированы» при пустоте
            if (tasks.isEmpty() && !isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Дела не запланированы",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onClick = onTaskClick
                        )
                    }
                }
            }
        }
    }
}

// 2. Stateful (подключение к TaskViewModel)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel = viewModel(),
    onTaskClick: (Long) -> Unit
) {
    val tasks by viewModel.allTasks.observeAsState(emptyList())
    val isLoading = false // Учебный флаг загрузки

    TaskContent(
        tasks = tasks,
        isLoading = isLoading,
        onRefresh = {
            // Имитация обновления данных
        },
        onTaskClick = onTaskClick
    )
}

// --- Примеры Task для Preview ---
private val sampleTasksForPreview = listOf(
    Task(id = 1, title = "Сдать лабораторную работу по Compose", description = "Проверить LazyColumn, Card и модификаторы", dueDate = System.currentTimeMillis(), priority = 3, reminderTime = System.currentTimeMillis()),
    Task(id = 2, title = "Купить продукты на ужин", description = "Молоко, хлеб, фрукты", dueDate = System.currentTimeMillis(), priority = 2, reminderTime = 0L),
    Task(id = 3, title = "Прочитать главу книги", description = "Архитектура Android приложений", dueDate = System.currentTimeMillis(), priority = 1, reminderTime = 0L)
)

// --- 3 Превью: Пусто, Загрузка, 3 задачи разной срочности ---

@Preview(name = "1. Пусто", showBackground = true)
@Composable
fun TaskContentEmptyPreview() {
    MaterialTheme {
        TaskContent(
            tasks = emptyList(),
            isLoading = false,
            onRefresh = {},
            onTaskClick = {}
        )
    }
}

@Preview(name = "2. Загрузка", showBackground = true)
@Composable
fun TaskContentLoadingPreview() {
    MaterialTheme {
        TaskContent(
            tasks = emptyList(),
            isLoading = true,
            onRefresh = {},
            onTaskClick = {}
        )
    }
}

@Preview(name = "3. Три задачи (High, Medium, Low)", showBackground = true)
@Composable
fun TaskContentLoadedPreview() {
    MaterialTheme {
        TaskContent(
            tasks = sampleTasksForPreview,
            isLoading = false,
            onRefresh = {},
            onTaskClick = {}
        )
    }
}
