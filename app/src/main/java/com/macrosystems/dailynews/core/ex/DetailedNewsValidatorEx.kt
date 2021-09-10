package com.macrosystems.dailynews.core.ex

import com.macrosystems.dailynews.data.model.parcelable.DetailedNewsParcelable
import com.macrosystems.dailynews.ui.view.viewstate.DetailedNewsViewState

private fun isImageUrlEmpty(imageUrl: String) = imageUrl.isEmpty()
private fun isTitleEmpty(title: String) = title.isEmpty()
private fun isDateEmpty(date: String) = date.isEmpty()
private fun isDescriptionEmpty(description: String) = description.isEmpty()
private fun isVideoUrlEmpty(videoUrl: String) = videoUrl.isEmpty()

fun DetailedNewsParcelable.validateForUIEvents(): DetailedNewsViewState {
    return DetailedNewsViewState(
        emptyImageUrl = isImageUrlEmpty(imageUrl),
        emptyTitle = isTitleEmpty(title),
        emptyDate = isDateEmpty(date),
        emptyDescription = isDescriptionEmpty(description),
        emptyVideoUrl = isVideoUrlEmpty(videoUrl)
    )
}