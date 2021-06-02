package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NASAApi
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.database.AsteroidsRepository
import com.udacity.asteroidradar.database.getDatabase
import kotlinx.coroutines.launch
import java.lang.Exception

enum class AsteroidApiFilter(val value: String) { VIEW_WEEK("week"), VIEW_TODAY("today"), VIEW_SAVED("saved") }

class MainViewModel(application: Application) : AndroidViewModel(application) {
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

    private val databaseAsteroid = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(databaseAsteroid)

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    init {
        getImageOfTheDay()
    }
    val asteroids = asteroidsRepository.asteroids

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
        if (asteroidsRepository.asteroids.value.isNullOrEmpty()) {
            _statusAsteroids.value = NASAApiStatus.LOADING
        }

        viewModelScope.launch {
            try {
                // todo change what happens when app goes offline and what happens to each menu options
                    /*
                    * Fetch and display the asteroids from the database and only fetch the asteroids from today onwards, ignoring asteroids before today.
                    * Also, display the asteroids sorted by date (Check SQLite documentation to get sorted data using a query).
                    * */
                val dateList = getNextSevenDaysFormattedDates()
                val startDate = dateList[0]
                val endDate =  when (filter) {
                    AsteroidApiFilter.VIEW_WEEK -> dateList.last()
                    AsteroidApiFilter.VIEW_TODAY -> dateList.get(0)
                    AsteroidApiFilter.VIEW_SAVED -> dateList.last() //todo implement show saved
                }
                asteroidsRepository.refreshAsteroids(startDate, endDate)
                _statusAsteroids.value = NASAApiStatus.DONE
            }
            catch (e: Exception) {
                if (asteroidsRepository.asteroids.value.isNullOrEmpty()) {
                    _statusAsteroids.value = NASAApiStatus.ERROR
                }
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

    /**
     * Factory for constructing MainViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}