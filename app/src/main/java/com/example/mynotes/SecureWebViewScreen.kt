package com.example.mynotes

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun SecureWebViewScreen() {
    AndroidView(factory = { context ->
        WebView(context).apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = false // Hardening: disable JS if not needed
            loadUrl("https://example.com") // Secure HTTPS
        }
    })
}
