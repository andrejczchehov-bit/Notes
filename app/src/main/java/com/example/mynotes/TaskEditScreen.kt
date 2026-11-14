package com.example.mynotes

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Compose-экран TaskEditScreen для создания и редактирования задачи с поддержкой дедлайнов,
 * приоритетов, уведомлений, тест-тегов и анимационного индикатора сохранения (Лабораторная работа №9).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    taskId: Long = -1L,
    viewModel: TaskViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentProfile by viewModel.currentProfile.observeAsState("Личный")
    val coroutineScope = rememberCoroutineScope()

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var priority by rememberSaveable { mutableStateOf(2) } // 1: Low, 2: Medium, 3: High
    var dueDate by rememberSaveable { mutableStateOf(System.currentTimeMillis() + 86400000L) }
    var reminderTime by rememberSaveable { mutableStateOf(0L) }
    var isReminderEnabled by rememberSaveable { mutableStateOf(false) }
    var isExistingTask by rememberSaveable { mutableStateOf(false) }
    var isSaving by rememberSaveable { mutableStateOf(false) }

    // Загрузка задачи при редактировании через LaunchedEffect (Модуль 2)
    LaunchedEffect(taskId) {
        if (taskId != -1L) {
            isExistingTask = true
            val task = AppDatabase.getDatabase(context).taskDao().getTaskById(taskId)
            task?.let {
                title = it.title
                description = it.description
                priority = it.priority
                dueDate = it.dueDate
                reminderTime = it.reminderTime
                isReminderEnabled = it.reminderTime > 0
            }
        }
    }

    // Запрос разрешения на уведомления (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Разрешение на уведомления не предоставлено", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Выбор даты и времени
    fun showDateTimePicker(onSelected: (Long) -> Unit, initialTime: Long) {
        val calendar = Calendar.getInstance().apply { timeInMillis = initialTime }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        onSelected(calendar.timeInMillis)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isExistingTask) "Редактировать задачу" else "Новая задача ($currentProfile)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название задачи") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text("Приоритет:", fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilterChip(
                    selected = priority == 1,
                    onClick = { priority = 1 },
                    label = { Text("Низкий") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF4CAF50).copy(alpha = 0.2f),
                        selectedLabelColor = Color(0xFF4CAF50)
                    )
                )
                FilterChip(
                    selected = priority == 2,
                    onClick = { priority = 2 },
                    label = { Text("Средний") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFF9800).copy(alpha = 0.2f),
                        selectedLabelColor = Color(0xFFFF9800)
                    )
                )
                FilterChip(
                    selected = priority == 3,
                    onClick = { priority = 3 },
                    label = { Text("Высокий") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFE53935).copy(alpha = 0.2f),
                        selectedLabelColor = Color(0xFFE53935)
                    )
                )
            }

            HorizontalDivider()

            // Срок выполнения
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Срок выполнения", fontWeight = FontWeight.Bold)
                    Text(sdf.format(Date(dueDate)), style = MaterialTheme.typography.bodyMedium)
                }
                Button(onClick = { showDateTimePicker({ dueDate = it }, dueDate) }) {
                    Text("Изменить срок")
                }
            }

            HorizontalDivider()

            // Напоминание
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Напомнить о задаче", fontWeight = FontWeight.Bold)
                Switch(
                    checked = isReminderEnabled,
                    onCheckedChange = {
                        isReminderEnabled = it
                        if (it) {
                            checkNotificationPermission()
                            if (reminderTime == 0L) reminderTime = dueDate
                        }
                    }
                )
            }

            if (isReminderEnabled) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (reminderTime > 0) sdf.format(Date(reminderTime)) else "Не выбрано",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedButton(onClick = { showDateTimePicker({ reminderTime = it }, if (reminderTime > 0) reminderTime else dueDate) }) {
                        Text("Время напоминания")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка сохранения или пульсирующий индикатор сохранения (Модуль 1)
            if (isSaving) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SavingIndicator()
                }
            } else {
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            Toast.makeText(context, "Введите название задачи", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isSaving = true
                        coroutineScope.launch {
                            val finalReminder = if (isReminderEnabled) reminderTime else 0L
                            val task = Task(
                                id = if (isExistingTask) taskId else 0L,
                                title = title,
                                description = description,
                                dueDate = dueDate,
                                priority = priority,
                                reminderTime = finalReminder,
                                profileName = currentProfile
                            )

                            if (isExistingTask) {
                                viewModel.update(task)
                                if (finalReminder > 0) {
                                    NotificationWorker.schedule(context, task)
                                } else {
                                    NotificationWorker.cancel(context, task.id)
                                }
                            } else {
                                viewModel.insert(task)
                                if (finalReminder > 0) {
                                    NotificationWorker.schedule(context, task)
                                }
                            }

                            delay(600) // Имитация паузы для анимации сохранения
                            Toast.makeText(context, "Задача сохранена", Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.saveButton) // Тест-тег для сохранения (Модуль 3)
                ) {
                    Text("Сохранить")
                }
            }

            // Кнопка удаления
            if (isExistingTask) {
                OutlinedButton(
                    onClick = {
                        val task = Task(
                            id = taskId,
                            title = title,
                            description = description,
                            dueDate = dueDate,
                            priority = priority,
                            reminderTime = reminderTime,
                            profileName = currentProfile
                        )
                        viewModel.delete(task)
                        NotificationWorker.cancel(context, taskId)
                        Toast.makeText(context, "Задача удалена", Toast.LENGTH_SHORT).show()
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Задача удалена")
                }
            }
        }
    }
}

/**
 * Индикатор сохранения на основе rememberInfiniteTransition с тремя пульсирующими точками (Лабораторная работа №9).
 */
@Composable
fun SavingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "saving")
    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val alpha3 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        Text("Сохранение", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = alpha1)))
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = alpha2)))
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = alpha3)))
    }
}
