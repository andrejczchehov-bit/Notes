package com.example.mynotes.auth.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.delay

sealed interface AuthResult {
    data class Success(val accessToken: String, val refreshToken: String) : AuthResult
    data class AuthFailed(val attemptsLeft: Int, val message: String) : AuthResult
    data class AuthLocked(val secondsRemaining: Int, val message: String) : AuthResult
}

class AuthRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_repo_prefs", Context.MODE_PRIVATE)
    private val appContext = context.applicationContext

    companion object {
        private const val MAX_ATTEMPTS = 4
        private const val LOCK_DURATION_MS = 5 * 60 * 1000L // 5 minutes
    }

    suspend fun login(login: String, pass: String): AuthResult {
        delay(600) // Simulate network delay

        val lockUntil = prefs.getLong("lock_until", 0L)
        val now = System.currentTimeMillis()
        if (now < lockUntil) {
            val secondsRemaining = ((lockUntil - now) / 1000).toInt()
            AuthAudit.logEvent(appContext, "LOCK", "Попытка входа во время блокировки. Осталось ${secondsRemaining}с")
            return AuthResult.AuthLocked(secondsRemaining, "Вход заблокирован. Повторите попытку через $secondsRemaining сек.")
        }

        if (login == "admin" && pass == "1234") {
            prefs.edit().putInt("failed_attempts", 0).remove("lock_until").apply()
            AuthAudit.logEvent(appContext, "LOGIN_OK", "Успешный вход пользователя: $login")
            return AuthResult.Success("access_token_mock_123", "refresh_token_mock_789")
        } else {
            var attempts = prefs.getInt("failed_attempts", 0) + 1
            val attemptsLeft = MAX_ATTEMPTS - attempts

            AuthAudit.logEvent(appContext, "LOGIN_FAIL", "Неверный логин или пароль. Попытка $attempts из $MAX_ATTEMPTS")

            if (attempts >= MAX_ATTEMPTS) {
                val newLockUntil = System.currentTimeMillis() + LOCK_DURATION_MS
                prefs.edit().putLong("lock_until", newLockUntil).putInt("failed_attempts", 0).apply()
                AuthAudit.logEvent(appContext, "LOCK", "Превышено число попыток ($MAX_ATTEMPTS). Блокировка на 5 минут.")
                return AuthResult.AuthLocked(300, "Превышено число попыток входа. Вход заблокирован на 5 минут.")
            } else {
                prefs.edit().putInt("failed_attempts", attempts).apply()
                return AuthResult.AuthFailed(attemptsLeft, "Неверный пароль, осталось попыток: $attemptsLeft")
            }
        }
    }

    fun getLockTimeRemaining(): Int {
        val lockUntil = prefs.getLong("lock_until", 0L)
        val now = System.currentTimeMillis()
        return if (now < lockUntil) ((lockUntil - now) / 1000).toInt() else 0
    }
}
