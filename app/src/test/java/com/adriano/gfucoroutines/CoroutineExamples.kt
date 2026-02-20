package com.adriano.gfucoroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

class CoroutineExamples {

    @Before
    fun setup() {
        println("__________________________________________________________________________________")
        repeat(3) { println() }
    }

    @After
    fun tearDown() {
        repeat(3) { println() }
        println("__________________________________________________________________________________")
    }

}