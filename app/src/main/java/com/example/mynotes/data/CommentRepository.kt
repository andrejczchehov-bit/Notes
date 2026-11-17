package com.example.mynotes.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CommentRepository {
    private const val PREF_NAME = "comments_prefs"

    fun getComments(context: Context, postId: Int): List<Comment> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString("comments_$postId", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Comment>>() {}.type
            Gson().fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addComment(context: Context, postId: Int, author: String, text: String): List<Comment> {
        val comments = getComments(context, postId).toMutableList()
        val newComment = Comment(postId = postId, author = author, text = text)
        comments.add(0, newComment)
        saveComments(context, postId, comments)
        return comments
    }

    fun deleteComment(context: Context, postId: Int, commentId: String): List<Comment> {
        val comments = getComments(context, postId).toMutableList()
        comments.removeAll { it.id == commentId }
        saveComments(context, postId, comments)
        return comments
    }

    private fun saveComments(context: Context, postId: Int, comments: List<Comment>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(comments)
        prefs.edit().putString("comments_$postId", json).apply()
    }
}
