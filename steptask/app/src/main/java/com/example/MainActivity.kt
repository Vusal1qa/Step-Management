package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.LocalAppStrings
import com.example.data.local.SettingsManager
import com.example.data.local.getAppStrings
import com.example.ui.navigation.Screen
import com.example.ui.screens.AddEditTaskScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StepFocusScreen
import com.example.ui.screens.TaskDetailScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TaskViewModel by viewModels()
    private val settingsManager: SettingsManager by lazy { SettingsManager.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by settingsManager.themeMode.collectAsStateWithLifecycle()
            val appLanguage by settingsManager.appLanguage.collectAsStateWithLifecycle()
            val languageCode = settingsManager.getCurrentLanguageCode()
            val appStrings = remember(appLanguage, languageCode) { getAppStrings(languageCode) }

            CompositionLocalProvider(LocalAppStrings provides appStrings) {
                MyApplicationTheme(themeMode = themeMode) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        StepTaskApp(
                            viewModel = viewModel,
                            settingsManager = settingsManager
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StepTaskApp(
    viewModel: TaskViewModel,
    settingsManager: SettingsManager
) {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Home) }
    val currentScreen = backStack.lastOrNull() ?: Screen.Home

    BackHandler(enabled = backStack.size > 1) {
        backStack.removeAt(backStack.size - 1)
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            (fadeIn() + slideInHorizontally { width -> width / 5 })
                .togetherWith(fadeOut() + slideOutHorizontally { width -> -width / 5 })
        },
        label = "screenTransition"
    ) { screen ->
        when (screen) {
            is Screen.Home -> {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { taskId ->
                        backStack.add(Screen.TaskDetail(taskId))
                    },
                    onNavigateToFocusMode = { taskId ->
                        backStack.add(Screen.StepFocus(taskId = taskId, initialStepIndex = 0))
                    },
                    onNavigateToCreate = {
                        backStack.add(Screen.AddEditTask(taskId = null))
                    },
                    onNavigateToEdit = { taskId ->
                        backStack.add(Screen.AddEditTask(taskId = taskId))
                    },
                    onNavigateToSettings = {
                        backStack.add(Screen.Settings)
                    }
                )
            }

            is Screen.TaskDetail -> {
                TaskDetailScreen(
                    taskId = screen.taskId,
                    viewModel = viewModel,
                    onNavigateBack = {
                        if (backStack.size > 1) backStack.removeAt(backStack.size - 1)
                    },
                    onNavigateToEdit = { taskId ->
                        backStack.add(Screen.AddEditTask(taskId = taskId))
                    },
                    onNavigateToFocusMode = { taskId, stepIndex ->
                        backStack.add(Screen.StepFocus(taskId = taskId, initialStepIndex = stepIndex))
                    }
                )
            }

            is Screen.StepFocus -> {
                StepFocusScreen(
                    taskId = screen.taskId,
                    initialStepIndex = screen.initialStepIndex,
                    viewModel = viewModel,
                    onNavigateBack = {
                        if (backStack.size > 1) backStack.removeAt(backStack.size - 1)
                    }
                )
            }

            is Screen.AddEditTask -> {
                AddEditTaskScreen(
                    taskId = screen.taskId,
                    viewModel = viewModel,
                    onNavigateBack = {
                        if (backStack.size > 1) backStack.removeAt(backStack.size - 1)
                    },
                    onTaskSaved = { savedId ->
                        if (backStack.size > 1) backStack.removeAt(backStack.size - 1)
                        if (screen.taskId == null) {
                            backStack.add(Screen.TaskDetail(savedId))
                        }
                    }
                )
            }

            is Screen.Settings -> {
                SettingsScreen(
                    viewModel = viewModel,
                    settingsManager = settingsManager,
                    onNavigateBack = {
                        if (backStack.size > 1) backStack.removeAt(backStack.size - 1)
                    }
                )
            }
        }
    }
}
