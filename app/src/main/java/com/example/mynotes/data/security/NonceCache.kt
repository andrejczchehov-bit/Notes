package com.example.mynotes.data.security

import java.util.concurrent.ConcurrentHashMap

object NonceCache {
    private val cache = ConcurrentHashMap<String, Long>()
    private const val EXPIRATION_TIME_MS = 60 * 1000L // 60 seconds

    fun isNonceValid(nonce: String): Boolean {
        cleanExpired()
        val now = System.currentTimeMillis()
        if (cache.containsKey(nonce)) {
            return false // Replay attack detected
        }
        cache[nonce] = now
        return true
    }

    private fun cleanExpired() {
        val now = System.currentTimeMillis()
        val iterator = cache.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (now - entry.value > EXPIRATION_TIME_MS) {
                iterator.remove()
            }
        }
    }
}
