package ru.igm.tsp.data.remote.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

class DoubleTypeAdapter : TypeAdapter<Double?>() {
    @Throws(IOException::class)
    override fun read(jsonReader: JsonReader): Double? {
        if (jsonReader.peek() == com.google.gson.stream.JsonToken.NULL) {
            jsonReader.nextNull()
            return null
        }
        val str = jsonReader.nextString()
        return if (str.isEmpty()) null else str.toDouble()
    }

    @Throws(IOException::class)
    override fun write(jsonWriter: JsonWriter, value: Double?) {
        if (value == null) {
            jsonWriter.nullValue()
        } else {
            jsonWriter.value(value)
        }
    }
}