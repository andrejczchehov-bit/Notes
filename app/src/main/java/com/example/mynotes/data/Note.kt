package com.example.mynotes.data

import kotlinx.serialization.Serializable

/**
 * Модель заметки для сериализации в JSON (Лабораторная работа №11).
 */
@Serializable
data class Note(
    val id: Long,
    val title: String,
    val body: String,
    val updatedAt: Long
)
