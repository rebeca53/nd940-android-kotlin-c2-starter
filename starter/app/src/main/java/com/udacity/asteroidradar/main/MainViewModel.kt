package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NASAApi
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

enum class AsteroidApiFilter(val value: String) { VIEW_WEEK("week"), VIEW_TODAY("today"), VIEW_SAVED("saved") }

class MainViewModel : ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }
    enum class NASAApiStatus { LOADING, ERROR, DONE }

    private val _statusImageOfDay = MutableLiveData<NASAApiStatus>()
    val statusImageOfDay: LiveData<NASAApiStatus>
        get() = _statusImageOfDay
    private val _statusAsteroids = MutableLiveData<NASAApiStatus>()
    val statusAsteroids: LiveData<NASAApiStatus>
        get() = _statusAsteroids
    private var _imageOfDay = MutableLiveData<PictureOfDay>()
    val imageOfDay: LiveData<PictureOfDay>
        get() = _imageOfDay
    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    init {
        getImageOfTheDay()
        getAsteroids(AsteroidApiFilter.VIEW_WEEK)
    }

    private fun getImageOfTheDay() {
        _statusImageOfDay.value = NASAApiStatus.LOADING
        viewModelScope.launch {
            try {
                _imageOfDay.value = NASAApi.retrofitService.getImageOfDay()
                Log.d(TAG, "getImageOfTheDay returns ${_imageOfDay.value}")
                _statusImageOfDay.value = NASAApiStatus.DONE
            } catch (e: Exception) {
                _statusImageOfDay.value = NASAApiStatus.ERROR
                Log.e(TAG, "getImageOfDay Failure: ${e.message}")
            }
        }
    }

    private fun getAsteroids(filter: AsteroidApiFilter) {
        _statusAsteroids.value = NASAApiStatus.LOADING
        viewModelScope.launch {
            try {
                val dateList = getNextSevenDaysFormattedDates()
                val startDate = dateList[0]
                val endDate =  when (filter) {
                    AsteroidApiFilter.VIEW_WEEK -> dateList.last()
                    AsteroidApiFilter.VIEW_TODAY -> dateList.get(0)
                    AsteroidApiFilter.VIEW_SAVED -> dateList.last() //todo implement show saved
                }
                val asteroidResult = NASAApi.retrofitService.getAsteroids(startDate, endDate)
                val gson = Gson().toJson(asteroidResult)
                val jsonObject= JSONObject(gson)
                val asteroidList = parseAsteroidsJsonResult(jsonObject)
                // todo only dump list if verbose mode
                asteroidList.forEach {
                    Log.d(TAG, "getAsteroids returns $it")
                }
                _asteroids.value = asteroidList
                _statusAsteroids.value = NASAApiStatus.DONE
            }
            catch (e: Exception) {
                _statusAsteroids.value = NASAApiStatus.ERROR
                _asteroids.value = ArrayList()
                Log.e(TAG, "getAsteroids Failure: ${e.message}")
            }
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    fun updateFilter(filter: AsteroidApiFilter) {
        getAsteroids(filter)
    }

}