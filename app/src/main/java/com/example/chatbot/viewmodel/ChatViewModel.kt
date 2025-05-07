package com.example.chatbot.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.BOT_MESSAGE_KEY
import com.example.chatbot.MessageModel
import com.example.chatbot.USER_MESSAGE_KEY
import com.example.chatbot.saveMessages
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch


class ChatViewModel(application: Application) : AndroidViewModel(application) {
    val messageList = mutableStateListOf<MessageModel>()
    val generativeModel = GenerativeModel("gemini-2.0-flash", Constants.apiKey)

    fun saveAllMessages() {
        viewModelScope.launch {
            val userMessages = messageList
                .filter {
                    it.role == "user"
                }
                .map {
                    it.message
                }
                .toSet()
            val botMessages = messageList
                .filter {
                    it.role == "model"
                }
                .map {
                    it.message
                }.toSet()
            saveMessages(getApplication(), userMessages, USER_MESSAGE_KEY)
            saveMessages(getApplication(), botMessages, BOT_MESSAGE_KEY)
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun sendMessage(question: String) {
        if ("Who is the best English Teacher" == question) {
            messageList.add(MessageModel("Sandugash Abdyrakhimova", "model"))
            saveAllMessages()
            return
        } else {
            viewModelScope.launch {
                try {
                    messageList.add(MessageModel(question, "user"))
                    saveAllMessages()

                    messageList.add(MessageModel("Typing....", "model"))
                    saveAllMessages()

                    val chat = generativeModel.startChat(
                        history = messageList.map {
                            content(if (it.role == "user") "user" else "model") {
                                text(it.message)
                            }
                        }.toList()
                    )

                    val response = chat.sendMessage(question)

                    messageList.removeLast()

                    val responseText = response.text ?: "Empty response"
                    messageList.add(MessageModel(responseText, "model"))

                    saveAllMessages()

                } catch (e: Exception) {
                    messageList.removeLast()
                    messageList.add(MessageModel("Error: ${e.message}", "model"))
                    saveAllMessages()
                }
            }
        }
    }

    fun clearMessage() {
        viewModelScope.launch {
            messageList.clear()
            saveMessages(getApplication(), emptySet(), USER_MESSAGE_KEY)
            saveMessages(getApplication(), emptySet(), BOT_MESSAGE_KEY)
        }
    }

    fun saveAllMessagesOnExit() {
        saveAllMessages()
    }
}










