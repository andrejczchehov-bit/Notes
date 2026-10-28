package com.example.mynotes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Экран списка задач (TaskHomeScreen) на Jetpack Compose в Material 3.
 * Реализует State Hoisting: разделение на stateless (TaskHomeContent) и stateful (TaskHomeScreen) функции.
 */

// Статичный (Stateless) компонент, принимающий только состояние и колбэки (State Hoisting)
@Composable
fun TaskHomeContent(
    taskCount: Int,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить задачу"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Мои задачи",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Сегодня у вас $taskCount задач",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Stateful компонент, связанный с TaskViewModel (получает счетчик из LiveData)
@Composable
fun TaskHomeScreen(
    viewModel: TaskViewModel = viewModel(),
    onAddClick: () -> Unit
) {
    val tasks by viewModel.allTasks.observeAsState(emptyList())

    TaskHomeContent(
        taskCount = tasks.size,
        onAddClick = onAddClick
    )
}

@Preview(showBackground = true)
@Composable
fun TaskHomeContentPreview() {
    MaterialTheme {
        TaskHomeContent(
            taskCount = 5,
            onAddClick = {}
        )
    }
}
