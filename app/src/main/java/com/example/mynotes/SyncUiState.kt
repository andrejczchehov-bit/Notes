package com.example.mynotes

/**
 * Состояния облачной синхронизации (Лабораторная работа №7).
 */
sealed interface SyncUiState {
    object Idle : SyncUiState
    object Loading : SyncUiState
    data class Success(val tasks: List<Task>, val isBackgroundSync: Boolean = false) : SyncUiState
    data class Error(val errorType: NetworkErrorType, val message: String) : SyncUiState
}

enum class NetworkErrorType {
    NO_INTERNET, TIMEOUT, SERVER_ERROR
}
