package com.example.chatbot

import MainChat
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.chatbot.ui.theme.ChatBotTheme
import com.example.chatbot.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        setContent {
            ChatBotTheme {
                MainChat(viewModel = chatViewModel)
            }
        }
    }
}