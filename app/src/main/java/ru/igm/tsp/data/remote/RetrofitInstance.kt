package ru.igm.tsp.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.igm.tsp.data.local.ConfigManager

object RetrofitInstance {

    fun getApi(configManager: ConfigManager): AuthApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(configManager.apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(AuthApiService::class.java)
    }

}