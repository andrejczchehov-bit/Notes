package com.example.mynotes.auth.data

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object AuthAudit {
    private const val LOG_FILENAME = "auth_audit.log"

    fun logEvent(context: Context, event: String, details: String = "") {
        try {
            val file = File(context.filesDir, LOG_FILENAME)
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val logEntry = "[$timestamp] $event: $details\n"
            file.appendText(logEntry)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun readLogs(context: Context): String {
        return try {
            val file = File(context.filesDir, LOG_FILENAME)
            if (file.exists()) file.readText() else "Логи пусты"
        } catch (e: Exception) {
            "Ошибка чтения логов: ${e.message}"
        }
    }
}
