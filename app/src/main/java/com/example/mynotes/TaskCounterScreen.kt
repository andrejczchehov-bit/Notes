package com.example.mynotes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Часть 1 Лабораторной работы №4: Счётчик количества задач (TaskCounterScreen).
 * Демонстрирует использование rememberSaveable для сохранения состояния при конфигурационных изменениях.
 */
@Composable
fun TaskCounterScreen(modifier: Modifier = Modifier) {
    // Используем rememberSaveable вместо remember, чтобы значение счетчика (N) сохранялось
    // при конфигурационных изменениях (например, при повороте экрана устройства).
    // Обычный remember помнит значение только пока активна текущая композиция в памяти,
    // но сбрасывается при пересоздании Activity/Fragment. rememberSaveable автоматически
    // сохраняет данные в Bundle и восстанавливает их после пересоздания.
    var taskCount by rememberSaveable { mutableStateOf(0) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Всего задач: $taskCount",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { taskCount++ }
                ) {
                    Text("Добавить задачу (+1)")
                }

                OutlinedButton(
                    onClick = { if (taskCount > 0) taskCount-- }
                ) {
                    Text("Удалить задачу (-1)")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskCounterScreenPreview() {
    MaterialTheme {
        TaskCounterScreen()
    }
}
