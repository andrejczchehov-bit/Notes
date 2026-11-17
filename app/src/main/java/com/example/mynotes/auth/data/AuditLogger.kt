package com.example.mynotes.auth.data

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object AuditLogger {
    private const val LOG_FILENAME = "audit.log"

    fun logScanEvent(context: Context, message: String) {
        try {
            val file = File(context.filesDir, LOG_FILENAME)
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val entry = "[$timestamp] $message\n"
            file.appendText(entry)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun readAuditLogs(context: Context): List<String> {
        return try {
            val file = File(context.filesDir, LOG_FILENAME)
            if (file.exists()) file.readLines().reversed() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
