package com.example.mynotes

/**
 * Объект с тестовыми тегами (TestTags) для UI-тестирования через Compose Test (Лабораторная работа №9).
 */
object TestTags {
    const val searchField = "searchField"
    const val addTask = "addTask"
    const val saveButton = "saveButton"
    fun taskRow(id: Long) = "taskRow_$id"
}
