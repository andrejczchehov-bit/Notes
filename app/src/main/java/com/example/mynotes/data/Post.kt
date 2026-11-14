package com.example.mynotes.data

import com.google.gson.annotations.SerializedName

/**
 * Модель данных Post для запросов через Retrofit (Лабораторная работа №10).
 */
data class Post(
    @SerializedName("id") val id: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String
)
