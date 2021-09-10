package com.macrosystems.dailynews.ui.view.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.macrosystems.beerfinder.core.dialog.launcher.DialogFragmentLauncher
import com.macrosystems.dailynews.R
import com.macrosystems.dailynews.core.dialog.ErrorDialog
import com.macrosystems.dailynews.core.ex.parseNewsDetailsFromHtml
import com.macrosystems.dailynews.core.ex.showDialog
import com.macrosystems.dailynews.data.model.constants.Constants.NINE_TO_FIVE_WEB_URL
import com.macrosystems.dailynews.data.model.parcelable.DetailedNewsParcelable
import com.macrosystems.dailynews.databinding.NewsFeedFragmentBinding
import com.macrosystems.dailynews.ui.adapters.NewsFeedAdapter
import com.macrosystems.dailynews.ui.view.viewstate.NewsFeedViewState
import com.macrosystems.dailynews.ui.viewmodel.NewsFeedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class NewsFeedFragment @Inject constructor(
    private val newsFeedAdapter: NewsFeedAdapter,
    private val dialogFragmentLauncher: DialogFragmentLauncher,
    private val glide: RequestManager
) : Fragment() {

    private var _binding: NewsFeedFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsFeedViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = NewsFeedFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        viewModel.getNewsFeedList()
        setUpRecyclerView()
        initObservers()
        initRecyclerViewListeners()

    }

    private fun setUpRecyclerView() {
        binding.newsRecyclerView.apply {
            adapter = this@NewsFeedFragment.newsFeedAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initObservers() {
        with(viewModel){
            newsFeedList.observe(viewLifecycleOwner) {
                newsFeedAdapter.setData(it)
            }

            featuredNews.observe(viewLifecycleOwner) { featuredNews ->
                loadFeaturedNews(featuredNews)

            }

            lifecycleScope.launchWhenStarted {
                viewState.collect { viewState ->
                    updateUI(viewState)
                }
            }
        }
    }

    private fun updateUI(viewState: NewsFeedViewState) {
        with(binding) {
            progressBar.isVisible = viewState.isLoading

            if (viewState.error) showErrorDialog()
        }
    }

    private fun initRecyclerViewListeners() {
        newsFeedAdapter.onClickListener = {
            val action = NewsFeedFragmentDirections.actionNewsFeedFragmentToDetailedNewsFragment(it.parseNewsDetailsFromHtml())
            findNavController().navigate(action)
        }
    }

    private fun loadFeaturedNews(featuredNews: DetailedNewsParcelable) {
        YoYo.with(Techniques.SlideInDown).duration(777).playOn(binding.clFeaturedNewsLayout)
        binding.clFeaturedNewsLayout.isVisible = true

        glide.load(featuredNews.imageUrl).into(binding.ivFeaturedNew)
        with(binding){
            tvFeaturedNewsTitle.text = featuredNews.title
            tvFeaturedNewsDate.text = featuredNews.date

            clFeaturedNewsLayout.setOnClickListener {
                val action = NewsFeedFragmentDirections.actionNewsFeedFragmentToDetailedNewsFragment(featuredNews)
                findNavController().navigate(action)
            }
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
        inflater.inflate(R.menu.news_feed_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btnContact9to5 -> {
                intentTo9to5Web()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun intentTo9to5Web() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(NINE_TO_FIVE_WEB_URL))
        startActivity(intent)
    }


}