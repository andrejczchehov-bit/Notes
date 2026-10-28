package com.example.mynotes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Лабораторная работа №3: Компоновка и выравнивание
 * Экран профиля пользователя (ProfileScreen) с адаптивной версткой через BoxWithConstraints.
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    userName: String = "Александр Смирнов",
    userStatus: String = "Android-разработчик • Студент",
    completedCount: Int = 12,
    inProgressCount: Int = 5
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Обертка BoxWithConstraints для получения ограничений по ширине (maxWidth)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // maxWidth доступен напрямую как свойство BoxWithConstraintsScope
            val isTablet = maxWidth > 600.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1, 2, 3. Аватар, Имя и Статус (адаптивная верстка: Row для планшета, Column для телефона)
                if (isTablet) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AvatarView()
                        Spacer(modifier = Modifier.width(24.dp))
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = userStatus,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AvatarView()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = userStatus,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Два счётчика в Row, разделены Spacer с weight (как подписчики / подписки)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatCounter(title = "Выполнено", count = completedCount)
                    
                    // Spacer с weight распределяет оставшееся пространство равномерно между счетчиками
                    Spacer(modifier = Modifier.weight(1f))
                    
                    StatCounter(title = "В работе", count = inProgressCount)
                }

                Spacer(modifier = Modifier.weight(1f))

                // 5, 6. Кнопки «Редактировать профиль» и «Настройки» на всю ширину с отступами
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp) // отступ 8 dp между кнопками
                ) {
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Редактировать профиль")
                    }

                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Настройки")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 7. Нижняя панель из трёх иконок через Arrangement.SpaceEvenly
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = "Главная")
                    }
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Задачи")
                    }
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Настройки")
                    }
                }
            }
        }
    }
}

@Composable
fun AvatarView() {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Аватар",
            modifier = Modifier.size(36.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
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

// --- Превью для телефона (widthDp = 360, меньше 600 dp -> вертикальная верстка) ---
@Preview(name = "Phone Profile", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ProfileScreenPhonePreview() {
    MaterialTheme {
        ProfileScreen()
    }
}

// --- Превью для планшета (widthDp = 720, больше 600 dp -> горизонтальная верстка) ---
@Preview(name = "Tablet Profile", showBackground = true, widthDp = 720, heightDp = 900)
@Composable
fun ProfileScreenTabletPreview() {
    MaterialTheme {
        ProfileScreen()
    }
}
