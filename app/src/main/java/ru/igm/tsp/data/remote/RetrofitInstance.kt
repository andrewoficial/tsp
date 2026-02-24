package ru.igm.tsp.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.igm.tsp.data.repository.AuthApiService

// Замените на ваш реальный базовый URL
private const val BASE_URL = "https://ws.safezone.support/"

object RetrofitInstance {
    // Ленивая инициализация. Retrofit будет создан только при первом обращении.
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
}