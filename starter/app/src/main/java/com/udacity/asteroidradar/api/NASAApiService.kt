package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET


private const val BASE_URL = "https://api.nasa.gov/"
private const val API_KEY = "gWsKYvf9fGZLOAptxl15G2iRLV32MdJdKlZDxSGL"

// todo: replace hard-coded by parameters
private const val START_DATE = "2021-05-29"
private const val END_DATE = "2021-06-05"

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

    @GET("neo/rest/v1/feed?start_date=$START_DATE&end_date=$END_DATE&api_key=$API_KEY")
    suspend fun getAsteroids(): // suspend will let function to run in a coroutine scope
            Any
}

object NASAApi {
    val retrofitService : NASAApiService by lazy {
        retrofit.create(NASAApiService::class.java)
    }
}