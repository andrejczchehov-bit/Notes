package com.example.mynotes.data

import com.google.gson.annotations.SerializedName

/**
 * Модель данных NewsResponse и Post для работы с Spaceflight News API v4 (Лабораторная работа №10).
 */
data class NewsResponse(
    @SerializedName("results") val results: List<Post>
)

data class Post(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("summary") val body: String,
    @SerializedName("image_url") val imageUrl: String
)
