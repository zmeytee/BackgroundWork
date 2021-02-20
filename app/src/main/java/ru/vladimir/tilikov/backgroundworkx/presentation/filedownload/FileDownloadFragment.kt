package ru.vladimir.tilikov.backgroundworkx.presentation.filedownload

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkInfo
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.vladimir.tilikov.backgroundworkx.R
import ru.vladimir.tilikov.backgroundworkx.databinding.FragmentFileDownloadBinding
import ru.vladimir.tilikov.backgroundworkx.utils.toast
import timber.log.Timber

@FlowPreview
@ExperimentalCoroutinesApi
class FileDownloadFragment : Fragment(R.layout.fragment_file_download) {

    private val binding by viewBinding(FragmentFileDownloadBinding::bind)
    private val viewModel by viewModels<FileDownloadViewModel>()
    private var alertDialog: AlertDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
        setListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        alertDialog?.dismiss()
    }

    private fun bindViewModel() {
        with(viewModel) {
            downloadWorkState
                .onEach { workInfo ->
                    workInfo?.let { handleUiVisibility(it) }
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    private fun setListeners() {
        with(binding) {
            downloadFileButton.setOnClickListener { downloadFile() }
            cancelDownloadButton.setOnClickListener { viewModel.cancelDownload() }
        }
    }

    private fun downloadFile() {
        val url = binding.urlEditTextLayout.editText?.text.toString().trim()
        viewModel.downloadFile(url)
    }

    private fun handleUiVisibility(workInfo: WorkInfo) {
        val isFinished = workInfo.state.isFinished
        onWorkIsFinished(isFinished)

        when (workInfo.state) {
            WorkInfo.State.ENQUEUED -> binding.progressBar2.isVisible = false
            WorkInfo.State.RUNNING -> binding.waitDownloadTextView.isVisible = false
            WorkInfo.State.SUCCEEDED -> toast(R.string.file_download_success)
            WorkInfo.State.CANCELLED -> toast(R.string.downloading_canceled)
            WorkInfo.State.BLOCKED -> toast(R.string.downloading_blocked)
            WorkInfo.State.FAILED -> showAlertDialog(R.string.file_download_error)
        }

        Timber.d("Work state = ${workInfo.state}")
    }

    private fun onWorkIsFinished(isFinished: Boolean) {
        with(binding) {
            urlEditTextLayout.isEnabled = isFinished
            downloadFileButton.isVisible = isFinished
            cancelDownloadButton.isVisible = !isFinished
            progressBar.isVisible = !isFinished
            waitDownloadTextView.isVisible = !isFinished
            progressBar2.isVisible = !isFinished
        }
    }

    private fun showAlertDialog(@StringRes message: Int) {
        alertDialog = AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("Retry") { _, _ -> downloadFile() }
            .create()

        alertDialog?.show()
    }
}
