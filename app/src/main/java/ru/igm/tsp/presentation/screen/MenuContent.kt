package ru.igm.tsp.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuContent(
    username: String?,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Меню",
                style = MaterialTheme.typography.titleLarge
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            if (username != null) {
                Text(
                    text = "Пользователь: $username",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                Divider()
            }

            TextButton(onClick = onSettingsClick) {
                Text("Настройки")
            }

            TextButton(onClick = onLogoutClick) {
                Text("Выйти")
            }
        }
    }
}