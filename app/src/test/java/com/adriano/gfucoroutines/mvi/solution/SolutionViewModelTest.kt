package com.adriano.gfucoroutines.mvi.solution

import app.cash.turbine.test
import com.adriano.gfucoroutines.mvi.ExerciseIntent
import com.adriano.gfucoroutines.mvi.ExerciseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SolutionViewModelTest {

    @Test
    fun `handleCalculateData emits Loading then Success`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val viewModel = SolutionViewModel()
            
            viewModel.state.test {
                assertEquals(ExerciseState.Idle, awaitItem())
                
                viewModel.processIntent(ExerciseIntent.CalculateDataIntent)
                
                assertEquals(ExerciseState.Loading, awaitItem())
                
                advanceTimeBy(1001)
                
                assertEquals(ExerciseState.Success("Calculated: 42"), awaitItem())
                
                cancelAndIgnoreRemainingEvents()
            }
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `handleFetchUser with valid ID emits Loading then Success`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val viewModel = SolutionViewModel()
            
            viewModel.state.test {
                assertEquals(ExerciseState.Idle, awaitItem())
                
                viewModel.processIntent(ExerciseIntent.FetchUserIntent(1))
                
                assertEquals(ExerciseState.Loading, awaitItem())
                
                val successState = awaitItem()
                assertTrue(successState is ExerciseState.Success)
                assertTrue((successState as ExerciseState.Success).message.contains("User:"))
                
                cancelAndIgnoreRemainingEvents()
            }
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `handleFetchUser with invalid ID emits Loading then Error`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val viewModel = SolutionViewModel()
            
            viewModel.state.test {
                assertEquals(ExerciseState.Idle, awaitItem())
                
                viewModel.processIntent(ExerciseIntent.FetchUserIntent(-1))
                
                assertEquals(ExerciseState.Loading, awaitItem())
                
                val errorState = awaitItem()
                assertTrue(errorState is ExerciseState.Error)
                assertEquals("Invalid ID", (errorState as ExerciseState.Error).message)
                
                cancelAndIgnoreRemainingEvents()
            }
        } finally {
            Dispatchers.resetMain()
        }
    }
}
