package com.example.mynotes

import androidx.lifecycle.LiveData

interface ITaskRepository {
    fun getTasksByProfile(profileName: String, ownerLogin: String): LiveData<List<Task>>
    suspend fun insert(task: Task): Long
    suspend fun update(task: Task)
    suspend fun delete(task: Task)
    suspend fun getTaskById(id: Long): Task?
    suspend fun getAllTasksList(ownerLogin: String): List<Task>
    suspend fun deleteCompletedTasks()
}

class TaskRepository(private val taskDao: TaskDao) : ITaskRepository {
    override fun getTasksByProfile(profileName: String, ownerLogin: String): LiveData<List<Task>> {
        return taskDao.getTasksByProfile(profileName, ownerLogin)
    }

    override suspend fun insert(task: Task): Long {
        return taskDao.insert(task)
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

    override suspend fun getAllTasksList(ownerLogin: String): List<Task> {
        return taskDao.getAllTasksList(ownerLogin)
    }

    override suspend fun deleteCompletedTasks() {
        taskDao.deleteCompletedTasks()
    }
}
