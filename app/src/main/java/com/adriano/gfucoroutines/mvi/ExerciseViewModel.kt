package com.adriano.gfucoroutines.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adriano.gfucoroutines.mvi.data.FakeApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class ExerciseViewModel : ViewModel() {

    private val fakeApi = FakeApi()

    private val _state = MutableStateFlow<ExerciseState>(ExerciseState.Idle)
    val state: StateFlow<ExerciseState> = _state.asStateFlow()

    fun processIntent(intent: ExerciseIntent) {
        when (intent) {
            // Modul 2
            is ExerciseIntent.LoadUserDataIntent -> handleLoadUserData()
            is ExerciseIntent.CancelOngoingWorkIntent -> handleCancelOngoingWork()
            is ExerciseIntent.ImplicitWaitIntent -> handleImplicitWait()
            is ExerciseIntent.ExplicitWaitIntent -> handleExplicitWait()
            is ExerciseIntent.RefactorCallbackIntent -> handleRefactorCallback()

            // Modul 3
            is ExerciseIntent.LoadDatabaseAndNetworkIntent -> handleLoadDatabaseAndNetwork()
            is ExerciseIntent.CustomScopeCancellationIntent -> handleCustomScopeCancellation()

            // Modul 4
            is ExerciseIntent.IncrementCounterIntent -> handleIncrementCounter()
            is ExerciseIntent.LoadRiskyDataIntent -> handleLoadRiskyData()
            is ExerciseIntent.GlobalExceptionHandlingIntent -> handleGlobalExceptionHandling()

            // Modul 5
            is ExerciseIntent.SearchQueryChangedIntent -> handleSearchQueryChanged(intent.query)
            is ExerciseIntent.FlowProcessingPipelineIntent -> handleFlowProcessingPipeline()

            // Modul 6
            is ExerciseIntent.CalculateDataIntent -> handleCalculateData()
            is ExerciseIntent.FetchUserIntent -> handleFetchUser(intent.userId)
        }
    }

    // =========================================================================
    // MODULE 2: Grundlagen der Kotlin Coroutinen
    // =========================================================================

    // EXERCISE 1: async / await
    private fun handleLoadUserData() {
        // TODO: Load the user profile (`fakeApi.fetchUserProfile()`) and
        // the user avatar (`fakeApi.fetchUserAvatar()`) in PARALLEL.
        // Wait for both to finish, then update the state with the combined result:
        // _state.value = ExerciseState.Success("Profile: \$profile, Avatar: \$avatar")
    }

    // EXERCISE 2: Job Hierarchy & Cancellation
    private fun handleCancelOngoingWork() {
        // Scenario: A user starts a download, but clicks a button again to cancel it.

        // TODO: 1. If `downloadJob` is already running, cancel it.
        // TODO: 2. Start a new coroutine on 'viewModelScope'
        // Inside the coroutine:
        //   - Update state to Loading
        //   - Call `fakeApi.downloadLargeFile()`
        //   - Update state to Success("Download Complete!") upon completion.
    }

    // EXERCISE 3: Implicit Wait (coroutineScope)
    private fun handleImplicitWait() {
        // Scenario: A parent process needs to upload three images concurrently.
        // It should automatically complete ONLY when all three children finish.

        // TODO: Use `coroutineScope { ... }` to launch 3 child coroutines
        // calling `fakeApi.uploadImage(id)`.
        // TODO: After the `coroutineScope` block, update the state to Success.
    }

    // EXERCISE 4: Explicit Wait (job.join)
    private fun handleExplicitWait() {
        // Scenario: Start a background syncing job explicitly and wait for it to finish.

        // TODO: Start a new coroutine using `launch` and assign it to a variable `job`.
        // Inside the coroutine, call `fakeApi.syncBackgroundData()`.
        // TODO: Call `job.join()` to wait for it.
        // TODO: Update the state to Success after it completes.
    }

    // EXERCISE 5: Refactoring Callbacks (suspendCancellableCoroutine)
    private fun handleRefactorCallback() {
        // Scenario: You have an old third-party library that fetches location data using a callback.

        // TODO: Create a suspend function that uses `suspendCancellableCoroutine`
        // to wrap `fakeApi.legacyGetLocation(...)`.
        // TODO: Call your new suspend function here and update state to Success with the result.
    }

    // =========================================================================
    // MODULE 3: Coroutine Context und Dispatchers
    // =========================================================================

    // EXERCISE 6: withContext & Dispatchers
    private fun handleLoadDatabaseAndNetwork() {
        // TODO: 1. Fetch data from the database using `fakeApi.loadFromDatabaseBlocking()`.
        // WARNING: This is a BLOCKING call (Thread.sleep). You MUST switch to the appropriate
        // Dispatcher so you don't block the Main thread!

        // TODO: 2. After getting the DB data, fetch network data using `fakeApi.fetchNetworkData()`.

        // TODO: 3. Combine both and update the state:
        // _state.value = ExerciseState.Success("DB: \$dbResult, Net: \$netResult")
    }

    // EXERCISE 7: Custom Scope Cancellation
    private fun handleCustomScopeCancellation() {
        // Scenario: An activity or specific component has its own `CoroutineScope`.

        // TODO: Create a custom `CoroutineScope` (e.g., `CoroutineScope(Dispatchers.Default + Job())`).
        // TODO: Launch `fakeApi.syncComponentData()` inside this scope.
        // TODO: Delay for 2 seconds (`delay(2000)`), then cancel the scope to stop the syncing task.
        // TODO: Update state to Success("Scope cancelled successfully").
    }

    // =========================================================================
    // MODULE 4: Fehlerbehandlung und Shared Mutable State
    // =========================================================================

    // EXERCISE 8: Shared Mutable State & Mutex
    private fun handleIncrementCounter() {
        // TODO create a variable sharedCounter
        // launch 100 coroutines to increase the counter
        // use a Mutex or a single Thread approach
    }

    // EXERCISE 9: Exception Handling
    private fun handleLoadRiskyData() {
        // Scenario: We want to load Weather, News, and Ads at the same time.
        // Notice that `fetchAds()` will throw an Exception!
        // We want Weather and News to STILL load successfully even if Ads fail.

        // TODO: Use `supervisorScope` so the failure of one child doesn't cancel the others.
        // TODO: Inside the scope, use `async` to fetch Weather, News, and Ads.
        // TODO: Use try-catch around the `await()` call for the Ads to prevent a crash.

        // Expected result if done right:
        // _state.value = ExerciseState.Success("Weather: \$w, News: \$n, Ads Failed")
    }

    // EXERCISE 10: Global Exception Handling (CoroutineExceptionHandler)
    private fun handleGlobalExceptionHandling() {
        // Scenario: You execute a "fire-and-forget" analytics upload using `launch`.
        // If it fails, the app shouldn't crash.

        // TODO: Create a `CoroutineExceptionHandler` to catch the exception.
        // TODO: Launch a coroutine ON viewModelScope with this handler and call `fakeApi.fetchAds()`.
        // TODO: Inside the exception handler, update the state to Error with the caught exception message.
    }

    // =========================================================================
    // MODULE 5: Asynchronous Flow und Reaktive Programmierung
    // =========================================================================

    // EXERCISE 11: Flow & flatMapLatest
    private fun handleSearchQueryChanged(query: String) {
        // TODO: Setup a MutableSharedFlow or MutableStateFlow for the search query and
        // use .flatMapLatest { fakeApi.fetchSearchResults(it) } to fetch results.
        // Collect the flow and update the `_state`.
        // TODO: Emit the new query to your query Flow so flatMapLatest can process it.
    }

    // EXERCISE 12: Flow Processing Pipeline (flatMapMerge)
    private fun handleFlowProcessingPipeline() {
        // Scenario: Create a data pipeline using a flow builder to emit raw user IDs.

        // TODO: Use `flow { emit(...) }` to emit IDs (e.g., 1, 2, 3).
        // TODO: Use intermediate operators like `filter` or `map`.
        // TODO: Fetch detailed profiles for each valid ID concurrently using `flatMapMerge` and `fakeApi.fetchUserDetails`.
        // TODO: Collect the flow and update the state to Success.
    }

    // =========================================================================
    // MODULE 6: Testing Coroutines
    // =========================================================================

    // EXERCISE 13: Unit Testing basic Coroutines
    // TODO (Student): Check `ExerciseViewModelTest.kt` for your tasks!
    // This method is already implemented for you to test.
    var simpleState: ExerciseState = ExerciseState.Idle
        private set

    private fun handleCalculateData() {
        viewModelScope.launch {
            simpleState = ExerciseState.Loading
            delay(1000)
            simpleState = ExerciseState.Success("Calculated: 42")
        }
    }

    // EXERCISE 14: Unit Testing Exceptions and Flows (with Turbine)
    // TODO (Student): Check `ExerciseViewModelTest.kt` for your tasks!
    // This method is already implemented for you to test.
    private fun handleFetchUser(userId: Int) {
        viewModelScope.launch {
            _state.value = ExerciseState.Loading
            try {
                if (userId < 0) throw IllegalArgumentException("Invalid ID")
                val details = fakeApi.fetchUserDetails(userId)
                _state.value = ExerciseState.Success("User: $details")
            } catch (e: Exception) {
                _state.value = ExerciseState.Error(e.message ?: "Error fetching user")
            }
        }
    }
}
