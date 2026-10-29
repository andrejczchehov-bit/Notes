package com.example.mynotes

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {
    fun getTasksByProfile(profileName: String): LiveData<List<Task>> {
        return taskDao.getTasksByProfile(profileName)
    }

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }

    suspend fun getTaskById(id: Long): Task? {
        return taskDao.getTaskById(id)
    }
}
