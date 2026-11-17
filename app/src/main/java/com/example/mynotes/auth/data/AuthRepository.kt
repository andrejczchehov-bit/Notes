package com.example.mynotes.auth.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.delay

sealed interface AuthResult {
    data class Success(val accessToken: String, val refreshToken: String) : AuthResult
    data class RequiresTwoFactor(val login: String) : AuthResult
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
        delay(600)

        val lockUntil = prefs.getLong("lock_until", 0L)
        val now = System.currentTimeMillis()
        if (now < lockUntil) {
            val secondsRemaining = ((lockUntil - now) / 1000).toInt()
            AuthAudit.logEvent(appContext, "LOCK", "Попытка входа во время блокировки. Осталось ${secondsRemaining}с")
            return AuthResult.AuthLocked(secondsRemaining, "Вход заблокирован. Повторите попытку через $secondsRemaining сек.")
        }

        val trimmedLogin = login.trim().lowercase()

        if (trimmedLogin == "admin" && pass == "1234") {
            prefs.edit().putInt("failed_attempts", 0).remove("lock_until").apply()
            
            val contextObj = AccessContext(
                userId = "admin",
                role = Role.ADMIN,
                scopes = setOf("pass:view", "scan:execute", "audit:read")
            )
            AuthManager.saveAccessContext(appContext, contextObj)

            AuthAudit.logEvent(appContext, "LOGIN_OK", "Успешный вход администратора: admin")
            return AuthResult.Success("access_token_mock_123", "refresh_token_mock_789")
        } else {
            val code = (100000..999999).random().toString()
            prefs.edit().putString("pending_2fa_code", code).putString("pending_2fa_login", login).apply()
            NotificationHelper.sendTwoFactorCodeNotification(appContext, code)
            AuthAudit.logEvent(appContext, "2FA_SENT", "Отправлен код подтверждения для $login")
            return AuthResult.RequiresTwoFactor(login)
        }
    }

    fun verifyTwoFactorCode(inputCode: String): AuthResult {
        val savedCode = prefs.getString("pending_2fa_code", null)
        val savedLogin = prefs.getString("pending_2fa_login", "Пользователь") ?: "Пользователь"
        
        if (savedCode != null && savedCode == inputCode.trim()) {
            prefs.edit().remove("pending_2fa_code").remove("pending_2fa_login").putInt("failed_attempts", 0).remove("lock_until").apply()
            
            val trimmed = savedLogin.trim().lowercase()
            val role = if (trimmed == "guard") Role.SECURITY else Role.EMPLOYEE
            val scopes = if (trimmed == "guard") {
                setOf("pass:view", "scan:execute")
            } else {
                setOf("pass:view")
            }

            val contextObj = AccessContext(
                userId = savedLogin,
                role = role,
                scopes = scopes
            )
            AuthManager.saveAccessContext(appContext, contextObj)

            AuthAudit.logEvent(appContext, "LOGIN_OK", "Успешный вход через 2FA для $savedLogin (Роль: $role)")
            return AuthResult.Success("access_token_mock_123", "refresh_token_mock_789")
        } else {
            AuthAudit.logEvent(appContext, "LOGIN_FAIL", "Неверный 2FA код")
            return AuthResult.AuthFailed(3, "Неверный код подтверждения")
        }
    }

    fun getLockTimeRemaining(): Int {
        val lockUntil = prefs.getLong("lock_until", 0L)
        val now = System.currentTimeMillis()
        return if (now < lockUntil) ((lockUntil - now) / 1000).toInt() else 0
    }
}
