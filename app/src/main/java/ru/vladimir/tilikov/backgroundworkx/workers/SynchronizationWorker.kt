package ru.vladimir.tilikov.backgroundworkx.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import timber.log.Timber

class SynchronizationWorker(
    context: Context,
    params: WorkerParameters
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        (0..10).asFlow()
            .onStart { Timber.d("--> Synchronization start <--") }
            .onEach { Timber.d("Synchronization progress: ${it * 10}%") }
            .onEach { delay(1000) }
            .collect()

        return Result.success()
    }
}