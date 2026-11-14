package com.example.mynotes

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

/**
 * Экран облачной синхронизации (Лабораторная работа №7, Задание 2)
 * с поддержкой кэша, таймера backoff, дифференцированных ошибок и безопасной фабрики ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudSyncScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val syncViewModel: SyncViewModel = viewModel(factory = viewModelFactory {
        initializer { SyncViewModel(context.applicationContext as Application) }
    })

    val uiState by syncViewModel.uiState.collectAsState()
    val cooldownSeconds by syncViewModel.cooldownSeconds.collectAsState()

    // Автоматический запуск синхронизации при первом открытии в состоянии Idle
    LaunchedEffect(Unit) {
        if (uiState is SyncUiState.Idle) {
            syncViewModel.syncTasks()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Облачная синхронизация") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { syncViewModel.syncTasks() }, enabled = cooldownSeconds == 0) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Синхронизировать")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is SyncUiState.Idle,
                is SyncUiState.Loading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Синхронизация с облаком...", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                is SyncUiState.Error -> {
                    val state = uiState as SyncUiState.Error
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val errorIcon = when (state.errorType) {
                            NetworkErrorType.NO_INTERNET -> Icons.Default.Warning
                            NetworkErrorType.TIMEOUT -> Icons.Default.Info
                            NetworkErrorType.SERVER_ERROR -> Icons.Default.Close
                        }

                        Icon(
                            imageVector = errorIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )

                        if (cooldownSeconds > 0) {
                            Text(
                                text = "Повторить через: ${cooldownSeconds}с",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { syncViewModel.syncTasks() },
                            enabled = cooldownSeconds == 0
                        ) {
                            Text(if (cooldownSeconds > 0) "Ожидание (${cooldownSeconds}с)" else "Повторить")
                        }
                    }
                }
                is SyncUiState.Success -> {
                    val state = uiState as SyncUiState.Success
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (state.isBackgroundSync) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            Text(
                                text = "Обновление в фоне...",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = "Синхронизировано задач: ${state.tasks.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        if (state.tasks.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Нет задач для синхронизации", style = MaterialTheme.typography.bodyLarge)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.tasks, key = { it.id }) { task ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(task.title, fontWeight = FontWeight.Bold)
                                            if (task.description.isNotBlank()) {
                                                Text(task.description, style = MaterialTheme.typography.bodySmall)
                                            }
                                            Text("Профиль: ${task.profileName}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
