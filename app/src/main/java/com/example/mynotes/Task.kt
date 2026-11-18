package com.example.mynotes

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Модель задачи (Entity) с аннотацией @Immutable для оптимизации производительности (Лабораторная работа №9).
 */
@Immutable
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val dueDate: Long,
    val priority: Int, // 1: Low, 2: Medium, 3: High
    val isCompleted: Boolean = false,
    val reminderTime: Long = 0L,
    val profileName: String = "Личный",
    val ownerLogin: String = "admin"
)
