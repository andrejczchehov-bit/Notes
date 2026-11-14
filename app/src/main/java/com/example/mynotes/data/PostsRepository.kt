package com.example.mynotes.data

/**
 * Репозиторий новостей для работы с Spaceflight News API через runCatching (Лабораторная работа №10).
 */
class PostsRepository(private val api: PostsApi = RetrofitClient.api) {
    suspend fun getPosts(): Result<List<Post>> = runCatching {
        api.getPosts().results
    }

    suspend fun getPost(id: Int): Result<Post> = runCatching {
        api.getPost(id)
    }
}
