package com.example.mynotes.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Конфигурация Json для сериализации/десериализации списка заметок (Лабораторная работа №11).
 */
object NotesJson {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun encode(notes: List<Note>): String {
        return json.encodeToString(notes)
    }

    fun decode(jsonString: String): List<Note> {
        return json.decodeFromString(jsonString)
    }
}
