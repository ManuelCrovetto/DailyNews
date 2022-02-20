package com.macrosystems.dailynews.domain

import com.macrosystems.dailynews.data.model.news.*
import com.macrosystems.dailynews.data.network.response.Result
import com.macrosystems.dailynews.data.services.NewsFeedService
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetNewsFeedTest {

    @RelaxedMockK
    private lateinit var newsFeedService: NewsFeedService

    lateinit var getNewsFeed: GetNewsFeed

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getNewsFeed = GetNewsFeed(newsFeedService)
    }

    @Test
    fun `api returns empty list`() = runBlocking {
        coEvery { newsFeedService.getNewsFeed() } returns Result.Success(emptyList())
        val response = getNewsFeed()
        coVerify(exactly = 1){ newsFeedService.getNewsFeed() }
        assert(response is Result.Success)
        when(response) {
            is Result.Error -> {}
            is Result.Success -> assert(response.data.isEmpty())
        }
    }

    @Test
    fun `api returns returns a not empty list`() = runBlocking {
        val list = mockNewsResponseList()
        coEvery { newsFeedService.getNewsFeed() } returns Result.Success(list)
        val response = getNewsFeed()
        coVerify (exactly = 1) { newsFeedService.getNewsFeed() }
        assert(response is Result.Success)
        when (response) {
            is Result.Error -> {}
            is Result.Success -> {
                assert(response.data.isNotEmpty())
            }
        }
    }

    @Test
    fun `api returns error`() = runBlocking {
        coEvery { newsFeedService.getNewsFeed() } returns Result.Error(Exception())
        val response = getNewsFeed()
        coVerify (exactly = 1) { newsFeedService.getNewsFeed() }
        assert(response is Result.Error)
    }

    private fun mockNewsResponseList(): List<NewsResponse> {
        val list = mutableListOf<NewsResponse>()
        ('a'..'z').forEach { _ ->
            list.add(
                NewsResponse(
                    author = 1,
                    categories = listOf(1,2),
                    comment_status = "",
                    content = Content(true, ""),
                    date = "",
                    date_gmt = "",
                    excerpt = Excerpt(false, ""),
                    featured_media = 23,
                    format = "",
                    guid = Guid(""),
                    id = 28998374,
                    link = "",
                    meta = listOf("hello", "hello"),
                    modified = "",
                    modified_gmt = "",
                    ping_status = "",
                    slug = "",
                    status = "",
                    sticky = false,
                    tags = listOf("", ""),
                    template = "",
                    title = Title(""),
                    type = "",
                    yoast_head = ""
                )
            )
        }
        return list
    }
}