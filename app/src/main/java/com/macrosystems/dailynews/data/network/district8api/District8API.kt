package com.macrosystems.dailynews.data.network.district8api

import com.macrosystems.dailynews.data.model.news.NewsResponse
import retrofit2.Response
import retrofit2.http.GET

interface District8API {

    @GET("posts/")
    suspend fun getNews(): Response<List<NewsResponse>>
}