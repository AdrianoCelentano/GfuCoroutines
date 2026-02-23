package com.gfu.coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

suspend fun main() {

}

// Day 1

fun basicCoroutine() {

    // simple way to start a coroutine anywhere
    runBlocking {

        // runBlocking starts a coroutine in the caller thread
        println(Thread.currentThread().name)

        launch {
            // child coroutines are started in the same thread, if not specified otherwise
            println("child coroutine #1 starts in thread ${Thread.currentThread().name}")
            delay(1.seconds)
            println("child one is done")
        }

        launch {
            println("child coroutine #2 starts in thread ${Thread.currentThread().name}")
            delay(200.milliseconds)
            println("child two finishes")
        }

        println("End of runBlocking reached")
    }

    println("Thread is free again and function continues")

    //TODO is there parallelism or concurrency possible here ?
}

fun asyncAwaitCoroutine() {

    runBlocking {

        // coroutine starts here, but gives back a deferred value
        val deferredOne = async {
            println("async one starts")
            delay(1.seconds)
            println("async one ends")
            return@async 1
        }

        // second coroutine starts here
        val deferredTwo = async {
            println("async two starts")
            delay(500.milliseconds)
            println("async two ends")
            return@async 2
        }

        // deferreds can be used to suspend until the coroutine completes
        println("start waiting for results")
        val resultOne = deferredOne.await()
        val resultTwo = deferredTwo.await()
        println("results: $resultOne + $resultTwo")

    }

    //TODO is this now parallelism or concurrency ?
}

fun contextSwitch() {
    runBlocking {

        println("coroutine start on thread ${Thread.currentThread().name}")

        println("with context start")
        withContext(Dispatchers.Default) { // switches the Dispatcher

            launch {
                println("child coroutine #1 starts in thread ${Thread.currentThread().name}")
                delay(1.seconds)
                println("child coroutine #1 end")
            }
            launch {
                println("child coroutine #2 starts in thread ${Thread.currentThread().name}")
                delay(500.milliseconds)
                println("child coroutine #2 end")
            }
        }
        // withContext is suspending until all child coroutines are completed
        println("with context end")

        launch(Dispatchers.Default) {
            println("coroutine #3 starts in thread ${Thread.currentThread().name}")
            delay(800.milliseconds)
            println("coroutine #3 ends")
        }
    }

    //TODO is there parallelism or concurrency possible here ?
}

fun jobCancel() {

    runBlocking {

        println("coroutine start")

        // a job is responsible for the lifecycle of a coroutine
        // every coroutine has its own job
        val job = launch {
            println("inner coroutine start")
            delay(1.seconds)
            println("inner coroutine end")
        }

        println("job cancel")
        job.cancel()
    }
}

fun jobJoin() {

    runBlocking {

        val job = launch {
            println("inner coroutine start")
            delay(1.seconds)
            println("inner coroutine end")
        }

        println("Job join start")
        job.join()
        println("Job join end")
    }
}

// Day 2

fun basicScopeCoroutine() {

    // The route of all coroutines is a CoroutineScope
    // Each CoroutineScope contains a CoroutineContext, which contains a Dispatcher and a Job
    val scope = CoroutineScope(Dispatchers.Default + Job())

    // Job of the coroutine is added to the scopes context
    scope.launch {
        println("coroutine start")
        delay(1.seconds)
        println("coroutine end")
    }

    //cancels the jobs of the coroutine and all its child coroutines
//    scope.cancel()

    keepApplicationRunning()
}

fun runBlockingScope() {

    runBlocking {

        // runBlocking is also creating a coroutineScope
        // new coroutines are always started as functions of CoroutineScope
        // this is the CoroutineScope created by runBlocking
        this.launch {
            println("this is a CoroutineScope ${this is CoroutineScope}")
        }

    }
}

fun coroutineContext() {

    runBlocking {

        // a CoroutineScope is a container for the CoroutineContext
        // the CoroutineContext gives access to the Job of the current Coroutine among other things
        val runBlockingCoroutineJob = coroutineContext[Job]

        this.launch {
            // Parent context + child Job = child context
            // inside a child Coroutine, you get the job of the child Coroutine
            val innerCoroutineJob = coroutineContext[Job]
        }
    }
}

fun unconfinedDispatcher() {
    runBlocking {
        launch(Dispatchers.Unconfined) { // not confined -- will work with main thread
            println("Unconfined      : I'm working in thread ${Thread.currentThread().name}")
            delay(500) // suspending function executed on DefaultExecutor thread
            println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
        }
        launch { // context of the parent, main runBlocking coroutine
            println("main runBlocking: I'm working in thread ${Thread.currentThread().name}")
            delay(1000)
            println("main runBlocking: After delay in thread ${Thread.currentThread().name}")
        }
    }
}

fun newJobNewScope() {
    runBlocking {
        val request = launch {
            // it spawns two other jobs
            launch(Job()) {
                println("${coroutineContext[Job]} I run in my own Job and execute independently!")
                delay(1000)
                println("${coroutineContext[Job]} I am not affected by cancellation of the request")
            }
            // and the other inherits the parent context
            launch {
                delay(100)
                println("${coroutineContext[Job]} I am a child of the request coroutine")
                delay(1000)
                println("${coroutineContext[Job]} I will not execute this line if my parent request is cancelled")
            }
        }
        delay(500)
        request.cancel() // cancel processing of the request
        println("main: Who has survived request cancellation?")
        delay(1000) // delay the main thread for a second to see what happens
    }
}

fun ChildCompleteParentComplete() {
    runBlocking {
        val request = launch {
            repeat(3) { i -> // launch a few children jobs
                launch {
                    delay((i + 1) * 200L) // variable delay 200ms, 400ms, 600ms
                    println("Coroutine $i is done")
                }
            }
            println("request: I'm done and I don't explicitly join my children that are still active")
        }
        request.join() // wait for completion of the request, including all its children
        println("Now processing of the request is complete")
    }
}

fun `scope gives life(cycle)`() {

    class LifetimeScopedThing {

        private val mainScope = CoroutineScope(Dispatchers.Default)

        fun doSomething() {
            repeat(10) { index ->
                mainScope.launch {
                    delay(index * 100L)
                    println("Coroutine $index is done")
                }
            }
        }

        fun destroy() {
            mainScope.cancel()
        }
    }

    runBlocking {
        val activity = LifetimeScopedThing()
        activity.doSomething()
        println("Launched coroutines")
        delay(600L)
        println("Destroying activity!")
        activity.destroy() // cancels all coroutines
        delay(1000)
    }
}

// Suspends the main thread to allow background tasks in 'scope'
// to complete before the process terminates.
private fun keepApplicationRunning() {
    runBlocking { delay(2.seconds) }
}

private suspend fun test() {
    val d = coroutineScope {
        async {  }
    }
    d.await()
}