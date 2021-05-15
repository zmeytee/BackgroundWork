package ru.vladimir.tilikov.backgroundworkx.repositories

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import ru.vladimir.tilikov.backgroundworkx.workers.DownloadWorker
import ru.vladimir.tilikov.backgroundworkx.workers.SynchronizationWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class FileRepository(private val context: Context) {

    suspend fun startDownloadFileWork(url: String) {
        withContext(Dispatchers.IO) {
            val workData = workDataOf(
                DownloadWorker.DOWNLOAD_URL_KEY to url
            )

            val workConstraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(workData)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 20, TimeUnit.SECONDS)
                .setConstraints(workConstraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(DOWNLOAD_WORK_NAME, ExistingWorkPolicy.KEEP, workRequest)
        }
    }

    suspend fun cancelDownload() {
        withContext(Dispatchers.IO) {
            Timber.d("Cancel work")
            WorkManager.getInstance(context).cancelUniqueWork(DOWNLOAD_WORK_NAME)
        }
    }

    fun getDownloadWorkInfo(): Flow<WorkInfo?> {
        return getDownloadWorkInfoFlow()
            .mapLatest { it.firstOrNull() }
            .catch { Timber.e(it) }
            .flowOn(Dispatchers.IO)
    }

    private fun getDownloadWorkInfoFlow(): Flow<List<WorkInfo>> {
        return WorkManager
            .getInstance(context)
            .getWorkInfosForUniqueWorkLiveData(DOWNLOAD_WORK_NAME)
            .asFlow()
    }

    @SuppressLint("RestrictedApi")
    suspend fun startSynchronization() {
        withContext(Dispatchers.IO) {

//            public static final long MIN_PERIODIC_INTERVAL_MILLIS = 15 * 60 * 1000L; // 15 minutes.
            val workRequest =
                PeriodicWorkRequestBuilder<SynchronizationWorker>(15, TimeUnit.MINUTES)
                    .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                SYNCHRONIZATION_WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    suspend fun cancelSynchronization() {
        withContext(Dispatchers.IO) {
            Timber.d("Cancel synchronization")
            WorkManager.getInstance(context).cancelUniqueWork(SYNCHRONIZATION_WORK_NAME)
        }
    }

    companion object {
        const val DOWNLOAD_WORK_NAME = "download work id"
        const val SYNCHRONIZATION_WORK_NAME = "sync work id"
    }
}