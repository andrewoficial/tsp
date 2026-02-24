package ru.igm.tsp.presentation.screen

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.igm.tsp.presentation.viewmodel.AuthViewModel
import java.io.ByteArrayInputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit, // Функция, которая вызовется при успешном входе
    viewModel: AuthViewModel = viewModel() // Получаем ViewModel
) {
    val uiState by viewModel.uiState.collectAsState() // Собираем состояние из ViewModel
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var captchaValue by remember { mutableStateOf("") }

    // Если вход успешен, вызываем колбэк для навигации
    LaunchedEffect(uiState.isLoginSuccessful) {
        if (uiState.isLoginSuccessful) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Вход в систему", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Имя пользователя") },
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        // Отображение капчи
        uiState.captchaData?.let { captcha ->
            val imageBitmap = remember(captcha.captchaImage) {
                try {
                    val base64Image = captcha.captchaImage.substringAfter("base64,") // 1. Отрезаем все, что до "base64,"
                    val imageBytes = Base64.decode(base64Image, Base64.DEFAULT) // 2. Декодируем "чистые" данные

                    val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(imageBytes))
                    bitmap.asImageBitmap()
                } catch (e: Exception) {
                    null
                }
            }

            imageBitmap?.let {
                Image(
                    bitmap = it,
                    contentDescription = "Капча",
                    modifier = Modifier
                        .fillMaxWidth() // 1. Занять всю доступную ширину
                        .height(150.dp) // 2. Задать желаемую высоту
                        .padding(horizontal = 8.dp), // 3. Добавить небольшие отступы по бокам
                    contentScale = ContentScale.FillWidth // 4. Ключевая строка!

             )
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = captchaValue,
                onValueChange = { captchaValue = it },
                label = { Text("Введите текст с картинки") },
                singleLine = true
            )
        }

        Spacer(Modifier.height(16.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    viewModel.login(username, password, captchaValue)
                },
                enabled = username.isNotBlank() && password.isNotBlank() && captchaValue.isNotBlank()
            ) {
                Text("Войти")
            }
        }

        // Отображение ошибки
        uiState.errorMessage?.let { error ->
            Spacer(Modifier.height(8.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        TextButton(onClick = { viewModel.loadCaptcha() }) {
            Text("Обновить капчу")
        }
    }
}