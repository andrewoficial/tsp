package ru.igm.tsp.domain.service

import android.util.Log
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException

object MessageValidator {
    private val TAG = "MessageValidator"

    fun isValidRawMessage(rawMessage: String): Boolean {
        Log.d(TAG, "Validating message: ${rawMessage.take(100)}...")

        // 1. Примитивная валидация
        if (rawMessage.length < 5) {
            Log.w(TAG, "Message too short: ${rawMessage.length} characters")
            return false
        }

        // 2. Проверяем, что это вообще валидный JSON
        return try {
            val jsonElement = com.google.gson.Gson().fromJson(rawMessage, JsonElement::class.java)
            val jsonObject = jsonElement.asJsonObject

            // Проверяем наличие обязательных полей
            if (!jsonObject.has("uuid")) {
                Log.w(TAG, "Message missing 'uuid' field")
                return false
            }

            if (!jsonObject.has("messageType")) {
                Log.w(TAG, "Message missing 'messageType' field")
                return false
            }

            Log.d(TAG, "Message validation passed")
            true
        } catch (e: JsonSyntaxException) {
            Log.w(TAG, "Invalid JSON format", e)
            false
        }
    }
}