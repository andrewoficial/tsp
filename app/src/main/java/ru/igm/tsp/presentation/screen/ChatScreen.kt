package ru.igm.tsp.presentation.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.igm.tsp.domain.model.ConnectionState
import ru.igm.tsp.domain.model.message.ChatMessage
import ru.igm.tsp.presentation.viewmodel.ChatViewModel
import ru.igm.tsp.presentation.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    chatViewModel: ChatViewModel = viewModel(),
    menuViewModel: MenuViewModel = viewModel()
) {
    val TAG = "ChatScreen"
    Log.d(TAG, "ChatScreen recomposition")

    // Получаем состояния из ViewModel
    val chatUiState by chatViewModel.uiState.collectAsState()
    val menuUiState by menuViewModel.uiState.collectAsState()

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val listState = rememberLazyListState()

    // Отслеживаем только последнее сообщение для прокрутки
    val lastMessageId by remember { derivedStateOf {
        chatUiState.messages.lastOrNull()?.uuid
    } }

    var showHistoryMenu by remember { mutableStateOf(false) }

    // Отслеживаем количество сообщений для логирования
    val messageCount by remember { derivedStateOf { chatUiState.messages.size } }

    // Логируем изменение количества сообщений
    LaunchedEffect(messageCount) {
        Log.d(TAG, "Message count changed to: $messageCount")
    }

    // Автоматическая прокрутка вниз при новых сообщениях
    LaunchedEffect(lastMessageId) {
        Log.d(TAG, "LaunchedEffect triggered for lastMessageId: $lastMessageId")
        if (chatUiState.messages.isNotEmpty()) {
            val startTime = System.currentTimeMillis()
            listState.animateScrollToItem(chatUiState.messages.size - 1)
            val endTime = System.currentTimeMillis()
            Log.d(TAG, "Scroll animation took ${endTime - startTime}ms")
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuContent(
                username = menuUiState.username,
                onSettingsClick = {
                    scope.launch { drawerState.close() }
                    // TODO: Навигация на экран настроек
                },
                onLogoutClick = {
                    menuViewModel.logout {
                        navController.navigate("login") {
                            popUpTo("chat") { inclusive = true }
                        }
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Чат") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Меню")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showHistoryMenu = !showHistoryMenu }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Ещё")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Если меню управления историей открыто
                    if (showHistoryMenu) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text("Управление историей", style = MaterialTheme.typography.titleMedium)

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        chatViewModel.downloadHistory()
                                        showHistoryMenu = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Загрузить историю")
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        chatViewModel.clearHistory()
                                        showHistoryMenu = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Очистить историю")
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        chatViewModel.interruptGeneration()
                                        showHistoryMenu = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Прервать генерацию")
                                }
                            }
                        }
                    }

                    // Список сообщений
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        state = listState,
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            items = chatUiState.messages,
                            key = { message -> message.uuid }
                        ) { message ->
                            MessageBubble(message = message)
                        }
                    }

                    // Поле ввода и кнопка отправки
                    MessageInput(
                        onSendClick = { text ->
                            chatViewModel.sendMessage(text)
                        }
                    )
                }

                // Индикатор состояния соединения
                ConnectionStatusIndicator(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    connectionStatus = chatUiState.connectionStatus
                )
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val isFromMe = message.direction == "outgoing"
    val backgroundColor = if (isFromMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isFromMe) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = backgroundColor,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.content.body,
                color = if (isFromMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun MessageInput(onSendClick: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            label = { Text("Введите сообщение") }
        )
        Spacer(Modifier.width(8.dp))
        IconButton(
            onClick = {
                if (text.isNotBlank()) {
                    onSendClick(text)
                    text = ""
                }
            }
        ) {
            Icon(Icons.Filled.Send, contentDescription = "Отправить")
        }
    }
}

@Composable
fun ConnectionStatusIndicator(
    modifier: Modifier = Modifier,
    connectionStatus: ConnectionState
) {
    val (backgroundColor, text) = when (connectionStatus) {
        is ConnectionState.Connected -> Color(0xFF4CAF50) to "Подключено"
        is ConnectionState.Connecting -> Color(0xFFFF9800) to "Подключение..."
        is ConnectionState.Disconnected -> Color(0xFF757575) to "Не в сети"
        is ConnectionState.Error -> Color(0xFFF44336) to "Ошибка: ${connectionStatus.message}"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall
        )
    }
}