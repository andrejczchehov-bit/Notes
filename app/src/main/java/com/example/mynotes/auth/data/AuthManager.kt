package com.example.mynotes.auth.data

import android.content.Context
import com.google.gson.Gson

object AuthManager {
    private const val PREF_NAME = "auth_manager_prefs"

    fun saveAccessContext(context: Context, accessContext: AccessContext) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(accessContext)
        prefs.edit()
            .putString("access_context", json)
            .putString("current_login", accessContext.userId)
            .putBoolean("is_admin", accessContext.role == Role.ADMIN)
            .apply()
    }

    fun getAccessContext(context: Context): AccessContext? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString("access_context", null) ?: return null
        return try {
            Gson().fromJson(json, AccessContext::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun getCurrentUserLogin(context: Context): String {
        val ctx = getAccessContext(context)
        return ctx?.userId ?: "Пользователь"
    }

    fun isAdmin(context: Context): Boolean {
        return getAccessContext(context)?.role == Role.ADMIN
    }

    fun isSecurity(context: Context): Boolean {
        return getAccessContext(context)?.role == Role.SECURITY
    }

    fun hasScope(context: Context, scope: String): Boolean {
        val ctx = getAccessContext(context) ?: return false
        return ctx.scopes.contains(scope)
    }
}
