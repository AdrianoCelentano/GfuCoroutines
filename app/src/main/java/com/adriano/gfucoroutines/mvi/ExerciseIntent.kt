package com.adriano.gfucoroutines.mvi

sealed class ExerciseIntent {
    // ==========================================
    // Modul 2: Grundlagen
    // ==========================================
    object LoadUserDataIntent : ExerciseIntent()
    object CancelOngoingWorkIntent : ExerciseIntent()
    object ImplicitWaitIntent : ExerciseIntent()
    object ExplicitWaitIntent : ExerciseIntent()
    object RefactorCallbackIntent : ExerciseIntent()

    // ==========================================
    // Modul 3: Coroutine Context und Dispatchers
    // ==========================================
    object LoadDatabaseAndNetworkIntent : ExerciseIntent()
    object CustomScopeCancellationIntent : ExerciseIntent()

    // ==========================================
    // Modul 4: Fehlerbehandlung und Shared Mutable State
    // ==========================================
    object IncrementCounterIntent : ExerciseIntent()
    object LoadRiskyDataIntent : ExerciseIntent()
    object GlobalExceptionHandlingIntent : ExerciseIntent()

    // ==========================================
    // Modul 5: Asynchronous Flow
    // ==========================================
    object FlowBasicsIntent : ExerciseIntent()
    object FlowOperatorsIntent : ExerciseIntent()
    object FlowExceptionHandlingIntent : ExerciseIntent()
    object FlowContextIntent : ExerciseIntent()
    object FlowCombineIntent : ExerciseIntent()
    data class SearchQueryChangedIntent(val query: String) : ExerciseIntent() // Flow Flattening
    object FlowProcessingPipelineIntent : ExerciseIntent()
    object FlowStateSharedIntent : ExerciseIntent()
    object FlowBufferingIntent : ExerciseIntent()
    object FlowCallbackIntent : ExerciseIntent()

    // ==========================================
    // Modul 6: Unit Tests
    // ==========================================
    object CalculateDataIntent : ExerciseIntent()
    data class FetchUserIntent(val userId: Int) : ExerciseIntent()
}
