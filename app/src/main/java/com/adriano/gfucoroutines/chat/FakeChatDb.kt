package com.adriano.gfucoroutines.chat

import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap

class FakeChatDb {
    private val usersCache = ConcurrentHashMap<Int, User>()
    private val messagesCache = ConcurrentHashMap<Int, Message>()

    suspend fun getUser(id: Int): User? {
        delay(50) // Simulate fast disk read
        return usersCache[id]
    }

    suspend fun saveUser(user: User) {
        delay(50) // Simulate fast disk write
        usersCache[user.id] = user
    }

    suspend fun getMessage(id: Int): Message? {
        delay(50) // Simulate fast disk read
        return messagesCache[id]
    }

    suspend fun saveMessage(message: Message) {
        delay(50) // Simulate fast disk write
        messagesCache[message.id] = message
    }
}
