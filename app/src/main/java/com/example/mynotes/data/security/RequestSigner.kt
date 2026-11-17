package com.example.mynotes.data.security

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object RequestSigner {
    private const val SECRET_KEY = "my_secret_key_123"
    private const val HMAC_ALGO = "HmacSHA256"

    fun sign(method: String, path: String, body: ByteArray, timestamp: Long, nonce: String): String {
        val bodyHash = body.sha256Hex()
        val canonicalString = "$method\n$path\n$bodyHash\n$timestamp\n$nonce"

        val secretKeySpec = SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), HMAC_ALGO)
        val mac = Mac.getInstance(HMAC_ALGO).apply {
            init(secretKeySpec)
        }
        val hmacBytes = mac.doFinal(canonicalString.toByteArray(Charsets.UTF_8))
        return hmacBytes.joinToString("") { "%02x".format(it) }
    }
}

fun ByteArray.sha256Hex(): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(this)
    return digest.joinToString("") { "%02x".format(it) }
}
