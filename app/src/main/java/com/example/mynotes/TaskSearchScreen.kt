package com.example.mynotes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Часть 2 Лабораторной работы №4: Stateless-поле ввода и поиск по задачам.
 */

// Stateless-компонент поля поиска (не хранит собственное состояние, управляется снаружи)
@Composable
fun TaskSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Поиск задач...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Поиск"
            )
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
}

// Stateful-экран поиска, управляющий состоянием query и фильтрацией списка задач
@Composable
fun TaskSearchScreen(
    tasks: List<Task>,
    modifier: Modifier = Modifier
) {
    // Состояние query хранится через rememberSaveable для сохранения при повороте экрана
    var query by rememberSaveable { mutableStateOf("") }

    // Фильтрация списка задач по поисковому запросу (если query пустой — показываем все задачи)
    val filteredTasks = remember(query, tasks) {
        if (query.isBlank()) {
            tasks
        } else {
            tasks.filter { task ->
                task.title.contains(query, ignoreCase = true) ||
                        task.description.contains(query, ignoreCase = true)
            }
        }
    }

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
            // Поле поиска (Stateless)
            TaskSearchBar(
                query = query,
                onQueryChange = { query = it }
            )

            // Отображение количества введенных символов и кнопка сброса
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Введено символов: ${query.length}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                TextButton(
                    onClick = { query = "" }
                ) {
                    Text("Очистить")
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Список отфильтрованных задач через LazyColumn
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun TaskSearchScreenPreview() {
    val sampleTasks = listOf(
        Task(id = 1, title = "Изучить Jetpack Compose", description = "Состояние и rememberSaveable", dueDate = System.currentTimeMillis(), priority = 3, reminderTime = 0L),
        Task(id = 2, title = "Купить продукты", description = "Молоко, хлеб", dueDate = System.currentTimeMillis(), priority = 2, reminderTime = 0L),
        Task(id = 3, title = "Сделать лабу №4", description = "Поиск и фильтрация задач", dueDate = System.currentTimeMillis(), priority = 1, reminderTime = 0L)
    )

    MaterialTheme {
        TaskSearchScreen(tasks = sampleTasks)
    }
}
