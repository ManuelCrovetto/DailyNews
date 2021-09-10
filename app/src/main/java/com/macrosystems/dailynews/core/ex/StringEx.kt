package com.macrosystems.dailynews.core.ex

import android.annotation.SuppressLint
import java.lang.Exception
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun String.formatDate(): String? {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val formatter = SimpleDateFormat("dd MM yyyy")
        formatter.format(parser.parse(this)!!)

    } catch (e: Exception){
        e.printStackTrace()
        return null
    }
}
