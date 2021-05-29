package com.udacity.asteroidradar.api

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://api.nasa.gov/"
private const val API_KEY = "gWsKYvf9fGZLOAptxl15G2iRLV32MdJdKlZDxSGL"

// todo: replace hard-coded by parameters
private const val START_DATE = "2015-09-07"
private const val END_DATE = "2015-09-08"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface NASAApiService {
    @GET("planetary/apod?api_key=$API_KEY")
    fun getImageOfDay():
            Call<String>

    @GET("neo/rest/v1/feed?start_date=$START_DATE&end_date=$END_DATE&api_key=$API_KEY")
    fun getAsteroids():
            Call<String>
}

object NASAApi {
    val retrofitService : NASAApiService by lazy {
        retrofit.create(NASAApiService::class.java)
    }
}