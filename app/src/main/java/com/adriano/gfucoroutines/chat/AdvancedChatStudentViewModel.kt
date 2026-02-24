package com.adriano.gfucoroutines.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// TODO 1: Observe the ChatUiState models (Loading, Success, Error)
// sealed class ChatUiState

class AdvancedChatStudentViewModel : ViewModel() {

    private val api = FakeChatApi()
    private val db = FakeChatDb()

    // TODO 2: Setup MutableStateFlow for the search query (initial value "")
    // val searchQuery = ...

    // TODO 3: Create an internal MutableStateFlow to hold the loaded `List<ChatItemUI>?`
    // private val _rawChatItems = ...

    // TODO 4: Use `combine` with `debounce` to combine `_rawChatItems` and `searchQuery`
    // Filter the items based on the search query. If query is blank, show all.
    // Return a `StateFlow<ChatUiState>`.
    
    // Placeholder to keep the code compiling before TODO 4 is done
    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun loadChatData() {
        viewModelScope.launch {
            try {
                // TODO 5: Fetch ChatData from the API
                
                // TODO 6: Call processMessagesConcurrently
                
                // TODO 7: Update _rawChatItems with the result
            } catch (e: Exception) {
                // Handle fatal loading error
            }
        }
    }

    private suspend fun processMessagesConcurrently(messages: List<MessageData>): List<ChatItemUI> {
        // TODO 8: Convert `messages` to Flow (`asFlow()`)
        // TODO 9: Use `flatMapMerge(concurrency = 4)` to limit parallel processing to 4 items at a time
        // TODO 10: Inside `flatMapMerge`, call `loadMessageDetails` and emit the result
        // TODO 11: Collect the results back into a `List` (`toList()`)
        
        return emptyList() // Placeholder
    }

    private suspend fun loadMessageDetails(messageData: MessageData): ChatItemUI {
        // TODO 12: Use `supervisorScope` (instead of `coroutineScope`) to wrap parallel operations 
        // down below so that one failing child won't crash the entire scope.
        // TODO 13: Use `async` to fetch User and Message concurrently calling `getOrFetchUser` and `getOrFetchMessage`
        // TODO 14: Use `try-catch` when calling `await()`. If successful, return `ChatItemUI.Success`.
        // If an exception is thrown after all retries fail, return `ChatItemUI.Error`
        
        return ChatItemUI.Error(messageData) // Placeholder
    }

    private suspend fun getOrFetchUser(userId: Int): User {
        // TODO 15: Check `db.getUser(userId)`. If not null, return it.
        // TODO 16: Try to fetch `api.fetchUser(userId)` using a retry mechanism (max 2 retries). 
        // Wrap the API call in `withTimeout(2000L)` so it throws an exception and retries if it takes too long.
        // TODO 17: Save the fetched user to the DB and return it.
        
        throw NotImplementedError() 
    }

    private suspend fun getOrFetchMessage(messageId: Int): Message {
        // TODO 18: Check DB, fetch from API (with retry and `withTimeout(2000L)`), save to DB, and return.
        throw NotImplementedError() 
    }
}
