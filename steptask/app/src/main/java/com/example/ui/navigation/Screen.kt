package com.example.ui.navigation

sealed class Screen {
    data object Home : Screen()
    data class TaskDetail(val taskId: Long) : Screen()
    data class StepFocus(val taskId: Long, val initialStepIndex: Int = 0) : Screen()
    data class AddEditTask(val taskId: Long? = null) : Screen()
    data object Settings : Screen()
}
