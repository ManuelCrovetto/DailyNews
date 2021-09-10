package com.macrosystems.dailynews.ui.view.viewstate

data class DetailedNewsViewState(
    val emptyImageUrl: Boolean = false,
    val emptyTitle: Boolean = false,
    val emptyDate: Boolean = false,
    val emptyDescription: Boolean = false,
    val emptyVideoUrl: Boolean = false
)