package com.adriano.gfucoroutines.chat

data class MessageData(val userId: Int, val messageId: Int)
data class ChatData(val chatMessages: List<MessageData>)

data class User(val id: Int, val name: String, val avatarUrl: String)
data class Message(val id: Int, val text: String, val timestamp: Long)

sealed class ChatItemUI {
    data class Success(val user: User, val message: Message) : ChatItemUI()
    data class Error(val messageData: MessageData) : ChatItemUI()
}
