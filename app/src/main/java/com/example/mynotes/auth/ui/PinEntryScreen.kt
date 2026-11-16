package com.example.mynotes.auth.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
                    Text(
                        text = "Введите 4-значный PIN",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) enteredPin = it },
                        label = { Text("PIN") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (errorMessage != null) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Button(
                        onClick = {
                            val inputHash = PinHelper.hashPin(enteredPin)
                            if (inputHash == savedPinHash) {
                                onPinCorrect()
                            } else {
                                errorMessage = "Неверный PIN"
                                enteredPin = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enteredPin.length == 4
                    ) {
                        Text("Войти")
                    }

                    TextButton(onClick = { isRecoveryMode = true; errorMessage = null }) {
                        Text("Забыли PIN? Восстановить")
                    }
                }
            }
        }
    }
}
