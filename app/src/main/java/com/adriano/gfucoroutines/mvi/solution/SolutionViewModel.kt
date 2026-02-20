package com.adriano.gfucoroutines.mvi.solution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adriano.gfucoroutines.mvi.ExerciseIntent
import com.adriano.gfucoroutines.mvi.ExerciseState
import com.adriano.gfucoroutines.mvi.data.FakeApi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SolutionViewModel : ViewModel() {

    private val fakeApi = FakeApi()

    private val _state = MutableStateFlow<ExerciseState>(ExerciseState.Idle)
    val state: StateFlow<ExerciseState> = _state.asStateFlow()

    // Used for Exercise 5
    private val searchQueryFlow = MutableSharedFlow<String>(replay = 1)

    init {
        // Setup for EXERCISE 5
        viewModelScope.launch {
            searchQueryFlow
                .flatMapLatest { query ->
                    flow {
                        _state.value = ExerciseState.Loading
                        val result = fakeApi.fetchSearchResults(query)
                        emit(result)
                    }
                }
                .catch { e ->
                    // Handle any potential errors during the search
                    _state.value = ExerciseState.Error(e.message ?: "Unknown error")
                }
                .collect { result ->
                    _state.value = ExerciseState.Success(result)
                }
        }
    }

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
    // EXERCISE 1: async / await (Topic 2.2.2 in PDF & asyncAwaitCoroutine in Main.kt)
    // =========================================================================
    private fun handleLoadUserData() {
        viewModelScope.launch {
            _state.value = ExerciseState.Loading
            
            try {
                // start both asynchronously
                val profileDeferred = async { fakeApi.fetchUserProfile() }
                val avatarDeferred = async { fakeApi.fetchUserAvatar() }
                
                // wait for both results
                val profile = profileDeferred.await()
                val avatar = avatarDeferred.await()
                
                _state.value = ExerciseState.Success("Profile: \$profile, Avatar: \$avatar")
            } catch (e: Exception) {
                _state.value = ExerciseState.Error(e.message ?: "Failed to load user data")
            }
        }
    }

    // =========================================================================
    // EXERCISE 2: withContext & Dispatchers (Topic 3.2 in PDF & contextSwitch in Main.kt)
    // =========================================================================
    private fun handleLoadDatabaseAndNetwork() {
        viewModelScope.launch {
            _state.value = ExerciseState.Loading

            try {
                // 1. Fetch data from the database using a blocking call, securely on IO dispatcher
                val dbResult = withContext(Dispatchers.IO) {
                    fakeApi.loadFromDatabaseBlocking()
                }
                
                // 2. Fetch network data (this is safe on the Main dispatcher because fakeApi internally delays safely)
                val netResult = fakeApi.fetchNetworkData()
                
                // 3. Combine both and update the state
                _state.value = ExerciseState.Success("DB: \$dbResult, Net: \$netResult")
            } catch (e: Exception) {
                _state.value = ExerciseState.Error(e.message ?: "Operation failed")
            }
        }
    }

    // =========================================================================
    // EXERCISE 3: Job Hierarchy & Cancellation (Main.kt examples)
    // =========================================================================
    private var downloadJob: Job? = null

    private fun handleCancelOngoingWork() {
        // 1. Cancel ongoing job if it exists
        downloadJob?.cancel()
        
        // 2. Start a new job
        downloadJob = viewModelScope.launch {
            _state.value = ExerciseState.Loading
            try {
                fakeApi.downloadLargeFile()
                _state.value = ExerciseState.Success("Download Complete!")
            } catch (e: CancellationException) {
                _state.value = ExerciseState.Success("Download Cancelled!")
                // Rethrowing CancellationException is good practice if we were using a CoroutineExceptionHandler
                // but for our simple state setup, catching it here allows us to update the UI State.
                // Depending on the use case, it could also be swallowed or rethrown.
            }
        }
    }

    // =========================================================================
    // EXERCISE 4: Shared Mutable State & Mutex (Topic 4.2 in PDF)
    // =========================================================================
    private var sharedCounter = 0
    private val mutex = Mutex()

    private fun handleIncrementCounter() {
        viewModelScope.launch {
            _state.value = ExerciseState.Loading
            sharedCounter = 0

            val jobs = List(100) {
                launch(Dispatchers.Default) {
                    // Protect the shared mutable state with a Mutex
                    mutex.withLock {
                        sharedCounter++
                    }
                }
            }
            
            jobs.forEach { it.join() }
            
            _state.value = ExerciseState.Success("Final Counter Value: \$sharedCounter")
        }
    }

    // =========================================================================
    // EXERCISE 5: Flow & flatMapLatest (Topic 5.3 in PDF)
    // =========================================================================
    private fun handleSearchQueryChanged(query: String) {
        // Emit the query to the flow to trigger flatMapLatest
        searchQueryFlow.tryEmit(query)
    }

    // =========================================================================
    // EXERCISE 6: Exception Handling (Topic 4.1 in PDF)
    // =========================================================================
    private fun handleLoadRiskyData() {
        viewModelScope.launch {
            _state.value = ExerciseState.Loading

            // We use supervisorScope so the failure of Ads does not cancel the Weather/News
            supervisorScope {
                val weatherDeferred = async { fakeApi.fetchWeather() }
                val newsDeferred = async { fakeApi.fetchNews() }
                val adsDeferred = async { fakeApi.fetchAds() }

                val weather = try { weatherDeferred.await() } catch (e: Exception) { "Weather Error" }
                val news = try { newsDeferred.await() } catch (e: Exception) { "News Error" }
                
                val ads = try { 
                    adsDeferred.await() 
                } catch (e: Exception) { 
                    "Ads Failed" 
                }

                _state.value = ExerciseState.Success("Weather: \$weather, News: \$news, Ads: \$ads")
            }
        }
    }
}
