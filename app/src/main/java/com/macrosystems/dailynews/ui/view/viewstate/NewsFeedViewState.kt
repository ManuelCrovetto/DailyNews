package com.macrosystems.dailynews.ui.view.viewstate

data class NewsFeedViewState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: Boolean = false,
    val isEmptyList: Boolean = false
)