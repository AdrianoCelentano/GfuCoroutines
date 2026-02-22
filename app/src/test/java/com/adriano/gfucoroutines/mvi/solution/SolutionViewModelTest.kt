package com.adriano.gfucoroutines.mvi.solution

import app.cash.turbine.test
import com.adriano.gfucoroutines.mvi.ExerciseIntent
import com.adriano.gfucoroutines.mvi.ExerciseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SolutionViewModelTest {

    val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun before() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun after() {
        Dispatchers.resetMain()
    }

    @Test
    fun `handleCalculateData updates simpleState Loading then Success`() = runTest(testDispatcher) {
        val viewModel = SolutionViewModel()

        assertEquals(ExerciseState.Idle, viewModel.simpleState)

        viewModel.processIntent(ExerciseIntent.CalculateDataIntent)

//        runCurrent() // only needed for StandardTestDispatcher

        assertEquals(ExerciseState.Loading, viewModel.simpleState)
        
//        advanceTimeBy(1001) // advance after the delay
        advanceUntilIdle() // we only need the final state

        assertEquals(ExerciseState.Success("Calculated: 42"), viewModel.simpleState)
    }

    @Test
    fun `handleFetchUser with valid ID emits Loading then Success`() = runTest(testDispatcher) {

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
    }

    @Test
    fun `handleFetchUser with invalid ID emits Loading then Error`() = runTest(testDispatcher) {
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
    }
}
