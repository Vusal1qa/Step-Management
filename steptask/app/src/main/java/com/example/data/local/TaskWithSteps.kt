package com.example.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class TaskWithSteps(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val steps: List<TaskStepEntity>
) {
    val totalSteps: Int get() = steps.size
    val completedStepsCount: Int get() = steps.count { it.isCompleted }
    val progress: Float get() = if (steps.isEmpty()) (if (task.isCompleted) 1f else 0f) else completedStepsCount.toFloat() / steps.size.toFloat()
    val isAllStepsCompleted: Boolean get() = steps.isNotEmpty() && steps.all { it.isCompleted }
}
