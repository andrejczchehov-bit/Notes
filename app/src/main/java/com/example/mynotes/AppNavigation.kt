package com.example.mynotes

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.gson.Gson
import kotlinx.coroutines.launch

/**
 * Лабораторная работа №6, №7 и №10: Навигация в Jetpack Compose
 * Включает HomeScreen, SettingsScreen, CloudSyncScreen, PostsScreen, PostDetailScreen, TaskNotFoundScreen,
 * анимации переходов, передачу JSON-аргументов и Deep Link.
 */

// CompositionLocal для глобального управления настройками и темой через SettingsViewModel
val LocalSettingsViewModel = staticCompositionLocalOf<SettingsViewModel> {
    error("SettingsViewModel not provided")
}

// 1. Экран приветствия (Home)
@Composable
fun HomeScreen(
    onNavigateToList: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Добро пожаловать в Задачник!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Удобный инструмент для планирования ваших дел, управления задачами и контроля прогресса.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onNavigateToList,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Перейти к списку")
            }
        }
    }
}

// 2. Расширенный экран настроек (SettingsScreen) с управлением ThemeMode, языком новостей и forceDarkContent
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val settingsViewModel = LocalSettingsViewModel.current
    val coroutineScope = rememberCoroutineScope()

    val themeMode by settingsViewModel.themeMode.collectAsState()
    val forceDark by settingsViewModel.forceDarkContent.collectAsState()
    val newsLanguage by settingsViewModel.newsLanguage.collectAsState()
    val showCompleted by settingsViewModel.showCompletedTasks.collectAsState()
    val soundEnabled by settingsViewModel.soundEnabled.collectAsState()
    val vibrationEnabled by settingsViewModel.vibrationEnabled.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Внешний вид
            Text("Внешний вид", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Язык новостей:", fontWeight = FontWeight.Medium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterChip(
                            selected = newsLanguage == "Русский",
                            onClick = { settingsViewModel.setNewsLanguage("Русский") },
                            label = { Text("Русский") }
                        )
                        FilterChip(
                            selected = newsLanguage == "Английский",
                            onClick = { settingsViewModel.setNewsLanguage("Английский") },
                            label = { Text("Английский") }
                        )
                    }

                    HorizontalDivider()

                    Text("Режим темы:", fontWeight = FontWeight.Medium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterChip(
                            selected = themeMode == ThemeMode.SYSTEM,
                            onClick = { settingsViewModel.setThemeMode(ThemeMode.SYSTEM) },
                            label = { Text("Системная") }
                        )
                        FilterChip(
                            selected = themeMode == ThemeMode.LIGHT,
                            onClick = { settingsViewModel.setThemeMode(ThemeMode.LIGHT) },
                            label = { Text("Светлая") }
                        )
                        FilterChip(
                            selected = themeMode == ThemeMode.DARK,
                            onClick = { settingsViewModel.setThemeMode(ThemeMode.DARK) },
                            label = { Text("Тёмная") }
                        )
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Принудительно тёмная тема", fontWeight = FontWeight.Medium)
                        Switch(
                            checked = forceDark,
                            onCheckedChange = { settingsViewModel.setForceDarkContent(it) }
                        )
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Показывать выполненные задачи", fontWeight = FontWeight.Medium)
                        Switch(
                            checked = showCompleted,
                            onCheckedChange = { settingsViewModel.toggleShowCompleted(it) }
                        )
                    }
                }
            }

            // 2. Уведомления
            Text("Уведомления", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = null)
                            Text("Звук уведомлений", fontWeight = FontWeight.Medium)
                        }
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = { settingsViewModel.toggleSound(it) }
                        )
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null)
                            Text("Вибрация при напоминании", fontWeight = FontWeight.Medium)
                        }
                        Switch(
                            checked = vibrationEnabled,
                            onCheckedChange = { settingsViewModel.toggleVibration(it) }
                        )
                    }
                }
            }

            // 3. Данные и синхронизация
            Text("Данные и синхронизация", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { showClearDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Очистить выполненные задачи")
                    }

                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Задачи успешно экспортированы", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Экспорт задач")
                    }
                }
            }

            // 4. О приложении
            Text("О приложении", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Версия 1.0.0", fontWeight = FontWeight.Medium)
                }
            }
        }
    }

    // Диалог подтверждения очистки выполненных задач
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Удалить выполненные задачи?") },
            text = { Text("Все задачи, отмеченные как выполненные, будут навсегда удалены из базы данных.") },
            confirmButton = {
                Button(
                    onClick = {
                        showClearDialog = false
                        coroutineScope.launch {
                            val db = AppDatabase.getDatabase(context)
                            db.taskDao().deleteCompletedTasks()
                            Toast.makeText(context, "Выполненные задачи удалены", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

// 3. Экран ошибки «Задача не найдена»
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskNotFoundScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ошибка") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
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
                text = "Задача не найдена",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) {
                Text("Вернуться к списку")
            }
        }
    }
}

// 4. Граф навигации NavHost с анимациями переходов, JSON-аргументами и Deep Link
@Composable
fun AppNavGraph(
    viewModel: TaskViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()

    CompositionLocalProvider(LocalSettingsViewModel provides settingsViewModel) {
        NavHost(
            navController = navController,
            startDestination = "home",
            enterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { 300 }) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { 300 }) }
        ) {
            // Маршрут "home"
            composable("home") {
                HomeScreen(
                    onNavigateToList = {
                        navController.navigate("list")
                    }
                )
            }

            // Маршрут "list" (передает сложный аргумент - JSON задачи через Uri.encode)
            composable("list") {
                MainTaskAppScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { task ->
                        val json = Uri.encode(Gson().toJson(task))
                        navController.navigate("details/json_$json")
                    },
                    onAddNewTask = {
                        navController.navigate("details/json_-1")
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    },
                    onNavigateToSync = {
                        navController.navigate("sync")
                    },
                    onNavigateToPosts = {
                        navController.navigate("posts")
                    }
                )
            }

            // Маршрут "sync" — облачная синхронизация
            composable("sync") {
                CloudSyncScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Маршрут "posts" — посты из сети (Retrofit, Лаба 10)
            composable("posts") {
                val settingsViewModel = LocalSettingsViewModel.current
                val newsLanguage by settingsViewModel.newsLanguage.collectAsState()
                com.example.mynotes.data.PostsScreen(
                    newsLanguage = newsLanguage,
                    onBack = {
                        navController.popBackStack()
                    },
                    onPostClick = { postId ->
                        navController.navigate("post_detail/$postId?lang=$newsLanguage")
                    }
                )
            }

            // Маршрут "post_detail/{postId}?lang={lang}" — детальный экран поста (Лаба 10)
            composable(
                route = "post_detail/{postId}?lang={lang}",
                arguments = listOf(
                    navArgument("postId") { type = NavType.IntType },
                    navArgument("lang") { type = NavType.StringType; defaultValue = "Русский" }
                )
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getInt("postId") ?: 1
                val lang = backStackEntry.arguments?.getString("lang") ?: "Русский"
                com.example.mynotes.data.PostDetailScreen(
                    postId = postId,
                    newsLanguage = lang,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Маршрут "details/{taskArg}" с поддержкой Deep Link и JSON
            composable(
                route = "details/{taskArg}",
                arguments = listOf(navArgument("taskArg") { type = NavType.StringType }),
                deepLinks = listOf(navDeepLink { uriPattern = "app://mynotes/details/{taskArg}" })
            ) { backStackEntry ->
                val taskArg = backStackEntry.arguments?.getString("taskArg") ?: "-1"

                val validTaskId = remember(taskArg) {
                    if (taskArg.startsWith("json_")) {
                        val jsonStr = taskArg.removePrefix("json_")
                        if (jsonStr == "-1") {
                            -1L
                        } else {
                            try {
                                val decodedJson = Uri.decode(jsonStr)
                                val task = Gson().fromJson(decodedJson, Task::class.java)
                                task?.id ?: -999L
                            } catch (e: Exception) {
                                -999L
                            }
                        }
                    } else {
                        taskArg.toLongOrNull() ?: -999L
                    }
                }

                val context = LocalContext.current
                var resolvedTaskId by remember { mutableStateOf<Long?>(null) }

                LaunchedEffect(validTaskId) {
                    if (validTaskId == -1L || validTaskId == -999L) {
                        resolvedTaskId = validTaskId
                    } else {
                        val found = AppDatabase.getDatabase(context).taskDao().getTaskById(validTaskId)
                        resolvedTaskId = if (found != null) validTaskId else -999L
                    }
                }

                when (resolvedTaskId) {
                    null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    -999L -> {
                        TaskNotFoundScreen(onBack = { navController.popBackStack() })
                    }
                    else -> {
                        TaskEditScreen(
                            taskId = resolvedTaskId!!,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }

            // Настройки
            composable("settings") {
                SettingsScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
