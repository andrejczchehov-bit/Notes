package com.example.mynotes.auth.data

data class AccessContext(
    val userId: String,
    val role: Role,
    val scopes: Set<String>
)
