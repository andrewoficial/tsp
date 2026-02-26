package ru.igm.tsp.domain.service

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import ru.igm.tsp.data.remote.GsonProvider
import ru.igm.tsp.data.remote.adapters.LongTypeAdapter
import ru.igm.tsp.domain.model.message.ChatMessage

object MessageParser {
    private val gson = GsonProvider.createGson()
    private val TAG = "MessageParser"

    fun parse(rawMessage: String): ChatMessage? {
        return try {
            Log.d(TAG, "Attempting to parse message: ${rawMessage.take(100)}...")
            val message = gson.fromJson(rawMessage, ChatMessage::class.java)
            Log.d(TAG, "Successfully parsed message with UUID: ${message.uuid}")
            message
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "JSON parsing error", e)
            Log.e(TAG, "Failed to parse message: $rawMessage")
            null
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during parsing", e)
            Log.e(TAG, "Failed to parse message: $rawMessage")
            null
        }
    }
}