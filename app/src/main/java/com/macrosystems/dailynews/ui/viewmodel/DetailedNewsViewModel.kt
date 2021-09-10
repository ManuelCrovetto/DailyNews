package com.macrosystems.dailynews.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.macrosystems.dailynews.core.ex.validateForUIEvents
import com.macrosystems.dailynews.data.model.parcelable.DetailedNewsParcelable
import com.macrosystems.dailynews.ui.view.viewstate.DetailedNewsViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DetailedNewsViewModel @Inject constructor() : ViewModel(){

    private val _viewState = MutableStateFlow(DetailedNewsViewState())
    val viewState: StateFlow<DetailedNewsViewState>
        get() = _viewState


    fun validateNewsDetails(detailedNewsFragment: DetailedNewsParcelable) {
        _viewState.value = detailedNewsFragment.validateForUIEvents()
    }

}