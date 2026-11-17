package com.example.mynotes.data

import java.util.*

data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val postId: Int,
    val author: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
