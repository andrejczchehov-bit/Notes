package com.example.mynotes.auth.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mynotes.auth.data.AuditLogger
import com.example.mynotes.auth.data.AuthManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen() {
    val context = LocalContext.current
    val isSecurity = remember { AuthManager.isSecurity(context) || AuthManager.hasScope(context, "scan:execute") }
    val currentGuard = remember { AuthManager.getCurrentUserLogin(context) }

    var badgeIdInput by remember { mutableStateOf("") }
    var scanResultMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Сканирование пропусков") })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (!isSecurity) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "У вас нет доступа",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Этот раздел доступен только службе безопасности (Security).",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Сканер пропусков",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = badgeIdInput,
                        onValueChange = { badgeIdInput = it },
                        label = { Text("ID сотрудника / пропуска") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (badgeIdInput.isNotBlank()) {
                                val message = "Сотрудник $badgeIdInput отсканирован пользователем $currentGuard. Результат: Разрешено"
                                AuditLogger.logScanEvent(context, message)
                                scanResultMessage = message
                                Toast.makeText(context, "Пропуск разрешен", Toast.LENGTH_SHORT).show()
                                badgeIdInput = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = badgeIdInput.isNotBlank()
                    ) {
                        Text("Имитировать сканирование")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (scanResultMessage != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(
                                text = scanResultMessage!!,
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
