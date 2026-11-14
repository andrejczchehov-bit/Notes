package com.example.mynotes.data

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Состояния UI для экрана заметок (Лабораторная работа №11).
 */
sealed interface NotesUiState {
    object Loading : NotesUiState
    data class Success(val notes: List<Note>) : NotesUiState
    data class Error(val message: String) : NotesUiState
}

/**
 * ViewModel для управления состоянием, файлами, экспортом и импортом через SAF (Лабораторная работа №11).
 */
class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NotesFileRepository(application.filesDir)

    private val _uiState = MutableStateFlow<NotesUiState>(NotesUiState.Loading)
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = NotesUiState.Loading
            repository.loadNotes()
                .onSuccess { notes ->
                    _uiState.value = NotesUiState.Success(notes)
                }
                .onFailure { error ->
                    _uiState.value = NotesUiState.Error(error.localizedMessage ?: "Ошибка загрузки заметок")
                }
        }
    }

    fun saveNote(note: Note) {
        viewModelScope.launch {
            val currentNotes = (_uiState.value as? NotesUiState.Success)?.notes.orEmpty()
            val index = currentNotes.indexOfFirst { it.id == note.id }
            val updatedNotes = if (index >= 0) {
                currentNotes.toMutableList().apply { set(index, note) }
            } else {
                currentNotes + note
            }
            repository.saveNotes(updatedNotes)
                .onSuccess {
                    _uiState.value = NotesUiState.Success(updatedNotes)
                }
                .onFailure { error ->
                    _uiState.value = NotesUiState.Error(error.localizedMessage ?: "Ошибка сохранения заметки")
                }
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            val currentNotes = (_uiState.value as? NotesUiState.Success)?.notes.orEmpty()
            val updatedNotes = currentNotes.filter { it.id != id }
            repository.saveNotes(updatedNotes)
                .onSuccess {
                    _uiState.value = NotesUiState.Success(updatedNotes)
                }
                .onFailure { error ->
                    _uiState.value = NotesUiState.Error(error.localizedMessage ?: "Ошибка удаления заметки")
                }
        }
    }

    fun importNotes(notes: List<Note>) {
        viewModelScope.launch {
            repository.saveNotes(notes)
                .onSuccess {
                    _uiState.value = NotesUiState.Success(notes)
                }
                .onFailure { error ->
                    _uiState.value = NotesUiState.Error(error.localizedMessage ?: "Ошибка импорта заметок")
                }
        }
    }

    // SAF Экспорт (Лабораторная работа №11)
    fun exportTo(uri: Uri, context: android.content.Context) {
        viewModelScope.launch {
            val notes = (_uiState.value as? NotesUiState.Success)?.notes.orEmpty()
            runCatching {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    val jsonString = NotesJson.encode(notes)
                    outputStream.write(jsonString.toByteArray())
                }
            }.onSuccess {
                Toast.makeText(context, "Экспорт выполнен успешно", Toast.LENGTH_SHORT).show()
            }.onFailure { e ->
                Toast.makeText(context, "Ошибка экспорта: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // SAF Импорт (Лабораторная работа №11)
    fun importFrom(uri: Uri, context: android.content.Context) {
        viewModelScope.launch {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val jsonString = inputStream.bufferedReader().use { it.readText() }
                    val notes = NotesJson.decode(jsonString)
                    importNotes(notes)
                }
            }.onSuccess {
                Toast.makeText(context, "Импорт выполнен успешно", Toast.LENGTH_SHORT).show()
            }.onFailure { e ->
                Toast.makeText(context, "Ошибка импорта: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
