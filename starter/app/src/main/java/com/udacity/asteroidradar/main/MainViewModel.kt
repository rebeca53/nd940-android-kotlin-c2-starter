package com.udacity.asteroidradar.main

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.udacity.asteroidradar.api.NASAApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception


class MainViewModel : ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }

    //todo set image of day with request result
    private lateinit var currentResponse: String
    private var _imageOfDay = MutableLiveData<Bitmap>()
        val imageOfDay: LiveData<Bitmap>
            get() = _imageOfDay

    init {
        getImageOfTheDay()
        getAsteroids()
    }

    private fun getImageOfTheDay() {
        viewModelScope.launch {
            try {
                var pictureOfDay = NASAApi.retrofitService.getImageOfDay()
                currentResponse = pictureOfDay.toString()
                Log.d(TAG, "getImageOfTheDay returns $pictureOfDay")
            } catch (e: Exception) {
                currentResponse = "getImageOfDay Failure: ${e.message}"
            }
        }
    }

    private fun getAsteroids() {
        viewModelScope.launch {
            try {
                val asteroidResult = NASAApi.retrofitService.getAsteroids()
                val gson = Gson().toJson(asteroidResult)
                val jsonObject= JSONObject(gson)
                var listIterator = parseAsteroidsJsonResult(jsonObject)
                listIterator.forEach {
                    Log.d(TAG, "getAsteroids returns $it")
                }
            }
            catch (e: Exception) {
                currentResponse = "getAsteroids Failure: ${e.message}"
            }
        }
    }
}