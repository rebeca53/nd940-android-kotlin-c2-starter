package com.udacity.asteroidradar.main

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.api.NASAApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }

    private lateinit var currentResponse: String
    private var _imageOfDay = MutableLiveData<Bitmap>()
        val imageOfDay: LiveData<Bitmap>
            get() = _imageOfDay

    init {
        getImageOfTheDay()
        getAsteroids()
    }
    private fun getImageOfTheDay() {
        NASAApi.retrofitService.getImageOfDay().enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                currentResponse = response.body().toString()
                Log.d(TAG, "getImageOfTheDay() Response is ${response.body().toString()}")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                currentResponse = "Failure: " + t.message
                Log.e(TAG, "getImageOfTheDay() Failure: " + t.message)
            }
        })
    }

    private fun getAsteroids() {
        NASAApi.retrofitService.getAsteroids().enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d(TAG, "getAsteroids() Response is ${response.body().toString()}")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "getAsteroids() Failure: " + t.message)
            }
        })
    }
}