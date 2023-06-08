package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.asDomainModel
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.network.AsteroidApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(private val database: AsteroidDatabase) {

    val allAsteroids: LiveData<List<Asteroid>> =
        database.asteroidDao.getAllAsteroid().map {
            it.asDomainModel()
        }

    val weekAsteroids: LiveData<List<Asteroid>> =
        database.asteroidDao.getWeekAsteroid(
            getNextSevenDaysFormattedDates().first() + 1,
            getNextSevenDaysFormattedDates().last()
        ).map {
            it.subList(1, it.size)
            it.asDomainModel()
        }

    val todayAsteroids: LiveData<List<Asteroid>> = database.asteroidDao.getTodayAsteroid(
        getNextSevenDaysFormattedDates().first()
    ).map {
        it.asDomainModel()
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroidsJsonString = AsteroidApiService.asteroidApi.getAsteroids()
                val asteroidsJson = JSONObject(asteroidsJsonString)
                val data = parseAsteroidsJsonResult(asteroidsJson)
                database.asteroidDao.insertAll(data)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun deleteAsteroidsPreviousDate() {
        withContext(Dispatchers.IO) {
            database.asteroidDao.deleteAsteroidPreviousDate(getNextSevenDaysFormattedDates().first())
        }
    }
}

