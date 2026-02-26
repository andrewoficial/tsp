package ru.igm.tsp.data.remote.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

class IntTypeAdapter : TypeAdapter<Int?>() {
    @Throws(IOException::class)
    override fun read(jsonReader: JsonReader): Int? {
        if (jsonReader.peek() == com.google.gson.stream.JsonToken.NULL) {
            jsonReader.nextNull()
            return null
        }
        val str = jsonReader.nextString()
        return if (str.isEmpty()) null else str.toInt()
    }

    @Throws(IOException::class)
    override fun write(jsonWriter: JsonWriter, value: Int?) {
        if (value == null) {
            jsonWriter.nullValue()
        } else {
            jsonWriter.value(value)
        }
    }
}