package com.example.mynotes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * SyncViewModel с поддержкой защиты от множественных запросов, retry с бэк-оффом,
 * кэшированием, логированием и безопасной инициализацией (Лабораторная работа №7, Задание 2).
 */
class SyncViewModel(
    application: Application,
    private val taskRepository: ITaskRepository? = null
) : AndroidViewModel(application) {
    private val repository: ITaskRepository

    init {
        var tempRepo: ITaskRepository? = null
        try {
            println("SyncViewModel: initializing repository from AppDatabase")
            val taskDao = AppDatabase.getDatabase(application).taskDao()
            tempRepo = TaskRepository(taskDao)
            println("SyncViewModel: repository initialized successfully")
        } catch (e: Exception) {
            println("SyncViewModel init ERROR: ${e.message}")
            e.printStackTrace()
        }
        repository = taskRepository ?: tempRepo ?: object : ITaskRepository {
            override fun getTasksByProfile(profileName: String): LiveData<List<Task>> = MutableLiveData<List<Task>>(emptyList())
            override suspend fun insert(task: Task) {}
            override suspend fun update(task: Task) {}
            override suspend fun delete(task: Task) {}
            override suspend fun getTaskById(id: Long): Task? = null
            override suspend fun getAllTasksList(): List<Task> = emptyList()
            override suspend fun deleteCompletedTasks() {}
        }
    }

    private val _uiState = MutableStateFlow<SyncUiState>(SyncUiState.Idle)
    val uiState: StateFlow<SyncUiState> = _uiState.asStateFlow()

    private var isRequestActive = false
    private var retryCount = 0
    var cachedTasks: List<Task>? = null
        private set

    private val _cooldownSeconds = MutableStateFlow(0)
    val cooldownSeconds: StateFlow<Int> = _cooldownSeconds.asStateFlow()

    private var cooldownJob: Job? = null

    fun syncTasks(forceErrorForTest: NetworkErrorType? = null, forceSuccess: Boolean = false) {
        println("SyncViewModel: syncTasks() called. isRequestActive=$isRequestActive, cooldown=${_cooldownSeconds.value}")
        if (isRequestActive) return
        if (_cooldownSeconds.value > 0) return

        viewModelScope.launch {
            isRequestActive = true

            // Если есть кэш, показываем Success в фоновом режиме, иначе Loading
            if (cachedTasks != null && _uiState.value !is SyncUiState.Error) {
                println("SyncViewModel: displaying cached tasks in background sync mode")
                _uiState.value = SyncUiState.Success(cachedTasks!!, isBackgroundSync = true)
            } else {
                println("SyncViewModel: setting state to Loading")
                _uiState.value = SyncUiState.Loading
            }

            println("SyncViewModel: starting network delay (1000ms)...")
            delay(1000)

            val isError = forceErrorForTest != null || (!forceSuccess && Random.nextInt(5) == 0)
            println("SyncViewModel: network delay finished. isError=$isError")

            if (isError) {
                retryCount++
                val (type, message) = if (forceErrorForTest != null) {
                    val msg = when (forceErrorForTest) {
                        NetworkErrorType.NO_INTERNET -> "Нет подключения к интернету"
                        NetworkErrorType.TIMEOUT -> "Превышено время ожидания (Таймаут)"
                        NetworkErrorType.SERVER_ERROR -> "Внутренняя ошибка сервера"
                    }
                    Pair(forceErrorForTest, msg)
                } else {
                    val errorTypes = listOf(
                        Pair(NetworkErrorType.NO_INTERNET, "Нет подключения к интернету"),
                        Pair(NetworkErrorType.TIMEOUT, "Превышено время ожидания (Таймаут)"),
                        Pair(NetworkErrorType.SERVER_ERROR, "Внутренняя ошибка сервера")
                    )
                    errorTypes.random()
                }

                println("SyncViewModel: Error occurred -> type=$type, message=$message, retryCount=$retryCount")
                _uiState.value = SyncUiState.Error(type, message)

                val delayMs = if (retryCount > 1) 1000L * (1L shl (retryCount - 2).coerceAtMost(3)) else 0L
                if (delayMs > 0) {
                    println("SyncViewModel: starting backoff cooldown for ${delayMs}ms")
                    startCooldown(delayMs)
                }
            } else {
                retryCount = 0
                val tasks = try {
                    println("SyncViewModel: fetching tasks from repository...")
                    val fetched = repository.getAllTasksList()
                    println("SyncViewModel: successfully fetched ${fetched.size} tasks")
                    fetched
                } catch (e: Exception) {
                    println("SyncViewModel: error fetching tasks: ${e.message}")
                    emptyList()
                }
                cachedTasks = tasks
                _uiState.value = SyncUiState.Success(tasks, isBackgroundSync = false)
                println("SyncViewModel: state updated to Success with ${tasks.size} tasks")
            }

            isRequestActive = false
            println("SyncViewModel: syncTasks finished. isRequestActive reset to false")
        }
    }

    private fun startCooldown(delayMillis: Long) {
        cooldownJob?.cancel()
        val seconds = (delayMillis / 1000).toInt()
        _cooldownSeconds.value = seconds
        cooldownJob = viewModelScope.launch {
            for (s in seconds downTo 1) {
                delay(1000)
                _cooldownSeconds.value = s - 1
            }
        }
    }
}
