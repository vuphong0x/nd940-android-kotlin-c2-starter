package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.Asteroid

@Dao
interface AsteroidDAO {

    @Query("SELECT * FROM asteroid_table ORDER BY date(closeApproachDate)")
    fun getAllAsteroid(): LiveData<List<Asteroid>>

    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate =:date ORDER BY date(closeApproachDate) ASC")
    fun getTodayAsteroid(date: String): LiveData<List<Asteroid>>

    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate BETWEEN :startDate AND :endDate ORDER BY date(closeApproachDate)")
    fun getWeekAsteroid(startDate: String, endDate: String): LiveData<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(asteroids: List<Asteroid>)

    @Query("DELETE FROM asteroid_table WHERE date(closeApproachDate) < date(:date)")
    fun deleteAsteroidPreviousDate(date: String)
}
