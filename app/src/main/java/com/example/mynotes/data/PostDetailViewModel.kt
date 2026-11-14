package com.example.mynotes.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Состояния UI для детального экрана поста (Лабораторная работа №10).
 */
sealed interface PostDetailUiState {
    object Loading : PostDetailUiState
    data class Success(val post: Post) : PostDetailUiState
    data class Error(val message: String) : PostDetailUiState
}

/**
 * ViewModel для загрузки отдельного поста по ID с учетом языка (Лабораторная работа №10).
 */
class PostDetailViewModel(
    private val postId: Int,
    private val newsLanguage: String = "Русский",
    private val repository: PostsRepository = PostsRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow<PostDetailUiState>(PostDetailUiState.Loading)
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    init {
        loadPost()
    }

    fun loadPost() {
        viewModelScope.launch {
            _uiState.value = PostDetailUiState.Loading
            repository.getPost(postId, newsLanguage)
                .onSuccess { post ->
                    _uiState.value = PostDetailUiState.Success(post)
                }
                .onFailure { throwable ->
                    _uiState.value = PostDetailUiState.Error(throwable.localizedMessage ?: "Не удалось загрузить пост")
                }
        }
    }
}
