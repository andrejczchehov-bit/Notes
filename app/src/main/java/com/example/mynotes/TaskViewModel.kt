package com.example.mynotes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    private val _currentProfile = MutableLiveData("Личный")
    val currentProfile: LiveData<String> get() = _currentProfile

    val allTasks: LiveData<List<Task>>

    init {
        // Сначала инициализируем базу данных и репозиторий
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)

        // Только теперь настраиваем switchMap, когда repository уже существует
        allTasks = _currentProfile.switchMap { profile ->
            repository.getTasksByProfile(profile)
        }
    }

    fun setProfile(profileName: String) {
        if (_currentProfile.value != profileName) {
            _currentProfile.value = profileName
        }
    }

    fun insert(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }
}
