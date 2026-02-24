package ru.igm.tsp.data.repository

import android.util.Log
import ru.igm.tsp.domain.model.CaptchaResponse
import ru.igm.tsp.domain.model.LoginRequest
import ru.igm.tsp.domain.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Интерфейс для Retrofit, описывающий API эндпоинты
interface AuthApiService {
    @GET("api/v1/auth/captcha")
    suspend fun getCaptcha(): Response<CaptchaResponse>

    @POST("api/v1/auth/login") // Замените на ваш URL, если он другой
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}

// Сам класс-репозиторий, который будет использовать ViewModel
class AuthRepository(private val apiService: AuthApiService) {
    companion object {
        private const val TAG = "AuthRepository"
    }

    suspend fun getCaptcha(): Result<CaptchaResponse> {
        return try {
            Log.i(TAG, "Start getting captcha...")
            val response = apiService.getCaptcha()
            if (response.isSuccessful && response.body() != null) {
                Log.i(TAG, "Received answer")

                val captcha = response.body()!!
                Log.i(TAG, "Captcha ID: ${captcha.captchaId}")
                Log.i(TAG, "Captcha Image (first 100 chars): ${captcha.captchaImage.take(100)}") // <-- Добавьте эту строку
                Result.success(captcha)

                    //Result.success(response.body()!!)
            } else {
                Log.w(TAG, "Error getting captcha " + response.code() + " msg " + response.message())
                Result.failure(Exception("Ошибка ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Exception while getting captcha "  + e.message.toString())
            Result.failure(e)
        }
    }

    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = apiService.login(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                // Попробуем извлечь ошибку от сервера, как в вашем Angular коде
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Ошибка ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}