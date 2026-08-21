package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.export.JsonBackupManager
import com.example.data.local.AppDatabase
import com.example.data.local.CategoryManager
import com.example.data.local.TaskStepEntity
import com.example.data.repository.TaskRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class JsonBackupTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: TaskRepository
    private lateinit var categoryManager: CategoryManager
    private lateinit var backupManager: JsonBackupManager

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = TaskRepository(database.taskDao())
        categoryManager = CategoryManager.getInstance(context)
        backupManager = JsonBackupManager(repository, categoryManager)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testExportAndImportCycle() = runBlocking {
        // Create initial tasks
        val taskId = repository.createTask(
            title = "Sample Task 1",
            description = "Description 1",
            category = "Work",
            steps = listOf(
                TaskStepEntity(stepIndex = 0, whatToDo = "Step 1", howToDoIt = "How 1", tipsOrAdvice = "Tip 1"),
                TaskStepEntity(stepIndex = 1, whatToDo = "Step 2", howToDoIt = "How 2", tipsOrAdvice = "Tip 2")
            )
        )
        assertTrue(taskId > 0)

        // Export to JSON
        val json = backupManager.exportToJsonString()
        assertNotNull(json)
        assertTrue(json.contains("Sample Task 1"))
        assertTrue(json.contains("Step 1"))

        // Preview backup
        val previewResult = backupManager.parseBackupPreview(json)
        assertTrue(previewResult.isSuccess)
        val preview = previewResult.getOrNull()
        assertNotNull(preview)
        assertEquals(1, preview!!.tasksCount)
        assertEquals(2, preview.stepsCount)

        // Clear and restore (Replace mode)
        repository.clearAllTasksAndSteps()
        val emptyTasks = repository.getAllTasksWithStepsOnce()
        assertEquals(0, emptyTasks.size)

        val importResult = backupManager.importFromJsonString(json, replaceExisting = true)
        assertTrue(importResult.isSuccess)
        assertEquals(1, importResult.importedTasksCount)
        assertEquals(2, importResult.importedStepsCount)

        val restoredTasks = repository.getAllTasksWithStepsOnce()
        assertEquals(1, restoredTasks.size)
        assertEquals("Sample Task 1", restoredTasks[0].task.title)
        assertEquals(2, restoredTasks[0].steps.size)
    }

    @Test
    fun testInvalidJsonHandling() = runBlocking {
        val previewResult = backupManager.parseBackupPreview("invalid json text here")
        assertFalse(previewResult.isSuccess)

        val importResult = backupManager.importFromJsonString("invalid json text here", replaceExisting = false)
        assertFalse(importResult.isSuccess)
        assertNotNull(importResult.errorMessage)
    }
}
