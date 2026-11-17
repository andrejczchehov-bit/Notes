package com.example.mynotes.security

import android.content.Context
import android.os.Build
import android.os.Debug
import android.widget.Toast

object IntegrityChecks {
    fun isDebuggerAttached(): Boolean {
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger()
    }

    fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.contains("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.BRAND.contains("generic")
                || Build.DEVICE.contains("generic")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("google_sdk"))
    }

    fun performChecks(context: Context) {
        if (isDebuggerAttached()) {
            Toast.makeText(context, "Обнаружена отладка (Debugger connected)", Toast.LENGTH_LONG).show()
        }
        if (isEmulator()) {
            Toast.makeText(context, "Обнаружена работа на эмуляторе", Toast.LENGTH_LONG).show()
        }
    }
}
