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
}
