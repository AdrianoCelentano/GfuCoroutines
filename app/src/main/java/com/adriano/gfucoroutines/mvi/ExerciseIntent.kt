package com.adriano.gfucoroutines.mvi

sealed class ExerciseIntent {
    // Topic: async / await & parallel execution
    object LoadUserDataIntent : ExerciseIntent()

    // Topic: withContext & Dispatchers
    object LoadDatabaseAndNetworkIntent : ExerciseIntent()

    // Topic: Job Hierarchy & Cancellation
    object CancelOngoingWorkIntent : ExerciseIntent()

    // Topic: Shared Mutable State & Mutex
    object IncrementCounterIntent : ExerciseIntent()

    // Topic: Flow & flatMapLatest
    data class SearchQueryChangedIntent(val query: String) : ExerciseIntent()

    // Topic: Exception Handling & SupervisorJob/CoroutineExceptionHandler
    object LoadRiskyDataIntent : ExerciseIntent()
}
