package com.example.mynotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.MaterialTheme

sealed class AppScreen {
    object Home : AppScreen()
    data class Edit(val taskId: Long) : AppScreen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: TaskViewModel = viewModel()
            var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }

            MaterialTheme {
                when (val screen = currentScreen) {
                    is AppScreen.Home -> {
                        MainTaskAppScreen(
                            viewModel = viewModel,
                            onNavigateToDetail = { taskId ->
                                currentScreen = AppScreen.Edit(taskId)
                            },
                            onAddNewTask = {
                                currentScreen = AppScreen.Edit(-1L)
                            }
                        )
                    }
                    is AppScreen.Edit -> {
                        TaskEditScreen(
                            taskId = screen.taskId,
                            viewModel = viewModel,
                            onBack = {
                                currentScreen = AppScreen.Home
                            }
                        )
                    }
                }
            }
        }
    }
}
