package com.example.mynotes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mynotes.auth.data.AuthManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Обновленный экран профиля (ProfileScreen) по требованиям Лабораторной работы №8 & №14.
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    completedCount: Int = 12,
    inProgressCount: Int = 5,
    onOpenPass: () -> Unit = {},
    onOpenScan: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentLogin = remember { AuthManager.getCurrentUserLogin(context).ifBlank { "Гость" } }
    val isAdmin = remember { AuthManager.isAdmin(context) }
    val isSecurity = remember { AuthManager.isSecurity(context) || AuthManager.hasScope(context, "scan:execute") }

    val userRoleText = when {
        isAdmin -> "Администратор"
        isSecurity -> "Служба безопасности"
        else -> "Пользователь"
    }

    var isLoadingSync by remember { mutableStateOf(false) }
    var showSavedMessage by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    AppTheme {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 2. Заголовок «Мой профиль» (headlineMedium)
                Text(
                    text = "Мой профиль",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // 3. Использование ProfileCard (иконка Person, динамическое имя и роль без иконки редактирования)
                ProfileCard(
                    title = currentLogin,
                    subtitle = userRoleText,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingContent = null,
                    onClick = null
                )

                // 4. Блок «Статистика» (2-3 строки текста, стиль bodyLarge)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Статистика активности",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "• Выполнено задач: $completedCount",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "• Задач в процессе: $inProgressCount",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 7. Сообщение «Изменения сохранены» с LiveRegionMode.Assertive (появляется на 3 секунды)
                if (showSavedMessage) {
                    Text(
                        text = "Изменения сохранены",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Доступ к пропуску и сканеру (Лаба 14)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppButton(
                        text = "Открыть пропуск",
                        onClick = onOpenPass,
                        style = ButtonStyle.PRIMARY,
                        size = ButtonSize.MEDIUM,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isSecurity) {
                        AppButton(
                            text = "Открыть сканер",
                            onClick = onOpenScan,
                            style = ButtonStyle.SECONDARY,
                            size = ButtonSize.MEDIUM,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // 5. Группа кнопок с использованием AppButton
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Большая PRIMARY «Сохранить изменения»
                    AppButton(
                        text = "Сохранить изменения",
                        onClick = {
                            showSavedMessage = true
                            coroutineScope.launch {
                                delay(3000)
                                showSavedMessage = false
                            }
                        },
                        style = ButtonStyle.PRIMARY,
                        size = ButtonSize.LARGE,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Средняя SECONDARY «Отмена»
                    AppButton(
                        text = "Отмена",
                        onClick = {},
                        style = ButtonStyle.SECONDARY,
                        size = ButtonSize.MEDIUM,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Маленькая OUTLINED «Сбросить»
                    AppButton(
                        text = "Сбросить",
                        onClick = {},
                        style = ButtonStyle.OUTLINED,
                        size = ButtonSize.SMALL,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 6. Компонент LoadingButton («Синхронизировать»)
                LoadingButton(
                    text = "Синхронизировать",
                    onClick = {
                        isLoadingSync = true
                        coroutineScope.launch {
                            delay(2000)
                            isLoadingSync = false
                        }
                    },
                    isLoading = isLoadingSync,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun StatCounter(title: String, count: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// 8. Превью для светлой и темной темы
@Preview(name = "Light Theme", showBackground = true)
@Composable
fun ProfileScreenLightProfilePreview() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        ProfileScreen()
    }
}

@Preview(name = "Dark Theme", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenDarkProfilePreview() {
    AppTheme(themeMode = ThemeMode.DARK) {
        ProfileScreen()
    }
}
