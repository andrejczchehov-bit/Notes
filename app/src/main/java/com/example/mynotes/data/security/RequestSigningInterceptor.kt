package com.example.mynotes.data.security

import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.util.UUID

class RequestSigningInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val method = originalRequest.method
        val path = originalRequest.url.encodedPath

        val bodyBytes = originalRequest.body?.let { requestBody ->
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            buffer.readByteArray()
        } ?: ByteArray(0)

        val timestamp = System.currentTimeMillis()
        val nonce = UUID.randomUUID().toString()

        val signature = RequestSigner.sign(method, path, bodyBytes, timestamp, nonce)

        val signedRequest = originalRequest.newBuilder()
            .header("X-Timestamp", timestamp.toString())
            .header("X-Nonce", nonce)
            .header("X-Signature", signature)
            .build()

        return chain.proceed(signedRequest)
    }
}
