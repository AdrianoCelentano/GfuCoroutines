package com.adriano.gfucoroutines.chat

import kotlinx.coroutines.delay
import kotlin.random.Random

class FakeChatApi {

    // Returns a list of 50 chat messages
    suspend fun fetchChatData(): ChatData {
        delay(1000) // Simulate network delay
        val messages = (1..50).map { i ->
            MessageData(
                userId = (1..10).random(), // 10 possible users
                messageId = i
            )
        }
        return ChatData(messages)
    }

    suspend fun fetchUser(userId: Int): User {
        delay(Random.nextLong(300, 1500)) // Variable delay
        
        // Simulate random API failure (approx 10% chance)
        if (Random.nextFloat() < 0.1f) {
            throw Exception("Network Error fetching user $userId")
        }

        return User(
            id = userId,
            name = "User $userId",
            avatarUrl = "https://i.pravatar.cc/150?u=$userId"
        )
    }

    suspend fun fetchMessage(messageId: Int): Message {
        delay(Random.nextLong(300, 1500)) // Variable delay
        
        // Simulate random API failure (approx 10% chance)
        if (Random.nextFloat() < 0.1f) {
            throw Exception("Network Error fetching message $messageId")
        }

        return Message(
            id = messageId,
            text = "This is the content for message $messageId. Hello world!",
            timestamp = System.currentTimeMillis() - Random.nextLong(0, 86400000) // Random time in last 24h
        )
    }
}
