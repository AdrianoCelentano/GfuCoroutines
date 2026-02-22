package com.adriano.gfucoroutines.mvi

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
class ExerciseViewModelTest {

    // MODULE 6: Testing Coroutines
    
    // TODO 1: Create a class-level `testDispatcher` using `StandardTestDispatcher()`
    // val testDispatcher = StandardTestDispatcher()

    // TODO 2: Create a @Before method to set the Main dispatcher
    // @Before
    // fun setUp() {
    //     Dispatchers.setMain(testDispatcher)
    // }

    // TODO 3: Create an @After method to reset the Main dispatcher
    // @After
    // fun tearDown() {
    //     Dispatchers.resetMain()
    // }
    
    @Test
    fun `handleCalculateData updates simpleState Loading then Success`() = runTest { // TODO 4: Pass `testDispatcher` into `runTest(...)`
        
        // TODO 5: Initialize your ViewModel
        
        // TODO 6: Assert that `viewModel.simpleState` is initially `ExerciseState.Idle`
        
        // TODO 7: Send CalculateDataIntent to the ViewModel

        // TODO 8: Call `runCurrent()` to execute pending coroutines up to the first suspension point
        
        // TODO 9: Assert that `viewModel.simpleState` is now `ExerciseState.Loading`
        
        // TODO 10: Advance time by 1001ms using advanceTimeBy()
        
        // TODO 11: Assert that `viewModel.simpleState` is now `ExerciseState.Success("Calculated: 42")`
    }

    @Test
    fun `handleFetchUser with valid ID emits Loading then Success`() = runTest { // TODO 12: Pass `testDispatcher`
        // TODO 13: Initialize your ViewModel
        // TODO 14: Send FetchUserIntent with a valid ID (e.g. 1)
        // TODO 15: Use Turbine (viewModel.state.test { ... }) to verify the emissions (Idle -> Loading -> Success)
    }

    @Test
    fun `handleFetchUser with invalid ID emits Loading then Error`() = runTest { // TODO 16: Pass `testDispatcher`
        // TODO 17: Send FetchUserIntent with an invalid ID (e.g. -1)
        // TODO 18: Use Turbine to verify the emissions (Idle -> Loading -> Error)
        // Verify that the Error state has the expected message ("Invalid ID").
    }
}
