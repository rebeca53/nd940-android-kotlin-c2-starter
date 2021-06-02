package com.udacity.asteroidradar.worker

import android.content.Context
import androidx.work.*
import com.udacity.asteroidradar.database.AsteroidsRepository
import com.udacity.asteroidradar.database.getDatabase
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params)  {
    companion object {
        const val WORK_NAME = "RefreshDataWorker"
        const val START_DATE = "start_date"
        const val END_DATE = "end_date"
    }

    private val startDate = params.inputData.getString(START_DATE) ?: ""
    private val endDate = params.inputData.getString(END_DATE) ?: ""

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidsRepository(database)
        return try {
            repository.refreshAsteroids(startDate, endDate)
            Result.success()
        }
        catch (e: HttpException) {
            Result.retry()
        }
    }

}