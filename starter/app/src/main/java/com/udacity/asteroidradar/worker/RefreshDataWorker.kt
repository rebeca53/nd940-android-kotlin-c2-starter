package com.udacity.asteroidradar.worker

import android.content.Context
import androidx.work.*
import com.udacity.asteroidradar.api.getTodayFormattedDate
import com.udacity.asteroidradar.database.AsteroidsRepository
import com.udacity.asteroidradar.database.getDatabase
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params)  {
    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    private val date = getTodayFormattedDate()

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidsRepository(database)
        return try {
            repository.refreshAsteroids(date, date)
            Result.success()
        }
        catch (e: HttpException) {
            Result.retry()
        }
    }

}