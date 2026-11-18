package com.example.mynotes.auth.data

import android.content.Context
import com.google.gson.Gson

object AuthManager {
    private const val PREF_NAME = "auth_manager_prefs"

    fun saveAccessContext(context: Context, accessContext: AccessContext) {
        try {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val json = Gson().toJson(accessContext)
            prefs.edit()
                .putString("access_context", json)
                .putString("current_login", accessContext.userId)
                .putBoolean("is_admin", accessContext.role == Role.ADMIN)
                .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAccessContext(context: Context): AccessContext? {
        return try {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val json = prefs.getString("access_context", null) ?: return null
            Gson().fromJson(json, AccessContext::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun getCurrentUserLogin(context: Context): String {
        return try {
            val ctx = getAccessContext(context)
            ctx?.userId ?: "admin"
        } catch (e: Exception) {
            "admin"
        }
    }

    fun isAdmin(context: Context): Boolean {
        return try {
            getAccessContext(context)?.role == Role.ADMIN
        } catch (e: Exception) {
            false
        }
    }

    fun isSecurity(context: Context): Boolean {
        return try {
            getAccessContext(context)?.role == Role.SECURITY
        } catch (e: Exception) {
            false
        }
    }

    fun hasScope(context: Context, scope: String): Boolean {
        return try {
            val ctx = getAccessContext(context) ?: return false
            ctx.scopes.contains(scope)
        } catch (e: Exception) {
            false
        }
    }
}
