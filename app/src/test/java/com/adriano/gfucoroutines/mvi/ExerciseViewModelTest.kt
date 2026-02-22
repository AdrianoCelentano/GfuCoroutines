package com.adriano.gfucoroutines.mvi

import app.cash.turbine.test
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
class ExerciseViewModelTest {

    // MODULE 6: Testing Coroutines
    
    @Test
    fun `handleCalculateData emits Loading then Success`() = runTest {
        // TODO 1: Set the Main dispatcher to a StandardTestDispatcher using this test's `testScheduler`.
        // This ensures delay() and advanceTimeBy() use the same virtual clock.
        // Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        
        try {
            // TODO 2: Initialize your ViewModel
            
            // TODO 3: Send CalculateDataIntent to the ViewModel
            
            // TODO 4: Use Turbine (state.test { ... }) to verify:
            // - the initial Idle state is emitted
            // - the Loading state is emitted
            // - advance time by 1001ms using advanceTimeBy()
            // - the Success("Calculated: 42") state is emitted
            // - cancel and ignore any remaining events.
            
        } finally {
            // TODO 5: Reset the Main dispatcher
            // Dispatchers.resetMain()
        }
    }

    @Test
    fun `handleFetchUser with valid ID emits Loading then Success`() = runTest {
        // TODO 6: Apply the same Dispatchers.setMain setup
        
        try {
            // TODO 7: Initialize your ViewModel
            // TODO 8: Send FetchUserIntent with a valid ID (e.g. 1)
            // TODO 9: Use Turbine to verify the emissions (Idle -> Loading -> Success)
            
        } finally {
            // TODO 10: Reset the Main dispatcher
        }
    }

    @Test
    fun `handleFetchUser with invalid ID emits Loading then Error`() = runTest {
        // TODO 11: Apply the same setup
        try {
            // TODO 12: Send FetchUserIntent with an invalid ID (e.g. -1)
            // TODO 13: Use Turbine to verify the emissions (Idle -> Loading -> Error)
            // Verify that the Error state has the expected message ("Invalid ID").
        } finally {
            // TODO 14: Reset the Main dispatcher
        }
    }
}
