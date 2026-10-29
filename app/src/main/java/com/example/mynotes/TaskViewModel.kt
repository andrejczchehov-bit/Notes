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
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)

        // Вот тут switchMap будет перезапрашивать данные при смене профиля
        allTasks = _currentProfile.switchMap { profile ->
            repository.getTasksByProfile(profile)
        }
    }

    fun setProfile(profileName: String) {
        if (_currentProfile.value != profileName) {
            _currentProfile.value = profileName
        }
    }

    // ВАЖНО: Теперь метод insert сам берет текущий профиль из ViewModel,
    // а не из интерфейса!
    fun insert(task: Task) {
        val profile = _currentProfile.value ?: "Личный"
        val taskWithProfile = task.copy(profileName = profile)

        // ВОТ ТУТ ЛОГ
        println("DEBUG_PROFILE: Сохраняем задачу '${task.title}' с профилем: $profile")

        viewModelScope.launch {
            repository.insert(taskWithProfile)
        }
    }

    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }
}