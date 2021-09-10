package com.macrosystems.dailynews.domain

import com.macrosystems.dailynews.data.model.news.NewsResponse
import com.macrosystems.dailynews.data.network.response.Result
import com.macrosystems.dailynews.data.services.NewsFeedService
import javax.inject.Inject

class GetNewsFeed @Inject constructor(private val newsFeedService: NewsFeedService) {

    suspend operator fun invoke(): Result<List<NewsResponse>> = newsFeedService.getNewsFeed()
}