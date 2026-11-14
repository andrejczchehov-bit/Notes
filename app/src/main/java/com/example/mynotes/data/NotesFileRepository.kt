package com.example.mynotes.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Репозиторий для работы с файлами заметок с атомарной записью (Лабораторная работа №11).
 */
class NotesFileRepository(
    private val filesDir: File,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val file = File(filesDir, "notes.json")
    private val tempFile = File(filesDir, "notes.json.tmp")

    suspend fun loadNotes(): Result<List<Note>> = withContext(ioDispatcher) {
        runCatching {
            if (!file.exists()) {
                emptyList()
            } else {
                val jsonString = file.readText()
                NotesJson.decode(jsonString)
            }
        }
    }

    suspend fun saveNotes(notes: List<Note>): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val jsonString = NotesJson.encode(notes)
            // Атомарная запись: пишем во временный файл, затем переименовываем
            tempFile.writeText(jsonString)
            if (file.exists()) {
                file.delete()
            }
            if (!tempFile.renameTo(file)) {
                tempFile.copyTo(file, overwrite = true)
                tempFile.delete()
            }
        }
    }
}
