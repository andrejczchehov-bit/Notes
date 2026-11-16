package com.example.mynotes.auth.ui

import android.content.Context
import androidx.compose.foundation.layout.*
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
fun PinEntryScreen(
    onPinCorrect: () -> Unit,
    onResetToLogin: () -> Unit
) {
    val context = LocalContext.current
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isRecoveryMode by remember { mutableStateOf(false) }
    var recoveryCodeInput by remember { mutableStateOf("") }

    val prefs = context.getSharedPreferences("app_auth_secure", Context.MODE_PRIVATE)
    val savedPinHash = prefs.getString("pin_hash", null)
    val savedRecoveryHash = prefs.getString("recovery_hash", null)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (isRecoveryMode) "Восстановление доступа" else "Введите PIN") })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isRecoveryMode) {
                    Text(
                        text = "Введите код восстановления",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = recoveryCodeInput,
                        onValueChange = { recoveryCodeInput = it },
                        label = { Text("Код восстановления") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (errorMessage != null) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Button(
                        onClick = {
                            val inputHash = PinHelper.hashPin(recoveryCodeInput.trim())
                            if (inputHash == savedRecoveryHash) {
                                prefs.edit().remove("pin_hash").remove("recovery_hash").apply()
                                onResetToLogin()
                            } else {
                                errorMessage = "Неверный код восстановления"
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Подтвердить код")
                    }

                    TextButton(onClick = { isRecoveryMode = false; errorMessage = null }) {
                        Text("Назад к вводу PIN")
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
                                text = "Введите 4-значный PIN",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            PinDotsIndicator(pinLength = enteredPin.length)

                            if (errorMessage != null) {
                                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            NumericKeypad(
                                onDigitClick = { digit ->
                                    errorMessage = null
                                    if (enteredPin.length < 4) {
                                        enteredPin += digit
                                        if (enteredPin.length == 4) {
                                            val inputHash = PinHelper.hashPin(enteredPin)
                                            if (inputHash == savedPinHash) {
                                                onPinCorrect()
                                            } else {
                                                errorMessage = "Неверный PIN"
                                                enteredPin = ""
                                            }
                                        }
                                    }
                                },
                                onDeleteClick = {
                                    errorMessage = null
                                    if (enteredPin.isNotEmpty()) {
                                        enteredPin = enteredPin.dropLast(1)
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            TextButton(onClick = { isRecoveryMode = true; errorMessage = null }) {
                                Text("Забыли PIN? Восстановить")
                            }
                        }
                    }
                }
            }
        }
    }
}
