package ru.vladimir.tilikov.backgroundworkx.workers

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vladimir.tilikov.backgroundworkx.networking.Networking
import timber.log.Timber
import java.io.File

class DownloadWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val url = inputData.getString(DOWNLOAD_URL_KEY)

        if (url.isNullOrBlank()) return Result.failure()

        return withContext(Dispatchers.IO) {
            try {
                downloadFileFromUrl(url)
                Result.success()
            } catch (t: Throwable) {
                Timber.e(t)
                Result.failure()
            }
        }
    }

    private suspend fun downloadFileFromUrl(url: String) {
        Timber.d("Download from: $url")
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) return

        val folder = context.getExternalFilesDir(FOLDER)
        val fileName = Uri.parse(url).path?.split("/")?.last() ?: "NoName"
        val file = File(folder, fileName)

        try {
            file.outputStream().use { fileOutputStream ->
                Networking.fileApi.getFile(url)
                    .byteStream()
                    .use { inputStream ->
                        inputStream.copyTo(fileOutputStream)
                    }
            }
        } catch (t: Throwable) {
            file.delete()
            Timber.e(t)
            error("Save file error: ${t.message}")
        }
    }

    companion object {
        const val DOWNLOAD_URL_KEY = "download url key"
        const val FOLDER = "ownFiles"
    }
}