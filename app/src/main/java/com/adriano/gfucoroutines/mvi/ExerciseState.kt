package com.adriano.gfucoroutines.mvi

sealed class ExerciseState {
    object Idle : ExerciseState()
    object Loading : ExerciseState()
    data class Success(val message: String) : ExerciseState()
    data class Error(val message: String) : ExerciseState()
}
