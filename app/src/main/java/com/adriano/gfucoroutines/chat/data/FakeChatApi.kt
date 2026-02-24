package com.adriano.gfucoroutines.chat.data

import com.adriano.gfucoroutines.chat.model.ChatData
import com.adriano.gfucoroutines.chat.model.Message
import com.adriano.gfucoroutines.chat.model.MessageData
import com.adriano.gfucoroutines.chat.model.User
import kotlinx.coroutines.delay
import kotlin.random.Random

class FakeChatApi {

    private val names = listOf(
        "Adrian", "Sarah", "Michael", "Emma", "David",
        "Laura", "Kevin", "Julia", "Thomas", "Lisa"
    )

    private val phrases = listOf(
        "Hey, how are you doing today?",
        "Did you see the latest update on the project?",
        "Can we schedule a quick call for tomorrow?",
        "I just pushed the new commit. Could you review it?",
        "That sounds like a great plan! Let's do it.",
        "I'm running a bit late, start the meeting without me.",
        "Could you send me the link to that documentation?",
        "Awesome work on the new feature!",
        "Let me check and get back to you.",
        "Is there anything else we need to cover?"
    )

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
            name = names.getOrElse(userId - 1) { "User $userId" },
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
            text = phrases.random(),
            timestamp = System.currentTimeMillis() - Random.nextLong(
                0,
                86400000
            ) // Random time in last 24h
        )
    }
}
