package com.macrosystems.dailynews.data.model.parcelable

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


//We do Parcelize this object to be able to use it through SafeArgs.
@Parcelize
data class DetailedNewsParcelable(
    val imageUrl: String,
    val title: String,
    val date: String,
    val description: String,
    val videoUrl: String
) : Parcelable