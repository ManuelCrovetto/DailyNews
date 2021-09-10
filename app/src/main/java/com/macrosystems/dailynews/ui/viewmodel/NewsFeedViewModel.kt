package com.macrosystems.dailynews.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macrosystems.dailynews.core.ex.parseNewsDetailsFromHtml
import com.macrosystems.dailynews.data.model.news.NewsResponse
import com.macrosystems.dailynews.data.model.parcelable.DetailedNewsParcelable
import com.macrosystems.dailynews.data.network.response.Result
import com.macrosystems.dailynews.domain.GetNewsFeed
import com.macrosystems.dailynews.ui.view.viewstate.NewsFeedViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsFeedViewModel @Inject constructor(private val getNewsFeed: GetNewsFeed): ViewModel() {

    private val _viewState = MutableStateFlow(NewsFeedViewState())
    val viewState: StateFlow<NewsFeedViewState>
        get() = _viewState

    private val _newsFeedList = MutableLiveData<MutableList<NewsResponse>>()
    val newsFeedList: LiveData<MutableList<NewsResponse>>
        get() = _newsFeedList

    private val _featuredNews = MutableLiveData<DetailedNewsParcelable>()
    val featuredNews: LiveData<DetailedNewsParcelable>
        get() = _featuredNews

    init {
        getNewsFeedList()
    }

    fun getNewsFeedList() {
        viewModelScope.launch(IO) {
            _viewState.value = NewsFeedViewState(isLoading = true)

            when (val result = getNewsFeed()){
                is Result.Error -> {
                    result.message?.let {
                        _newsFeedList.postValue(mutableListOf())
                        _viewState.value = NewsFeedViewState(isLoading = false, error = true, isEmptyList = true)
                    } ?: run {
                        _newsFeedList.postValue(mutableListOf())
                        _viewState.value = NewsFeedViewState(isLoading = false, isEmptyList = true)
                    }
                }
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        _newsFeedList.postValue(mutableListOf())
                        _viewState.value = NewsFeedViewState(isLoading = false, success = true, isEmptyList = true)
                    } else {
                        _featuredNews.postValue(result.data[0].parseNewsDetailsFromHtml())
                        _newsFeedList.postValue(result.data.toMutableList())
                        _viewState.value = NewsFeedViewState(isLoading = false, success = true)
                    }
                }
            }
        }
    }
}