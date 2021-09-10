package com.macrosystems.dailynews.data.services

import com.macrosystems.dailynews.data.model.news.NewsResponse
import com.macrosystems.dailynews.data.network.district8api.District8API
import com.macrosystems.dailynews.data.network.response.Result
import java.lang.Exception
import javax.inject.Inject

class NewsFeedService @Inject constructor(private val district8API: District8API) {

    suspend fun getNewsFeed(): Result<List<NewsResponse>>{
        return try {
            val response = district8API.getNews()
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Result.Success(it)
                } ?: run {
                    Result.Error(null)
                }
            } else {
                Result.Error(null)
            }
        } catch (e: Exception) {
            Result.Error(Exception("Network error"))
        }
    }
}