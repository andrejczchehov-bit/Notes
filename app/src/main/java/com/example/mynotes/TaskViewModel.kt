package com.example.mynotes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.mynotes.auth.data.AuthManager
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    private val _currentProfile = MutableLiveData("Личный")
    val currentProfile: LiveData<String> get() = _currentProfile

    val allTasks: LiveData<List<Task>>

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)

        // Динамически получаем текущего пользователя из AuthManager при каждом запросе задач
        allTasks = _currentProfile.switchMap { profile ->
            val ownerLogin = AuthManager.getCurrentUserLogin(application)
            repository.getTasksByProfile(profile, ownerLogin)
        }
    }

    fun setProfile(profileName: String) {
        if (_currentProfile.value != profileName) {
            _currentProfile.value = profileName
        }
    }

    fun insert(task: Task) {
        val profile = _currentProfile.value ?: "Личный"
        val ownerLogin = AuthManager.getCurrentUserLogin(getApplication())
        val taskWithProfile = task.copy(profileName = profile, ownerLogin = ownerLogin)

        println("DEBUG_PROFILE: Сохраняем задачу '${task.title}' с профилем: $profile для пользователя: $ownerLogin")

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

    fun persist() {
        val ownerLogin = AuthManager.getCurrentUserLogin(getApplication())
        android.util.Log.d("TaskViewModel", "Auto-saving tasks state / persisting data for user $ownerLogin...")
    }
}
