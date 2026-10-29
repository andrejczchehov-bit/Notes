package com.example.mynotes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Перечисление экранов лабораторных работ для навигации в боковом меню (ModalNavigationDrawer).
 */
sealed class LabScreen(val title: String) {
    object TaskHome : LabScreen("Лаба 2: TaskHomeScreen")
    object BasicsDemo : LabScreen("Лаба 2: BasicsComposeDemo")
    object Profile : LabScreen("Лаба 3: ProfileScreen")
    object TwoColumn : LabScreen("Лаба 3: TwoColumnScreen")
    object Counter : LabScreen("Лаба 4: TaskCounterScreen")
    object Search : LabScreen("Лаба 4: TaskSearchScreen")
    object Expandable : LabScreen("Лаба 4: ExpandableTasksScreen")
    object QuickInput : LabScreen("Лаба 4: QuickTaskInputScreen")
    object PaginatedList : LabScreen("Лаба 5: PaginatedTaskListScreen")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabsContainerScreen(
    onBackToTasks: () -> Unit
) {
    var currentScreen by remember { mutableStateOf<LabScreen>(LabScreen.TaskHome) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val sampleTasks = remember {
        listOf(
            Task(id = 1, title = "Изучить Jetpack Compose", description = "Лабораторные работы", dueDate = System.currentTimeMillis(), priority = 3, reminderTime = System.currentTimeMillis()),
            Task(id = 2, title = "Купить продукты", description = "Молоко, хлеб", dueDate = System.currentTimeMillis(), priority = 2, reminderTime = 0L),
            Task(id = 3, title = "Сделать ДЗ", description = "По Compose", dueDate = System.currentTimeMillis(), priority = 1, reminderTime = 0L)
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Лабораторные работы",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()

                // Лаба 2
                NavigationDrawerItem(
                    label = { Text("Лаба 2: TaskHomeScreen") },
                    selected = currentScreen == LabScreen.TaskHome,
                    onClick = {
                        currentScreen = LabScreen.TaskHome
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Лаба 2: BasicsDemo") },
                    selected = currentScreen == LabScreen.BasicsDemo,
                    onClick = {
                        currentScreen = LabScreen.BasicsDemo
                        scope.launch { drawerState.close() }
                    }
                )
                // Лаба 3
                NavigationDrawerItem(
                    label = { Text("Лаба 3: ProfileScreen") },
                    selected = currentScreen == LabScreen.Profile,
                    onClick = {
                        currentScreen = LabScreen.Profile
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Лаба 3: TwoColumnScreen") },
                    selected = currentScreen == LabScreen.TwoColumn,
                    onClick = {
                        currentScreen = LabScreen.TwoColumn
                        scope.launch { drawerState.close() }
                    }
                )
                // Лаба 4
                NavigationDrawerItem(
                    label = { Text("Лаба 4: TaskCounterScreen") },
                    selected = currentScreen == LabScreen.Counter,
                    onClick = {
                        currentScreen = LabScreen.Counter
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Лаба 4: TaskSearchScreen") },
                    selected = currentScreen == LabScreen.Search,
                    onClick = {
                        currentScreen = LabScreen.Search
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Лаба 4: ExpandableTasks") },
                    selected = currentScreen == LabScreen.Expandable,
                    onClick = {
                        currentScreen = LabScreen.Expandable
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Лаба 4: QuickTaskInput") },
                    selected = currentScreen == LabScreen.QuickInput,
                    onClick = {
                        currentScreen = LabScreen.QuickInput
                        scope.launch { drawerState.close() }
                    }
                )
                // Лаба 5
                NavigationDrawerItem(
                    label = { Text("Лаба 5: PaginatedList") },
                    selected = currentScreen == LabScreen.PaginatedList,
                    onClick = {
                        currentScreen = LabScreen.PaginatedList
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentScreen.title) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Меню лаб")
                        }
                    },
                    actions = {
                        IconButton(onClick = onBackToTasks) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад к задачам")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    is LabScreen.TaskHome -> TaskHomeScreen(onAddClick = {})
                    is LabScreen.BasicsDemo -> BasicsComposeDemoScreen()
                    is LabScreen.Profile -> ProfileScreen()
                    is LabScreen.TwoColumn -> TwoColumnScreen(tasks = sampleTasks, onCompleteTask = {}, onAddTask = {})
                    is LabScreen.Counter -> TaskCounterScreen()
                    is LabScreen.Search -> TaskSearchScreen(tasks = sampleTasks)
                    is LabScreen.Expandable -> ExpandableTasksScreen()
                    is LabScreen.QuickInput -> QuickTaskInputScreen()
                    is LabScreen.PaginatedList -> PaginatedTaskListScreen()
                }
            }
        }
    }
}
