package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.export.BackupPreview
import com.example.data.export.ImportResult
import com.example.data.export.JsonBackupManager
import com.example.data.local.AppDatabase
import com.example.data.local.CategoryManager
import com.example.data.local.TaskStepEntity
import com.example.data.local.TaskWithSteps
import com.example.data.model.TaskTemplate
import com.example.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter {
    ALL,
    ACTIVE,
    COMPLETED
}

enum class TaskSort {
    NEWEST,
    PROGRESS
}

data class TaskStats(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val activeTasks: Int = 0,
    val totalSteps: Int = 0,
    val completedSteps: Int = 0,
    val overallPercentage: Int = 0
)

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    val categoryManager: CategoryManager = CategoryManager.getInstance(application)
    val jsonBackupManager: JsonBackupManager

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedFilter = MutableStateFlow(TaskFilter.ALL)
    val selectedFilter: StateFlow<TaskFilter> = _selectedFilter.asStateFlow()

    private val _selectedSort = MutableStateFlow(TaskSort.NEWEST)
    val selectedSort: StateFlow<TaskSort> = _selectedSort.asStateFlow()

    val rawTasks: StateFlow<List<TaskWithSteps>>

    init {
        val database = AppDatabase.getInstance(application)
        repository = TaskRepository(database.taskDao())
        jsonBackupManager = JsonBackupManager(repository, categoryManager)
        rawTasks = repository.allTasksWithSteps.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            val prefs = application.getSharedPreferences("task_management_prefs", android.content.Context.MODE_PRIVATE)
            val hasSeeded = prefs.getBoolean("has_seeded_initial_data", false)
            if (!hasSeeded) {
                repository.seedSampleDataIfEmpty()
                prefs.edit().putBoolean("has_seeded_initial_data", true).apply()
            }
        }
    }

    val stats: StateFlow<TaskStats> = rawTasks.combine(_searchQuery) { list, _ ->
        val totalTasks = list.size
        val completedTasks = list.count { it.task.isCompleted || (it.steps.isNotEmpty() && it.isAllStepsCompleted) }
        val activeTasks = totalTasks - completedTasks
        val totalSteps = list.sumOf { it.steps.size }
        val completedSteps = list.sumOf { it.completedStepsCount }
        val percentage = if (totalTasks > 0) ((completedTasks.toFloat() / totalTasks.toFloat()) * 100).toInt() else 0

        TaskStats(
            totalTasks = totalTasks,
            completedTasks = completedTasks,
            activeTasks = activeTasks,
            totalSteps = totalSteps,
            completedSteps = completedSteps,
            overallPercentage = percentage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskStats()
    )

    val manageableCategories: StateFlow<List<String>> = categoryManager.categories

    val categories: StateFlow<List<String>> = categoryManager.categories.combine(rawTasks) { catList, taskList ->
        val taskCats = taskList.map { it.task.category.trim() }.filter { it.isNotEmpty() && it != "All" }
        val combined = (listOf("All") + catList + taskCats).distinct()
        combined
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf("All", "Work", "Tech", "Life & Home", "Study", "Writing", "Health")
    )

    val filteredTasks: StateFlow<List<TaskWithSteps>> = combine(
        rawTasks,
        _searchQuery,
        _selectedCategory,
        _selectedFilter,
        _selectedSort
    ) { tasks, query, category, filter, sort ->
        var list = tasks

        // Filter by category
        if (category != "All") {
            list = list.filter { it.task.category.equals(category, ignoreCase = true) }
        }

        // Filter by status
        list = when (filter) {
            TaskFilter.ALL -> list
            TaskFilter.ACTIVE -> list.filter { !(it.task.isCompleted || (it.steps.isNotEmpty() && it.isAllStepsCompleted)) }
            TaskFilter.COMPLETED -> list.filter { it.task.isCompleted || (it.steps.isNotEmpty() && it.isAllStepsCompleted) }
        }

        // Filter by search query
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter { item ->
                item.task.title.lowercase().contains(q) ||
                item.task.description.lowercase().contains(q) ||
                item.task.category.lowercase().contains(q) ||
                item.steps.any { step ->
                    step.whatToDo.lowercase().contains(q) ||
                    step.howToDoIt.lowercase().contains(q) ||
                    step.tipsOrAdvice.lowercase().contains(q)
                }
            }
        }

        // Sorting
        list = when (sort) {
            TaskSort.NEWEST -> list.sortedByDescending { it.task.createdAt }
            TaskSort.PROGRESS -> list.sortedByDescending { it.progress }
        }

        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSelectedFilter(filter: TaskFilter) {
        _selectedFilter.value = filter
    }

    fun setSelectedSort(sort: TaskSort) {
        _selectedSort.value = sort
    }

    fun addCategory(name: String): Boolean {
        return categoryManager.addCategory(name)
    }

    fun deleteCategory(name: String) {
        viewModelScope.launch {
            categoryManager.deleteCategory(name)
            repository.reassignCategoryToGeneral(name)
            if (_selectedCategory.value.equals(name, ignoreCase = true)) {
                _selectedCategory.value = "All"
            }
        }
    }

    fun renameCategory(oldName: String, newName: String): Boolean {
        val success = categoryManager.renameCategory(oldName, newName)
        if (success) {
            viewModelScope.launch {
                repository.updateCategoryForTasks(oldName, newName)
                if (_selectedCategory.value.equals(oldName, ignoreCase = true)) {
                    _selectedCategory.value = newName
                }
            }
        }
        return success
    }

    fun getTaskFlow(taskId: Long): StateFlow<TaskWithSteps?> {
        return repository.getTaskWithStepsById(taskId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    fun toggleStepCompletion(step: TaskStepEntity) {
        viewModelScope.launch {
            repository.toggleStepCompletion(step)
        }
    }

    fun toggleTaskCompletion(taskWithSteps: TaskWithSteps) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(taskWithSteps)
        }
    }

    fun resetTaskProgress(taskId: Long) {
        viewModelScope.launch {
            repository.resetTaskProgress(taskId)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    fun duplicateTask(taskId: Long, onDone: ((Long) -> Unit)? = null) {
        viewModelScope.launch {
            val newId = repository.duplicateTask(taskId)
            if (newId != null && onDone != null) {
                onDone(newId)
            }
        }
    }

    fun saveTask(
        taskId: Long?,
        title: String,
        description: String,
        category: String,
        steps: List<TaskStepEntity>,
        onSaved: (Long) -> Unit
    ) {
        viewModelScope.launch {
            if (taskId != null && taskId > 0) {
                repository.updateTask(
                    taskId = taskId,
                    title = title,
                    description = description,
                    category = category,
                    steps = steps
                )
                onSaved(taskId)
            } else {
                val newId = repository.createTask(
                    title = title,
                    description = description,
                    category = category,
                    steps = steps
                )
                onSaved(newId)
            }
        }
    }

    fun createFromTemplate(template: TaskTemplate, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            // Also ensure template category is in category manager
            categoryManager.addCategory(template.category)
            val steps = template.steps.mapIndexed { index, step ->
                TaskStepEntity(
                    stepIndex = index,
                    whatToDo = step.whatToDo,
                    howToDoIt = step.howToDoIt,
                    tipsOrAdvice = step.tipsOrAdvice
                )
            }
            val id = repository.createTask(
                title = template.title,
                description = template.description,
                category = template.category,
                steps = steps
            )
            onCreated(id)
        }
    }

    fun exportToJson(onComplete: (String) -> Unit) {
        viewModelScope.launch {
            val json = jsonBackupManager.exportToJsonString()
            onComplete(json)
        }
    }

    fun previewBackup(jsonString: String): Result<BackupPreview> {
        return jsonBackupManager.parseBackupPreview(jsonString)
    }

    fun importFromJson(
        jsonString: String,
        replaceExisting: Boolean,
        onResult: (ImportResult) -> Unit
    ) {
        viewModelScope.launch {
            val result = jsonBackupManager.importFromJsonString(jsonString, replaceExisting)
            onResult(result)
        }
    }
}
