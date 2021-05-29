package com.udacity.asteroidradar.main

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NASAApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
        NASAApi.retrofitService.getImageOfDay().enqueue(object : Callback<PictureOfDay> {
            override fun onResponse(call: Call<PictureOfDay>, response: Response<PictureOfDay>) {
                currentResponse = response.body().toString()
                Log.d(TAG, "getImageOfTheDay() Response is ${response.body()?.url}")
            }

            override fun onFailure(call: Call<PictureOfDay>, t: Throwable) {
                currentResponse = "Failure: " + t.message
                Log.e(TAG, "getImageOfTheDay() Failure: " + t.message)
            }
        })
    }

    private fun getAsteroids() {
        NASAApi.retrofitService.getAsteroids().enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                val gson = Gson().toJson(response.body())
                val json = JSONObject(gson)
                var listAsteroid = arrayListOf<Asteroid>()
                response.body()?.let {
                    listAsteroid = parseAsteroidsJsonResult(json) }

                listAsteroid.forEach{
                    Log.d(TAG, "getAsteroids() Response is ${it.codename}")
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.e(TAG, "getAsteroids() Failure: " + t.message)
            }
        })
    }
}