package com.adriano.gfucoroutines.mvi.data.chat

import app.cash.turbine.test
import com.adriano.gfucoroutines.chat.AdvancedChatViewModel
import com.adriano.gfucoroutines.chat.ChatUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdvancedChatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchQuery triggers debounced filter on uiState`() = runTest(testDispatcher) {
        val viewModel = AdvancedChatViewModel()

        viewModel.uiState.test {
            // Initial state is Loading
            val initial = awaitItem()
            assertEquals(ChatUiState.Loading, initial)

            // Trigger load
            viewModel.loadChatData()

            // Advance time to allow the API to return the base list (1000ms delay in FakeApi)
            // Plus time for processing at least some items
            advanceTimeBy(5000)

            // Should emit loaded state (since query is empty initially)
            val fullListState = awaitItem()
            assertTrue(fullListState is ChatUiState.Success)
            val originalSize = (fullListState as ChatUiState.Success).items.size
            
            // Set query
            viewModel.searchQuery.value = "User 1"

            // Advance time but LESS than debounce period
            advanceTimeBy(100)
            expectNoEvents() // Debounce hasn't triggered yet

            // Advance past debounce period
            advanceTimeBy(200)

            // Now we should receive the filtered list
            val filteredState = awaitItem()
            assertTrue(filteredState is ChatUiState.Success)
            val filteredSize = (filteredState as ChatUiState.Success).items.size
            
            assertTrue("Filtered list ($filteredSize) should be smaller than original ($originalSize)", filteredSize < originalSize)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
