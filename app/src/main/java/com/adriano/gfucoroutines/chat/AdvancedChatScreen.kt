package com.adriano.gfucoroutines.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun AdvancedChatScreen(viewModel: AdvancedChatViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadChatData()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            label = { Text("Search by User or Text") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(16.dp))

        when (val state = uiState) {
            is ChatUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ChatUiState.Error -> {
                Text(
                    text = "Fatal Error: ${state.exception.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
            is ChatUiState.Success -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.items) { item ->
                        when (item) {
                            is ChatItemUI.Success -> ChatItemSuccessRow(item)
                            is ChatItemUI.Error -> ChatItemErrorRow(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatItemSuccessRow(item: ChatItemUI.Success) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.user.name.take(1)) // Just show first letter as avatar
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = item.user.name, style = MaterialTheme.typography.titleMedium)
                Text(text = item.message.text, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun ChatItemErrorRow(item: ChatItemUI.Error) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Failed to load message (${item.messageData.messageId}) for user ${item.messageData.userId}",
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}
