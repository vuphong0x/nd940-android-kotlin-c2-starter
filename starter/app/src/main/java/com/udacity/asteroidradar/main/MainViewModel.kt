package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import com.udacity.asteroidradar.utils.AsteroidFilter
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AsteroidDatabase.getInstance(application)
    private val repository = AsteroidRepository(database)

    private val allAsteroids = repository.allAsteroids
    private val weekAsteroids = repository.weekAsteroids
    private val todayAsteroids = repository.todayAsteroids

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _navigateToDetailScreen = MutableLiveData<Asteroid?>()
    val navigateToDetailScreen: LiveData<Asteroid?>
        get() = _navigateToDetailScreen

    private val asteroidFilter = MutableLiveData<AsteroidFilter>()
    private val _asteroids = asteroidFilter.switchMap() {
        when (it) {
            AsteroidFilter.TODAY -> todayAsteroids
            AsteroidFilter.WEEK -> weekAsteroids
            else -> allAsteroids
        }
    }

    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    init {
        viewModelScope.launch {
            refreshAsteroids()
            getPictureOfDay()
            setAsteroidFilter(AsteroidFilter.WEEK)
        }
    }


    private suspend fun refreshAsteroids() {
        repository.refreshAsteroids()
    }

    private suspend fun getPictureOfDay() {
        // Since the API get picture of day is experiencing errors, I will fake the data here.
//        val moshi = Moshi.Builder().build()
//        val adapter: JsonAdapter<PictureOfDay> = moshi.adapter(PictureOfDay::class.java)
//        _pictureOfDay.value =
//            adapter.fromJson(AsteroidApiService.pictureOfDayApi.getPictureOfDay(Constants.API_KEY))
        val pictureOfDay = PictureOfDay(
            mediaType = "image",
            title = "Stunning Sunset",
            url = "https://api.nasa.gov/assets/img/general/apod.jpg"
        )
        _pictureOfDay.value = pictureOfDay
    }

    fun onClickAsteroidItem(asteroid: Asteroid) {
        _navigateToDetailScreen.value = asteroid
    }

    fun onNavigatingCompleted() {
        _navigateToDetailScreen.value = null
    }

    fun setAsteroidFilter(filter: AsteroidFilter) {
        asteroidFilter.value = filter
    }
}
