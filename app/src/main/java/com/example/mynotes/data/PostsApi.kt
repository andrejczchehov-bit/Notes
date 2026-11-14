package com.example.mynotes.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API интерфейс для получения новостей из Spaceflight News API v4 (Лабораторная работа №10).
 */
interface PostsApi {
    @GET("articles/")
    suspend fun getPosts(@Query("limit") limit: Int = 20): NewsResponse

    @GET("articles/{id}/")
    suspend fun getPost(@Path("id") id: Int): Post
}
