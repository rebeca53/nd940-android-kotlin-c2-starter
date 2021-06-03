package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.PictureOfDay
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor { apiKeyInterceptor(it) }
    .build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi)) //to format getImageOfDay
    .addConverterFactory(GsonConverterFactory.create()) //to format getAsteroids in JSON
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()

interface NASAApiService {
    @GET("planetary/apod")
    suspend fun getImageOfDay(): // suspend will let function to run in a coroutine scope
            PictureOfDay

    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(@Query("start_date") startDate: String, @Query("end_date") endDate: String): // suspend will let function to run in a coroutine scope
            Any
}

object NASAApi {
    val retrofitService : NASAApiService by lazy {
        retrofit.create(NASAApiService::class.java)
    }
}

private fun apiKeyInterceptor(it: Interceptor.Chain): Response {
    val originalRequest = it.request()
    val originalHttpUrl = originalRequest.url()
    val newHttpUrl = originalHttpUrl.newBuilder()
        .addQueryParameter("api_key", BuildConfig.API_KEY)
        .build()
    val newRequest = originalRequest.newBuilder()
        .url(newHttpUrl)
        .build()
    return it.proceed(newRequest)
}