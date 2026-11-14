package com.example.mynotes.data

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Экран редактирования/создания заметки с автосохранением через DisposableEffect (Лабораторная работа №11).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    noteId: Long,
    onBack: () -> Unit,
    viewModel: NotesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var title by rememberSaveable { mutableStateOf("") }
    var body by rememberSaveable { mutableStateOf("") }
    var isLoaded by rememberSaveable { mutableStateOf(false) }

    // Загружаем заметку при редактировании
    LaunchedEffect(noteId) {
        if (noteId != -1L && !isLoaded) {
            val notes = (uiState as? NotesUiState.Success)?.notes.orEmpty()
            val existing = notes.find { it.id == noteId }
            if (existing != null) {
                title = existing.title
                body = existing.body
                isLoaded = true
            }
        }
    }

    // Сохранение при выходе через DisposableEffect (onDispose)
    val currentTitle = title
    val currentBody = body
    val idToSave = if (noteId == -1L) System.currentTimeMillis() else noteId

    DisposableEffect(Unit) {
        onDispose {
            if (currentTitle.isNotBlank() || currentBody.isNotBlank()) {
                val note = Note(
                    id = idToSave,
                    title = currentTitle.ifBlank { "Без названия" },
                    body = currentBody,
                    updatedAt = System.currentTimeMillis()
                )
                viewModel.saveNote(note)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == -1L) "Новая заметка" else "Редактировать заметку") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Заголовок") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                label = { Text("Текст заметки") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Готово")
            }
        }
    }
}
