package com.example.mynotes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Улучшения Лабораторной работы №5 (Шаги 5, 6, 7):
 * 1. Анимация появления новых элементов через modifier.animateItem()
 * 2. Пагинация по страницам (currentPage, pageSize = 20)
 * 3. Обработка края списка («Больше нет данных», когда загружено 200 задач)
 * 4. Фильтрация задач по приоритету (Все, HIGH, MEDIUM, LOW) через FilterChip
 * 5. Сохранение позиции скролла через rememberLazyListState
 */
@Composable
fun PaginatedTaskListScreen(modifier: Modifier = Modifier) {
    // Генерация базы из 200 задач
    val totalSourceTasks = remember {
        List(200) { index ->
            Task(
                id = (index + 1).toLong(),
                title = "Задача №${index + 1}",
                description = "Описание для задачи №${index + 1}",
                dueDate = System.currentTimeMillis() + (index * 3600000L),
                priority = (index % 3) + 1, // 1: Low, 2: Medium, 3: High
                reminderTime = 0L
            )
        }
    }

    val pageSize = 20
    var currentPage by rememberSaveable { mutableStateOf(1) }
    var selectedPriority by rememberSaveable { mutableStateOf<Int?>(null) } // null = Все, 3 = HIGH, 2 = MEDIUM, 1 = LOW
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Сохранение позиции скролла при добавлении элементов (Шаг 5)
    val listState = rememberLazyListState()

    // Текущие видимые задачи по пагинации (Шаг 2)
    val loadedCount = currentPage * pageSize
    val paginatedTasks = totalSourceTasks.take(loadedCount)

    // Фильтрация по выбранной категории приоритета (Шаг 4)
    val filteredTasks = remember(paginatedTasks, selectedPriority) {
        if (selectedPriority == null) {
            paginatedTasks
        } else {
            paginatedTasks.filter { it.priority == selectedPriority }
        }
    }

    val hasMoreData = loadedCount < totalSourceTasks.size

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
            // Заголовок экрана
            Text(
                text = "Все задачи (${filteredTasks.size})",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // Переключатель категорий (Фильтр по приоритету через FilterChip) (Шаг 4)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedPriority == null,
                    onClick = { selectedPriority = null },
                    label = { Text("Все") }
                )
                FilterChip(
                    selected = selectedPriority == 3,
                    onClick = { selectedPriority = 3 },
                    label = { Text("HIGH") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFE53935).copy(alpha = 0.2f),
                        selectedLabelColor = Color(0xFFE53935)
                    )
                )
                FilterChip(
                    selected = selectedPriority == 2,
                    onClick = { selectedPriority = 2 },
                    label = { Text("MEDIUM") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFF9800).copy(alpha = 0.2f),
                        selectedLabelColor = Color(0xFFFF9800)
                    )
                )
                FilterChip(
                    selected = selectedPriority == 1,
                    onClick = { selectedPriority = 1 },
                    label = { Text("LOW") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF4CAF50).copy(alpha = 0.2f),
                        selectedLabelColor = Color(0xFF4CAF50)
                    )
                )
            }

            // LazyColumn со сбережением позиции скролла через rememberLazyListState (Шаг 5)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Элементы списка с плавной анимацией появления через modifier.animateItem() (Шаг 1)
                itemsIndexed(filteredTasks, key = { _, task -> task.id }) { index, task ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                    ) {
                        TaskCard(
                            task = task,
                            onClick = {}
                        )
                    }

                    if (index < filteredTasks.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }

                // Кнопка пагинации / Состояние «Больше нет данных» (Шаг 2 и 3)
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(36.dp))
                        } else {
                            Button(
                                onClick = {
                                    isLoading = true
                                    coroutineScope.launch {
                                        delay(1000)
                                        currentPage++ // Увеличиваем страницу (Шаг 2)
                                        isLoading = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = hasMoreData // Если лимит исчерпан, кнопка неактивна (Шаг 3)
                            ) {
                                Text(if (hasMoreData) "Загрузить ещё" else "Больше нет данных")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Paginated Task List - Improved", showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun PaginatedTaskListScreenPreview() {
    MaterialTheme {
        PaginatedTaskListScreen()
    }
}
