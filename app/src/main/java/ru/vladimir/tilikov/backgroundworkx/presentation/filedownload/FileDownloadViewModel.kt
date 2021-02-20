package ru.vladimir.tilikov.backgroundworkx.presentation.filedownload

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ru.vladimir.tilikov.backgroundworkx.data.repositories.FileRepository

@ExperimentalCoroutinesApi
class FileDownloadViewModel(application: Application): AndroidViewModel(application) {

    private val repository = FileRepository(application)

    val downloadWorkState = repository.getDownloadWorkInfo()

    init {
        startSynchronization()
    }

    fun downloadFile(url: String) {
        viewModelScope.launch {
            repository.startDownloadFileWork(url)
        }
    }

    fun cancelDownload() {
        viewModelScope.launch {
            repository.cancelDownload()
        }
    }

    private fun startSynchronization() {
        viewModelScope.launch {
            repository.startSynchronization()
        }
    }

    private fun cancelSynchronization() {
        viewModelScope.launch {
            repository.cancelSynchronization()
        }
    }

    override fun onCleared() {
        cancelSynchronization()
        super.onCleared()
    }
}