package com.example.mynotes.data.realtime

import android.util.Log
import com.example.mynotes.data.security.RequestSigner
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.*
import java.util.UUID
import kotlin.math.min

class LiveSocketManager(
    private val url: String = "wss://echo.websocket.org"
) {
    private val client = OkHttpClient.Builder().build()
    private var webSocket: WebSocket? = null

    private val _connectionState = MutableStateFlow<SocketState>(SocketState.Disconnected)
    val connectionState: StateFlow<SocketState> = _connectionState.asStateFlow()

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> = _messages.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var reconnectAttempt = 0
    private var isManuallyClosed = false

    fun connect() {
        isManuallyClosed = false
        openSocket()
    }

    private fun openSocket() {
        if (_connectionState.value is SocketState.Connected) return
        _connectionState.value = SocketState.Connecting

        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("LiveSocketManager", "WebSocket Connected")
                _connectionState.value = SocketState.Connected
                reconnectAttempt = 0
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("LiveSocketManager", "Received: $text")
                _messages.value = _messages.value + text
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("LiveSocketManager", "WebSocket Closed: $reason")
                _connectionState.value = SocketState.Disconnected
                if (!isManuallyClosed) scheduleReconnect()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("LiveSocketManager", "WebSocket Failure: ${t.message}")
                _connectionState.value = SocketState.Error(t.localizedMessage ?: "Unknown error")
                if (!isManuallyClosed) scheduleReconnect()
            }
        })
    }

    private fun scheduleReconnect() {
        scope.launch {
            val delaySec = min(1L shl reconnectAttempt, 30L) // 1s, 2s, 4s, ..., max 30s
            reconnectAttempt++
            Log.d("LiveSocketManager", "Reconnecting in ${delaySec}s (attempt $reconnectAttempt)...")
            delay(delaySec * 1000L)
            if (!isManuallyClosed) {
                openSocket()
            }
        }
    }

    fun sendMessage(text: String) {
        val timestamp = System.currentTimeMillis()
        val nonce = UUID.randomUUID().toString()
        val bodyBytes = text.toByteArray(Charsets.UTF_8)
        val signature = RequestSigner.sign("WS", "live", bodyBytes, timestamp, nonce)

        val payload = "{\"message\":\"$text\",\"timestamp\":$timestamp,\"nonce\":\"$nonce\",\"signature\":\"$signature\"}"
        webSocket?.send(payload)
        _messages.value = _messages.value + "Sent: $text"
    }

    fun disconnect() {
        isManuallyClosed = true
        webSocket?.close(1000, "Client disconnected")
        webSocket = null
        _connectionState.value = SocketState.Disconnected
        scope.cancel()
    }
}

sealed interface SocketState {
    object Disconnected : SocketState
    object Connecting : SocketState
    object Connected : SocketState
    data class Error(val message: String) : SocketState
}
