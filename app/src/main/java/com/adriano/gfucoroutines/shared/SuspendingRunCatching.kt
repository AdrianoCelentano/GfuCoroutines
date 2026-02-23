package com.adriano.gfucoroutines.shared

import kotlinx.coroutines.CancellationException

public inline fun <T, R> T.runCatchingSuspending(block: T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        Result.failure(e)
    }
}