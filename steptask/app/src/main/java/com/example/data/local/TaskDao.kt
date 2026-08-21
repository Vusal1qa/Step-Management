package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasksWithSteps(): Flow<List<TaskWithSteps>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskWithStepsById(taskId: Long): Flow<TaskWithSteps?>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskWithStepsByIdOnce(taskId: Long): TaskWithSteps?

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    suspend fun getAllTasksWithStepsListOnce(): List<TaskWithSteps>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskOnly(taskId: Long)

    @Query("DELETE FROM task_steps")
    suspend fun deleteAllSteps()

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Transaction
    suspend fun clearAllData() {
        deleteAllSteps()
        deleteAllTasks()
    }

    @Transaction
    suspend fun deleteTaskById(taskId: Long) {
        deleteStepsForTask(taskId)
        deleteTaskOnly(taskId)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<TaskStepEntity>)

    @Query("DELETE FROM task_steps WHERE taskId = :taskId")
    suspend fun deleteStepsForTask(taskId: Long)

    @Update
    suspend fun updateStep(step: TaskStepEntity)

    @Query("UPDATE task_steps SET isCompleted = :isCompleted, completedAt = :completedAt WHERE id = :stepId")
    suspend fun setStepCompletion(stepId: Long, isCompleted: Boolean, completedAt: Long?)

    @Query("UPDATE tasks SET isCompleted = :isCompleted, updatedAt = :updatedAt WHERE id = :taskId")
    suspend fun setTaskCompletion(taskId: Long, isCompleted: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE task_steps SET isCompleted = :isCompleted, completedAt = :completedAt WHERE taskId = :taskId")
    suspend fun setAllStepsCompletionForTask(taskId: Long, isCompleted: Boolean, completedAt: Long?)

    @Query("UPDATE tasks SET category = :newCategory WHERE category = :oldCategory")
    suspend fun updateCategoryForTasks(oldCategory: String, newCategory: String)

    @Transaction
    suspend fun insertTaskWithSteps(task: TaskEntity, steps: List<TaskStepEntity>): Long {
        val taskId = insertTask(task)
        val stepsWithTaskId = steps.mapIndexed { index, step ->
            step.copy(taskId = taskId, stepIndex = index)
        }
        insertSteps(stepsWithTaskId)
        return taskId
    }

    @Transaction
    suspend fun updateTaskWithSteps(task: TaskEntity, steps: List<TaskStepEntity>) {
        updateTask(task)
        deleteStepsForTask(task.id)
        val stepsWithTaskId = steps.mapIndexed { index, step ->
            step.copy(taskId = task.id, stepIndex = index)
        }
        insertSteps(stepsWithTaskId)
    }
}
