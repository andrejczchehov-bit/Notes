package com.example.mynotes.auth.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_session_prefs", Context.MODE_PRIVATE)

    var accessToken: String? = null
        private set

    fun saveTokens(access: String, refresh: String) {
        accessToken = access
        val encryptedRefresh = Base64.encodeToString(refresh.toByteArray(), Base64.DEFAULT)
        prefs.edit().putString("refresh_token", encryptedRefresh).apply()
    }

    fun getRefreshToken(): String? {
        val stored = prefs.getString("refresh_token", null) ?: return null
        return try {
            String(Base64.decode(stored, Base64.DEFAULT))
        } catch (e: Exception) {
            stored
        }
    }

    fun clear() {
        accessToken = null
        prefs.edit().clear().apply()
    }
}
