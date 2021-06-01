package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "gWsKYvf9fGZLOAptxl15G2iRLV32MdJdKlZDxSGL"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi)) //to format getImageOfDay
    .addConverterFactory(GsonConverterFactory.create()) //to format getAsteroids in JSON
    .baseUrl(BASE_URL)
    .build()

interface NASAApiService {
    @GET("planetary/apod?api_key=$API_KEY")
    suspend fun getImageOfDay(): // suspend will let function to run in a coroutine scope
            PictureOfDay

    @GET("neo/rest/v1/feed?api_key=$API_KEY")
    suspend fun getAsteroids(@Query("start_date") startDate: String, @Query("end_date") endDate: String): // suspend will let function to run in a coroutine scope
            Any
}

object NASAApi {
    val retrofitService : NASAApiService by lazy {
        retrofit.create(NASAApiService::class.java)
    }
}