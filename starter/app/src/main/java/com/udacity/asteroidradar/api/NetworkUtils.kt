package com.udacity.asteroidradar.api

import android.annotation.SuppressLint
import android.util.Log
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.collections.ArrayList

@SuppressLint("LongLogTag")
fun parseAsteroidsJsonResult(jsonResult: JSONObject): ArrayList<Asteroid> {
    val nearEarthObjectsJson = jsonResult.getJSONObject("near_earth_objects")

    val asteroidList = ArrayList<Asteroid>()

    val nextSevenDaysFormattedDates = getNextSevenDaysFormattedDates()
    for (formattedDate in nextSevenDaysFormattedDates) {
        try {
            val dateAsteroidJsonArray = nearEarthObjectsJson.getJSONArray(formattedDate)

            for (i in 0 until dateAsteroidJsonArray.length()) {
                val asteroidJson = dateAsteroidJsonArray.getJSONObject(i)
                val id = asteroidJson.getLong("id")
                val codename = asteroidJson.getString("name")
                val absoluteMagnitude = asteroidJson.getDouble("absolute_magnitude_h")
                val estimatedDiameter = asteroidJson.getJSONObject("estimated_diameter")
                    .getJSONObject("kilometers").getDouble("estimated_diameter_max")

                val closeApproachData = asteroidJson
                    .getJSONArray("close_approach_data").getJSONObject(0)
                val relativeVelocity = closeApproachData.getJSONObject("relative_velocity")
                    .getDouble("kilometers_per_second")
                val distanceFromEarth = closeApproachData.getJSONObject("miss_distance")
                    .getDouble("astronomical")
                val isPotentiallyHazardous = asteroidJson
                    .getBoolean("is_potentially_hazardous_asteroid")

                val asteroid = Asteroid(id, codename, formattedDate, absoluteMagnitude,
                    estimatedDiameter, relativeVelocity, distanceFromEarth, isPotentiallyHazardous)
                asteroidList.add(asteroid)
            }
        } catch (jsonException: JSONException) {
            Log.e("parseAsteroidsJsonResult", "Unable to retrieve data because of JSON Exception")
        }
        catch (e: Exception) {
            Log.e("parseAsteroidsJsonResult", "Unable to retrieve data because of Exception")
        }
    }

    return asteroidList
}

private fun getNextSevenDaysFormattedDates(startDate: String): ArrayList<String> {
    val formattedDateList = ArrayList<String>()
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    val calendar = Calendar.getInstance()
    val date = dateFormat.parse(startDate)
    calendar.time = date
    for (i in 0..7) {
        val currentTime = calendar.time
        formattedDateList.add(dateFormat.format(currentTime))
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    return formattedDateList
}

private fun getNextSevenDaysFormattedDates(): ArrayList<String> {
    val formattedDateList = ArrayList<String>()

    val calendar = Calendar.getInstance()
    for (i in 0..Constants.DEFAULT_END_DATE_DAYS) {
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        formattedDateList.add(dateFormat.format(currentTime))
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    return formattedDateList
}