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

    // MODUL 6: Testen von Coroutines
    
    // TODO 1: Erstelle einen `testDispatcher` auf Klassenebene mit `StandardTestDispatcher()`

    // TODO 2: Erstelle eine @Before-Methode, um den Main-Dispatcher zu setzen

    // TODO 3: Erstelle eine @After-Methode, um den Main-Dispatcher zurückzusetzen

    @Test
    fun `handleCalculateData updates simpleState Loading then Success`() = runTest { // TODO 4: Übergib `testDispatcher` an `runTest(...)`
        
        // TODO 5: Initialisiere dein ViewModel
        
        // TODO 6: Stelle sicher, dass `viewModel.simpleState` anfangs `ExerciseState.Idle` ist
        
        // TODO 7: Sende CalculateDataIntent an das ViewModel

        // TODO 8: Rufe `runCurrent()` auf, um ausstehende Coroutines bis zum ersten Unterbrechungspunkt (suspension point) auszuführen
        
        // TODO 9: Stelle sicher, dass `viewModel.simpleState` jetzt `ExerciseState.Loading` ist
        
        // TODO 10: Spule die Zeit mit advanceTimeBy() um 1001ms vor
        
        // TODO 11: Stelle sicher, dass `viewModel.simpleState` jetzt `ExerciseState.Success("Calculated: 42")` ist
    }

    @Test
    fun `handleFetchUser with valid ID emits Loading then Success`() = runTest { // TODO 12: Übergib `testDispatcher`
        // TODO 13: Initialisiere dein ViewModel
        // TODO 14: Sende FetchUserIntent mit einer gültigen ID (z. B. 1)
        // TODO 15: Verwende Turbine (viewModel.state.test { ... }), um die Emissionen zu überprüfen (Idle -> Loading -> Success)
    }

    @Test
    fun `handleFetchUser with invalid ID emits Loading then Error`() = runTest { // TODO 16: Übergib `testDispatcher`
        // TODO 17: Sende FetchUserIntent mit einer ungültigen ID (z. B. -1)
        // TODO 18: Verwende Turbine, um die Emissionen zu überprüfen (Idle -> Loading -> Error)
        // Stelle sicher, dass der Error-State die erwartete Nachricht hat ("Invalid ID").
    }
}
