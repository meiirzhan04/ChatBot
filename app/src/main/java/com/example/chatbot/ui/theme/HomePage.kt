import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.chatbot.MessageModel
import com.example.chatbot.viewmodel.ChatViewModel


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun MainChat(viewModel: ChatViewModel) {
    var message by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        loadInitialMessages(
            messageList = viewModel.messageList,
            saveAllMessages = viewModel::saveAllMessages
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.saveAllMessagesOnExit()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            ChatTopBar(viewModel = viewModel)
        },
        bottomBar = {
            ChatInputField(
                message = message,
                onMessageChange = { message = it },
                onSendMessage = {
                    if (message.isNotBlank()) {
                        viewModel.sendMessage(message)
                        message = ""
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            MessageList(
                messageList = viewModel.messageList
            )

        }
    }
}

fun loadInitialMessages(
    messageList: MutableList<MessageModel>,
    saveAllMessages: () -> Unit
) {
    if (messageList.isEmpty()) {
        saveAllMessages()
    }
}

@Composable
fun ChatTopBar(viewModel: ChatViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "",
                tint = Color.Black
            )
        }
        Text(
            text = "Chat Bot",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        IconButton(
            onClick = {
                viewModel.clearMessage()
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "",
                tint = Color.Black
            )
        }
    }
}

@Composable
fun ChatInputField(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        HorizontalDivider(color = Color(0xFFDAE8E7), thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text(text = "Write your message", color = Color.Gray) },
            textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
            trailingIcon = {
                Box(
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .background(Color(0xFF_20A090))
                ) {
                    IconButton(onClick = onSendMessage, enabled = message.isNotBlank()) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.Blue,
                focusedContainerColor = Color(0xFF_F3F6F6),
                unfocusedContainerColor = Color(0xFF_F3F6F6),
            ),
            singleLine = true,
            shape = RoundedCornerShape(24.dp)
        )
    }
}
@Composable
fun MessageList(messageList: List<MessageModel>) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        reverseLayout = false,
    ) {
        items(messageList.size) { message ->
            val message = messageList[message]
            MessageRow(messageModel = message)
        }
    }
    LaunchedEffect(messageList.size) {
        listState.animateScrollToItem(messageList.size - 1)

    }
}

@Composable
fun MessageRow(messageModel: MessageModel) {
    val isModel = messageModel.role == "model"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isModel) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(if (isModel) Color(0xFF_F2F7FB) else Color(0xFF_20A090))
                .padding(16.dp)
        ) {
            Text(
                text = messageModel.message,
                style = TextStyle(color = if (isModel) Color.Black else Color.White, fontSize = 16.sp, fontWeight = FontWeight.Normal, letterSpacing = 0.5.sp)
            )
        }
    }
}
