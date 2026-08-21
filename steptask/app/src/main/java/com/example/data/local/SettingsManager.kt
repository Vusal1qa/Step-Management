package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    SYSTEM("system", "System Default", "Sistem Varsayılanı"),
    ENGLISH("en", "English", "English"),
    TURKISH("tr", "Turkish", "Türkçe")
}

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("task_management_settings_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _appLanguage = MutableStateFlow(loadAppLanguage())
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private fun loadThemeMode(): ThemeMode {
        val name = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        return try {
            ThemeMode.valueOf(name)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    }

    private fun loadAppLanguage(): AppLanguage {
        val code = prefs.getString(KEY_APP_LANGUAGE, AppLanguage.SYSTEM.code) ?: AppLanguage.SYSTEM.code
        return AppLanguage.entries.firstOrNull { it.code == code } ?: AppLanguage.SYSTEM
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    fun setAppLanguage(language: AppLanguage) {
        _appLanguage.value = language
        prefs.edit().putString(KEY_APP_LANGUAGE, language.code).apply()
    }

    fun getCurrentLanguageCode(): String {
        return when (_appLanguage.value) {
            AppLanguage.ENGLISH -> "en"
            AppLanguage.TURKISH -> "tr"
            AppLanguage.SYSTEM -> {
                val sysLang = Locale.getDefault().language
                if (sysLang.startsWith("tr")) "tr" else "en"
            }
        }
    }

    companion object {
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_APP_LANGUAGE = "key_app_language"

        @Volatile
        private var INSTANCE: SettingsManager? = null

        fun getInstance(context: Context): SettingsManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingsManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
