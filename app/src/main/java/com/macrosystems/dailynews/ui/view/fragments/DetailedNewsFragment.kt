package com.macrosystems.dailynews.ui.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.webkit.WebSettings
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager

import com.macrosystems.dailynews.core.dialog.launcher.DialogFragmentLauncher
import com.macrosystems.dailynews.R
import com.macrosystems.dailynews.core.dialog.ErrorDialog
import com.macrosystems.dailynews.core.ex.*

import com.macrosystems.dailynews.data.model.parcelable.DetailedNewsParcelable
import com.macrosystems.dailynews.databinding.DetailedNewsFragmentBinding
import com.macrosystems.dailynews.ui.view.dialogs.ChangeTextSizeDialog
import com.macrosystems.dailynews.ui.view.viewstate.DetailedNewsViewState
import com.macrosystems.dailynews.ui.viewmodel.DetailedNewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class DetailedNewsFragment @Inject constructor(
    private val glide: RequestManager,
    private val dialogFragmentLauncher: DialogFragmentLauncher
) : Fragment() {

    private var _binding: DetailedNewsFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailedNewsViewModel by viewModels()

    private val args: DetailedNewsFragmentArgs? by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DetailedNewsFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        args?.let { it ->
            viewModel.validateNewsDetails(it.newsDetails)
            initObservers(it.newsDetails)
        } ?: run {
            showErrorDialog()
        }
    }

    private fun initObservers(detailedNews: DetailedNewsParcelable) {
        with(viewModel){
            lifecycleScope.launchWhenStarted {
                viewState.collect { viewState ->
                    handleUI(viewState, detailedNews)
                }
            }
        }

    }

    private fun handleUI(viewState: DetailedNewsViewState, detailedNews: DetailedNewsParcelable) {
        with(binding) {
            if (!viewState.emptyImageUrl) glide.load(detailedNews.imageUrl).into(ivHeaderImage) else ivHeaderImage.isGone = true
            if (!viewState.emptyTitle) tvTitle.text = detailedNews.title else tvTitle.isGone = true
            if (!viewState.emptyDate) tvDate.text = detailedNews.date else tvDate.isGone = true
            if (!viewState.emptyDescription) tvDescription.text = detailedNews.description else tvDescription.isGone = true
            if (!viewState.emptyVideoUrl) setupVideo(detailedNews.videoUrl) else wvVideo.isGone = true
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupVideo(videoUrl: String) {
        with(binding) {
            wvVideo.isVisible = true
            val videoSettings = wvVideo.settings
            videoSettings.javaScriptEnabled = true
            videoSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
            videoSettings.loadWithOverviewMode = true
            videoSettings.useWideViewPort = true
            wvVideo.loadDataWithBaseURL(null, "<style>img{display: inline;height: auto;max-width: 100%;}</style>$videoUrl", "text/html",
            "utf-8", null)
        }
    }

    private fun showErrorDialog() {
        ErrorDialog.create(
            description = getString(R.string.error_description_dialog_placeholder),
            isDialogCancelable = true,
            positiveAction = ErrorDialog.Action(getString(R.string.ok_placeholder)) {
                it.dismiss()
            }
        ).showDialog(dialogFragmentLauncher = dialogFragmentLauncher, requireActivity())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.detailed_news_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btnChangeTextSize -> {
                showChangeTextSizeDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showChangeTextSizeDialog() {
        ChangeTextSizeDialog.create(
            title = getString(R.string.change_text_size_title_placeholder),
            isDialogCancelable = true,
            positiveAction = ChangeTextSizeDialog.Action("") { textSize ->
                changeTextSize(textSize)
            }
        ).showDialog(dialogFragmentLauncher = dialogFragmentLauncher, requireActivity())
    }

    private fun changeTextSize(textSize: Int) {
        with(binding){
            when (textSize) {
                1 -> {
                    tvTitle.setSmallTitle()
                    tvDate.setSmallDate()
                    tvDescription.setSmallDescription()
                }
                2 -> {
                    tvTitle.setMediumTitle()
                    tvDate.setMediumDate()
                    tvDescription.setMediumDescription()
                }
                3 -> {
                    tvTitle.setLargeTitle()
                    tvDate.setLargeDate()
                    tvDescription.setLargeDescription()
                }
            }
        }
    }


}