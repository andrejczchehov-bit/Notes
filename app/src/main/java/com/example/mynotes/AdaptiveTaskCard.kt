package com.example.mynotes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Адаптивная карточка задачи (AdaptiveTaskCard) с плейсхолдером изображения,
 * названием, приоритетом и кнопкой «Выполнить».
 * Автоматически меняет верстку с Row (картинка слева, текст справа) на Column (всё друг под другом)
 * в зависимости от доступной ширины.
 */
@Composable
fun AdaptiveTaskCard(
    task: Task,
    onCompleteClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val priorityText = when (task.priority) {
        3 -> "HIGH"
        2 -> "MEDIUM"
        else -> "LOW"
    }
    val priorityColor = when (task.priority) {
        3 -> Color.Red
        2 -> Color(0xFFFF9800) // Оранжевый
        else -> Color(0xFF4CAF50) // Зеленый
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            val isWide = maxWidth > 400.dp

            if (isWide) {
                // Широкий экран: Иконка слева, название и приоритет по центру, кнопка справа (Row)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TaskImagePlaceholder()

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = priorityColor,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = priorityText,
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Button(
                        onClick = { onCompleteClick(task.id) },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Выполнить")
                    }
                }
            } else {
                // Узкий экран: элементы расположены вертикально друг под другом (Column)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskImagePlaceholder()

                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        color = priorityColor,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = priorityText,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Button(
                        onClick = { onCompleteClick(task.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Выполнить")
                    }
                }
            }
        }
    }
}

@Composable
fun TaskImagePlaceholder() {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.List,
            contentDescription = "Плейсхолдер",
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Preview(name = "Adaptive Card - Wide", showBackground = true, widthDp = 450)
@Composable
fun AdaptiveTaskCardWidePreview() {
    MaterialTheme {
        AdaptiveTaskCard(
            task = Task(id = 1, title = "Сделать лабу по Compose", description = "", dueDate = System.currentTimeMillis(), priority = 3, reminderTime = 0L),
            onCompleteClick = {}
        )
    }
}

@Preview(name = "Adaptive Card - Narrow", showBackground = true, widthDp = 300)
@Composable
fun AdaptiveTaskCardNarrowPreview() {
    MaterialTheme {
        AdaptiveTaskCard(
            task = Task(id = 1, title = "Сделать лабу по Compose", description = "", dueDate = System.currentTimeMillis(), priority = 3, reminderTime = 0L),
            onCompleteClick = {}
        )
    }
}
