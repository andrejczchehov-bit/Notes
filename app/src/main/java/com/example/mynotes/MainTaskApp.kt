package com.example.mynotes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Единый продуктовый экран задач (MainTaskAppScreen), объединяющий все возможности лабораторных работ:
 * 1. Счётчик задач в TopAppBar («Мои задачи: N»)
 * 2. Поиск по задачам (TaskSearchBar)
 * 3. Пагинация и бесконечный скролл с LazyColumn, разделителями и анимацией (animateItem)
 * 4. Раскрывающиеся карточки с подробным описанием (ExpandableTaskCard)
 * 5. Адаптивный интерфейс для планшетов (> 600.dp - две колонки) и телефонов
 * 6. Модальное окно профиля с реальной статистикой выполненных и текущих задач
 * 7. Быстрое добавление задачи через FAB и диалоговое окно
 * 8. Переключение тёмной и светлой темы
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTaskAppScreen(
    viewModel: TaskViewModel,
    onNavigateToDetail: (Long) -> Unit
) {
    val tasks by viewModel.allTasks.observeAsState(emptyList())

    // Состояния интерфейса
    var query by rememberSaveable { mutableStateOf("") }
    var selectedPriority by rememberSaveable { mutableStateOf<Int?>(null) } // null = Все, 3 = HIGH, 2 = MEDIUM, 1 = LOW
    var isDarkTheme by rememberSaveable { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showQuickAddDialog by remember { mutableStateOf(false) }
    
    // Пагинация списка
    var displayLimit by rememberSaveable { mutableStateOf(20) }
    var isLoadingMore by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Фильтрация задач (поиск + приоритет)
    val filteredTasks = remember(tasks, query, selectedPriority) {
        tasks.filter { task ->
            val matchesQuery = query.isBlank() ||
                    task.title.contains(query, ignoreCase = true) ||
                    task.description.contains(query, ignoreCase = true)
            val matchesPriority = selectedPriority == null || task.priority == selectedPriority
            matchesQuery && matchesPriority
        }
    }

    val visibleTasks = filteredTasks.take(displayLimit)
    val hasMore = displayLimit < filteredTasks.size

    val customColorScheme = if (isDarkTheme) {
        darkColorScheme(
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            onBackground = Color.White,
            onSurface = Color.White
        )
    } else {
        lightColorScheme()
    }

    MaterialTheme(colorScheme = customColorScheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        // (Лаба 4: Счётчик) Мои задачи: N (реальное количество из базы)
                        Text(
                            text = "Мои задачи: ${tasks.size}",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        // (Лаба 4: Тёмная тема) Переключатель темы
                        IconButton(onClick = { isDarkTheme = !isDarkTheme }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Сменить тему"
                            )
                        }
                        // (Лаба 3: Профиль) Кнопка профиля
                        IconButton(onClick = { showProfileDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Профиль"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                // (Лаба 4: Быстрое добавление) Кнопка "+"
                FloatingActionButton(
                    onClick = { showQuickAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить задачу")
                }
            }
        ) { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // (Лаба 3: Две колонки / адаптивность) maxWidth > 600.dp -> планшет (2 колонки), иначе телефон (1 колонка)
                val isTablet = maxWidth > 600.dp

                if (isTablet) {
                    // Планшет: Две колонки (70% список, 30% панель управления)
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(0.7f)
                                .fillMaxHeight()
                        ) {
                            TaskSearchAndListContent(
                                query = query,
                                onQueryChange = { query = it },
                                selectedPriority = selectedPriority,
                                onPriorityChange = { selectedPriority = it },
                                visibleTasks = visibleTasks,
                                filteredSize = filteredTasks.size,
                                hasMore = hasMore,
                                isLoadingMore = isLoadingMore,
                                listState = listState,
                                onTaskClick = onNavigateToDetail,
                                onTaskChecked = { task, checked ->
                                    viewModel.update(task.copy(isCompleted = checked))
                                },
                                onLoadMore = {
                                    isLoadingMore = true
                                    coroutineScope.launch {
                                        delay(1000)
                                        displayLimit += 20
                                        isLoadingMore = false
                                    }
                                }
                            )
                        }

                        // Правая колонка управления для планшета
                        Column(
                            modifier = Modifier
                                .weight(0.3f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("Быстрое меню", fontWeight = FontWeight.Bold)
                                    Button(
                                        onClick = { showQuickAddDialog = true },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Добавить задачу")
                                    }
                                    OutlinedButton(
                                        onClick = { showProfileDialog = true },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Мой профиль")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Телефон: Одна колонка
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        TaskSearchAndListContent(
                            query = query,
                            onQueryChange = { query = it },
                            selectedPriority = selectedPriority,
                            onPriorityChange = { selectedPriority = it },
                            visibleTasks = visibleTasks,
                            filteredSize = filteredTasks.size,
                            hasMore = hasMore,
                            isLoadingMore = isLoadingMore,
                            listState = listState,
                            onTaskClick = onNavigateToDetail,
                            onTaskChecked = { task, checked ->
                                viewModel.update(task.copy(isCompleted = checked))
                            },
                            onLoadMore = {
                                isLoadingMore = true
                                coroutineScope.launch {
                                    delay(1000)
                                    displayLimit += 20
                                    isLoadingMore = false
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Диалог профиля (Лаба 3: Профиль на основе реальных задач из базы)
    if (showProfileDialog) {
        val completedCount = tasks.count { it.isCompleted }
        val inProgressCount = tasks.size - completedCount

        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text("Профиль пользователя") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text("Мой Задачник", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    Text("Всего задач в базе: ${tasks.size}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatCounter(title = "Выполнено", count = completedCount)
                        StatCounter(title = "В работе", count = inProgressCount)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("Закрыть")
                }
            }
        )
    }

    // Диалог быстрого добавления задачи (Лаба 4: Быстрое добавление)
    if (showQuickAddDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newDesc by remember { mutableStateOf("") }
        var newPriority by remember { mutableStateOf(2) } // Medium default

        AlertDialog(
            onDismissRequest = { showQuickAddDialog = false },
            title = { Text("Быстрое добавление задачи") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Название") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newDesc,
                        onValueChange = { newDesc = it },
                        label = { Text("Описание") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Приоритет:", style = MaterialTheme.typography.bodyMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterChip(
                            selected = newPriority == 1,
                            onClick = { newPriority = 1 },
                            label = { Text("Low") }
                        )
                        FilterChip(
                            selected = newPriority == 2,
                            onClick = { newPriority = 2 },
                            label = { Text("Med") }
                        )
                        FilterChip(
                            selected = newPriority == 3,
                            onClick = { newPriority = 3 },
                            label = { Text("High") }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            val task = Task(
                                title = newTitle,
                                description = newDesc,
                                dueDate = System.currentTimeMillis() + 86400000L,
                                priority = newPriority,
                                reminderTime = 0L
                            )
                            viewModel.insert(task)
                            showQuickAddDialog = false
                        }
                    }
                ) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuickAddDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun TaskSearchAndListContent(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedPriority: Int?,
    onPriorityChange: (Int?) -> Unit,
    visibleTasks: List<Task>,
    filteredSize: Int,
    hasMore: Boolean,
    isLoadingMore: Boolean,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onTaskClick: (Long) -> Unit,
    onTaskChecked: (Task, Boolean) -> Unit,
    onLoadMore: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // (Лаба 4: Поиск) Stateless поле поиска
        TaskSearchBar(
            query = query,
            onQueryChange = onQueryChange
        )

        // (Лаба 5: Фильтры) Переключатель категорий по приоритету
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(selected = selectedPriority == null, onClick = { onPriorityChange(null) }, label = { Text("Все") })
            FilterChip(selected = selectedPriority == 3, onClick = { onPriorityChange(3) }, label = { Text("HIGH") })
            FilterChip(selected = selectedPriority == 2, onClick = { onPriorityChange(2) }, label = { Text("MEDIUM") })
            FilterChip(selected = selectedPriority == 1, onClick = { onPriorityChange(1) }, label = { Text("LOW") })
        }

        // (Лаба 5: Списки и Пагинация) LazyColumn с заголовком, разделителями и анимацией
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Text(
                    text = "Все задачи ($filteredSize)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            if (visibleTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Дела не запланированы",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            } else {
                itemsIndexed(visibleTasks, key = { _, task -> task.id }) { index, task ->
                    // (Лаба 4: Раскрывающиеся карточки) Карточка с возможностью раскрытия описания
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                    ) {
                        ExpandableTaskCardView(
                            task = task,
                            onClick = { onTaskClick(task.id) },
                            onChecked = { checked -> onTaskChecked(task, checked) }
                        )
                    }

                    if (index < visibleTasks.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }

                // Кнопка пагинации / бесконечного скролла
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoadingMore) {
                            CircularProgressIndicator(modifier = Modifier.size(36.dp))
                        } else {
                            Button(
                                onClick = onLoadMore,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = hasMore
                            ) {
                                Text(if (hasMore) "Загрузить ещё" else "Больше нет данных")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Раскрывающаяся карточка задачи (интеграция Лабы 4 с реальной моделью Task)
@Composable
fun ExpandableTaskCardView(
    task: Task,
    onClick: () -> Unit,
    onChecked: (Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val dueDateStr = sdf.format(Date(task.dueDate))

    val priorityColor = when (task.priority) {
        3 -> Color.Red
        2 -> Color(0xFFFF9800)
        else -> Color(0xFF4CAF50)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Срок: $dueDateStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onChecked
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
                    Text(
                        text = if (task.description.isNotBlank()) task.description else "Нет описания",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onClick) {
                        Text("Редактировать задачу")
                    }
                }
            }
        }
    }
}
