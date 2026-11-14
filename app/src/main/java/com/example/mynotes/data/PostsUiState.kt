package com.example.mynotes.data

/**
 * Состояния UI для экрана постов (Лабораторная работа №10).
 */
sealed interface PostsUiState {
    object Loading : PostsUiState
    data class Success(val posts: List<Post>) : PostsUiState
    data class Error(val message: String) : PostsUiState
}
