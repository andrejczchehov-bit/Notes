package com.example.mynotes.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * PostsViewModel с поддержкой кэша в памяти, фонового обновления (isRefreshing)
 * и офлайн-режима при IOException (Лабораторная работа №10, PRO-уровень).
 */
class PostsViewModel(private val repository: PostsRepository = PostsRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow<PostsUiState>(PostsUiState.Loading)
    val uiState: StateFlow<PostsUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    var cachedPosts: List<Post> = emptyList()
        private set

    init {
        load()
    }

    fun load(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _isRefreshing.value = true
            } else if (cachedPosts.isEmpty()) {
                _uiState.value = PostsUiState.Loading
            } else {
                // Если кэш есть, сразу показываем его и запускаем обновление в фоне
                _uiState.value = PostsUiState.Success(cachedPosts)
                _isRefreshing.value = true
            }

            repository.getPosts()
                .onSuccess { posts ->
                    cachedPosts = posts
                    _uiState.value = PostsUiState.Success(posts)
                    _isRefreshing.value = false
                    _isOffline.value = false
                }
                .onFailure { throwable ->
                    _isRefreshing.value = false
                    if (throwable is IOException || cachedPosts.isNotEmpty()) {
                        _isOffline.value = true
                        if (cachedPosts.isNotEmpty()) {
                            // Офлайн режим: показываем кэш и бейдж Офлайн, не заменяя экран ошибкой
                            _uiState.value = PostsUiState.Success(cachedPosts)
                        } else {
                            _uiState.value = PostsUiState.Error("Нет подключения к интернету. Показаны кэшированные данные")
                        }
                    } else {
                        _uiState.value = PostsUiState.Error(throwable.localizedMessage ?: "Неизвестная ошибка")
                    }
                }
        }
    }
}
