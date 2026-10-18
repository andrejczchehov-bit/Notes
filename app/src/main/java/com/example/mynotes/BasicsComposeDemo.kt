package com.example.mynotes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Лабораторная работа: Декларативный UI на Jetpack Compose
 * Учебный файл: BasicsComposeDemo.kt
 * Иллюстрирует использование базовых компонентов (Text, Column, Row, Box), модификаторов,
 * state hoisting (поднятия состояния) и рекомпозиции на основе существующей модели Task.
 */

// Пример 1: Использование Text с заголовком задачи
// Иллюстрирует отображение текстового контента, стилизацию (fontWeight, fontSize) и modifier.padding
@Composable
fun TaskTitleText(task: Task) {
    // Комментарий: Компонент Text выводит заголовок задачи с отступами и жирным начертанием
    Text(
        text = task.title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(8.dp)
    )
}

// Пример 2: Использование Row
// Иллюстрирует размещение элементов в горизонтальную строку (индикатор приоритета + название + дата напоминания)
@Composable
fun TaskRowItem(task: Task) {
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val dateStr = if (task.reminderTime > 0) sdf.format(Date(task.reminderTime)) else "Без напоминания"

    // Комментарий: Row располагает индикатор приоритета, название и дату горизонтально с распределением пространства (weight)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Индикатор приоритета
        val priorityColor = when (task.priority) {
            3 -> Color.Red
            2 -> Color.Yellow
            else -> Color.Green
        }
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(priorityColor, shape = CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Название задачи (занимает доступное пространство через weight)
        Text(
            text = task.title,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        // Дата напоминания
        Text(
            text = dateStr,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

// Пример 3: Использование Column из трех задач
// Иллюстрирует вертикальное расположение элементов (список из задач) с отступами и заголовком
@Composable
fun TaskColumnList(tasks: List<Task>) {
    // Комментарий: Column выстраивает список задач вертикально друг под другом с заданным интервалом
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Список задач (Column из 3 элементов):",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        val demoTasks = tasks.take(3)
        if (demoTasks.isEmpty()) {
            Text(text = "Дела не запланированы", color = Color.Gray)
        } else {
            for (task in demoTasks) {
                TaskRowItem(task = task)
            }
        }
    }
}

// Пример 4: Использование Box с бейджем срочности поверх карточки (TopEnd)
// Иллюстрирует наложение элементов друг на друга с помощью Box и выравнивание через Modifier.align(Alignment.TopEnd)
@Composable
fun TaskCardWithBadge(task: Task, onClick: () -> Unit) {
    val priorityText = when (task.priority) {
        3 -> "Высокий"
        2 -> "Средний"
        else -> "Низкий"
    }
    val badgeColor = when (task.priority) {
        3 -> Color.Red
        2 -> Color.Magenta
        else -> Color.Blue
    }

    // Комментарий: Box позволяет накладывать бейдж срочности поверх содержимого карточки в правом верхнем углу (TopEnd)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color(0xFFF9F9F9), shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        // Основной контент карточки
        Column(
            modifier = Modifier.padding(end = 50.dp) // отступ справа для бейджа
        ) {
            Text(
                text = task.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = task.description,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }

        // Бейдж срочности в правом верхнем углу (TopEnd)
        Surface(
            color = badgeColor,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Text(
                text = priorityText,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

// Пример 5: Демонстрация State Hoisting (поднятия состояния) и рекомпозиции
// Иллюстрирует локальное состояние с remember и перерисовку интерфейса при изменении данных
@Composable
fun InteractiveTaskDemo() {
    // Комментарий: Состояние кликов сохраняется через remember, вызывая рекомпозицию при инкременте счетчика
    var clickCount by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Рекомпозиция — кликов по задаче: $clickCount",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { clickCount++ }) {
            Text("Имитировать клик")
        }
    }
}

// Главный демонстрационный экран лабораторной работы
@Composable
fun BasicsComposeDemoScreen() {
    val sampleTasks = listOf(
        Task(id = 1, title = "Изучить Jetpack Compose", description = "Разобрать Text, Column, Row, Box", dueDate = System.currentTimeMillis() + 86400000L, priority = 3, reminderTime = System.currentTimeMillis() + 43200000L),
        Task(id = 2, title = "Купить продукты", description = "Молоко, хлеб, фрукты", dueDate = System.currentTimeMillis() + 172800000L, priority = 2, reminderTime = 0L),
        Task(id = 3, title = "Позвонить коллеге", description = "Обсудить детали спринта", dueDate = System.currentTimeMillis() + 259200000L, priority = 1, reminderTime = System.currentTimeMillis() + 100000000L)
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Лабораторная: Декларативный UI",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Демонстрация 1: Text
            TaskTitleText(task = sampleTasks[0])

            // Демонстрация 2: Row
            TaskRowItem(task = sampleTasks[0])

            // Демонстрация 3: Column
            TaskColumnList(tasks = sampleTasks)

            // Демонстрация 4: Box с бейджем TopEnd и clickable
            TaskCardWithBadge(task = sampleTasks[1], onClick = {})

            // Демонстрация 5: State & Recomposition
            InteractiveTaskDemo()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BasicsComposeDemoPreview() {
    BasicsComposeDemoScreen()
}
