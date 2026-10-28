package com.example.mynotes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Учебный компонент TaskCard.
 * Демонстрирует работу с Material 3 Card, модификаторами (fillMaxWidth, padding, clickable),
 * Row, Column и цветными бейджами приоритета (HIGH - красный, MEDIUM - оранжевый, LOW - зеленый).
 */
@Composable
fun TaskCard(
    task: Task,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // Форматирование даты напоминания / срока
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val dateText = if (task.reminderTime > 0) {
        sdf.format(Date(task.reminderTime))
    } else {
        sdf.format(Date(task.dueDate))
    }

    // Определение цвета бейджа по приоритету:
    // 3: HIGH (Красный), 2: MEDIUM (Оранжевый), 1: LOW (Зеленый)
    val priorityColor = when (task.priority) {
        3 -> Color.Red
        2 -> Color(0xFFFF9800) // Оранжевый
        else -> Color(0xFF4CAF50) // Зеленый
    }

    val priorityName = when (task.priority) {
        3 -> "HIGH"
        2 -> "MEDIUM"
        else -> "LOW"
    }

    // Порядок модификаторов и их назначение:
    // 1. modifier.fillMaxWidth() — растягивает карточку на всю доступную ширину родительского контейнера.
    // 2. modifier.padding(...) — добавляет отступы вокруг карточки, чтобы элементы не прижимались к краям.
    // 3. clickable { onClick(task.id) — делает всю карточку интерактивной (кликабельной) и возвращает id задачи.
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick(task.id) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Строка 1: Название задачи (слева с weight) и дата напоминания/срока (справа)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // weight(1f) позволяет названию занять всю свободную ширину строки,
                // не вытесняя дату за пределы экрана.
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Строка 2: Описание задачи и бейдж приоритета
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Бейдж приоритета (HIGH красный, MEDIUM оранжевый, LOW зелёный)
                Surface(
                    color = priorityColor,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = priorityName,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskCardPreview() {
    MaterialTheme {
        Column {
            TaskCard(
                task = Task(id = 1, title = "Срочная задача", description = "Высокий приоритет", dueDate = System.currentTimeMillis(), priority = 3, reminderTime = System.currentTimeMillis()),
                onClick = {}
            )
            TaskCard(
                task = Task(id = 2, title = "Обычная задача", description = "Средний приоритет", dueDate = System.currentTimeMillis(), priority = 2, reminderTime = 0L),
                onClick = {}
            )
            TaskCard(
                task = Task(id = 3, title = "Легкая задача", description = "Низкий приоритет", dueDate = System.currentTimeMillis(), priority = 1, reminderTime = 0L),
                onClick = {}
            )
        }
    }
}
