package com.example.data.export

import com.example.data.local.CategoryManager
import com.example.data.local.TaskEntity
import com.example.data.local.TaskStepEntity
import com.example.data.local.TaskWithSteps
import com.example.data.repository.TaskRepository
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ImportResult(
    val isSuccess: Boolean,
    val importedTasksCount: Int = 0,
    val importedStepsCount: Int = 0,
    val importedCategoriesCount: Int = 0,
    val errorMessage: String? = null
)

data class BackupPreview(
    val tasksCount: Int,
    val stepsCount: Int,
    val categoriesCount: Int,
    val exportedAtString: String?,
    val rawJson: String
)

class JsonBackupManager(
    private val repository: TaskRepository,
    private val categoryManager: CategoryManager
) {

    suspend fun exportToJsonString(): String {
        val tasksWithSteps = repository.getAllTasksWithStepsOnce()
        val categories = categoryManager.categories.value

        val root = JSONObject()
        root.put("version", 1)
        root.put("app", "StepTask Management")
        root.put("exportedAt", System.currentTimeMillis())
        
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        root.put("exportedAtIso", dateFormat.format(Date()))

        // Categories
        val categoriesArray = JSONArray()
        categories.forEach { categoriesArray.put(it) }
        root.put("categories", categoriesArray)

        // Tasks
        val tasksArray = JSONArray()
        for (taskWithSteps in tasksWithSteps) {
            val taskObj = JSONObject()
            taskObj.put("title", taskWithSteps.task.title)
            taskObj.put("description", taskWithSteps.task.description)
            taskObj.put("category", taskWithSteps.task.category)
            taskObj.put("isCompleted", taskWithSteps.task.isCompleted)
            taskObj.put("createdAt", taskWithSteps.task.createdAt)
            taskObj.put("updatedAt", taskWithSteps.task.updatedAt)

            val stepsArray = JSONArray()
            for (step in taskWithSteps.steps) {
                val stepObj = JSONObject()
                stepObj.put("stepIndex", step.stepIndex)
                stepObj.put("whatToDo", step.whatToDo)
                stepObj.put("howToDoIt", step.howToDoIt)
                stepObj.put("tipsOrAdvice", step.tipsOrAdvice)
                stepObj.put("isCompleted", step.isCompleted)
                if (step.completedAt != null) {
                    stepObj.put("completedAt", step.completedAt)
                } else {
                    stepObj.put("completedAt", JSONObject.NULL)
                }
                stepsArray.put(stepObj)
            }
            taskObj.put("steps", stepsArray)
            tasksArray.put(taskObj)
        }
        root.put("tasks", tasksArray)

        return root.toString(2)
    }

    fun parseBackupPreview(jsonString: String): Result<BackupPreview> {
        return try {
            val trimmed = jsonString.trim()
            if (trimmed.isEmpty()) {
                return Result.failure(IllegalArgumentException("JSON content is empty"))
            }

            var tasksCount = 0
            var stepsCount = 0
            var categoriesCount = 0
            var exportedAtStr: String? = null

            if (trimmed.startsWith("[")) {
                // Raw array of tasks
                val array = JSONArray(trimmed)
                tasksCount = array.length()
                for (i in 0 until array.length()) {
                    val taskObj = array.getJSONObject(i)
                    val steps = taskObj.optJSONArray("steps")
                    if (steps != null) stepsCount += steps.length()
                }
            } else {
                val root = JSONObject(trimmed)
                exportedAtStr = root.optString("exportedAtIso").ifEmpty {
                    val timestamp = root.optLong("exportedAt", 0L)
                    if (timestamp > 0) SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(timestamp)) else null
                }
                val cats = root.optJSONArray("categories")
                if (cats != null) categoriesCount = cats.length()

                val tasks = root.optJSONArray("tasks")
                if (tasks != null) {
                    tasksCount = tasks.length()
                    for (i in 0 until tasks.length()) {
                        val taskObj = tasks.getJSONObject(i)
                        val steps = taskObj.optJSONArray("steps")
                        if (steps != null) stepsCount += steps.length()
                    }
                }
            }

            Result.success(
                BackupPreview(
                    tasksCount = tasksCount,
                    stepsCount = stepsCount,
                    categoriesCount = categoriesCount,
                    exportedAtString = exportedAtStr,
                    rawJson = trimmed
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importFromJsonString(jsonString: String, replaceExisting: Boolean): ImportResult {
        return try {
            val trimmed = jsonString.trim()
            if (trimmed.isEmpty()) {
                return ImportResult(isSuccess = false, errorMessage = "JSON string is empty")
            }

            val tasksToImport = mutableListOf<ParsedTaskData>()
            val categoriesToImport = mutableListOf<String>()

            if (trimmed.startsWith("[")) {
                // Array of tasks
                val array = JSONArray(trimmed)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    tasksToImport.add(parseTaskJson(obj))
                }
            } else {
                val root = JSONObject(trimmed)
                val catsArray = root.optJSONArray("categories")
                if (catsArray != null) {
                    for (i in 0 until catsArray.length()) {
                        val cat = catsArray.optString(i)
                        if (!cat.isNullOrBlank()) {
                            categoriesToImport.add(cat.trim())
                        }
                    }
                }

                val tasksArray = root.optJSONArray("tasks")
                if (tasksArray != null) {
                    for (i in 0 until tasksArray.length()) {
                        val obj = tasksArray.getJSONObject(i)
                        tasksToImport.add(parseTaskJson(obj))
                    }
                }
            }

            if (tasksToImport.isEmpty() && categoriesToImport.isEmpty()) {
                return ImportResult(
                    isSuccess = false,
                    errorMessage = "No valid tasks or categories found in JSON"
                )
            }

            if (replaceExisting) {
                repository.clearAllTasksAndSteps()
            }

            // Import categories
            var importedCats = 0
            for (cat in categoriesToImport) {
                if (categoryManager.addCategory(cat)) {
                    importedCats++
                }
            }

            // Also collect categories present in task objects
            for (taskData in tasksToImport) {
                if (taskData.category.isNotBlank() && taskData.category != "All") {
                    categoryManager.addCategory(taskData.category)
                }
            }

            // Import tasks & steps
            var importedSteps = 0
            for (taskData in tasksToImport) {
                val taskEntity = TaskEntity(
                    id = 0,
                    title = taskData.title,
                    description = taskData.description,
                    category = taskData.category.ifBlank { "General" },
                    isCompleted = taskData.isCompleted,
                    createdAt = if (taskData.createdAt > 0) taskData.createdAt else System.currentTimeMillis(),
                    updatedAt = if (taskData.updatedAt > 0) taskData.updatedAt else System.currentTimeMillis()
                )

                val stepEntities = taskData.steps.mapIndexed { index, s ->
                    importedSteps++
                    TaskStepEntity(
                        id = 0,
                        taskId = 0,
                        stepIndex = s.stepIndex.takeIf { it >= 0 } ?: index,
                        whatToDo = s.whatToDo,
                        howToDoIt = s.howToDoIt,
                        tipsOrAdvice = s.tipsOrAdvice,
                        isCompleted = s.isCompleted,
                        completedAt = s.completedAt
                    )
                }

                repository.importSingleTask(taskEntity, stepEntities)
            }

            ImportResult(
                isSuccess = true,
                importedTasksCount = tasksToImport.size,
                importedStepsCount = importedSteps,
                importedCategoriesCount = importedCats
            )
        } catch (e: Exception) {
            ImportResult(
                isSuccess = false,
                errorMessage = e.localizedMessage ?: "Failed to parse JSON file"
            )
        }
    }

    private fun parseTaskJson(obj: JSONObject): ParsedTaskData {
        val title = obj.optString("title", "Untitled Task")
        val description = obj.optString("description", "")
        val category = obj.optString("category", "General")
        val isCompleted = obj.optBoolean("isCompleted", false)
        val createdAt = obj.optLong("createdAt", System.currentTimeMillis())
        val updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())

        val stepsList = mutableListOf<ParsedStepData>()
        val stepsArray = obj.optJSONArray("steps")
        if (stepsArray != null) {
            for (i in 0 until stepsArray.length()) {
                val sObj = stepsArray.getJSONObject(i)
                val stepIndex = sObj.optInt("stepIndex", i)
                val whatToDo = sObj.optString("whatToDo", "")
                val howToDoIt = sObj.optString("howToDoIt", "")
                val tipsOrAdvice = sObj.optString("tipsOrAdvice", "")
                val stepCompleted = sObj.optBoolean("isCompleted", false)
                val completedAt = if (sObj.has("completedAt") && !sObj.isNull("completedAt")) {
                    sObj.optLong("completedAt")
                } else null

                stepsList.add(
                    ParsedStepData(
                        stepIndex = stepIndex,
                        whatToDo = whatToDo,
                        howToDoIt = howToDoIt,
                        tipsOrAdvice = tipsOrAdvice,
                        isCompleted = stepCompleted,
                        completedAt = completedAt
                    )
                )
            }
        }

        return ParsedTaskData(
            title = title,
            description = description,
            category = category,
            isCompleted = isCompleted,
            createdAt = createdAt,
            updatedAt = updatedAt,
            steps = stepsList
        )
    }

    private data class ParsedTaskData(
        val title: String,
        val description: String,
        val category: String,
        val isCompleted: Boolean,
        val createdAt: Long,
        val updatedAt: Long,
        val steps: List<ParsedStepData>
    )

    private data class ParsedStepData(
        val stepIndex: Int,
        val whatToDo: String,
        val howToDoIt: String,
        val tipsOrAdvice: String,
        val isCompleted: Boolean,
        val completedAt: Long?
    )
}
