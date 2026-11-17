package com.example.mynotes

import android.net.Uri
import android.widget.Toast
import android.content.Context
import com.example.mynotes.auth.ui.*
import com.example.mynotes.auth.data.AuthManager
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavDestination.Companion.hierarchy

/**
 * Лабораторная работа №6, №7, №10 и №11: Навигация в Jetpack Compose
 * Включает HomeScreen, SettingsScreen, CloudSyncScreen, PostsScreen, PostDetailScreen, NotesScreen, NoteEditScreen, TaskNotFoundScreen,
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

// 2. Расширенный экран настроек (SettingsScreen) с управлением ThemeMode, источником новостей и forceDarkContent
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: TaskViewModel,
    onBack: () -> Unit,
    onNavigateToSync: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val context = LocalContext.current
    val settingsViewModel = LocalSettingsViewModel.current
    val coroutineScope = rememberCoroutineScope()

    val themeMode by settingsViewModel.themeMode.collectAsState()
    val forceDark by settingsViewModel.forceDarkContent.collectAsState()
    val newsSource by settingsViewModel.newsSource.collectAsState()
    val showCompleted by settingsViewModel.showCompletedTasks.collectAsState()
    val soundEnabled by settingsViewModel.soundEnabled.collectAsState()
    val vibrationEnabled by settingsViewModel.vibrationEnabled.collectAsState()

    val currentProfile by viewModel.currentProfile.observeAsState("Личный")
    val profiles = listOf("Личный", "Рабочий", "Учеба")

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
                    Text("Источник новостей:", fontWeight = FontWeight.Medium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterChip(
                            selected = newsSource == "Космос",
                            onClick = { settingsViewModel.setNewsSource("Космос") },
                            label = { Text("Космос") }
                        )
                        FilterChip(
                            selected = newsSource == "Россия",
                            onClick = { settingsViewModel.setNewsSource("Россия") },
                            label = { Text("Россия") }
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

            // 3. Профиль
            Text("Профиль", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Column {
                            Text("Активный профиль", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(currentProfile, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider()

                    Text("Выбрать профиль:", fontWeight = FontWeight.Medium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        profiles.forEach { profile ->
                            FilterChip(
                                selected = currentProfile == profile,
                                onClick = { viewModel.setProfile(profile) },
                                label = { Text(profile) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onOpenProfile,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Открыть полный профиль")
                    }
                }
            }

            // 4. Данные и синхронизация
            Text("Данные и синхронизация", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onNavigateToSync,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Cloud, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Синхронизировать данные")
                    }

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
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_auth_secure", Context.MODE_PRIVATE) }
    val startDestination = "login"

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    CompositionLocalProvider(LocalSettingsViewModel provides settingsViewModel) {
        Scaffold(
            bottomBar = {
                // Показываем нижнюю панель только на 4 основных вкладках
                if (currentRoute in listOf("list", "notes", "settings", "posts")) {
                    NavigationBar {
                        NavigationBarItem(
                            selected = currentRoute == "list",
                            onClick = { navController.navigate("list") { popUpTo("home") { saveState = true }; launchSingleTop = true; restoreState = true } },
                            icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Задачи") },
                            label = { Text("Задачи") }
                        )
                        NavigationBarItem(
                            selected = currentRoute == "notes",
                            onClick = { navController.navigate("notes") { popUpTo("home") { saveState = true }; launchSingleTop = true; restoreState = true } },
                            icon = { Icon(Icons.Default.Description, contentDescription = "Заметки") },
                            label = { Text("Заметки") }
                        )
                        NavigationBarItem(
                            selected = currentRoute == "posts",
                            onClick = { navController.navigate("posts") { popUpTo("home") { saveState = true }; launchSingleTop = true; restoreState = true } },
                            icon = { Icon(Icons.Default.Language, contentDescription = "Новости") },
                            label = { Text("Новости") }
                        )
                        NavigationBarItem(
                            selected = currentRoute == "settings",
                            onClick = { navController.navigate("settings") { popUpTo("home") { saveState = true }; launchSingleTop = true; restoreState = true } },
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Настройки") },
                            label = { Text("Настройки") }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding),
                enterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(initialOffsetX = { 300 }) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(targetOffsetX = { 300 }) }
            ) {
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = {
                            val hasPin = !prefs.getString("pin_hash", null).isNullOrEmpty()
                            val target = if (hasPin) "pin_entry" else "pin_setup"
                            navController.navigate(target) {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onNavigateTo2FA = {
                            navController.navigate("verify_2fa")
                        }
                    )
                }
                composable("verify_2fa") {
                    CodeVerificationScreen(
                        onVerificationSuccess = {
                            val hasPin = !prefs.getString("pin_hash", null).isNullOrEmpty()
                            val target = if (hasPin) "pin_entry" else "pin_setup"
                            navController.navigate(target) {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onBackToLogin = {
                            navController.popBackStack()
                        }
                    )
                }
                composable("pin_setup") {
                    PinSetupScreen(onPinCreated = {
                        navController.navigate("list") {
                            popUpTo("pin_setup") { inclusive = true }
                        }
                    })
                }
                composable("pin_entry") {
                    PinEntryScreen(
                        onPinCorrect = {
                            navController.navigate("list") {
                                popUpTo("pin_entry") { inclusive = true }
                            }
                        },
                        onResetToLogin = {
                            navController.navigate("login") {
                                popUpTo("pin_entry") { inclusive = true }
                            }
                        }
                    )
                }
                composable("pass") {
                    PassScreen(onBack = { navController.popBackStack() })
                }
                composable("scan") {
                    ScanScreen()
                }
                composable("admin") {
                    AdminScreen()
                }
                composable("home") {
                    HomeScreen(onNavigateToList = { navController.navigate("list") })
                }
                composable("list") {
                    MainTaskAppScreen(
                        viewModel = viewModel,
                        onNavigateToDetail = { task ->
                            val json = Uri.encode(Gson().toJson(task))
                            navController.navigate("details/json_$json")
                        },
                        onAddNewTask = { navController.navigate("details/json_-1") },
                        onNavigateToSync = { navController.navigate("sync") }
                    )
                }
                composable("sync") {
                    CloudSyncScreen(onBack = { navController.popBackStack() })
                }
                composable("notes") {
                    com.example.mynotes.data.NotesScreen(
                        onBack = { navController.popBackStack() },
                        onNoteClick = { noteId -> navController.navigate("note_edit/$noteId") },
                        onAddNote = { navController.navigate("note_edit/-1") }
                    )
                }
                composable(
                    route = "note_edit/{noteId}",
                    arguments = listOf(navArgument("noteId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
                    com.example.mynotes.data.NoteEditScreen(noteId = noteId, onBack = { navController.popBackStack() })
                }
                composable("posts") {
                    val settingsViewModel = LocalSettingsViewModel.current
                    val newsSource by settingsViewModel.newsSource.collectAsState()
                    com.example.mynotes.data.PostsScreen(
                        newsSource = newsSource,
                        onBack = { navController.popBackStack() },
                        onPostClick = { postId -> navController.navigate("post_detail/$postId?source=$newsSource") }
                    )
                }
                composable(
                    route = "post_detail/{postId}?source={source}",
                    arguments = listOf(
                        navArgument("postId") { type = NavType.IntType },
                        navArgument("source") { type = NavType.StringType; defaultValue = "Космос" }
                    )
                ) { backStackEntry ->
                    val postId = backStackEntry.arguments?.getInt("postId") ?: 1
                    val source = backStackEntry.arguments?.getString("source") ?: "Космос"
                    com.example.mynotes.data.PostDetailScreen(postId = postId, newsSource = source, onBack = { navController.popBackStack() })
                }
                composable(
                    route = "details/{taskArg}",
                    arguments = listOf(navArgument("taskArg") { type = NavType.StringType }),
                    deepLinks = listOf(navDeepLink { uriPattern = "app://mynotes/details/{taskArg}" })
                ) { backStackEntry ->
                    val taskArg = backStackEntry.arguments?.getString("taskArg") ?: "-1"
                    val validTaskId = remember(taskArg) {
                        if (taskArg.startsWith("json_")) {
                            val jsonStr = taskArg.removePrefix("json_")
                            if (jsonStr == "-1") -1L else {
                                try {
                                    val decodedJson = Uri.decode(jsonStr)
                                    val task = Gson().fromJson(decodedJson, Task::class.java)
                                    task?.id ?: -999L
                                } catch (e: Exception) { -999L }
                            }
                        } else { taskArg.toLongOrNull() ?: -999L }
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
                        null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                        -999L -> TaskNotFoundScreen(onBack = { navController.popBackStack() })
                        else -> TaskEditScreen(taskId = resolvedTaskId!!, viewModel = viewModel, onBack = { navController.popBackStack() })
                    }
                }
                composable("settings") {
                    SettingsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onNavigateToSync = { navController.navigate("sync") },
                        onOpenProfile = { navController.navigate("profile") }
                    )
                }
                composable("profile") {
                    ProfileScreen(
                        onOpenPass = { navController.navigate("pass") },
                        onOpenScan = { navController.navigate("scan") }
                    )
                }
            }
        }
    }
}
