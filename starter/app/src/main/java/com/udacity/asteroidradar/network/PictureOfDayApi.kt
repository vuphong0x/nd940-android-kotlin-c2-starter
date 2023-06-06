package com.udacity.asteroidradar.network

import com.udacity.asteroidradar.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface PictureOfDayApi {
    @GET("planetary/apod")
    suspend fun getPictureOfDay(@Query("api_key") apiKey: String = Constants.API_KEY): String
}
