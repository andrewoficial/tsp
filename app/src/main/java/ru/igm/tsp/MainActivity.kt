package ru.igm.tsp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.igm.tsp.data.local.SessionManager
import ru.igm.tsp.presentation.screen.ChatScreen
import ru.igm.tsp.presentation.screen.LoginScreen
import ru.igm.tsp.presentation.theme.TSPTheme

class MainActivity : ComponentActivity() {
    // 1. Обещаем компилятору, что инициализируем sessionManager позже
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. Инициализируем здесь. В этот момент `this` - это валидный, готовый Context.
        sessionManager = SessionManager(this)

        setContent {
            TSPTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatAppNavigation(sessionManager)
                }
            }
        }
    }

}

@Composable
fun ChatAppNavigation(sessionManager: SessionManager) {
    val navController = rememberNavController()

    val startDestination = if (sessionManager.token != null) "chat" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    println("Навигация на экран чата...")
                    // 1. РЕАЛИЗУЕМ НАВИГАЦИЮ
                    navController.navigate("chat") {
                        // Чистим бэкстек, чтобы нельзя было вернуться назад на экран входа
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("chat") {
            ChatScreen(navController = navController)
        }
    }
}