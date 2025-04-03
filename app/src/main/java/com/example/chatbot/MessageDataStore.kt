package com.example.chatbot

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val USER_MESSAGE_KEY = stringPreferencesKey("user_message_key")
val BOT_MESSAGE_KEY = stringPreferencesKey("bot_message_key")

val Context.messageDataStore: DataStore<Preferences> by preferencesDataStore("message_data_store")

suspend fun saveMessages(context: Context, messages: Set<String>, key: Preferences.Key<String>) {
    context.messageDataStore.edit { preferences ->
        preferences[key] = messages.joinToString("|")
    }
}

fun getMessages(context: Context, key: Preferences.Key<String>): Flow<List<String>> {
    return context.messageDataStore.data.map { preferences ->
        preferences[key]?.split("|") ?: emptyList()
    }
}
