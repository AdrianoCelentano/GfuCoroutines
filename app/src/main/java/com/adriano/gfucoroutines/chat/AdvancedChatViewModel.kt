package com.adriano.gfucoroutines.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withTimeout

sealed class ChatUiState {
    data object Loading : ChatUiState()
    data class Success(val items: List<ChatItemUI>) : ChatUiState()
    data class Error(val exception: Throwable) : ChatUiState()
}

class AdvancedChatViewModel : ViewModel() {

    private val api = FakeChatApi()
    private val db = FakeChatDb()

    // 1. Setup Query StateFlow for debouncing
    val searchQuery = MutableStateFlow("")

    // 2. Setup internal StateFlow for the raw chat items
    private val _rawChatItems = MutableStateFlow<List<ChatItemUI>?>(null)

    // 3. Combine raw items with debounced search query
    @OptIn(FlowPreview::class)
    val uiState: StateFlow<ChatUiState> = combine(
        _rawChatItems,
        searchQuery.debounce(300)
    ) { items, query ->
        if (items == null) return@combine ChatUiState.Loading

        val filteredItems = if (query.isBlank()) {
            items
        } else {
            items.filter { item ->
                when (item) {
                    is ChatItemUI.Success -> {
                        item.user.name.contains(query, ignoreCase = true) ||
                                item.message.text.contains(query, ignoreCase = true)
                    }
                    is ChatItemUI.Error -> false // Don't match errors on search
                }
            }
        }
        ChatUiState.Success(filteredItems)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ChatUiState.Loading
    )

    suspend fun loadChatData() {
        try {
            val chatData = api.fetchChatData()

            // Process the list with concurrency limit of 4
            val processedItems = processMessagesConcurrently(chatData.chatMessages)
            
            _rawChatItems.value = processedItems
        } catch (e: Exception) {
            // Handle fatal errors (like failing to fetch the initial ChatData)
            _rawChatItems.value = emptyList() // or introduce a dedicated fatal error state
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun processMessagesConcurrently(messages: List<MessageData>): List<ChatItemUI> {
        return messages.asFlow()
            .flatMapMerge(concurrency = 4) { messageData ->
                flow {
                    emit(loadMessageDetails(messageData))
                }
            }.toList()
    }

    private suspend fun loadMessageDetails(messageData: MessageData): ChatItemUI {
        return supervisorScope {
            // Load User and Message in parallel
            val userDeferred = async { getOrFetchUser(messageData.userId) }
            val messageDeferred = async { getOrFetchMessage(messageData.messageId) }

            try {
                val user = userDeferred.await()
                val message = messageDeferred.await()
                ChatItemUI.Success(user, message)
            } catch (e: Exception) {
                // If either fails after all retries, return the Error placeholder
                ChatItemUI.Error(messageData)
            }
        }
    }

    private suspend fun getOrFetchUser(userId: Int): User {
        // 1. Try DB first
        val cached = db.getUser(userId)
        if (cached != null) return cached

        // 2. Fetch from API with Retry Logic and Timeout
        val user = retry(2) { 
            withTimeout(2000L) {
                api.fetchUser(userId) 
            }
        }
        
        // 3. Save to DB
        db.saveUser(user)
        return user
    }

    private suspend fun getOrFetchMessage(messageId: Int): Message {
        // 1. Try DB first
        val cached = db.getMessage(messageId)
        if (cached != null) return cached

        // 2. Fetch from API with Retry Logic and Timeout
        val message = retry(2) { 
            withTimeout(2000L) {
                api.fetchMessage(messageId) 
            }
        }
        
        // 3. Save to DB
        db.saveMessage(message)
        return message
    }

    // Generic Retry Helper
    private suspend fun <T> retry(
        times: Int,
        initialDelay: Long = 100, // 0.1 second
        maxDelay: Long = 1000,    // 1 second
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        repeat(times - 1) {
            try {
                return block()
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException && e !is kotlinx.coroutines.TimeoutCancellationException) {
                    throw e
                }
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
            }
        }
        return block() // last attempt
    }
}
