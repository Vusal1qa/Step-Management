package com.example.common

import kotlinx.coroutines.flow.MutableStateFlow

@kotlinx.serialization.Serializable
data class Task(val id: String, val title: String, val completed: Boolean = false)

object SampleRepository {
    private val sample = listOf(
        Task("1", "Örnek görev 1"),
        Task("2", "Örnek görev 2", completed = true),
    )

    fun getTasks() = MutableStateFlow(sample)
}
