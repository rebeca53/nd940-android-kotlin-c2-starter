package com.udacity.asteroidradar.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.Asteroid

class DetailViewModel(selectedAsteroid: Asteroid) : ViewModel() {
    private val _asteroid = MutableLiveData<Asteroid>()

    val asteroid: LiveData<Asteroid>
        get() = _asteroid

    init {
        _asteroid.value = selectedAsteroid
    }
}