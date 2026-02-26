package ru.igm.tsp.data.local

import android.content.Context
import ru.igm.tsp.domain.Constants
import androidx.core.content.edit

class ConfigManager(context: Context) {
    private val prefs = context.getSharedPreferences("app_config", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_API_URL = "api_url"
        private const val KEY_WS_URL = "ws_url"
        private const val KEY_CAPTCHA_PATH = "captcha_path"
        private const val KEY_LOGIN_PATH = "login_path"
    }

    var apiUrl: String
        get() = prefs.getString(KEY_API_URL, Constants.DEFAULT_API_URL) ?: Constants.DEFAULT_API_URL
        set(value) = prefs.edit { putString(KEY_API_URL, value) }

    var wsUrl: String
        get() = prefs.getString(KEY_WS_URL, Constants.DEFAULT_WS_URL) ?: Constants.DEFAULT_WS_URL
        set(value) = prefs.edit { putString(KEY_WS_URL, value) }

    var captchaPath: String
        get() = prefs.getString(KEY_CAPTCHA_PATH, Constants.DEFAULT_CAPTCHA_PATH) ?: Constants.DEFAULT_CAPTCHA_PATH
        set(value) = prefs.edit { putString(KEY_CAPTCHA_PATH, value) }

    var loginPath: String
        get() = prefs.getString(KEY_LOGIN_PATH, Constants.DEFAULT_LOGIN_PATH) ?: Constants.DEFAULT_LOGIN_PATH
        set(value) = prefs.edit { putString(KEY_LOGIN_PATH, value) }

    fun resetToDefaults() {
        apiUrl = Constants.DEFAULT_API_URL
        wsUrl = Constants.DEFAULT_WS_URL
    }
}