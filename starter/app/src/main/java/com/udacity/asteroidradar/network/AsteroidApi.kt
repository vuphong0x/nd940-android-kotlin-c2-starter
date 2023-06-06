package com.udacity.asteroidradar.network

import com.udacity.asteroidradar.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface AsteroidApi {
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): String
}
