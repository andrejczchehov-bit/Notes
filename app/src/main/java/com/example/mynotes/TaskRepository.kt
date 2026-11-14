package com.example.mynotes

import androidx.lifecycle.LiveData

interface ITaskRepository {
    fun getTasksByProfile(profileName: String): LiveData<List<Task>>
    suspend fun insert(task: Task)
    suspend fun update(task: Task)
    suspend fun delete(task: Task)
    suspend fun getTaskById(id: Long): Task?
    suspend fun getAllTasksList(): List<Task>
    suspend fun deleteCompletedTasks()
}

class TaskRepository(private val taskDao: TaskDao) : ITaskRepository {
    override fun getTasksByProfile(profileName: String): LiveData<List<Task>> {
        return taskDao.getTasksByProfile(profileName)
    }

    override suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    override suspend fun update(task: Task) {
        taskDao.update(task)
    }

    override suspend fun delete(task: Task) {
        taskDao.delete(task)
    }

    override suspend fun getTaskById(id: Long): Task? {
        return taskDao.getTaskById(id)
    }

    override suspend fun getAllTasksList(): List<Task> {
        return taskDao.getAllTasksList()
    }

    override suspend fun deleteCompletedTasks() {
        taskDao.deleteCompletedTasks()
    }
}
