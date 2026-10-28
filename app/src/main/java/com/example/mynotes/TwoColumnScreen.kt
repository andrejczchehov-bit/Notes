package com.example.mynotes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Экран TwoColumnScreen (Задание 2 из Лабораторной №3).
 * Левая колонка занимает 70% ширины (weight(0.7f)) — список задач.
 * Правая колонка занимает 30% ширины (weight(0.3f)) — панель управления/фильтров.
 */
@Composable
fun TwoColumnScreen(
    tasks: List<Task>,
    onCompleteTask: (Long) -> Unit,
    onAddTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Левая колонка (70% ширины) — Список задач через LazyColumn
            Column(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = "Список задач",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        AdaptiveTaskCard(
                            task = task,
                            onCompleteClick = onCompleteTask
                        )
                    }
                }
            }

            // Правая колонка (30% ширины) — Панель управления и кнопка добавления
            Column(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Управление",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onAddTask,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Добавить")
                }

                OutlinedButton(
                    onClick = { /* Фильтры */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Фильтры")
                }
            }
        }
    }
}

@Preview(name = "Two Column Screen", showBackground = true, widthDp = 840, heightDp = 600)
@Composable
fun TwoColumnScreenPreview() {
    val sampleTasks = listOf(
        Task(id = 1, title = "Сделать лабу по Compose", description = "", dueDate = System.currentTimeMillis(), priority = 3, reminderTime = 0L),
        Task(id = 2, title = "Купить продукты", description = "", dueDate = System.currentTimeMillis(), priority = 2, reminderTime = 0L),
        Task(id = 3, title = "Позвонить другу", description = "", dueDate = System.currentTimeMillis(), priority = 1, reminderTime = 0L)
    )
    MaterialTheme {
        TwoColumnScreen(
            tasks = sampleTasks,
            onCompleteTask = {},
            onAddTask = {}
        )
    }
}
