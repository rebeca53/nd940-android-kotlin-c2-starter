package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.gson.Gson
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.NASAApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidsRepository (private val database: AsteroidsDatabase) {
    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroids()) {
        it.asDomainModel()
    }

    suspend fun refreshAsteroids(startDate: String, endDate: String) {
        withContext(Dispatchers.IO) {
            val asteroidResult = NASAApi.retrofitService.getAsteroids(startDate, endDate)
            val gson = Gson().toJson(asteroidResult)
            val jsonObject= JSONObject(gson)
            val asteroidList = parseAsteroidsJsonResult(jsonObject)
            val asteroidsDatabase = asteroidList.map {
                DatabaseAsteroid (
                    id = it.id,
                    codename = it.codename,
                    closeApproachDate = it.closeApproachDate,
                    absoluteMagnitude = it.absoluteMagnitude,
                    estimatedDiameter = it.estimatedDiameter,
                    relativeVelocity = it.relativeVelocity,
                    distanceFromEarth = it.distanceFromEarth,
                    isPotentiallyHazardous = it.isPotentiallyHazardous
                )
            }.toTypedArray()

            database.asteroidDao.insertAll(*asteroidsDatabase)
        }
    }

    suspend fun getTodayAsteroids(today: String): LiveData<List<Asteroid>> {
        refreshAsteroids(today, today)
        return Transformations.map(database.asteroidDao.getTodayAsteroids(today)) {
            it.asDomainModel()
        }
    }

    suspend fun getWeekAsteroids(weekDates: List<String>): LiveData<List<Asteroid>> {
        refreshAsteroids(weekDates[0], weekDates.last())
        return Transformations.map(database.asteroidDao.getWeekAsteroids(weekDates)) {
            it.asDomainModel()
        }
    }

    fun deletePreviousAsteroids(startDate: String) {
        database.asteroidDao.deletePrevious(startDate)
    }
}