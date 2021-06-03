package com.udacity.asteroidradar.worker

import android.content.Context
import androidx.work.*
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.database.AsteroidsRepository
import com.udacity.asteroidradar.database.getDatabase
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params)  {
    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    private val date = getNextSevenDaysFormattedDates()

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidsRepository(database)
        return try {
            repository.deletePreviousAsteroids(date[0])
            repository.refreshAsteroids(date[0], date.last())
            Result.success()
        }
        catch (e: HttpException) {
            Result.retry()
        }
    }

}