package com.example.desktop

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.common.SampleRepository

@Composable
@Preview
fun App() {
    val tasksFlow = SampleRepository.getTasks()
    val tasks by tasksFlow.collectAsState()

    MaterialTheme {
        Column {
            Text("StepTask - Desktop", modifier = androidx.compose.ui.Modifier.padding(8.dp))
            for (t in tasks) {
                Text("- ${'$'}{t.title}")
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "StepTask") {
        App()
    }
}
