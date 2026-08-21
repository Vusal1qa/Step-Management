package com.example.web

import kotlinx.browser.document
import org.w3c.fetch.RequestInit
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private val scope = MainScope()

fun main() {
    // Basit olarak statik JSON fetch örneği
    val root = document.getElementById("root") ?: return
    root?.textContent = "StepTask - Web (yükleniyor...)"

    scope.launch {
        try {
            val res = window.fetch("/data/tasks.json")
            if (res.ok) {
                val txt = res.text().await()
                root.textContent = "Gelen JSON: ${'$'}{txt.take(200)}"
            } else {
                root.textContent = "JSON yüklenemedi: ${'$'}{res.status}"
            }
        } catch (e: Throwable) {
            root.textContent = "Hata: ${'$'}{e.message}"
        }
    }
}
