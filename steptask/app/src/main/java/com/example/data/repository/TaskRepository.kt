package com.example.data.repository

import com.example.data.local.TaskDao
import com.example.data.local.TaskEntity
import com.example.data.local.TaskStepEntity
import com.example.data.local.TaskWithSteps
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class TaskRepository(private val taskDao: TaskDao) {

    val allTasksWithSteps: Flow<List<TaskWithSteps>> = taskDao.getAllTasksWithSteps()

    fun getTaskWithStepsById(taskId: Long): Flow<TaskWithSteps?> =
        taskDao.getTaskWithStepsById(taskId)

    suspend fun getTaskWithStepsByIdOnce(taskId: Long): TaskWithSteps? =
        taskDao.getTaskWithStepsByIdOnce(taskId)

    suspend fun getAllTasksWithStepsOnce(): List<TaskWithSteps> =
        taskDao.getAllTasksWithStepsListOnce()

    suspend fun clearAllTasksAndSteps() {
        taskDao.clearAllData()
    }

    suspend fun importSingleTask(task: TaskEntity, steps: List<TaskStepEntity>): Long {
        return taskDao.insertTaskWithSteps(task, steps)
    }

    suspend fun createTask(
        title: String,
        description: String,
        category: String,
        steps: List<TaskStepEntity>
    ): Long {
        val task = TaskEntity(
            title = title.trim(),
            description = description.trim(),
            category = category.trim().ifEmpty { "General" },
            isCompleted = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return taskDao.insertTaskWithSteps(task, steps)
    }

    suspend fun updateTask(
        taskId: Long,
        title: String,
        description: String,
        category: String,
        steps: List<TaskStepEntity>
    ) {
        val existing = taskDao.getTaskWithStepsByIdOnce(taskId)
        val task = TaskEntity(
            id = taskId,
            title = title.trim(),
            description = description.trim(),
            category = category.trim().ifEmpty { "General" },
            isCompleted = existing?.task?.isCompleted ?: false,
            createdAt = existing?.task?.createdAt ?: System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        taskDao.updateTaskWithSteps(task, steps)
    }

    suspend fun deleteTask(taskId: Long) {
        taskDao.deleteTaskById(taskId)
    }

    suspend fun toggleStepCompletion(step: TaskStepEntity) {
        val newStatus = !step.isCompleted
        val completedAt = if (newStatus) System.currentTimeMillis() else null
        taskDao.setStepCompletion(step.id, newStatus, completedAt)

        // Check if all steps for this task are now completed or if at least one is incomplete
        val taskWithSteps = taskDao.getTaskWithStepsByIdOnce(step.taskId)
        if (taskWithSteps != null) {
            val allDone = taskWithSteps.steps.isNotEmpty() && taskWithSteps.steps.all {
                if (it.id == step.id) newStatus else it.isCompleted
            }
            if (allDone != taskWithSteps.task.isCompleted) {
                taskDao.setTaskCompletion(step.taskId, allDone)
            }
        }
    }

    suspend fun toggleTaskCompletion(taskWithSteps: TaskWithSteps) {
        val newStatus = !taskWithSteps.task.isCompleted
        val completedAt = if (newStatus) System.currentTimeMillis() else null
        taskDao.setTaskCompletion(taskWithSteps.task.id, newStatus)
        taskDao.setAllStepsCompletionForTask(taskWithSteps.task.id, newStatus, completedAt)
    }

    suspend fun resetTaskProgress(taskId: Long) {
        taskDao.setTaskCompletion(taskId, false)
        taskDao.setAllStepsCompletionForTask(taskId, false, null)
    }

    suspend fun updateCategoryForTasks(oldCategory: String, newCategory: String) {
        taskDao.updateCategoryForTasks(oldCategory, newCategory)
    }

    suspend fun reassignCategoryToGeneral(oldCategory: String) {
        taskDao.updateCategoryForTasks(oldCategory, "General")
    }

    suspend fun duplicateTask(taskId: Long): Long? {
        val original = taskDao.getTaskWithStepsByIdOnce(taskId) ?: return null
        val newTask = original.task.copy(
            id = 0,
            title = "${original.task.title} (Copy)",
            isCompleted = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val newSteps = original.steps.mapIndexed { index, step ->
            step.copy(
                id = 0,
                taskId = 0,
                stepIndex = index,
                isCompleted = false,
                completedAt = null
            )
        }
        return taskDao.insertTaskWithSteps(newTask, newSteps)
    }

    suspend fun seedSampleDataIfEmpty() {
        val current = taskDao.getAllTasksWithSteps().firstOrNull()
        if (current.isNullOrEmpty()) {
            // Task 1
            createTask(
                title = "Prepare a High-Impact Presentation",
                description = "Build and deliver a compelling slide deck that persuades key stakeholders.",
                category = "Work",
                steps = listOf(
                    TaskStepEntity(
                        stepIndex = 0,
                        whatToDo = "Define the Single Core Message & Audience Goal",
                        howToDoIt = "Write down in 1 sentence the main conclusion you want listeners to remember. Identify 3 specific questions or hesitations your audience will have.",
                        tipsOrAdvice = "If you cannot summarize the goal in under 20 words, refine it before making any slides."
                    ),
                    TaskStepEntity(
                        stepIndex = 1,
                        whatToDo = "Structure the Narrative Arc (Hook, Conflict, Solution, Action)",
                        howToDoIt = "Draft an outline with 5-6 sections:\n1. Hook: Relatable industry or customer pain point\n2. Context & Data: Why it matters now\n3. Proposed Solution: Core recommendations\n4. Roadmap & ROI: Timeline and expected impact\n5. Call to Action: Immediate next decision needed.",
                        tipsOrAdvice = "People remember stories and concrete numbers, not vague bullet lists."
                    ),
                    TaskStepEntity(
                        stepIndex = 2,
                        whatToDo = "Design Clean Visual Slides with Strict 1-Idea Rule",
                        howToDoIt = "Open your presentation tool. Create 1 slide per idea. Use large headings (28pt+), high-contrast visual charts, and bold keywords instead of paragraphs.",
                        tipsOrAdvice = "Never read your slides verbatim. Slides should support your speech, not replace it."
                    ),
                    TaskStepEntity(
                        stepIndex = 3,
                        whatToDo = "Rehearse with a Live Timer & Record Audio",
                        howToDoIt = "Stand up and present aloud from start to finish without pausing. Time your pacing to finish with at least 5 minutes left for Q&A.",
                        tipsOrAdvice = "Check for filler words ('um', 'like', 'you know') and pause silently instead."
                    )
                )
            )

            // Task 2
            createTask(
                title = "Debug & Resolve a Critical Software Bug",
                description = "Standard operating procedure to triage, isolate, fix, and verify production issues.",
                category = "Tech",
                steps = listOf(
                    TaskStepEntity(
                        stepIndex = 0,
                        whatToDo = "Reliably Reproduce the Bug in Local/Staging Environment",
                        howToDoIt = "Gather the exact user journey, device OS version, payload parameters, and error stack trace. Execute the reproduction steps until the bug triggers consistently.",
                        tipsOrAdvice = "A bug that cannot be reproduced cannot be proven fixed."
                    ),
                    TaskStepEntity(
                        stepIndex = 1,
                        whatToDo = "Isolate the Root Cause Using Log Breakpoints",
                        howToDoIt = "Set breakpoints around the suspected data transform or state mutation. Inspect variable states immediately preceding the fault.",
                        tipsOrAdvice = "Avoid guessing; trace state changes step by step."
                    ),
                    TaskStepEntity(
                        stepIndex = 2,
                        whatToDo = "Write an Automated Regression Test",
                        howToDoIt = "Write a unit test or integration test case that triggers the buggy state and asserts the expected correct outcome. Confirm the test fails prior to code changes.",
                        tipsOrAdvice = "This guarantees the issue will never regress in future releases."
                    ),
                    TaskStepEntity(
                        stepIndex = 3,
                        whatToDo = "Implement the Minimal Surgical Fix",
                        howToDoIt = "Apply the cleanest code modification that resolves the root failure without unnecessary refactoring of unrelated logic.",
                        tipsOrAdvice = "Keep diffs small and focused for rapid code review."
                    ),
                    TaskStepEntity(
                        stepIndex = 4,
                        whatToDo = "Run Entire Test Suite & Verify Safe Rollout",
                        howToDoIt = "Run local builds and automated CI pipelines. Deploy to staging, verify edge cases, and merge the Pull Request.",
                        tipsOrAdvice = "Monitor error logging dashboards for 15 minutes post-deployment."
                    )
                )
            )

            // Task 3
            createTask(
                title = "Weekend Home Reset & Workspace Ergonomics",
                description = "A refresh routine to clear physical clutter and optimize your creative environment for the week ahead.",
                category = "Life & Home",
                steps = listOf(
                    TaskStepEntity(
                        stepIndex = 0,
                        whatToDo = "Clear All Physical Surface Clutter",
                        howToDoIt = "Remove mugs, paper notes, and unneeded gadgets from your desk. Return each item to its designated drawer or storage container.",
                        tipsOrAdvice = "A clear desk equals a clear, focused mind."
                    ),
                    TaskStepEntity(
                        stepIndex = 1,
                        whatToDo = "Sanitize Keyboard, Mouse & Screen",
                        howToDoIt = "Wipe down keyboard keys, mouse grips, and trackpad with safe disinfectant wipes. Use a microfiber cloth with distilled water or screen cleaner on displays.",
                        tipsOrAdvice = "Turn off or lock your monitor first to see dust and smudges easily."
                    ),
                    TaskStepEntity(
                        stepIndex = 2,
                        whatToDo = "Cable Management & Charging Station Check",
                        howToDoIt = "Bundle loose cords with velcro ties. Ensure primary phone and laptop chargers are plugged in and ready.",
                        tipsOrAdvice = "Keep everyday charging cables within easy arm's reach."
                    ),
                    TaskStepEntity(
                        stepIndex = 3,
                        whatToDo = "Set Weekly Top 3 Objectives",
                        howToDoIt = "Open your planner or notes and write down your 3 most important goals for Monday through Friday.",
                        tipsOrAdvice = "Focus only on high-leverage outcomes."
                    )
                )
            )
        }
    }
}
