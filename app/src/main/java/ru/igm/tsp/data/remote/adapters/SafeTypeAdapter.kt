package ru.igm.tsp.data.remote.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

class SafeTypeAdapter<T> : TypeAdapter<T?>() {
    override fun read(jsonReader: JsonReader): T? {
        if (jsonReader.peek() == com.google.gson.stream.JsonToken.NULL) {
            jsonReader.nextNull()
            return null
        }
        
        try {
            when (jsonReader.peek()) {
                com.google.gson.stream.JsonToken.STRING -> {
                    val value = jsonReader.nextString()
                    return if (value.isEmpty()) null else value as T
                }
                com.google.gson.stream.JsonToken.NUMBER -> {
                    val value = jsonReader.nextLong()
                    return value as T
                }
                com.google.gson.stream.JsonToken.BOOLEAN -> {
                    val value = jsonReader.nextBoolean()
                    return value as T
                }
                else -> {
                    jsonReader.skipValue()
                    return null
                }
            }
        } catch (e: Exception) {
            jsonReader.skipValue()
            return null
        }
    }

    override fun write(jsonWriter: JsonWriter, value: T?) {
        if (value == null) {
            jsonWriter.nullValue()
        } else {
            jsonWriter.value(value.toString())
        }
    }
}