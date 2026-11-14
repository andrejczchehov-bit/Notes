package com.example.mynotes.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel для управления загрузкой и состоянием постов (Лабораторная работа №10).
 */
class PostsViewModel(private val repository: PostsRepository = PostsRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow<PostsUiState>(PostsUiState.Loading)
    val uiState: StateFlow<PostsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = PostsUiState.Loading
            repository.getPosts()
                .onSuccess { posts ->
                    _uiState.value = PostsUiState.Success(posts)
                }
                .onFailure { throwable ->
                    _uiState.value = PostsUiState.Error(throwable.localizedMessage ?: "Неизвестная ошибка")
                }
        }
    }
}
