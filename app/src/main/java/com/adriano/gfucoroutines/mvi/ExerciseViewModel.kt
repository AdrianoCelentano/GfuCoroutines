package com.adriano.gfucoroutines.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adriano.gfucoroutines.mvi.data.FakeApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel : ViewModel() {

    private val fakeApi = FakeApi()

    private val _state = MutableStateFlow<ExerciseState>(ExerciseState.Idle)
    val state: StateFlow<ExerciseState> = _state.asStateFlow()

    fun processIntent(intent: ExerciseIntent) {
        when (intent) {
            is ExerciseIntent.LoadUserDataIntent -> handleLoadUserData()
            is ExerciseIntent.LoadDatabaseAndNetworkIntent -> handleLoadDatabaseAndNetwork()
            is ExerciseIntent.CancelOngoingWorkIntent -> handleCancelOngoingWork()
            is ExerciseIntent.IncrementCounterIntent -> handleIncrementCounter()
            is ExerciseIntent.SearchQueryChangedIntent -> handleSearchQueryChanged(intent.query)
            is ExerciseIntent.LoadRiskyDataIntent -> handleLoadRiskyData()
        }
    }

    // =========================================================================
    // EXERCISE 1: async / await
    // =========================================================================
    private fun handleLoadUserData() {
        // TODO: Load the user profile (`fakeApi.fetchUserProfile()`) and
        // the user avatar (`fakeApi.fetchUserAvatar()`) in PARALLEL.
        // Wait for both to finish, then update the state with the combined result:
        // _state.value = ExerciseState.Success("Profile: \$profile, Avatar: \$avatar")
    }

    // =========================================================================
    // EXERCISE 2: withContext & Dispatchers
    // =========================================================================
    private fun handleLoadDatabaseAndNetwork() {

        // TODO: 1. Fetch data from the database using `fakeApi.loadFromDatabaseBlocking()`.
        // WARNING: This is a BLOCKING call (Thread.sleep). You MUST switch to the appropriate
        // Dispatcher so you don't block the Main thread!

        // TODO: 2. After getting the DB data, fetch network data using `fakeApi.fetchNetworkData()`.

        // TODO: 3. Combine both and update the state:
        // _state.value = ExerciseState.Success("DB: \$dbResult, Net: \$netResult")

    }

    // =========================================================================
    // EXERCISE 3: Job Hierarchy & Cancellation
    // =========================================================================

    private fun handleCancelOngoingWork() {
        // Scenario: A user starts a download, but clicks a button again to cancel it.

        // TODO: 1. If `downloadJob` is already running, cancel it.
        // TODO: 2. Start a new coroutine on 'viewModelScope'
        // Inside the coroutine:
        //   - Update state to Loading
        //   - Call `fakeApi.downloadLargeFile()`
        //   - Update state to Success("Download Complete!") upon completion.
    }

    // =========================================================================
    // EXERCISE 4: Shared Mutable State & Mutex
    // =========================================================================

    private fun handleIncrementCounter() {

        // TODO create a variable sharedCounter
        // launch 100 coroutines to increase the counter
        // use a Mutex or a single Thread approach
    }

    // =========================================================================
    // EXERCISE 5: Flow & flatMapLatest
    // =========================================================================

    // TODO: Setup a MutableSharedFlow or MutableStateFlow for the search query and
    // use .flatMapLatest { fakeApi.fetchSearchResults(it) } to fetch results.
    // Collect the flow and update the `_state`.

    private fun handleSearchQueryChanged(query: String) {
        // TODO: Emit the new query to your query Flow so flatMapLatest can process it.
    }

    // =========================================================================
    // EXERCISE 6: Exception Handling
    // =========================================================================
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
}
