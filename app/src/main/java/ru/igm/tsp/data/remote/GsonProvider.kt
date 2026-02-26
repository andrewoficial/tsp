package ru.igm.tsp.data.remote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ru.igm.tsp.data.remote.adapters.BooleanTypeAdapter
import ru.igm.tsp.data.remote.adapters.DoubleTypeAdapter
import ru.igm.tsp.data.remote.adapters.IntTypeAdapter
import ru.igm.tsp.data.remote.adapters.LongTypeAdapter

object GsonProvider {
    fun createGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Long::class.javaObjectType, LongTypeAdapter())
            .registerTypeAdapter(Long::class.java, LongTypeAdapter())
            .registerTypeAdapter(Int::class.javaObjectType, IntTypeAdapter())
            .registerTypeAdapter(Int::class.java, IntTypeAdapter())
            .registerTypeAdapter(Double::class.javaObjectType, DoubleTypeAdapter())
            .registerTypeAdapter(Double::class.java, DoubleTypeAdapter())
            .registerTypeAdapter(Boolean::class.javaObjectType, BooleanTypeAdapter())
            .registerTypeAdapter(Boolean::class.java, BooleanTypeAdapter())
            .create()
    }
}