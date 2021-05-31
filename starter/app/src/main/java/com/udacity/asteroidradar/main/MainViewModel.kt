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
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception


class MainViewModel : ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }

    private lateinit var currentResponse: String
    private var _imageOfDay = MutableLiveData<PictureOfDay>()
    val imageOfDay: LiveData<PictureOfDay>
        get() = _imageOfDay
    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    init {
        getImageOfTheDay()
        getAsteroids()
    }

    private fun getImageOfTheDay() {
        viewModelScope.launch {
            try {
                _imageOfDay.value = NASAApi.retrofitService.getImageOfDay()
                currentResponse = _imageOfDay.value.toString()
                Log.d(TAG, "getImageOfTheDay returns ${_imageOfDay.value}")
            } catch (e: Exception) {
                currentResponse = "getImageOfDay Failure: ${e.message}"
                Log.e(TAG, currentResponse)
            }
        }
    }

    private fun getAsteroids() {
        viewModelScope.launch {
            try {
                val asteroidResult = NASAApi.retrofitService.getAsteroids()
                val gson = Gson().toJson(asteroidResult)
                val jsonObject= JSONObject(gson)
                var asteroidList = parseAsteroidsJsonResult(jsonObject)
                asteroidList.forEach {
                    Log.d(TAG, "getAsteroids returns $it")
                }
                _asteroids.value = asteroidList
            }
            catch (e: Exception) {
                currentResponse = "getAsteroids Failure: ${e.message}"
                Log.e(TAG, currentResponse)
            }
        }
    }
}