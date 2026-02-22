package com.adriano.gfucoroutines.mvi.solution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adriano.gfucoroutines.mvi.ExerciseIntent
import com.adriano.gfucoroutines.mvi.ExerciseState
import com.adriano.gfucoroutines.mvi.data.FakeApi
import com.adriano.gfucoroutines.mvi.data.LocationCallback
import com.adriano.gfucoroutines.mvi.data.LocationResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SolutionViewModel : ViewModel() {

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
        viewModelScope.launch {
            _state.value = ExerciseState.Loading
            
            try {
                // start both asynchronously
                val profileDeferred = async { fakeApi.fetchUserProfile() }
                val avatarDeferred = async { fakeApi.fetchUserAvatar() }
                
                // wait for both results
                val profile = profileDeferred.await()
                val avatar = avatarDeferred.await()
                
                _state.value = ExerciseState.Success("Profile: $profile, Avatar: $avatar")
            } catch (e: Exception) {
                _state.value = ExerciseState.Error(e.message ?: "Failed to load user data")
            }
        }
    }

    // EXERCISE 2: Job Hierarchy & Cancellation
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
            }
        }
    }

    // EXERCISE 3: Implicit Wait (coroutineScope)
    private fun handleImplicitWait() {
        viewModelScope.launch {
            _state.value = ExerciseState.Loading
            try {
                // coroutineScope waits for all child coroutines to complete automatically
                coroutineScope {
                    launch { fakeApi.uploadImage(1) }
                    launch { fakeApi.uploadImage(2) }
                    launch { fakeApi.uploadImage(3) }
                }
                _state.value = ExerciseState.Success("All images uploaded successfully!")
            } catch (e: Exception) {
                _state.value = ExerciseState.Error(e.message ?: "Upload failed")
            }
        }
    }

    // EXERCISE 4: Explicit Wait (job.join)
    private fun handleExplicitWait() {
        viewModelScope.launch {
            _state.value = ExerciseState.Loading
            
            val job = launch {
                fakeApi.syncBackgroundData()
            }
            
            // Explicitly wait for the specific job to finish before proceeding
            job.join()
            
            _state.value = ExerciseState.Success("Background sync completed! Proceeding...")
        }
    }

    // EXERCISE 5: Refactoring Callbacks (suspendCancellableCoroutine)
    private suspend fun getLocationSuspend(): LocationResponse {
        return suspendCancellableCoroutine { continuation ->
            fakeApi.legacyGetLocation(object : LocationCallback {
                override fun onSuccess(location: LocationResponse) {
                    if (continuation.isActive) continuation.resume(location)
                }
                
                override fun onError(error: Exception) {
                    if (continuation.isActive) continuation.resumeWithException(error)
                }
            })
        }
    }

    private fun handleRefactorCallback() {
        viewModelScope.launch {
            _state.value = ExerciseState.Loading
            try {
                val location = getLocationSuspend()
                _state.value = ExerciseState.Success("Location: Lat ${location.lat}, Lon ${location.lon}")
            } catch (e: Exception) {
                _state.value = ExerciseState.Error("Failed to get location")
            }
        }
    }

    // =========================================================================
    // MODULE 3: Coroutine Context und Dispatchers
    // =========================================================================

    // EXERCISE 6: withContext & Dispatchers
    private fun handleLoadDatabaseAndNetwork() {
        viewModelScope.launch {
            _state.value = ExerciseState.Loading

            try {
                // 1. Fetch data from the database using a blocking call, securely on IO dispatcher
                val dbResult = withContext(Dispatchers.IO) {
                    fakeApi.loadFromDatabaseBlocking()
                }

                // 2. Fetch network data
                val netResult = fakeApi.fetchNetworkData()
                
                // 3. Combine both and update the state
                _state.value = ExerciseState.Success("DB: $dbResult, Net: $netResult")
            } catch (e: Exception) {
                _state.value = ExerciseState.Error(e.message ?: "Operation failed")
            }
        }
    }

    // EXERCISE 7: Custom Scope Cancellation
    private fun handleCustomScopeCancellation() {
        _state.value = ExerciseState.Loading
        
        // Create a custom scope bound to a specific lifecycle
        val customScope = CoroutineScope(Dispatchers.Default + Job())
        
        customScope.launch {
            // This will run indefinitely if not cancelled
            fakeApi.syncComponentData()
        }
        
        // Simulate clicking 'cancel' or destroying the component after 2 seconds
        viewModelScope.launch {
            delay(2000)
            customScope.cancel() // Cancels all jobs spawned within customScope
            _state.value = ExerciseState.Success("Custom scope cancelled! Memory leak prevented.")
        }
    }

    // =========================================================================
    // MODULE 4: Fehlerbehandlung und Shared Mutable State
    // =========================================================================

    // EXERCISE 8: Shared Mutable State & Mutex
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
            
            _state.value = ExerciseState.Success("Final Counter Value: $sharedCounter")
        }
    }

    // EXERCISE 9: Exception Handling
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

                _state.value = ExerciseState.Success("Weather: $weather, News: $news, Ads: $ads")
            }
        }
    }

    // EXERCISE 10: Global Exception Handling (CoroutineExceptionHandler)
    private fun handleGlobalExceptionHandling() {
        _state.value = ExerciseState.Loading
        
        val handler = CoroutineExceptionHandler { _, exception ->
            _state.value = ExerciseState.Error("Caught globally: ${exception.message}")
        }
        
        // Launch with the handler attached. The exception thrown by fetchAds() 
        // will be caught by the handler instead of crashing the app.
        viewModelScope.launch(handler) {
            fakeApi.fetchAds()
            // This line won't be reached because fetchAds throws
        }
    }

    // =========================================================================
    // MODULE 5: Asynchronous Flow und Reaktive Programmierung
    // =========================================================================

    // EXERCISE 11: Flow & flatMapLatest (Setup in init block)
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
    private fun handleSearchQueryChanged(query: String) {
        // Emit the query to the flow to trigger flatMapLatest
        searchQueryFlow.tryEmit(query)
    }

    // EXERCISE 12: Flow Processing Pipeline (flatMapMerge)
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleFlowProcessingPipeline() {
        viewModelScope.launch {
            _state.value = ExerciseState.Loading
            
            try {
                // 1. Create a flow of raw IDs
                val idFlow = flow {
                    emit(1)
                    emit(2)
                    emit(3)
                }
                
                val results = mutableListOf<String>()
                
                idFlow
                    // 2. Intermediate operators
                    .filter { it > 0 }
                    .map { id -> id * 10 }
                    // 3. Concurrently fetch details for each translated ID
                    .flatMapMerge { translatedId ->
                        flow {
                            val details = fakeApi.fetchUserDetails(translatedId)
                            emit(details)
                        }
                    }
                    // 4. Collect results
                    .collect { profile ->
                        results.add(profile)
                    }
                    
                _state.value = ExerciseState.Success("Fetched ${results.size} profiles concurrently!")
            } catch (e: Exception) {
                _state.value = ExerciseState.Error(e.message ?: "Pipeline failed")
            }
        }
    }

    // =========================================================================
    // MODULE 6: Testing Coroutines
    // =========================================================================

    // EXERCISE 13: Unit Testing basic Coroutines
    // Implemented for testing
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
    // Implemented for testing
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
