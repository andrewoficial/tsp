// data/remote/AuthApiService.kt
package ru.igm.tsp.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url
import ru.igm.tsp.domain.model.CaptchaResponse
import ru.igm.tsp.domain.model.LoginRequest
import ru.igm.tsp.domain.model.LoginResponse

interface AuthApiService {
    // Убираем путь из аннотации и добавляем параметр @Url
    @GET
    suspend fun getCaptcha(@Url url: String): Response<CaptchaResponse>

    @POST
    suspend fun login(@Url url: String, @Body request: LoginRequest): Response<LoginResponse>
}