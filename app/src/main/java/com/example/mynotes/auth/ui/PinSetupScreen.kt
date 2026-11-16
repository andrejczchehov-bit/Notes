package com.example.mynotes.auth.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mynotes.auth.data.PinHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinSetupScreen(
    onPinCreated: () -> Unit
) {
    val context = LocalContext.current
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isConfirming by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var recoveryCodeToShow by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (isConfirming) "Подтвердите PIN-код" else "Создайте PIN-код") })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (recoveryCodeToShow != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                    Text(
                        text = "Сохраните этот код для восстановления!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = recoveryCodeToShow!!,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = "Этот код показывается только один раз. Запишите его в безопасном месте.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = onPinCreated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Я сохранил код, продолжить")
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxHeight().fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(
                            text = if (isConfirming) "Повторите 4-значный PIN" else "Введите 4-значный PIN",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        PinDotsIndicator(pinLength = if (isConfirming) confirmPin.length else pin.length)

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    NumericKeypad(
                        onDigitClick = { digit ->
                            errorMessage = null
                            if (!isConfirming) {
                                if (pin.length < 4) {
                                    pin += digit
                                    if (pin.length == 4) {
                                        isConfirming = true
                                    }
                                }
                            } else {
                                if (confirmPin.length < 4) {
                                    confirmPin += digit
                                    if (confirmPin.length == 4) {
                                        if (pin == confirmPin) {
                                            val hashed = PinHelper.hashPin(pin)
                                            val prefs = context.getSharedPreferences("app_auth_secure", Context.MODE_PRIVATE)
                                            val recoveryCode = PinHelper.generateRecoveryCode()
                                            val recoveryHash = PinHelper.hashPin(recoveryCode)

                                            prefs.edit()
                                                .putString("pin_hash", hashed)
                                                .putString("recovery_hash", recoveryHash)
                                                .apply()

                                            recoveryCodeToShow = recoveryCode
                                        } else {
                                            errorMessage = "PIN-коды не совпадают. Начните заново."
                                            pin = ""
                                            confirmPin = ""
                                            isConfirming = false
                                        }
                                    }
                                }
                            }
                        },
                        onDeleteClick = {
                            errorMessage = null
                            if (!isConfirming) {
                                if (pin.isNotEmpty()) pin = pin.dropLast(1)
                            } else {
                                if (confirmPin.isNotEmpty()) confirmPin = confirmPin.dropLast(1)
                            }
                        },
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
            }
        }
    }
}
