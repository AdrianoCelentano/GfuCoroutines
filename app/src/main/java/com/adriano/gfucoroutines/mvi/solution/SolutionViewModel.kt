package com.adriano.gfucoroutines.mvi.solution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adriano.gfucoroutines.mvi.ExerciseIntent
import com.adriano.gfucoroutines.mvi.ExerciseState
import com.adriano.gfucoroutines.mvi.ExerciseState.Success
import com.adriano.gfucoroutines.mvi.data.FakeApi
import com.adriano.gfucoroutines.mvi.data.LocationCallback
import com.adriano.gfucoroutines.mvi.data.LocationResponse
import com.adriano.gfucoroutines.shared.runCatchingSuspending
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration.Companion.seconds

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
            is ExerciseIntent.FlowBasicsIntent -> handleFlowBasics()
            is ExerciseIntent.FlowOperatorsIntent -> handleFlowOperators()
            is ExerciseIntent.FlowExceptionHandlingIntent -> handleFlowExceptionHandling()
            is ExerciseIntent.FlowContextIntent -> handleFlowContext()
            is ExerciseIntent.FlowCombineIntent -> handleFlowCombine()
            is ExerciseIntent.SearchQueryChangedIntent -> handleSearchQueryChanged(intent.query)
            is ExerciseIntent.FlowProcessingPipelineIntent -> handleFlowProcessingPipeline()
            is ExerciseIntent.FlowStateSharedIntent -> handleFlowStateShared()
            is ExerciseIntent.FlowBufferingIntent -> handleFlowBuffering()
            is ExerciseIntent.FlowCallbackIntent -> handleFlowCallback()

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

                _state.value = Success("Profile: $profile, Avatar: $avatar")
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
                _state.value = Success("Download Complete!")
            } catch (e: CancellationException) {
                _state.value = Success("Download Cancelled!")
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
                _state.value = Success("All images uploaded successfully!")
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

            _state.value = Success("Background sync completed! Proceeding...")
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
                _state.value =
                    Success("Location: Lat ${location.lat}, Lon ${location.lon}")
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
                _state.value = Success("DB: $dbResult, Net: $netResult")
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
            _state.value = Success("Custom scope cancelled! Memory leak prevented.")
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

            _state.value = Success("Final Counter Value: $sharedCounter")
        }
    }

    // EXERCISE 9: Exception Handling
    private fun handleLoadRiskyData() {
        // HIER SIND 4 BEISPIELE ZUR EXCEPTION-BEHANDLUNG MIT ASYNC/AWAIT.
        // Kommentiere die Methoden ein/aus, um das Verhalten direkt zu testen.

        // BEISPIEL 1: Führt zum Absturz der App!
//        crashExampleAsyncWithOuterTryCatch()

        // BEISPIEL 2: Funktioniert ohne Absturz, dank Root-Coroutine (GlobalScope etc.).
//        successExampleWithRootCoroutine()

        // BEISPIEL 3: Funktioniert ohne Absturz, dank supervisorScope.
//        successExampleWithSupervisorScope()

        // BEISPIEL 4: Der sicherste und idiomatischste Weg (try-catch direkt in async).
//        successExampleWithInnerTryCatch()
    }

    /**
     * BEISPIEL 1: Warum dieser Code CRASHT (trotz try-catch)!
     *
     * Erklärung:
     * 1. `launch` startet eine Root-Coroutine in viewModelScope.
     * 2. `async` startet eine abhängige Kind-Coroutine.
     * 3. `fetchAds()` wirft eine Exception innerhalb der Kind-Coroutine (`async`).
     * 4. Bei normalen Coroutinen gilt die eiserne Regel der Structured Concurrency:
     *    Eine Exception im Kind eskaliert SOFORT zum Parent (`launch`).
     * 5. Der Parent (`launch`) bricht die gesamte Hierarchie ab und meldet
     *    den unkontrollierten Absturz an das System (-> App stürzt ab).
     * 6. Das `try-catch` um `a.await()` fängt zwar die Exception ab,
     *    aber der fatale Crash im Hintergrund ist zu diesem Zeitpunkt bereits im vollen Gange.
     */
    private fun crashExampleAsyncWithOuterTryCatch() {
        viewModelScope.launch { // Parent Coroutine
            val deferredAds = async { // Child Coroutine
                fakeApi.fetchAds() // Wirft Exception! Eskaliert SOFORT an Parent.
            }

            try {
                deferredAds.await() // Exception wird HIER geworfen und lokal gefangen...
            } catch (e: Exception) {
                // ... aber es ist zu spät! Der Parent ist wegen dem kaputten Kind bereits gecrasht.
                _state.value = ExerciseState.Error("Fehler gefangen, aber App stürzt trotzdem ab!")
            }
        }
    }

    /**
     * BEISPIEL 2: Warum eine Root-Coroutine NICHT crasht.
     *
     * Erklärung:
     * 1. Wenn `async` direkt auf dem `viewModelScope` (oder einem anderen ungebundenen Scope)
     *    aufgerufen wird, agiert es als ROOT-Coroutine, NICHT als Kind-Coroutine.
     * 2. Eine Root-Coroutine hat keinen Parent, dem sie den Fehler "melden" und den sie
     *    in den Abgrund reißen könnte.
     * 3. Deshalb schluckt eine `async`-Root-Coroutine den Fehler komplett und wirft ihn
     *    NUR beim Aufruf von `await()`.
     * 4. Dadurch fängt unser `try-catch` den Fehler erfolgreich auf, ohne Crash.
     */
    private fun successExampleWithRootCoroutine() {
        // HIER: `async` wird OHNE ein umschließendes `launch` block direkt
        // als Root-Coroutine auf viewModelScope gestartet.
        val deferredAds = viewModelScope.async {
            fakeApi.fetchAds() // Wirft Exception, aber als Root-Coroutine!
        }

        viewModelScope.launch {
            try {
                deferredAds.await() // Exception wird HIER geworfen...
            } catch (e: Exception) {
                // ...und erfolgreich gefangen. Kein übergeordneter Job, der crashen könnte!
                _state.value = ExerciseState.Error("Fehler sicher durch Root-Coroutine gefangen!")
            }

            launch {
                delay(500)
                _state.value =
                    ExerciseState.Success("Andere Aufgaben können sicher ausgeführt werden")
            }
        }
    }

    /**
     * BEISPIEL 3: Warum `supervisorScope` den Crash verhindert.
     *
     * Erklärung:
     * 1. `supervisorScope` fügt einen speziellen SupervisorJob in die Hierarchie ein.
     * 2. Die Sonderregel eines SupervisorJobs: Fällt ein Kind aus, stört das den Parent NICHT!
     * 3. Das Kind (`async`) stirbt zwar, aber der Parent (`launch`) läuft ungestört weiter.
     * 4. Wir können die Exception bei `await()` nun sicher mit try-catch fangen,
     *    ohne dass die App abstürzt.
     */
    private fun successExampleWithSupervisorScope() {
        viewModelScope.launch { // Parent Coroutine
            supervisorScope { // Schützt den Parent vor Fehlern der Kinder
                val deferredAds = async { // Child Coroutine ist jetzt durch Supervisor geschützt
                    fakeApi.fetchAds() // Wirft Exception. Kind stirbt. Parent ist es aber egal!
                }

                try {
                    deferredAds.await() // Exception wird gefangen, alles gut!
                } catch (e: Exception) {
                    _state.value = ExerciseState.Error("Fehler abgefangen mit supervisorScope!")
                }

                // Wir können hier sicher weiterarbeiten (andere Coroutinen werden nicht beeinträchtigt)
                launch {
                    delay(500)
                    _state.value = ExerciseState.Success("Wetter und News fertig!")
                }
            }
        }
    }

    /**
     * BEISPIEL 4: Die sicherste Variante (Fehler gar nicht erst eskalieren lassen).
     *
     * Erklärung:
     * Wir schieben das try-catch direkt IN die Kind-Coroutine.
     * Dadurch schlägt `async` offiziell gar nicht erst fehl, sondern fängt und behandelt
     * seinen eigenen Fehler intern. Es gibt also gar keinen Absturz, der zum Parent eskalieren könnte.
     */
    private fun successExampleWithInnerTryCatch() {
        viewModelScope.launch { // Parent Coroutine
            val deferredAds = async { // Child Coroutine
                try {
                    fakeApi.fetchAds() // Wirft Exception
                } catch (e: Exception) {
                    null // Wir fangen die Exception IM Kind ab und geben 'null' (oder Fallback-Wert) zurück
                }
            }

            val result = deferredAds.await() // await() wirft jetzt KEINE Exception mehr!
            if (result == null) {
                _state.value = ExerciseState.Error("Fehler 100% sicher abfangen im async Block.")
            } else {
                _state.value = ExerciseState.Success("Ads erfolgreich geladen")
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

    // EXERCISE 11: Grundlagen von Flows
    private fun handleFlowBasics() {
        viewModelScope.launch {
            val numbersFlow = flow {
                emit(1)
                delay(100)
                emit(2)
                delay(100)
                emit(3)
            }
            
            numbersFlow.collect { value ->
                _state.value = Success("Wert: $value")
                delay(50) // Just to make UI updates visible
            }
        }
    }

    // EXERCISE 12: Operatoren und Transformation
    private fun handleFlowOperators() {
        viewModelScope.launch {
            val result = mutableListOf<Int>()
            (1..5).asFlow()
                .filter { it % 2 != 0 } // 1, 3, 5
                .map { it * 10 } // 10, 30, 50
                .collect { 
                    result.add(it)
                    _state.value = Success("Gesammelt: $result")
                }
        }
    }

    // EXERCISE 13: Flow Lifecycle & Exception Handling
    private fun handleFlowExceptionHandling() {
        flow {
            emit(1)
            throw Exception("Fehler im Upstream!")
        }
        .onEach { data -> _state.value = Success("Daten: $data") }
        .catch { e -> _state.value = ExerciseState.Error("Gefangen: ${e.message}") }
        .onCompletion { e -> 
            val msg = if (e != null) "Abgeschlossen mit Fehler" else "Erfolgreich Abgeschlossen"
            println(msg)
        }
        .launchIn(viewModelScope)
    }

    // EXERCISE 14: Context Preservation & flowOn
    private fun handleFlowContext() {
        viewModelScope.launch {
            flow {
                val data = fakeApi.loadFromDatabaseBlocking()
                emit(data)
            }.flowOn(Dispatchers.IO)
            .collect { result ->
                _state.value = Success("Geladen: $result")
            }
        }
    }

    // EXERCISE 15: Flows kombinieren (Zip vs. Combine)
    private fun handleFlowCombine() {
        viewModelScope.launch {
            val flowA = flowOf("A", "B", "C").onEach { delay(10) }
            val flowB = flowOf(1, 2, 3).onEach { delay(15) }
            
            val combinedResults = mutableListOf<String>()
            flowA.combine(flowB) { a, b -> "$a-$b" }
                .collect { 
                    combinedResults.add(it)
                    _state.value = Success("Kombiniert: ${combinedResults.joinToString(", ")}")
                }
        }
    }

    // EXERCISE 16a: Flattening-Strategien (flatMapLatest)
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
                    _state.value = Success(result)
                }
        }
    }

    private fun handleSearchQueryChanged(query: String) {
        // Emit the query to the flow to trigger flatMapLatest
        searchQueryFlow.tryEmit(query)
    }

    // EXERCISE 16b: Flow Processing Pipeline (flatMapMerge)
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

                _state.value =
                    Success("Fetched ${results.size} profiles concurrently!")
            } catch (e: Exception) {
                _state.value = ExerciseState.Error(e.message ?: "Pipeline failed")
            }
        }
    }

    // EXERCISE 17: StateFlow und SharedFlow
    private val weatherStateFlow = flow {
        while (true) {
            delay(1.seconds)
            val weather = fakeApi.fetchWeather()
            emit(weather)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "Loading..."
    )

    private fun handleFlowStateShared() {
        _state.value = Success("StateFlow updated! Behält den aktuellsten Wert.")
    }

    // EXERCISE 18: Buffering & Backpressure
    private fun handleFlowBuffering() {
        viewModelScope.launch {
            val fastFlow = flow {
                for (i in 1..5) {
                    emit(i)
                }
            }
            
            val collected = mutableListOf<Int>()
            fastFlow
                .buffer() // Entkoppelt Emitter und Collector
                .collect { value ->
                    delay(500) // Langsamer Collector
                    collected.add(value)
                    _state.value = Success("Verarbeitet: ${collected.joinToString(", ")}")
                }
        }
    }

    // EXERCISE 19: ChannelFlow & CallbackFlow
    private fun handleFlowCallback() {
        viewModelScope.launch {
            val locationFlow = callbackFlow {
                val listener = object : com.adriano.gfucoroutines.mvi.data.LocationListener {
                    override fun onLocation(loc: LocationResponse) {
                        trySend(loc)
                    }
                }
                fakeApi.locationManager.requestUpdates(listener)
                
                awaitClose { 
                    fakeApi.locationManager.removeUpdates(listener) 
                }
            }
            
            val locations = mutableListOf<String>()
            locationFlow.take(3).collect { loc ->
                locations.add("(${loc.lat.toString().take(6)}, ${loc.lon.toString().take(6)})")
                _state.value = Success("Locations: ${locations.joinToString()}")
            }
        }
    }

    // =========================================================================
    // MODULE 6: Testing Coroutines
    // =========================================================================

    // EXERCISE 20: Unit Testing basic Coroutines
    // Implemented for testing
    var simpleState: ExerciseState = ExerciseState.Idle
        private set

    private fun handleCalculateData() {
        viewModelScope.launch {
            simpleState = ExerciseState.Loading
            delay(1000)
            simpleState = Success("Calculated: 42")
        }
    }

    // EXERCISE 21: Unit Testing Exceptions and Flows (with Turbine)
    // Implemented for testing
    private fun handleFetchUser(userId: Int) {
        viewModelScope.launch {
            _state.value = ExerciseState.Loading
            try {
                if (userId < 0) throw IllegalArgumentException("Invalid ID")
                val details = fakeApi.fetchUserDetails(userId)
                _state.value = Success("User: $details")
            } catch (e: Exception) {
                _state.value = ExerciseState.Error(e.message ?: "Error fetching user")
            }
        }
    }
}
