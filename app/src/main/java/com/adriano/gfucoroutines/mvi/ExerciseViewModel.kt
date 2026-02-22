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
    // MODUL 2: Grundlagen der Kotlin Coroutinen
    // =========================================================================

    // ÜBUNG 1: async / await
    private fun handleLoadUserData() {
        // TODO: Lade das Benutzerprofil (`fakeApi.fetchUserProfile()`) und
        // den Benutzer-Avatar (`fakeApi.fetchUserAvatar()`) PARALLEL.
        // Warte, bis beide abgeschlossen sind, und aktualisiere dann den State mit dem kombinierten Ergebnis:
        // _state.value = ExerciseState.Success("Profile: \${profile}, Avatar: \${avatar}")
    }

    // ÜBUNG 2: Job-Hierarchie & Abbrechen (Cancellation)
    private fun handleCancelOngoingWork() {
        // Szenario: Ein Benutzer startet einen Download, klickt aber erneut auf eine Schaltfläche, um ihn abzubrechen.

        // TODO: 1. Wenn `downloadJob` bereits läuft, breche ihn ab.
        // TODO: 2. Starte eine neue Coroutine im 'viewModelScope'.
        // Innerhalb der Coroutine:
        //   - Aktualisiere den State auf Loading.
        //   - Rufe `fakeApi.downloadLargeFile()` auf.
        //   - Aktualisiere nach Abschluss den State auf Success("Download Complete!").
    }

    // ÜBUNG 3: Implizites Warten (coroutineScope)
    private fun handleImplicitWait() {
        // Szenario: Ein übergeordneter Prozess muss drei Bilder gleichzeitig hochladen.
        // Er soll automatisch NUR dann abgeschlossen werden, wenn alle drei untergeordneten Prozesse (children) fertig sind.

        // TODO: Verwende `coroutineScope { ... }`, um 3 untergeordnete Coroutinen (child coroutines) zu starten,
        // die `fakeApi.uploadImage(id)` aufrufen.
        // TODO: Aktualisiere nach dem `coroutineScope`-Block den State auf Success.
    }

    // ÜBUNG 4: Explizites Warten (job.join)
    private fun handleExplicitWait() {
        // Szenario: Starte explizit einen Synchronisierungsjob im Hintergrund und warte darauf, dass er abgeschlossen wird.

        // TODO: Starte eine neue Coroutine mit `launch` und weise sie einer Variablen `job` zu.
        // Rufe innerhalb der Coroutine `fakeApi.syncBackgroundData()` auf.
        // TODO: Rufe `job.join()` auf, um darauf zu warten.
        // TODO: Aktualisiere nach Abschluss den State auf Success.
    }

    // ÜBUNG 5: Callbacks Refaktorieren (suspendCancellableCoroutine)
    private fun handleRefactorCallback() {
        // Szenario: Du hast eine alte Drittanbieter-Bibliothek, die Standortdaten über einen Callback abruft.

        // TODO: Erstelle eine suspend-Funktion, die `suspendCancellableCoroutine` verwendet,
        // um `fakeApi.legacyGetLocation(...)` zu verpacken (wrappen).
        // TODO: Rufe deine neue suspend-Funktion hier auf und aktualisiere den State auf Success mit dem Ergebnis.
    }

    // =========================================================================
    // MODUL 3: Coroutine Context und Dispatchers
    // =========================================================================

    // ÜBUNG 6: withContext & Dispatchers
    private fun handleLoadDatabaseAndNetwork() {
        // TODO: 1. Rufe Daten aus der Datenbank über `fakeApi.loadFromDatabaseBlocking()` ab.
        // WARNUNG: Dies ist ein BLOCKIERENDER Aufruf (Thread.sleep). Du MUSST in den entsprechenden
        // Dispatcher wechseln, damit du den Main-Thread nicht blockierst!

        // TODO: 2. Nachdem du die DB-Daten erhalten hast, rufe Netzwerkdaten über `fakeApi.fetchNetworkData()` ab.

        // TODO: 3. Kombiniere beide und aktualisiere den State:
        // _state.value = ExerciseState.Success("DB: \${dbResult}, Net: \${netResult}")
    }

    // ÜBUNG 7: Custom Scope Cancellation
    private fun handleCustomScopeCancellation() {
        // Szenario: Eine Activity oder eine bestimmte Komponente hat ihren eigenen `CoroutineScope`.

        // TODO: Erstelle einen eigenen `CoroutineScope` (z. B. `CoroutineScope(Dispatchers.Default + Job())`).
        // TODO: Starte `fakeApi.syncComponentData()` innerhalb dieses Scopes.
        // TODO: Warte 2 Sekunden (`delay(2000)`) und breche dann den Scope ab, um die Synchronisierungsaufgabe zu stoppen.
        // TODO: Aktualisiere den State auf Success("Scope cancelled successfully").
    }

    // =========================================================================
    // MODUL 4: Fehlerbehandlung und Shared Mutable State
    // =========================================================================

    // ÜBUNG 8: Shared Mutable State & Mutex
    private fun handleIncrementCounter() {
        // TODO: Erstelle eine Variable sharedCounter
        // Starte 100 Coroutinen, um den Counter zu erhöhen
        // Verwende einen Mutex oder einen Single-Thread-Ansatz
    }

    // ÜBUNG 9: Fehlerbehandlung (Exception Handling)
    private fun handleLoadRiskyData() {
        // Szenario: Wir möchten Wetter, Nachrichten und Werbung (Ads) gleichzeitig laden.
        // Beachte, dass `fetchAds()` eine Exception werfen wird!
        // Wir möchten, dass Wetter und Nachrichten TROTZDEM erfolgreich geladen werden, auch wenn die Werbung fehlschlägt.

        // TODO: Verwende `supervisorScope`, damit der Fehler einer untergeordneten Coroutine (child) die anderen nicht abbricht.
        // TODO: Verwende innerhalb des Scopes `async`, um Wetter, Nachrichten und Werbung abzurufen.
        // TODO: Verwende try-catch um den `await()`-Aufruf für die Werbung, um einen Absturz zu verhindern.

        // Erwartetes Ergebnis, falls richtig umgesetzt:
        // _state.value = ExerciseState.Success("Weather: \${w}, News: \${n}, Ads Failed")
    }

    // ÜBUNG 10: Globale Fehlerbehandlung (CoroutineExceptionHandler)
    private fun handleGlobalExceptionHandling() {
        // Szenario: Du führst einen "fire-and-forget" Analytics-Upload mit `launch` aus.
        // Wenn es fehlschlägt, darf die App nicht abstürzen.

        // TODO: Erstelle einen `CoroutineExceptionHandler`, um die Exception abzufangen.
        // TODO: Starte eine Coroutine AUF dem viewModelScope mit diesem Handler und rufe `fakeApi.fetchAds()` auf.
        // TODO: Aktualisiere innerhalb des Exception-Handlers den State auf Error mit der abgefangenen Fehlermeldung.
    }

    // =========================================================================
    // MODUL 5: Asynchronous Flow und Reaktive Programmierung
    // =========================================================================

    // ÜBUNG 11: Flow & flatMapLatest
    private fun handleSearchQueryChanged(query: String) {
        // TODO: Richte einen MutableSharedFlow oder MutableStateFlow für die Suchanfrage ein und
        // verwende .flatMapLatest { fakeApi.fetchSearchResults(it) }, um die Ergebnisse abzurufen.
        // Sammle (collect) den Flow und aktualisiere den `_state`.
        // TODO: Sende (emit) die neue Anfrage an deinen Query-Flow, damit flatMapLatest sie verarbeiten kann.
    }

    // ÜBUNG 12: Flow-Verarbeitungspipeline (Flow Processing Pipeline, flatMapMerge)
    private fun handleFlowProcessingPipeline() {
        // Szenario: Erstelle eine Datenpipeline, die einen Flow Builder verwendet, um rohe Benutzer-IDs zu emittieren (emit).

        // TODO: Verwende `flow { emit(...) }`, um IDs zu emittieren (z. B. 1, 2, 3).
        // TODO: Verwende Zwischenoperatoren (intermediate operators) wie `filter` oder `map`.
        // TODO: Rufe detaillierte Profile für jede gültige ID gleichzeitig mit `flatMapMerge` und `fakeApi.fetchUserDetails` ab.
        // TODO: Sammle (collect) den Flow und aktualisiere den State auf Success.
    }

    // =========================================================================
    // MODUL 6: Testen von Coroutines
    // =========================================================================

    // ÜBUNG 13: Unit-Testing von grundlegenden Coroutines
    // TODO (Student): Sieh in `ExerciseViewModelTest.kt` nach deinen Aufgaben!
    // Diese Methode wurde bereits für dich implementiert, damit du sie testen kannst.
    var simpleState: ExerciseState = ExerciseState.Idle
        private set

    private fun handleCalculateData() {
        viewModelScope.launch {
            simpleState = ExerciseState.Loading
            delay(1000)
            simpleState = ExerciseState.Success("Calculated: 42")
        }
    }

    // ÜBUNG 14: Unit-Testing von Exceptions und Flows (mit Turbine)
    // TODO (Student): Sieh in `ExerciseViewModelTest.kt` nach deinen Aufgaben!
    // Diese Methode wurde bereits für dich implementiert, damit du sie testen kannst.
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
