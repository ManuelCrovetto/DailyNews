package com.macrosystems.dailynews.core.ex

import android.content.Context
import android.widget.TextView
import com.macrosystems.dailynews.R

fun TextView.setSmallTitle(){
    this.setTextAppearance(this.context, R.style.Headline)
}

fun TextView.setMediumTitle() {
    this.setTextAppearance(this.context, R.style.Headline_Medium)
}

fun TextView.setLargeTitle() {
    this.setTextAppearance(this.context, R.style.Headline_Large)
}

fun TextView.setSmallDate() {
    this.setTextAppearance(this.context, R.style.Caption_Two)
}

fun TextView.setMediumDate() {
    this.setTextAppearance(this.context, R.style.Caption_Two_Medium)
}

fun TextView.setLargeDate() {
    this.setTextAppearance(this.context, R.style.Caption_Two_Large)
}

fun TextView.setSmallDescription() {
    this.setTextAppearance(this.context, R.style.Body_One)
}

fun TextView.setMediumDescription() {
    this.setTextAppearance(this.context, R.style.Body_One_Medium)
}

fun TextView.setLargeDescription() {
    this.setTextAppearance(this.context, R.style.Body_One_Large)
}








