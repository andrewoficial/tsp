package ru.igm.tsp.data.remote.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException

// Этот адаптер научит Gson правильно обрабатывать Long поля
class LongTypeAdapter : TypeAdapter<Long?>() {
    @Throws(IOException::class)
    override fun read(jsonReader: JsonReader): Long? {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull()
            return null
        }
        val str = jsonReader.nextString()
        // Если строка пустая, возвращаем null. Иначе - конвертируем в Long.
        return if (str.isEmpty()) null else str.toLong()
    }

    @Throws(IOException::class)
    override fun write(jsonWriter: JsonWriter, value: Long?) {
        if (value == null) {
            jsonWriter.nullValue()
        } else {
            jsonWriter.value(value)
        }
    }
}