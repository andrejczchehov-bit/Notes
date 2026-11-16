package com.example.mynotes.auth.data

import java.security.MessageDigest

object PinHelper {
    fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun generateRecoveryCode(): String {
        // 8 characters, excluding ambiguous letters (0, O, I, l, 1)
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..8)
            .map { chars.random() }
            .joinToString("")
    }
}
