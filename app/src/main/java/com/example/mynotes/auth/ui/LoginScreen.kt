package com.example.mynotes.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mynotes.auth.data.AuthRepository
import com.example.mynotes.auth.data.AuthResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateTo2FA: () -> Unit
) {
    val context = LocalContext.current
    val authRepo = remember { AuthRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var secondsRemaining by remember { mutableStateOf(authRepo.getLockTimeRemaining()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(secondsRemaining) {
        if (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining = authRepo.getLockTimeRemaining()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Авторизация") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Вход в систему",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = login,
                onValueChange = { login = it },
                label = { Text("Логин (admin или любой другой)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль (1234 для admin)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (secondsRemaining > 0) {
                Text(
                    text = "Блокировка входа: осталось ${secondsRemaining}с",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        when (authRepo.login(login, password)) {
                            is AuthResult.Success -> {
                                isLoading = false
                                onLoginSuccess()
                            }
                            is AuthResult.RequiresTwoFactor -> {
                                isLoading = false
                                onNavigateTo2FA()
                            }
                            is AuthResult.AuthFailed -> {
                                isLoading = false
                                errorMessage = "Неверный пароль"
                            }
                            is AuthResult.AuthLocked -> {
                                isLoading = false
                                errorMessage = "Вход заблокирован"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && secondsRemaining == 0
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Войти")
                }
            }
        }
    }
}
