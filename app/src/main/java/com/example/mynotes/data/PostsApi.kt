package com.example.mynotes.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API интерфейс для получения постов (Лабораторная работа №10).
 */
interface PostsApi {
    @GET("posts")
    suspend fun getPosts(@Query("_limit") limit: Int = 20): List<Post>

    @GET("posts/{id}")
    suspend fun getPost(@Path("id") id: Int): Post
}
