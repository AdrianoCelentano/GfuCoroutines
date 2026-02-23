package com.adriano.gfucoroutines.mvi.data

import kotlinx.coroutines.delay

class FakeApi {

    suspend fun fetchUserProfile(): String {
        delay(1000)
        return "User_Adrian"
    }

    suspend fun fetchUserAvatar(): String {
        delay(1500)
        return "Avatar_URL"
    }

    fun loadFromDatabaseBlocking(): String {
        Thread.sleep(2000) // Simulating a blocking database call
        return "Local_Data_123"
    }

    suspend fun fetchNetworkData(): String {
        delay(1000)
        return "Network_Data_456"
    }
    
    suspend fun downloadLargeFile() {
        // Simulating a long-running download task
        for (i in 1..10) {
            delay(500)
            println("Downloading chunk $i...")
        }
    }

    suspend fun fetchWeather(): String {
        delay(800)
        return "Sunny"
    }

    suspend fun fetchNews(): String {
        delay(1200)
        return "Breaking News"
    }

    suspend fun fetchAds(): String {
        delay(500)
        throw Exception("Failed to load ads! Network error.")
    }

    suspend fun fetchSearchResults(query: String): String {
        delay(1000) // Simulate network delay
        return "Results for '$query'"
    }

    // New for ImplicitWaitIntent
    suspend fun uploadImage(imageId: Int) {
        delay((500..1500).random().toLong()) // Random upload time
        println("Uploaded image $imageId")
    }

    // New for ExplicitWaitIntent
    suspend fun syncBackgroundData() {
        delay(2000)
        println("Background sync finished")
    }

    // New for CustomScopeCancellationIntent
    suspend fun syncComponentData() {
        while(true) {
            delay(500)
            println("Syncing component data...")
        }
    }

    // New for RefactorCallbackIntent
    fun legacyGetLocation(callback: LocationCallback) {
        Thread {
            Thread.sleep(1000) // Simulating old async work
            callback.onSuccess(LocationResponse(48.1371, 11.5754)) // Munich coords
        }.start()
    }

    // New for FlowProcessingPipelineIntent
    suspend fun fetchUserDetails(userId: Int): String {
        delay((300..800).random().toLong()) // Simulate variable network delay
        return "User Profile for ID $userId"
    }

    // New for CallbackFlow
    val locationManager = FakeLocationManager()
}

class FakeLocationManager {
    private var listener: LocationListener? = null
    private var active = false

    fun requestUpdates(listener: LocationListener) {
        this.listener = listener
        active = true
        Thread {
            var i = 1
            while (active) {
                Thread.sleep(500)
                if (active) this.listener?.onLocation(LocationResponse(48.0 + (i * 0.01), 11.0 + (i * 0.01)))
                i++
            }
        }.start()
    }

    fun removeUpdates(listener: LocationListener) {
        if (this.listener == listener) {
            active = false
            this.listener = null
        }
    }
}

// Helper models for Callback refactoring exercise
data class LocationResponse(val lat: Double, val lon: Double)

interface LocationCallback {
    fun onSuccess(location: LocationResponse)
    fun onError(error: Exception)
}

interface LocationListener {
    fun onLocation(loc: LocationResponse)
}
