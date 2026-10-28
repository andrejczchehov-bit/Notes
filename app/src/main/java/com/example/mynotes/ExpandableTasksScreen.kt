package com.example.mynotes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Часть 3 Лабораторной работы №4: Раскрывающиеся карточки задач (ExpandableTaskCard).
 * Состояние expanded является локальным (remember), State Hoisting здесь не требуется.
 */
@Composable
fun ExpandableTaskCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    // Локальное состояние с remember (State Hoisting не требуется, состояние карточки изолировано)
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Анимированное появление подробного описания при раскрытии
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ExpandableTasksScreen(modifier: Modifier = Modifier) {
    // Список из 5 задач для демонстрации раскрывающихся карточек
    val sampleTasks = listOf(
        "Изучить Jetpack Compose" to "Разобраться с базовыми компонентами Text, Column, Row, Box.",
        "Настроить State Hoisting" to "Понять разницу между stateful и stateless компонентами.",
        "Реализовать LazyColumn" to "Эффективный вывод списков с использованием ключей items.",
        "Изучить rememberSaveable" to "Сохранение UI-состояния при поворотах экрана и конфигурации.",
        "Создать раскрывающиеся карточки" to "Локальное управление состоянием expanded через remember."
    )

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
            Text(
                text = "Раскрывающиеся задачи",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sampleTasks) { (title, desc) ->
                    ExpandableTaskCard(title = title, description = desc)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun ExpandableTasksScreenPreview() {
    MaterialTheme {
        ExpandableTasksScreen()
    }
}
